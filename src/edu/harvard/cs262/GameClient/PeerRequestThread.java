package edu.harvard.cs262.GameClient;

import java.rmi.RemoteException;

import java.lang.Thread;

/**
 * The PeerRequestThread is a thread that clients run that pings
 * the master for a new list of peers. This way, if new slaves join
 * the game, the client is able to access them.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class PeerRequestThread extends Thread {
    private GameClient client;
    private int timeout;

    /**
     * The constructor for the PeerRequestThread.  Sets up the
     * thread's client and timeout variables.
     * 
     * @param  client  A {@link GameClient} that represents which
     *         client is running the thread
     *         
     * @param  timeout  An int that represents how long the thread
     *         should wait before pinging the master again
     */
    public PeerRequestThread(GameClient client, int timeout) {
        this.client = client;
        this.timeout = timeout;
    }

    /**
     * The function the thread runs to ping the server for
     * the updated list of peers.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(this.timeout);
                this.client.getUpdatedPeers();
            } catch (InterruptedException e) {
            } catch (RemoteException e) {
            }
        }
    }
}
