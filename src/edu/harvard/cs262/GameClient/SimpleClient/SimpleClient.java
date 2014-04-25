package edu.harvard.cs262.GameClient.SimpleClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.GameClient;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.Exceptions.NotMasterException;

public class SimpleClient implements GameClient {
	protected GameDisplay display;
	protected GameInputParser inputParser;
	protected GameServer master;
	protected LinkedList<GameServer> slaves;

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
			this.display.render(snapshot);
		} catch (NotMasterException e) {
			this.master = e.getMaster();
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean addPeer(GameServer server) throws RemoteException {
		this.slaves.add(server);
		return true;
	}

	public boolean removePeer(GameServer server) throws RemoteException {
		this.slaves.remove(server);
		return true;
	}

	public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
		this.slaves = (LinkedList) servers;
		return true;
	}
}