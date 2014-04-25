package edu.harvard.cs262.GameClient.SimpleClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.UUID;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameServer.ClusterGameServer;
import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.GameClient.GameClient;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.Exceptions.NotMasterException;

public class SimpleClient implements GameClient {
	private GameDisplay display;
	private GameInputParser inputParser;
	private GameServer master;
	private Hashtable<UUID, ClusterServer> slaves;

	public SimpleClient(GameDisplay display, GameInputParser inputParser, ClusterGameServer master) {
		this.display = display;
		this.inputParser = inputParser;
		this.master = master;
		//this.slaves = new LinkedList<GameServer>();
        // XXX temporary
        try {
            this.slaves = master.getWorkers();
        }
        catch (RemoteException e) {
		    this.slaves = new Hashtable<UUID, ClusterServer>();

        }
	}

	public void sendInput(String input) {
		GameCommand command = this.inputParser.parseInput(input);
		try {
			GameSnapshot snapshot = this.master.sendCommand(command);
			this.display.render(snapshot);
		} catch (NotMasterException e) {
			this.master = (ClusterGameServer)e.getMaster();

            // XXX retry for now - possibly an infinite loop?
            this.sendInput(input);
		} catch (RemoteException e) {
			//System.out.println(e.getMessage());
            for (UUID id : this.slaves.keySet()) {
              System.out.format("Trying %s\n", id);
              ClusterGameServer w = (ClusterGameServer)this.slaves.get(id);
              // XXX should ask if its the master?
              if (this.checkServer(w)) {
                  System.out.format("Changing master to %s\n", id);
                  this.master = w;
                    // XXX retry for now - possibly an infinite loop?
                    this.sendInput(input);
                    break;
              }
            }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

    public boolean checkServer(ClusterServer s) {
        try {
            s.PingServer();
            return true;
        }
        catch (RemoteException e) {
            return false;
        }

    }

	public boolean addPeer(GameServer server) throws RemoteException {
		//this.slaves.add(server);
		return true;
	}

	public boolean removePeer(GameServer server) throws RemoteException {
		//this.slaves.remove(server);
		return true;
	}

	public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
		//this.slaves = new LinkedList(servers);
		return true;
	}
}
