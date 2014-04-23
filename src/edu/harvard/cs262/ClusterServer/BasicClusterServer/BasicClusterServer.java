package edu.harvard.cs262.ClusterServer.BasicClusterServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.UUID;

import java.util.concurrent.locks.ReentrantLock;

import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.ClusterServer.BasicClusterServer.IdServerPair;

public class BasicClusterServer implements ClusterServer {
	private static final long serialVersionUID = 1L;
	public UUID uuid;
    private ClusterServer master;
	private Hashtable<UUID, ClusterServer> workers;
    private ReentrantLock lock;
    private boolean amMaster;

	public BasicClusterServer(){
		super();
		workers = new Hashtable<UUID, ClusterServer>();
        // create lock
        lock = new ReentrantLock();
        master = null;
        amMaster = false;
		uuid = UUID.randomUUID();

	}

    @Override
	public UUID getUUID() throws RemoteException {
		return this.uuid;
    }

    @Override
	public Hashtable<UUID, ClusterServer> getWorkers() throws RemoteException {
		return workers;
    }
	@Override
	public boolean registerWorker(ClusterServer server) throws RemoteException {
        UUID key = server.getUUID();
        // lock cvar
        lock.lock();

        // add worker to free workers and allworkers lists
		workers.put(key, server);

        lock.unlock();

        System.out.format("Registered Worker %s\n", key.toString());
        System.out.flush();

		return true;
	}

    private boolean removeWorker(UUID workerID) {
        // if this is not a current worker, return
		if (null == workers.get(workerID)){
			return true;
		}

        // lock cvar and remove worker from all lists
        lock.lock();
		workers.remove(workerID);
        lock.unlock();

		return true;
    }
	@Override
	public boolean unregisterWorker(UUID workerID) throws RemoteException{
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

        if (!minAliveWorkerId.equals(this.uuid))
            return false;

        System.out.println("Running leader election");
        Hashtable<UUID, ClusterServer> activePeers = new Hashtable<UUID, ClusterServer>();
        for (UUID id : this.workers.keySet()) {
            activePeers.put(this.uuid, this);
            if (!(id.equals(this.uuid))) {
                ClusterServer peer = this.workers.get(id);
                try {
                    IdServerPair pair = (IdServerPair)(peer.StartLeaderElection());
                    if (pair == null)
                        return false;
                    else {
                        activePeers.put((UUID)pair.id, (ClusterServer)pair.server);
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
            }
            catch (RemoteException e) {
                continue;
            }
        }

        /*
        // remove deadPeers
        for (UUID deadID : deadPeers) {
            this.removeWorker(deadID);
        }
        try {
            this.master.setWorkers(this.workers);
        }
        catch (RemoteException e) {
            // XXX handle;
        }
        */

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
            }
            else
                this.amMaster = false;

            this.master = newMaster;
            return true;
        }
        catch (RemoteException e) {
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
        }
        catch (RemoteException e) {
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

