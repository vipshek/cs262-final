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
public class BasicServer {
    // Main - connect and register to Queue
	public static void main(String args[]){
		try{
			if (System.getSecurityManager()==null){
				System.setSecurityManager(new SecurityManager());
			}

            // check args
            if (args.length < 4) {
                System.out.println("Usage: BasicServer host port serverName master?");
                System.exit(1);
            }

			// Generate stub to bind to registry
			BasicClusterServer mySrv = new BasicClusterServer();
			ClusterServer stub = (ClusterServer)UnicastRemoteObject.exportObject(mySrv, 0);

            ClusterServer master;

			// Connect to registry and find master server (getting name/host from args)
			String hostname = args[0];
            String name = args[2];
			Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(args[1]));

            if (args[3].equals("true")) {
                registry.rebind(name, stub);
                mySrv.setMaster(mySrv);
                mySrv.registerWorker(mySrv);
                System.out.format("Master ready (id: %s)\n", mySrv.getUUID().toString());
            }
            else {
                master = (ClusterServer) registry.lookup(name);
                mySrv.setMaster(master);

                // Register with the queueing server
                master.registerWorker(mySrv);
                System.out.format("Slave ready (id: %s)\n", mySrv.getUUID().toString());
            }

            // ping queue server
            Hashtable<UUID, ClusterServer> workers;
            // XXX need to check some field (leader election ongoing)
            while (true) {
                Thread.sleep(1000);
                if (mySrv.isMaster())
                    break;
                try {
                    master = mySrv.getMaster();
                    workers = master.getWorkers();
                    mySrv.setWorkers(workers);
                    System.out.format("Master (%s) still up (%d workers)\n", mySrv.getMaster().getUUID().toString(), workers.size());
                }
                catch (RemoteException e) {
                    System.out.println("Master down");
                    mySrv.runLeaderElection();
                }
            }
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
            System.err.println(e.getMessage());
		}
	}
}

