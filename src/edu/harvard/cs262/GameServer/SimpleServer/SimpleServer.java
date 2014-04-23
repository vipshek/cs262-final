package edu.harvard.cs262.GameServer.SimpleServer;

import java.rmi.RemoteException;
import java.lang.UnsupportedOperationException;

import java.util.List;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

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
		throw new UnsupportedOperationException();
	}
	public boolean sendDiff(GameDiff diff) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	public boolean addPeer(GameServer server) throws RemoteException {
		throw new UnsupportedOperationException();	
	}
	public boolean removePeer(GameServer server) throws RemoteException {
		throw new UnsupportedOperationException();
	}
	public boolean sendPeerList(List<GameServer> servers) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	// SLAVE => MASTER METHODS
	public boolean getState() throws RemoteException {
		throw new UnsupportedOperationException();
	}
	public boolean getDiff(long start) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	// GENERAL SERVER => SERVER METHODS
	public boolean sendHeartbeat() throws RemoteException {
		throw new UnsupportedOperationException();
	}
	public boolean register() throws RemoteException {
		throw new UnsupportedOperationException();
	} // also throws NotMasterException
	public boolean unregister() throws RemoteException {
		throw new UnsupportedOperationException();
	} // also throws NotMasterException
}