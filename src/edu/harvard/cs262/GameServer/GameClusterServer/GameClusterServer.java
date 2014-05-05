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

/**
 * The GameClusterServer class contains an implementation of the GameServer
 * interface. It contains a simple leader election algorithm that is tolerant
 * to many types of failure, as well as 100% replication across slave servers.
 *
 * @author Twitch Plays Battleship Group
 *
 * @version 1.0, April 2014
 */
public class GameClusterServer implements GameServer {
    private GameCommandProcessor processor;
    private Game game;
    public UUID uuid;
    private GameServer master;
    private Hashtable<UUID, GameServer> peers;
    private boolean amMaster;
    public boolean inLeaderElection;
    private UUID electorID;

    /**
     *  This is an instantiation of the most basic type of server necessary to implement
     *  this system. This initializes all important instance variables and should be
     *  called whenever a new server joins the system.
     *
     * @param processor  A {@link GameCommandProcessor} that will process the commands
     *        for this game
     * @param game  A {@link Game} that represents the current game
     */
    public GameClusterServer(GameCommandProcessor processor, Game game) {
        this.processor = processor;
        this.game = game;

        peers = new Hashtable<UUID, GameServer>();
        master = null;
        amMaster = false;
        inLeaderElection = false;
        electorID = null;
        uuid = UUID.randomUUID();
    }

    /**
     *  When the client wants to play a move on the game, it calls the sendCommand method
     *  on the master. If the invoked server is not the master, the NotMasterException is
     *  thrown and the client should redirect their call to the proper master. If this is
     *  the master, it executes the command issued by the client and updates all peers
     *  on the new status of the game.
     */
    public GameSnapshot sendCommand(GameCommand command) throws RemoteException, NotMasterException {
        if (!this.amMaster) {
            throw new NotMasterException(this.master);
        }
        this.processor.addCommand(command);
        GameCommand decidedCommand = this.processor.getCommand();
        this.game.executeCommand(decidedCommand);
        this.updatePeersState(1);
        return this.game.getSnapshot();
    }

    /**
     *  If this is the master, it should have the most up-to-date information on the state
     *  of the game. To get info on the game, for the purpose of displaying a surface level
     *  screenshot to the user, this method is called. If the invoked server is not the 
     *  master, the NotMasterException is thrown and the invoking server should redirect
     *  the call to the proper master server.
     */
    @Override
    public GameSnapshot getSnapshot() throws RemoteException, NotMasterException {
        if (!this.amMaster) {
            throw new NotMasterException(this.master);
        }
        return this.game.getSnapshot();
    }

    /**
     *  To make sure the game state is consistent and replicated across all slaves,
     *  setState is called to update the state of a peer that may be behind. First,
     *  the server's frame, how recent its knowledge of the game is, is checked. If the
     *  server's frame is behind the frame of the input state, the server's frame and
     *  state is updated.
     */
    @Override
    public boolean setState(GameState state) throws RemoteException {
        if (state.getFrame() > this.game.getState().getFrame()) {
            this.game.setState(state);
        }
        System.out.println(this.game.getState());
        return true;
    }

    /**
     *  When a new server joins the system, it should be added to all slaves' peer lists.
     *  This method sends the {@link UUID} of the new server and a reference to the new
     *  server's {@link GameServer} object so it can be added to the peer list
     *  {@link Hashtable} of the called upon server.
     */
    @Override
    public boolean addPeer(UUID id, GameServer server) throws RemoteException {
        UUID key = server.getUUID();

        // add peer to peer hashtable
        peers.put(key, server);

        // simple print statements
        System.out.format("Registered peer %s\n", key.toString());
        System.out.flush();

        return true;
    }

    /**
     *  When a peer server is down, it should be removed from the peer lists of all
     *  currently operating servers. This method implements the removal of that dead
     *  peer from the called server's peer list. If the peer is in the peer list at all,
     *  it will be removed and a boolean indicating the success and completion of the
     *  removal is returned.
     */
    @Override
    public boolean removePeer(UUID id) throws RemoteException {
        System.out.format("Removing peer %s\n", id.toString());
        // if this is not a current peer, return
        if (null == peers.get(id)) {
            return true;
        }

        peers.remove(id);

        return true;
    }

