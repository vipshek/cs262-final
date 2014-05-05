package edu.harvard.cs262.GameServer.GameClusterServer;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameServer;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.UUID;

import java.lang.Thread;

/**
 * The LeaderElectThread is a thread that clients run that pings
 * the master for a new list of peers. This way, if new slaves join
 * the game, the client is able to access them.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class LeaderElectThread extends Thread {
    private GameServer server;
    private int timeout;
    private Registry localRegistry;
    private String name;
    private GameServer stub;

    public LeaderElectThread(GameServer server, int timeout, Registry localRegistry, String name, GameServer stub) {
        this.server = server;
        this.timeout = timeout;
        this.localRegistry = localRegistry;
        this.name = name;
        this.stub = stub;
    }

    /**
     * The function the thread runs to ping the server for
     * the updated list of peers.
     */
    public void run() {
        // pings master to make sure it's still up
        Hashtable<UUID, GameServer> peers;
        GameServer master;
        try {
            while (true) {
                try {
                    Thread.sleep(this.timeout);
                    if (this.server.isMaster()) {
                        //if we are the master, rebind to the registry
                        this.localRegistry.rebind(this.name, this.stub);
                        break;
                    }
                    //if we are not the master, update peers from current master
                    master = this.server.getMaster();
                    peers = master.getPeers();
                    this.server.setPeers(peers);
                } catch (RemoteException e) {
                    System.out.println("Master down");
                    this.server.runLeaderElection();
                } catch (InterruptedException e) {
                }
            }
        } catch (Exception e) {
            System.err.println("Leader election exception: " + e.toString());
        }
    }
}
