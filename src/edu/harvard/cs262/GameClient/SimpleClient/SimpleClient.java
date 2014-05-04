package edu.harvard.cs262.GameClient.SimpleClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.UUID;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.GameClient;
import edu.harvard.cs262.GameClient.PeerRequestThread;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameSnapshot;
import edu.harvard.cs262.Exceptions.NotMasterException;

public class SimpleClient implements GameClient {
    public GameDisplay display;
    private GameInputParser inputParser;
    protected GameServer master;
    private Hashtable<UUID, GameServer> slaves;
    private PeerRequestThread thread;

    public SimpleClient(GameDisplay display, GameInputParser inputParser, GameServer master) {
        this.display = display;
        this.inputParser = inputParser;
        this.master = master;
        this.slaves = new Hashtable<UUID, GameServer>();
        this.thread = new PeerRequestThread(this, 250);
        this.thread.start();
    }

    public GameServer findNewMaster() {
        try {
            this.master.getSnapshot();
            return this.master;
        } catch (NotMasterException e) {
            this.setMaster(e.getMaster());
            System.out.format("Changing Master\n");
            return e.getMaster();
        } catch (RemoteException e) {
            boolean foundNewMaster = false;
            while (!foundNewMaster) {
                    for (UUID id : this.slaves.keySet()) {
                        System.out.format("Trying %s\n", id);
                        GameServer w = this.slaves.get(id);
                        if (this.checkServer(w) && this.isMaster(w)) {
                            System.out.format("Changing Master to %s\n", id);
                            this.setMaster(w);
                            return w;
                        }
                    }
                    try {
                    Thread.sleep(1000);
                    }
                    catch (InterruptedException ie) {
                    }
            }
            return null;
        } catch (Exception e) {
                return null;
        }
    }

    public void sendInput(String input) {
        GameCommand command = this.inputParser.parseInput(input);
        try {
            System.out.format("Sending command to %s\n", this.master.getUUID());
            GameSnapshot snapshot = this.master.sendCommand(command);
            this.display.render(snapshot);
        } catch (NotMasterException e) {
            GameServer newMaster = this.findNewMaster();
            if (newMaster != null)
                    this.sendInput(input);
            else
                    return;
        } catch (RemoteException e) {
                    GameServer newMaster = this.findNewMaster();
                    if (newMaster != null)
                            this.sendInput(input);
                    else
                            return;
        } catch (Exception e) {
        }
    }

    public void setMaster(GameServer server) {
            this.master = server;
    }

    public boolean checkServer(GameServer s) {
        try {
            s.pingServer();
            return true;
        } catch (RemoteException e) {
            return false;
        }

    }
    public boolean isMaster(GameServer s) {
        try {
            boolean master = s.isMaster();
            return master;
        } catch (RemoteException e) {
            return false;
        }

    }

    public boolean addPeer(UUID id, GameServer server) throws RemoteException {
        this.slaves.put(id, server);
        return true;
    }

    public boolean removePeer(UUID id) throws RemoteException {
        this.slaves.remove(id);
        return true;
    }

    public boolean setPeers(Hashtable<UUID, GameServer> servers) throws RemoteException {
        this.slaves = servers;
        return true;
    }

    public Hashtable<UUID, GameServer> getSlaves() {
            return this.slaves;
    }

    public boolean getUpdatedPeers() throws RemoteException {
        try {
            this.slaves = master.getPeers();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
