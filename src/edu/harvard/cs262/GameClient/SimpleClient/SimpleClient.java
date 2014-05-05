package edu.harvard.cs262.GameClient.SimpleClient;

import java.rmi.RemoteException;

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

/**
 * Basic client that, given correctly implemented dependencies (display and 
 * inputParser) runs a game by allowing an executable class to instantiate
 * this one and send inputs to the master by calling the sendInput() method.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class SimpleClient implements GameClient {
    public GameDisplay display;
    private GameInputParser inputParser;
    /**
     * This client's view of which server is currently the master.
     */
    protected GameServer master;
    /**
     * List of slaves to contact in case the master server goes down.
     */
    private Hashtable<UUID, GameServer> slaves;
    private PeerRequestThread thread;

    /**
     * Initialize instance variables and forks the PeerRequestThread.
     */
    public SimpleClient(GameDisplay display, GameInputParser inputParser, GameServer master) {
        this.display = display;
        this.inputParser = inputParser;
        this.master = master;
        this.slaves = new Hashtable<UUID, GameServer>();
        this.thread = new PeerRequestThread(this, 250);
        this.thread.start();
    }

    /**
     * Causes this client to begin searching for a new master server.
     * @return The new master server.
     */
    public GameServer findNewMaster() {
        // Make sure the current master is actually down
        try {
            this.master.getSnapshot();
            return this.master;
        } 
        // If the server is up but no longer is master, just update variable
        catch (NotMasterException e) {
            this.setMaster(e.getMaster());
            System.out.format("Changing Master\n");
            return e.getMaster();
        } 
        // If the server is actually down...
        catch (RemoteException e) {
            boolean foundNewMaster = false;
            // Keep pinging until the new master is found
            while (!foundNewMaster) {
                // Ping every slave server
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
                } catch (InterruptedException ie) {
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Take an input string, pass it to the input parser, and send the
     * resulting command to the master server.
     *
     * @param input A string properly formatted for this game's input
     * parser to return a relevant command.
     */
    public void sendInput(String input) {
        // Parse input string
        GameCommand command = this.inputParser.parseInput(input);
        try {
            // Send command to master server
            System.out.format("Sending command to %s\n", this.master.getUUID());
            GameSnapshot snapshot = this.master.sendCommand(command);
            // Render resulting game snapshot
            this.display.render(snapshot);
        } catch (NotMasterException e) {
            // Update master if the previous server is no longer the master
            GameServer newMaster = this.findNewMaster();
            if (newMaster != null)
                this.sendInput(input);
            else
                return;
        } catch (RemoteException e) {
            // Find new master server if the current one is down
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

    /**
     * Ping input server to make sure that it is still up.
     * @param s A GameServer to ping.
     * @return boolean True if server is up, false otherwise.
     */
    public boolean checkServer(GameServer s) {
        try {
            s.pingServer();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Ping input server to test that it is the master.
     * @param s A GameServer to ping.
     * @return boolean True if server is up, false otherwise.
     */
    public boolean isMaster(GameServer s) {
        try {
            boolean master = s.isMaster();
            return master;
        } catch (RemoteException e) {
            return false;
        }

    }

    /**
     * Adds a new slave server to the list of slaves.
     * 
     * @param  id  The UUID of the new server to be added
     * @param  server  The {@link GameServer} object to be added to the slaves
     * @return a boolean that is true if the new slave is correctly added
     * @throws RemoteException when a communication error occurs. Required by extending RMI
     */
    public boolean addPeer(UUID id, GameServer server) throws RemoteException {
        this.slaves.put(id, server);
        return true;
    }

    /**
     * Removes a slave server from the list of slaves
     * 
     * @param  id  The UUID of the server to be removed
     * @return a boolean that is true if the slave is correctly removed
     * @throws RemoteException when a communication error occurs. Required by extending RMI
     */
    public boolean removePeer(UUID id) throws RemoteException {
        this.slaves.remove(id);
        return true;
    }

    /**
     * Sets the list of slaves
     * 
     * @param  servers  A Hashtable that matches UUID to {@link GameServer} objects
     *         that represents the slave servers
     * @return a boolean that is true if the slave list is correctly set to the
     *         passed-in list of slaves
     * @throws RemoteException when a communication error occurs. Required by extending RMI
     */
    public boolean setPeers(Hashtable<UUID, GameServer> servers) throws RemoteException {
        this.slaves = servers;
        return true;
    }

    /**
     * Gets the list of slaves
     * 
     * @return A Hashtable of UUIDs to {@link GameServer} objects that represents
     *         the slave servers
     */
    public Hashtable<UUID, GameServer> getSlaves() {
        return this.slaves;
    }

    /**
     * Gets the updated list of slaves from the master and sets
     * the slave list to the list returned from the master
     * 
     * @return a boolean that is true if the slave list is correctly set
     *         and false if there is an RMI error
     * @throws RemoteException when a communication error occurs. Required by extending RMI
     */
    public boolean getUpdatedPeers() throws RemoteException {
        try {
            this.slaves = master.getPeers();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
