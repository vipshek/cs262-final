package edu.harvard.cs262.ClusterServer.BasicClusterServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Hashtable;
import java.util.UUID;

import java.util.concurrent.locks.ReentrantLock;

import edu.harvard.cs262.ClusterServer.BasicClusterServer.BasicClusterServer;
import edu.harvard.cs262.ClusterServer.ClusterServer;
public class BasicSlave {
    // Main - connect and register to Queue
	public static void main(String args[]){
		try{
			if (System.getSecurityManager()==null){
				System.setSecurityManager(new SecurityManager());
			}

            // check args
            if (args.length < 3) {
                System.out.println("Usage: BasicSlave host port serverName");
                System.exit(1);
            }

			// Generate stub to bind to registry
			BasicClusterServer mySrv = new BasicClusterServer();
			ClusterServer stub = (ClusterServer)UnicastRemoteObject.exportObject(mySrv, 0);

			// Connect to registry and find master server (getting name/host from args)
			String hostname = args[0];
            String name = args[2];
			Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(args[1]));
			ClusterServer master = (ClusterServer) registry.lookup(name);
            mySrv.setMaster(master);

			// Register with the queueing server
			mySrv.uuid = master.registerWorker(mySrv);

			System.out.format("Slave ready (id: %s)\n", mySrv.uuid.toString());
            System.out.flush();

            // ping queue server
            Hashtable<UUID, ClusterServer> workers;
            while (true) {
                Thread.sleep(1000);
                try {
                    master = mySrv.getMaster();
                    workers = master.getWorkers();
                    mySrv.setWorkers(workers);
                    System.out.format("Master still up (%d workers)\n", workers.size());
                }
                catch (RemoteException e) {
                    System.out.println("Master down");
                    mySrv.runLeaderElection();
                }
            }
		} catch (Exception e) {
			System.err.println("Slave exception: " + e.toString());
            System.err.println(e.getMessage());
		}
	}
}

