package edu.harvard.cs262.DistributedGame;

import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import edu.harvard.cs262.Exceptions.NotMasterException;
import java.rmi.RemoteException;

import java.util.Hashtable;
import java.util.UUID;
import java.lang.Thread;

public class GameRequestThread extends Thread {
	private UpdateableClient client;
	private GameServer server;

	public GameRequestThread(GameServer server, UpdateableClient client) {
		this.client = client;
		this.server = server;
	}

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
