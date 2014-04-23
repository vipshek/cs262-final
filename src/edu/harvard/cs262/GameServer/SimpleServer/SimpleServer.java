package edu.harvard.cs262.GameServer.SimpleServer;

import java.rmi.RemoteException;
import java.lang.UnsupportedOperationException;

import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

public class SimpleServer implements GameServer {
	private GameCommandProcessor processor;
	private Game game;

	public SimpleServer(GameCommandProcessor processor, Game game) {
		this.processor = processor;
		this.game = game;
	}

	public GameSnapshot sendCommand(GameCommand command) throws RemoteException {
		this.processor.startProcessor();
		this.processor.addCommand(command);
		GameCommand decidedCommand=this.processor.getCommand();
		this.game.executeCommand(decidedCommand);
		return this.game.getSnapshot();
	} // also throws NotMasterException

	public GameSnapshot getSnapshot() throws RemoteException {
		return this.game.getSnapshot();
	}

	// MASTER => SLAVE METHODS
	public boolean sendState(GameState state) throws RemoteException {
		throw UnsupportedOperationException();
	}
	public boolean sendDiff(GameDiff diff) throws RemoteException {
		throw UnsupportedOperationException();
	}

	public boolean addPeer(GameServer server) throws RemoteException {
		throw UnsupportedOperationException();	
	}
	public boolean removePeer(GameServer server) throws RemoteException {
		throw UnsupportedOperationException();
	}
	public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
		throw UnsupportedOperationException();
	}

	// SLAVE => MASTER METHODS
	public boolean getState() throws RemoteException {
		throw UnsupportedOperationException();
	}
	public boolean getDiff(long start) throws RemoteException {
		throw UnsupportedOperationException();
	}

	// GENERAL SERVER => SERVER METHODS
	public boolean sendHeartbeat() throws RemoteException {
		throw UnsupportedOperationException();
	}
	public boolean register() throws RemoteException {
		throw UnsupportedOperationException();
	} // also throws NotMasterException
	public boolean unregister() throws RemoteException {
		throw UnsupportedOperationException();
	} // also throws NotMasterException
}