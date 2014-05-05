package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameClusterServer.LeaderElectThread;
import edu.harvard.cs262.GameServer.GameServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.UUID;

/**
 * The VotingClusterServer class contains the code that the servers
 * run in the Voting Game. The servers attempt leader election, pinging the master
 * to see if it is still up.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingClusterServer {
    /**
     * The main function that servers execute. Connects the clients
     * to the registry and shows them the game screen and allows them
     * to vote up or down on the number shown.
     * 
     * @param args  An array of strings given at the command line {
     * 	    0  The hostname/IP address of the remote RMI registry (if a slave, localhost otherwise)
     * 	    1  The port for the remote RMI registry
     * 	    2  The port for the local RMI registry (for rebinding when a slave becomes the master)
     * 	    3  The name for the master server - will be the same across all registries
     * 	    4  boolean - whether this server should start as a master or slave
     * }
     */
    public static void main(String args[]) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            // check args
            if (args.length < 5) {
                System.out.println("Usage: VotingClusterServer remotehost remoteport localport serverName master?");
                System.exit(1);
            }

            VotingCommandProcessor processor = new VotingCommandProcessor();
            VotingGame game = new VotingGame(0);

            GameClusterServer mySrv = new GameClusterServer(processor, game);
            GameServer stub = (GameServer) UnicastRemoteObject.exportObject(mySrv, 0);

            GameServer master;

            // Connect to registry and find master server (getting name/host from args)
            String hostname = args[0];
            String name = args[3];
            Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(args[1]));
            Registry localRegistry = LocateRegistry.getRegistry(Integer.parseInt(args[2]));

            if (args[4].equals("true")) {
                localRegistry.rebind(name, stub);
                mySrv.setMaster(mySrv);
                mySrv.addPeer(mySrv.getUUID(), mySrv);
                System.out.format("Master ready (id: %s)\n", mySrv.getUUID().toString());
            } else {
                master = (GameServer) registry.lookup(name);
                mySrv.setMaster(master);

                // Register with the master
                master.addPeer(mySrv.getUUID(), mySrv);
                System.out.format("Slave ready (id: %s)\n", mySrv.getUUID().toString());
            }

            // pings master to make sure it's still up
            LeaderElectThread lt = new LeaderElectThread(mySrv, 1000, localRegistry, name, stub);
            lt.start();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
    }
}
