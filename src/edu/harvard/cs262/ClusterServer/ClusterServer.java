package edu.harvard.cs262.ClusterServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.UUID;

public interface ClusterServer extends Remote {
  public boolean PingServer() throws RemoteException;

  public UUID getUUID() throws RemoteException;

  public boolean registerWorker(ClusterServer server) throws RemoteException;

  public boolean unregisterWorker(UUID workerID) throws RemoteException;

  public Hashtable<UUID, ClusterServer> getWorkers() throws RemoteException;

  public boolean setWorkers(Hashtable<UUID, ClusterServer> workers) throws RemoteException;

  public Object StartLeaderElection() throws RemoteException;

  public boolean CloseLeaderElection(UUID id, ClusterServer newLeader) throws RemoteException;

  public ClusterServer getMaster() throws RemoteException;

  public boolean isMaster() throws RemoteException;
}
