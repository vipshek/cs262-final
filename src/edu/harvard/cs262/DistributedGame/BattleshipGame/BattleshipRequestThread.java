package edu.harvard.cs262.DistributedGame.BattleshipGame;

import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;

import java.rmi.RemoteException;

import java.lang.Thread;

public class BattleshipRequestThread extends Thread {
    private UpdateableClient client;
    private GameServer server;

    public BattleshipRequestThread(GameServer server, UpdateableClient client) {
        this.client = client;
        this.server = server;
    }

    public void run() {
        while (true) {
            try {
                GameSnapshot snapshot = server.getSnapshot();
                client.updateDisplay(snapshot);
                Thread.sleep(250);
            } catch (InterruptedException e) {
                System.err.println("Request thread exception: " + e.toString());
            } catch (RemoteException e) {
                System.err.println("Request thread exception: " + e.toString());
            } catch (Exception e) {
                System.err.println("Request thread exception: " + e.toString());
            }
        }
    }
}
