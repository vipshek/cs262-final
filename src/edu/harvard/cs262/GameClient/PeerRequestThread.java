package edu.harvard.cs262.GameClient;

import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.Exceptions.NotMasterException;
import java.rmi.RemoteException;

import java.util.Hashtable;
import java.util.UUID;
import java.lang.Thread;

public class PeerRequestThread extends Thread {
    private GameClient client;
    private int timeout;

    public PeerRequestThread(GameClient client, int timeout) {
        this.client = client;
        this.timeout = timeout;
    }

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
