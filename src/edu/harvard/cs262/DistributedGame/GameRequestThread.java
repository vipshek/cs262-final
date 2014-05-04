package edu.harvard.cs262.DistributedGame;

import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import edu.harvard.cs262.Exceptions.NotMasterException;
import java.rmi.RemoteException;

import java.lang.Thread;

/**
 * The GameRequestThread is a thread that asks the server every 250 ms
 * for the newest snapshot. This ensures that clients can watch the game and
 * will see updates to their games even if they do not input any commands.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class GameRequestThread extends Thread {
    private UpdateableClient client;
    private GameServer server;

    /**
     * Constructor for the GameRequestThread. Sets up the server to talk to 
     * and the current client.
     * @param  server  A {@link GameServer} that represents the current master
     * @param  client  An {@link UpdateableClient} that represents the 
     *         client that the thread is updating.
     * @return the constructed GameRequestThread
     */
    public GameRequestThread(GameServer server, UpdateableClient client) {
        this.client = client;
        this.server = server;
    }

    /**
     * This thread will ask the server every 250ms for the newest snapshot.
     * If there is a RemoteException or a NotMasterException, the thread will
     * attempt to find the new master and update its server variable 
     * accordingly.  If it cannot find a new master of there is an 
     * InterruptedException, there is nothing we can do, so the thread 
     * simply dies.
     */
    public void run() {
        while (true) {
            try {
                GameSnapshot snapshot = this.server.getSnapshot();
                client.updateDisplay(snapshot);
                Thread.sleep(250);
            } catch (InterruptedException e) {
                //System.err.println("Request thread exception: " + e.toString());
                return;
            } catch (RemoteException e) {
                GameServer newMaster = this.client.findNewMaster();
                if (newMaster != null)
                    this.server = newMaster;
                else
                    return;
            } catch (NotMasterException e) {
                GameServer newMaster = this.client.findNewMaster();
                if (newMaster != null)
                    this.server = newMaster;
                else
                    return;
            }
        }
    }
}
