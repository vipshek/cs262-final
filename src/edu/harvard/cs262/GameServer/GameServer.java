package edu.harvard.cs262.GameServer;

import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.Exceptions.NotMasterException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.UUID;

/**
 * A GameServer is a general interface for the methods the server must implement in
 * order to properly interact with the client and the game being played.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 * 
 */

public interface GameServer extends Remote {
    /**
     *
     * This method is used by a client to ask this server to execute a command in the game.
     *
     * @param command A {@link GameCommand} sent by the client for the server to execute
     *
     * @return A snapshot that describes what the game looks like now for the client to 
     *         see the effect of recent move(s)
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     * @throws NotMasterException when the command is sent to this server, and this is not
     *                            the master. Client then knows to find the master.
     */
    public GameSnapshot sendCommand(GameCommand command) throws RemoteException, NotMasterException;

    /**
     *
     * This method is called so that someone, usually the client, can get a surface-level
     * of the game for the user to view.
     *
     * @return A snapshot that describes what the game looks like now for the client to 
     *         see the effect of recent move(s)
     *
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     * @throws NotMasterException when the command is sent to this server, and this is not
     *         the master. Client then knows to find the master.
     */
    public GameSnapshot getSnapshot() throws RemoteException, NotMasterException;

    /**
     *
     * This method would be called to update this server's impression of the current game.
     *
     * @param state A {@link GameState} the server should update to. The server should forget
     *        its idea of the current game state and replace it with state.
     *
     * @return A boolean indicating if the state was properly updated.
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean setState(GameState state) throws RemoteException;

    /**
     *
     * When a new server enters the system, the server will be useless until other
     * peers know about it. This method is used to add the new peer to a list of
     * peers implementing the back end of the system.
     *
     * @param id The {@link UUID} of the new peer server being added to the system
     * @param server The {@link GameServer} that is to be added to the system
     *
     * @return A boolean indicating if the peer server was successfully added
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean addPeer(UUID id, GameServer server) throws RemoteException;

    /**
     *
     * When a peer goes down, the rest of the system needs to remove the peer
     * from their list of peers. This method is used to do so.
     *
     * @param id The {@link UUID} of the peer server that should be removed from the system
     *
     * @return A boolean indicating if the peer server was properly removed. 
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean removePeer(UUID id) throws RemoteException;

    /**
     *
     * For a new peer, or a thoroughly confused one, the complete list of peers
     * can be set with this method.
     *
     * @param peers A {@link Hashtable} of {@link UUID}s and {@link GameServer}s that are 
     *        currently working in the system
     *
     * @return A boolean indicating if the peer list was properly set on this server
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean setPeers(Hashtable<UUID, GameServer> peers) throws RemoteException;

    /**
     *
     * To pull this server's impression of the entire game, one can call this method 
     * and be returned an {@link GameState} of what this server thinks is the current
     * situation.
     *
     * @return A {@link GameState} that describes the entire state of the game as far as
     *         this server knows.
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public GameState getState() throws RemoteException;

    /**
     *
     * For others to make sure that a server is up and responsive, they can ping each other 
     * as a heart beat check.
     *
     * @return A boolean indicating if the server is still alive
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean pingServer() throws RemoteException;

    /**
     *
     * Hi, what's your name? this method allows servers to ask each other for 
     * their {@link UUID}s. 
     *
     * @return a {@link UUID} of the server 
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public UUID getUUID() throws RemoteException;

    /**
     *
     * Ask the server for its knowledge of the working peer servers.
     *
     * @return A {@link Hashtable} relating all the {@link UUID}s and {@link GameServer}
     *         objects of the peer servers in the system
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public Hashtable<UUID, GameServer> getPeers() throws RemoteException;

    /**
     *
     * Tells the server to start the leader election protocol because a past master
     * has gone down.
     *
     * @return A {@link UUID}-{@link GameServer} pair that is running the leader election
     *         protocol 
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public Object startLeaderElection() throws RemoteException;

    // XXX to comment
    public boolean runLeaderElection() throws RemoteException;


    /**
     *
     * The leader election protocol has completed. A new master has been chosen.
     * Long live the king. 
     *
     * @param id          The {@link UUID} of the new master server
     * @param newLeader   The {@link GameServer} that is the new master
     *
     * @return A boolean indicating that this server has updated its knowledge of who
     *         the master is
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean closeLeaderElection(UUID id, GameServer newLeader) throws RemoteException;

    /**
     *
     * Ask who the current master is, as far as this server knows.
     *
     * @return Reference to the server that is currently the master. 
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public GameServer getMaster() throws RemoteException;

    /**
     *
     * Ask this server if it is the master of the system.
     *
     * @return Boolean indicating whether or not this server is currently the master.
     * 
     * @throws RemoteException when a communication error occurs. Required by extending RMI.
     */
    public boolean isMaster() throws RemoteException;
}