    /**
     *  This method is called to inform a server of the peers available in the system.
     *  The input {@link Hashtable} of {@link UUID}s and {@link GameServer}s is used as
     *  the new set of peers on the called server.
     */
    @Override
    public boolean setPeers(Hashtable<UUID, GameServer> peers) throws RemoteException {
        this.peers = peers;
        return true;
    }

    /**
     *  This class is a general wrapper for how to send the info a server has about the
     *  current state of the game.
     */
    private class SendStateWrapper implements Callable<Boolean> {
        GameServer peer;
        GameState state;

        /**
         *  This allows instantiation of the {@link SendStateWrapper} class.
         *
         *  @param peer A {@link GameServer} that wants to send its game state to some other
         *              server
         *  @param state A {@link GameState} that is the current state of the game as far as
         *               the calling server knows
         *
         */
        public SendStateWrapper(GameServer peer, GameState state) {
            this.peer = peer;
            this.state = state;
        }

        /**
         *  This method allows the calling server to finish the process of sending its
         *  state to another server
         *
         *  @throws RemoteException if a general communication error occurs. This is
         *                          required by extending RMI.
         */
        public Boolean call() throws RemoteException {
            return peer.setState(this.state);
        }
    }

    /**
     *  This tells this server to send its knowledge of the current game state
     *  to all other peers. The method blocks until the ratio of updated peers
     *  to total peers is greater than or equal to the input ratio frac.
     *  The protocol here tries to update all peers regardless, however,
     *  as long as at least frac have been updated, the method returns
     *  successfully.
     *
     *  @param frac The float ratio of the number of peers that must be updated in order
     *              for this peer state update to be considered successful.
     *
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
                        //e.printStackTrace();
                    }
                }
            }
        }

    }

    // SLAVE => MASTER METHODS
    /**
     *  Ask this server to return the {@link GameState} as far as it knows.
     */
    public GameState getState() throws RemoteException {
        return this.game.getState();
    }

    /**
     *  Just a method to check that the server is still alive and responsive.
     *  If this returns true, the server is responsive. If it throws a RemoteException,
     *  we know the server is either deadlocked somehow or is otherwise down.
     */
    @Override
    public boolean pingServer() throws RemoteException {
        return true;
    }

    /**
     *  Asks the server for its {@link UUID}. Simply returns the instance variable uuid.
     */
    @Override
    public UUID getUUID() throws RemoteException {
        return this.uuid;
    }

    /**
     *  Ask the server for a {@link Hashtable} of the peer servers it knows about.
     *  Simply returns the instance variable peers.
     */
    @Override
    public Hashtable<UUID, GameServer> getPeers() throws RemoteException {
        return this.peers;
    }


    /**
     *  This class is used to create a tuple-like object of a server's {@link UUID} and
     *  a reference to the server itself.
     */
    private class IdServerPair {
        GameServer server;
        UUID id;

        /**
         *  Creates an instance of an {@link IdServerPair}. 
         *   
         *  @param id The {@link UUID} of the server
         *  @param server The {@link GameServer} that is being referred to by this pair
         */  
        public IdServerPair(UUID id, GameServer server) {
            this.id = id;
            this.server = server;
        }
    }

    /**
     *  If the master is up and running, return null to indicate that leader election
     *  does not need to happen. Otherwise, the server returns its own {@link UUID} and
     *  a reference to itself.
     */
    @Override
    public Object startLeaderElection() {
        System.out.println("Received request to start leader election");
        if (this.master != null && this.checkMaster())
            return null;

        IdServerPair pair = new IdServerPair(this.uuid, this);
        return pair;
    }

