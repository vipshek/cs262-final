package edu.harvard.cs262.GameClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.harvard.cs262.GameServer.GameServer;

import java.util.List;

public interface GameClient extends Remote {
  public boolean addPeer(GameServer server) throws RemoteException;

  public boolean removePeer(GameServer server) throws RemoteException;

  public boolean sendPeerList(List<GameServer> servers) throws RemoteException;
}