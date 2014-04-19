package edu.harvard.cs262.GameServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameServer extends Remote {
	// CLIENT => MASTER METHODS
	public GameSnapshot sendCommand(GameCommand command) throws RemoteException; // also throws NotMasterException
	public GameSnapshot getSnapshot() throws RemoteException;

	// MASTER => SLAVE METHODS
	public boolean sendState(GameState state) throws RemoteException;
	public boolean sendDiff(GameDiff diff) throws RemoteException;

	public boolean addPeer(GameServer server) throws RemoteException;
	public boolean removePeer(GameServer server) throws RemoteException;
	public boolean sendPeerList(List<GameServer> servers) throws RemoteException;

	// SLAVE => MASTER METHODS
	public boolean getState() throws RemoteException;
	public boolean getDiff(long start) throws RemoteException;

	// GENERAL SERVER => SERVER METHODS
	public boolean sendHeartbeat() throws RemoteException;
	public boolean register() throws RemoteException; // also throws NotMasterException
	public boolean unregister() throws RemoteException; // also throws NotMasterException
}