    /**
     *  When the master goes down, a new master must be decided and consistently agreed
     *  upon by the remaining servers. This is done by making sure that leader election
     *  is only done by the server with the smallest known {@link UUID} as well as
     *  making sure that peer is still alive. Then, the peer with the min {@link UUID}
     *  looks through all the servers and determines which one is the most up to date with
     *  the state of the game. This up-to-date peer is the new master. It then looks
     *  through all the known peers and compiles a list of all dead peers and tells the
     *  new master which peers are dead removes them from its peer list.
     */
    public boolean runLeaderElection() {
        // check if leader election should be aborted
        if (this.inLeaderElection) {
                // heartbeat to the elector - if it is still up, abandon leader election
                GameServer elector = this.peers.get(this.electorID);
                if (elector != null && this.checkServer(elector))
                    return false;
        }
        // only run if we are the minimum alive ID
        ArrayList<UUID> sortedIds = new ArrayList<UUID>(this.peers.keySet());
        Collections.sort(sortedIds);
        UUID minAlivePeerId = this.uuid;
        for (UUID id : sortedIds) {
            if (this.checkServer(this.peers.get(id)))
                minAlivePeerId = id;
        }

        this.inLeaderElection = true;
        electorID = minAlivePeerId;

        if (!minAlivePeerId.equals(this.uuid))
            return false;

        System.out.println("Running leader election");

        Hashtable<UUID, GameServer> activePeers = new Hashtable<UUID, GameServer>();
        long maxFrame = this.game.getState().getFrame();
        UUID maxUUID = this.uuid;

        for (UUID id : this.peers.keySet()) {
            activePeers.put(this.uuid, this);
            if (!(id.equals(this.uuid))) {
                GameServer peer = this.peers.get(id);
                try {
                    IdServerPair pair = (IdServerPair) (peer.startLeaderElection());
                    if (pair == null)
                        return false;
                    else {
                        long peerFrame = peer.getState().getFrame();
                        if (peerFrame > maxFrame) {
                                maxFrame = peerFrame;
                                maxUUID = id;
                        }
                        activePeers.put((UUID) pair.id, (GameServer) pair.server);
                    }
                }
                // skip unreachable peers
                catch (RemoteException e) {
                    continue;
                }
            }
        }

        // pick peer with max frame to be new master
        GameServer newMaster = activePeers.get(maxUUID);

        ArrayList<UUID> deadPeers = new ArrayList<UUID>();
        for (UUID id : this.peers.keySet()) {
            GameServer peer = this.peers.get(id);
            try {
                peer.closeLeaderElection(maxUUID, newMaster);
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
            // if master is still alive, it will remove dead peers later
        }

        return true;
    }

    /**
     *  In closing the leader election protocol, this server updates who it
     *  believes is the master and prints out the {@link UUID} of that new master.
     */
    public boolean closeLeaderElection(UUID id, GameServer newLeader) {
        System.out.format("The master is now %s.\n", id.toString());
        this.setMaster(newLeader);
        this.inLeaderElection = false;
        return true;
    }

    /**
     *  This method simply asks this server who it believes to be the current master. 
     */
    public GameServer getMaster() {
        return this.master;
    }

    /**
     *  This method is used to tell this server who the master is now. If this server
     *  is now the master, which would show by the {@link UUID}s being the same, 
     *  then print to stdout "I am the master" and update instance variable amMaster.
     *  Update instance variable master to know who the master is.
     *  If for some reason this method breaks halfway through, probably when getting
     *  the {@link UUID} of the master, then setting the master was not fully performed
     *  and the method returns false.
     */
    public boolean setMaster(GameServer newMaster) {
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

    /**
     *  Takes in a reference to a server s and pings it. If the ping causes an error,
     *  then it is probably down so return false. If the ping carries out fine, 
     *  it is probably up and running so return true.
     */
    public boolean checkServer(GameServer s) {
        try {
            s.pingServer();
            return true;
        } catch (RemoteException e) {
            return false;
        }

    }

    /**
     *  Ask if this server thinks it is the master. 
     */
    public boolean isMaster() {
        return this.amMaster;
    }

    /**
     *  Ask this server if it knows who the master is and check if that master is still up.
     *  If this server knows who the master is and that master is running, return true.
     *  Otherwise, this server forgets the server it might have known and return false.
     */
    public boolean checkMaster() {
        if (this.master != null && checkServer(this.master))
            return true;
        else {
            this.master = null;
            return false;
        }
    }

    /**
     *  Simply format this server's {@link UUID} as an informative string and return it.
     *  Return format: "GameClusterServer THIS_UUID".
     */
    @Override
    public String toString() {
            return String.format("GameClusterServer %s", this.uuid);
    }

}
