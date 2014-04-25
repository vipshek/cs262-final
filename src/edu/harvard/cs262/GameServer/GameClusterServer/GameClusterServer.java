package edu.harvard.cs262.GameServer.GameClusterServer;

import edu.harvard.cs262.ClusterServer.BasicClusterServer.IdServerPair;
import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.DistributedGame.*;
import edu.harvard.cs262.Exceptions.NotMasterException;
import edu.harvard.cs262.GameServer.ClusterGameServer;
import edu.harvard.cs262.GameServer.GameServer;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameClusterServer implements ClusterGameServer {
  private static final long serialVersionUID = 1L;
  private GameCommandProcessor processor;
  private Game game;
  public UUID uuid;
  private ClusterServer master;
  private Hashtable<UUID, ClusterGameServer> workers;
  private boolean amMaster;

  public GameClusterServer(GameCommandProcessor processor, Game game) {
    this.processor = processor;
    this.game = game;

    workers = new Hashtable<UUID, ClusterServer>();
    // create lock
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
    return this.game.getSnapshot();
  } // also throws NotMasterException

  public GameSnapshot getSnapshot() throws RemoteException {
    return this.game.getSnapshot();
  }

  // MASTER => SLAVE METHODS
  public boolean sendState(GameState state) throws RemoteException {
    this.game.setState(state);
    return true;
  }

  public boolean sendDiff(GameDiff diff) throws RemoteException {
    diff.apply(this.game);
    return true;
  }

  private class SendStateWrapper implements Callable<Boolean> {
    ClusterGameServer peer;
    GameState state;

    public SendStateWrapper(ClusterGameServer peer, GameState state) {
      this.peer = peer;
      this.state = state;
    }

    public Boolean call() throws RemoteException {
      return peer.sendState(this.state);
    }
  }

  /*
   * Calling this method will send state to all peers (if this is the master).
   * It blocks until at least frac fraction of the peers have been updated successfully.
   * Even after stopped blocking, tries to update past the rest.
   *
   * Returns true if successful (for frac)
   */
  private boolean updatePeersState(float frac) throws NotMasterException {
    // What if servers go down while in this function, so no longer possible?
    int updatedPeers = 0;
    int failedPeers = 0;
    ArrayList<Future<Boolean>> sendStateFutures = new ArrayList<Future<Boolean>>();
    ExecutorService pool = Executors.newFixedThreadPool(10);
    for (ClusterGameServer peer : this.workers.values()) {
      sendStateFutures.add(pool.submit(new SendStateWrapper(peer, this.game.getState())));
    }
    while (true) {
      int num_workers = workers.size();
      if (num_workers == 0)
        return true;
      for (Future<Boolean> activeSendState :  sendStateFutures) {
        if (activeSendState.isDone()) {
          
        }
      }
    }

  }

  public boolean addPeer(GameServer server) throws RemoteException {
    throw new UnsupportedOperationException();
  }

  public boolean removePeer(GameServer server) throws RemoteException {
    throw new UnsupportedOperationException();
  }

  public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
    throw new UnsupportedOperationException();
  }

  // SLAVE => MASTER METHODS
  public GameState getState() throws RemoteException {
    return this.game.getState();
  }

  public boolean getDiff(long start) throws RemoteException {
    // This makes no sense
    throw new UnsupportedOperationException();
  }

  // GENERAL SERVER => SERVER METHODS
  public boolean sendHeartbeat() throws RemoteException {
    throw new UnsupportedOperationException();
  }

  public boolean register() throws RemoteException {
    throw new UnsupportedOperationException();
  } // also throws NotMasterException

  public boolean unregister() throws RemoteException {
    throw new UnsupportedOperationException();
  } // also throws NotMasterException

  @Override
  public UUID getUUID() throws RemoteException {
    return this.uuid;
  }

  @Override
  public Hashtable<UUID, ClusterGameServer> getWorkers() throws RemoteException {
    // WTF is a clusterserver, and why does it not have to be a game server?
    return workers;
  }

  @Override
  public boolean registerWorker(ClusterServer server) throws RemoteException {
    UUID key = server.getUUID();

    // add worker to free workers and allworkers lists
    workers.put(key, server);

    System.out.format("Registered Worker %s\n", key.toString());
    System.out.flush();

    return true;
  }

  private boolean removeWorker(UUID workerID) {
    // if this is not a current worker, return
    if (null == workers.get(workerID)) {
      return true;
    }

    workers.remove(workerID);

    return true;
  }

  @Override
  public boolean unregisterWorker(UUID workerID) throws RemoteException {
    System.out.format("Removing worker %s\n", workerID.toString());
    return this.removeWorker(workerID);
  }

  @Override
  public boolean PingServer() throws RemoteException {
    return true;
  }

  @Override
  public Object StartLeaderElection() {
    System.out.println("Received request to start leader election");
    if (this.master != null && this.checkMaster())
      return null;

    IdServerPair pair = new IdServerPair(this.uuid, this);
    return pair;
  }

  public boolean runLeaderElection() {
    // only run if we are the minimum alive ID
    ArrayList<UUID> sortedIds = new ArrayList(this.workers.keySet());
    Collections.sort(sortedIds);
    UUID minAliveWorkerId = this.uuid;
    for (UUID id : sortedIds) {
      if (this.checkServer(this.workers.get(id)))
        minAliveWorkerId = id;
    }

    // XXX need to set some field "leader election ongoing"
    if (!minAliveWorkerId.equals(this.uuid))
      return false;

    System.out.println("Running leader election");

    Hashtable<UUID, ClusterServer> activePeers = new Hashtable<UUID, ClusterServer>();
    for (UUID id : this.workers.keySet()) {
      activePeers.put(this.uuid, this);
      if (!(id.equals(this.uuid))) {
        ClusterServer peer = this.workers.get(id);
        try {
          IdServerPair pair = (IdServerPair) (peer.StartLeaderElection());
          if (pair == null)
            return false;
          else {
            activePeers.put((UUID) pair.id, (ClusterServer) pair.server);
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
    ClusterServer newMaster = activePeers.get(minId);

    ArrayList<UUID> deadPeers = new ArrayList<UUID>();
    for (UUID id : this.workers.keySet()) {
      ClusterServer peer = this.workers.get(id);
      try {
        peer.CloseLeaderElection(minId, newMaster);
      } catch (RemoteException e) {
        deadPeers.add(id);
        continue;
      }
    }

    // remove deadPeers
    try {
      for (UUID deadID : deadPeers) {
        newMaster.unregisterWorker(deadID);
      }
    } catch (RemoteException e) {
      // XXX handle;
    }

    return true;
  }

  public boolean CloseLeaderElection(UUID id, ClusterServer newLeader) {
    this.setMaster(newLeader);
    return true;
  }

  public ClusterServer getMaster() {
    return this.master;
  }

  public boolean setMaster(ClusterServer newMaster) {
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

  public boolean setWorkers(Hashtable<UUID, ClusterServer> workers) {
    this.workers = workers;
    return true;
  }

  public boolean checkServer(ClusterServer s) {
    try {
      s.PingServer();
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
