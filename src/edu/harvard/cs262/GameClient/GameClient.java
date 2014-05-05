package edu.harvard.cs262.GameClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.harvard.cs262.GameServer.GameServer;

import java.util.Hashtable;
import java.util.UUID;

/**
 * A GameClient is a general interface allowing a client to receive messages
 * from the server indicating which other slave servers exist in the
 * distributed system.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public interface GameClient extends Remote {
	/**
	 * Add a new slave to the list of servers that this client can message.
	 */
    public boolean addPeer(UUID id, GameServer server) throws RemoteException;

    /**
     * Remove a slave from client's list of servers.
     */
    public boolean removePeer(UUID id) throws RemoteException;

    /**
     * Overwrite this client's list of slaves with an entirely new one.
     */
    public boolean setPeers(Hashtable<UUID, GameServer> servers) throws RemoteException;

    /**
     * Command this client to refresh its peer list by contacting the master.
     */
    public boolean getUpdatedPeers() throws RemoteException;
}