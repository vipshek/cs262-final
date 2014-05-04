package edu.harvard.cs262.GameClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.harvard.cs262.GameServer.GameServer;

import java.util.Hashtable;
import java.util.UUID;

public interface GameClient extends Remote {
    public boolean addPeer(UUID id, GameServer server) throws RemoteException;

    public boolean removePeer(UUID id) throws RemoteException;

    public boolean setPeers(Hashtable<UUID, GameServer> servers) throws RemoteException;

    public boolean getUpdatedPeers() throws RemoteException;
}