package edu.harvard.cs262.GameServer.GameClusterServer;

import edu.harvard.cs262.DistributedGame.*;
import edu.harvard.cs262.Exceptions.NotMasterException;
import edu.harvard.cs262.GameServer.GameServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameClusterServer implements GameServer {
  private static final long serialVersionUID = 1L;
  private GameCommandProcessor processor;
  private Game game;
  public UUID uuid;
  private GameServer master;
  private Hashtable<UUID, GameServer> peers;
  private boolean amMaster;

  public GameClusterServer(GameCommandProcessor processor, Game game) {
    this.processor = processor;
    this.game = game;

    peers = new Hashtable<UUID, GameServer>();
    master = null;
    amMaster = false;
    uuid = UUID.randomUUID();
  }

  public GameSnapshot sendCommand(GameCommand command) throws RemoteException, NotMasterException {
    if (!this.amMaster) {
      throw new NotMasterException(this.master);
    }
    this.processor.startProcessor();
    this.processor.addCommand(command);
    GameCommand decidedCommand = this.processor.getCommand();
    this.game.executeCommand(decidedCommand);
    this.updatePeersState(1);
    return this.game.getSnapshot();
  } // also throws NotMasterException

  public GameSnapshot getSnapshot() throws RemoteException {
    return this.game.getSnapshot();
  }

  @Override
  public boolean setState(GameState state) throws RemoteException {
    if (state.getFrame() > this.game.getState().getFrame()) {
      this.game.setState(state);
    }
    System.out.println(this.game.getState());
    return true;
  }

  @Override
  public boolean addPeer(UUID id, GameServer server) throws RemoteException {
    UUID key = server.getUUID();

    // add worker to free workers and allworkers lists
    peers.put(key, server);

    System.out.format("Registered Worker %s\n", key.toString());
    System.out.flush();

    return true;
  }

  @Override
  public boolean removePeer(UUID id) throws RemoteException {
    System.out.format("Removing worker %s\n", id.toString());
    // if this is not a current worker, return
    if (null == peers.get(id)) {
      return true;
    }

    peers.remove(id);

    return true;
  }

  @Override
  public boolean setPeers(Hashtable<UUID, GameServer> peers) throws RemoteException {
    this.peers = peers;
    return true;
  }

  private class SendStateWrapper implements Callable<Boolean> {
    GameServer peer;
    GameState state;

    public SendStateWrapper(GameServer peer, GameState state) {
      this.peer = peer;
      this.state = state;
    }

    public Boolean call() throws RemoteException {
      return peer.setState(this.state);
    }
  }

  /*
   * Calling this method will send state to all peers (if this is the master).
   * It blocks until at least frac fraction of the peers have been updated successfully.
   * Even after stopped blocking, tries to update past the rest.
   * 0<frac<=1
   * Returns true if successful (for frac)
   */
  private boolean updatePeersState(float frac) throws NotMasterException {
    if (!this.isMaster())
      throw new NotMasterException(this.master);

    // Track number of successful/unsuccesful updates
    int updatedPeers = 0;
    int failedPeers = 0;
    int num_peers = peers.size()-1;

    ArrayList<Future<Boolean>> sendStateFutures = new ArrayList<Future<Boolean>>();
    ExecutorService pool = Executors.newFixedThreadPool(10);
    // Spin off a thread to update each server
    for (UUID id : peers.keySet()) {
      if (!id.equals(this.uuid))
        sendStateFutures.add(pool.submit(new SendStateWrapper(peers.get(id), this.game.getState())));
    }
    while (true) {
      if (num_peers == 0 || ((float) updatedPeers) / num_peers >= frac)
        return true;
      if (((float) failedPeers) / num_peers > 1 - frac)
        return false;
      for (Future<Boolean> activeSendState : sendStateFutures) {
        if (activeSendState.isDone()) {
          try {
            if (activeSendState.get()) {
              updatedPeers++;
            } else {
              failedPeers++;
            }
          } catch (Exception e) {
            // Update failed!
            failedPeers++;
            e.printStackTrace();
          }
        }
      }
    }

  }

  // SLAVE => MASTER METHODS
  public GameState getState() throws RemoteException {
    return this.game.getState();
  }

  @Override
  public GameDiff getDiff(GameState state) throws RemoteException {
    return this.game.getDiff(state);
  }

  @Override
  public boolean pingServer() throws RemoteException {
    return true;
  }

  @Override
  public UUID getUUID() throws RemoteException {
    return this.uuid;
  }

  @Override
  public Hashtable<UUID, GameServer> getPeers() throws RemoteException {
    return this.peers;
  }


  private class IdServerPair {
    GameServer server;
    UUID id;

    public IdServerPair(UUID id, GameServer server) {
      this.id = id;
      this.server = server;
    }
  }

  @Override
  public Object startLeaderElection() {
    System.out.println("Received request to start leader election");
    if (this.master != null && this.checkMaster())
      return null;

    IdServerPair pair = new IdServerPair(this.uuid, this);
    return pair;
  }

  public boolean runLeaderElection() {
    // only run if we are the minimum alive ID
    ArrayList<UUID> sortedIds = new ArrayList(this.peers.keySet());
    Collections.sort(sortedIds);
    UUID minAliveWorkerId = this.uuid;
    for (UUID id : sortedIds) {
      if (this.checkServer(this.peers.get(id)))
        minAliveWorkerId = id;
    }

    // XXX need to set some field "leader election ongoing"
    if (!minAliveWorkerId.equals(this.uuid))
      return false;

    System.out.println("Running leader election");

    Hashtable<UUID, GameServer> activePeers = new Hashtable<UUID, GameServer>();
    for (UUID id : this.peers.keySet()) {
      activePeers.put(this.uuid, this);
      if (!(id.equals(this.uuid))) {
        GameServer peer = this.peers.get(id);
        try {
          IdServerPair pair = (IdServerPair) (peer.startLeaderElection());
          if (pair == null)
            return false;
          else {
            activePeers.put((UUID) pair.id, (GameServer) pair.server);
          }
        }
        // skip unreachable peers
        catch (RemoteException e) {
          continue;
        }
      }
    }

    // pick peer with minimum id
    UUID minId = Collections.min(activePeers.keySet());
    GameServer newMaster = activePeers.get(minId);

    ArrayList<UUID> deadPeers = new ArrayList<UUID>();
    for (UUID id : this.peers.keySet()) {
      GameServer peer = this.peers.get(id);
      try {
        peer.closeLeaderElection(minId, newMaster);
      } catch (RemoteException e) {
        deadPeers.add(id);
        continue;
      }
    }

    // remove deadPeers
    try {
      for (UUID deadID : deadPeers) {
        newMaster.removePeer(deadID);
      }
    } catch (RemoteException e) {
      // XXX handle;
    }

    return true;
  }

  public boolean closeLeaderElection(UUID id, GameServer newLeader) {
    this.setMaster(newLeader);
    return true;
  }

  public GameServer getMaster() {
    return this.master;
  }

  public boolean setMaster(GameServer newMaster) {
    // XXX need to catch exception  here; probably a better way to do this
    try {
      if (newMaster.getUUID().equals(this.uuid)) {
        System.out.println("I am the master!");
        this.amMaster = true;
      } else
        this.amMaster = false;

      this.master = newMaster;
      return true;
    } catch (RemoteException e) {
      return false;
    }
  }

  public boolean checkServer(GameServer s) {
    try {
      s.pingServer();
      return true;
    } catch (RemoteException e) {
      return false;
    }

  }

  public boolean isMaster() {
    return this.amMaster;
  }

  public boolean checkMaster() {
    if (this.master != null && checkServer(this.master))
      return true;
    else {
      this.master = null;
      return false;
    }
  }

}
