package edu.harvard.cs262.GameServer;

import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameDiff;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.Exceptions.NotMasterException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.UUID;

public interface GameServer extends Remote {
  // CLIENT => MASTER METHODS
  public GameSnapshot sendCommand(GameCommand command) throws RemoteException, NotMasterException;

  public GameSnapshot getSnapshot() throws RemoteException, NotMasterException;

  // MASTER => SLAVE METHODS
  public boolean setState(GameState state) throws RemoteException;

  public boolean addPeer(UUID id, GameServer server) throws RemoteException;

  public boolean removePeer(UUID id) throws RemoteException;

  public boolean setPeers(Hashtable<UUID, GameServer> peers) throws RemoteException;

  // SLAVE => MASTER METHODS
  public GameState getState() throws RemoteException;

  public GameDiff getDiff(GameState state) throws RemoteException;

  // GENERAL SERVER => SERVER METHODS
  public boolean pingServer() throws RemoteException;

  public UUID getUUID() throws RemoteException;

  public Hashtable<UUID, GameServer> getPeers() throws RemoteException;

  public Object startLeaderElection() throws RemoteException;

  public boolean closeLeaderElection(UUID id, GameServer newLeader) throws RemoteException;

  public GameServer getMaster() throws RemoteException;

  public boolean isMaster() throws RemoteException;
}
