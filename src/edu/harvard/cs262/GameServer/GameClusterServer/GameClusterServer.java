package edu.harvard.cs262.GameServer.GameClusterServer;

import java.rmi.RemoteException;
import java.lang.UnsupportedOperationException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.UUID;

import java.util.concurrent.locks.ReentrantLock;

import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.ClusterServer.BasicClusterServer.IdServerPair;

import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.Exceptions.NotMasterException;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameServer.ClusterGameServer;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

public class GameClusterServer implements ClusterGameServer {
	private static final long serialVersionUID = 1L;
	private GameCommandProcessor processor;
	private Game game;
	public UUID uuid;
    private ClusterServer master;
	private Hashtable<UUID, ClusterServer> workers;
    private ReentrantLock lock;
    private boolean amMaster;

	public GameClusterServer(GameCommandProcessor processor, Game game) {
		this.processor = processor;
		this.game = game;

		workers = new Hashtable<UUID, ClusterServer>();
        // create lock
        lock = new ReentrantLock();
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
		GameCommand decidedCommand=this.processor.getCommand();
		this.game.executeCommand(decidedCommand);
		return this.game.getSnapshot();
	} // also throws NotMasterException

	public GameSnapshot getSnapshot() throws RemoteException {
		return this.game.getSnapshot();
	}

	// MASTER => SLAVE METHODS
	public boolean sendState(GameState state) throws RemoteException {
		throw new UnsupportedOperationException();
	}
	public boolean sendDiff(GameDiff diff) throws RemoteException {
		throw new UnsupportedOperationException();
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
	public boolean getState() throws RemoteException {
		throw new UnsupportedOperationException();
	}
	public boolean getDiff(long start) throws RemoteException {
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
                deadPeers.add(id);
                continue;
            }
        }

        // remove deadPeers
        try {
            for (UUID deadID : deadPeers) {
                newMaster.unregisterWorker(deadID);
            }
        }
        catch (RemoteException e) {
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
