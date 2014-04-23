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
public class BasicMaster {
    // start up a queue server
	public static void main(String args[]){
		try{
			if (System.getSecurityManager()==null){
				System.setSecurityManager(new SecurityManager());
			}

            // check args
            if (args.length < 3) {
                System.out.println("Usage: BasicMaster serverName host port");
                System.exit(1);
            }

            // create Queue
			BasicClusterServer mySrv = new BasicClusterServer();
			ClusterServer stub = (ClusterServer)UnicastRemoteObject.exportObject(mySrv, 0);

            // bind Queue using name in args
			Registry registry = LocateRegistry.getRegistry(args[1], Integer.parseInt(args[2]));
			registry.rebind(args[0], stub);

			System.out.println("Master ready");
		} catch (Exception e) {
			System.err.println("Master exception: " + e.toString());
		}
	}
}
