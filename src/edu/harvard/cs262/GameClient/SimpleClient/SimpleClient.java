package edu.harvard.cs262.GameClient.SimpleClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.LinkedList;

import edu.harvard.cs262.GameClient.GameClient;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

public class SimpleClient implements GameClient {
	private GameDisplay display;
	private GameInputParser inputParser;
	private GameServer master;
	private LinkedList<GameServer> slaves;

	public SimpleClient(GameDisplay display, GameInputParser inputParser, GameServer master) {
		this.display = display;
		this.inputParser = inputParser;
		this.master = master;
		this.slaves = new LinkedList<GameServer>();
	}

	public void sendInput(String input) {
		GameCommand command = this.inputParser.parseInput(input);
		try {
			GameSnapshot snapshot = this.master.sendCommand(command);
		} catch (NotMasterException e) {
			this.master = e.getMaster();
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		this.display.render(snapshot);
	}

	public boolean addPeer(GameServer server) throws RemoteException {
		this.slaves.add(server);
	}

	public boolean removePeer(GameServer server) throws RemoteException {
		this.slaves.remove(server);
	}

	public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
		this.slaves = servers;
	}
}