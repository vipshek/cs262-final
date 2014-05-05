import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalFacade;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameServer.GameClusterServer.LeaderElectThread;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingGame;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingCommand;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingCommandProcessor;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingInputParser;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingState;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingDisplay;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Integration tests to test communication between clients and servers
 * and the master and its slaves.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class IntegrationTest {

    private ArrayList<GameClusterServer> slaves;
    private GameServer master;
    private UpdateableClient client;
    private String name;
    private Registry localRegistry;
    private VotingGame game;
    private VotingCommandProcessor processor;

	/**
	 * Connects to the registry and sets up a master, two slaves, and client
     *
     * @throws java.lang.Exception
	 */
	@Before
    public void setUp() throws Exception {
        // create game (no processor needed)
        this.game = new VotingGame(0);
        this.processor = new VotingCommandProcessor();

        // Connect to registry
        String hostname = "localhost";
        this.name = "master";
        int port = 1099;

        Registry registry = LocateRegistry.getRegistry(hostname, port);
        this.localRegistry = LocateRegistry.getRegistry(port);

        // set up master
        GameClusterServer masterServer = new GameClusterServer(processor, game);
        GameServer stub = (GameServer) UnicastRemoteObject.exportObject(masterServer, 0);
        localRegistry.rebind(name, stub);
        masterServer.setMaster(masterServer);
        masterServer.addPeer(masterServer.getUUID(), masterServer);
        LeaderElectThread lt = new LeaderElectThread(masterServer, 1000, localRegistry, name, stub);
        lt.start();

        // set up two slaves
        slaves =  new ArrayList<GameClusterServer>();
        master = (GameServer) registry.lookup(name);
        for (int i = 0; i < 2; i++) {
            GameClusterServer slaveServer = new GameClusterServer(processor, game);
            GameServer slaveStub = (GameServer) UnicastRemoteObject.exportObject(slaveServer, 0);
            slaveServer.setMaster(masterServer);

            // Register with the master
            master.addPeer(slaveServer.getUUID(), slaveServer);
            slaves.add(slaveServer);

            // start leader election thread for slave
            LeaderElectThread slave_lt = new LeaderElectThread(slaveServer, 1000, localRegistry, name, slaveStub);
            slave_lt.start();
        }

        // set up client
        VotingInputParser parser = new VotingInputParser();
        Screen screen = TerminalFacade.createScreen();
        VotingDisplay display = new VotingDisplay(screen);
        this.client = new UpdateableClient(display, parser, master);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests leader election by simulating a crash on the master
     * and then ensuring that only one of the two slaves is now 
     * a master.
     */
	@Test
	public void leaderElectionIntegration() {
        try {
            // wait
            Thread.sleep(2000);

            // kill master
            master.simulateCrash();

            // wait
            Thread.sleep(3000);

            // check that exactly one slave is now the master
            int numMasters = 0;
            for (int i = 0; i < 2; i++) {
                if (this.slaves.get(i).isMaster())
                    numMasters++;
            }
            assertEquals(1, numMasters);
        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Tests that replication works by setting the state of the master
     * and then checking after 3 seconds if all the slaves updated their
     * states to the master's state.
     */
    @Test
    public void replicationIntegration() {
        try {
            // change voting game state to 5
            VotingState newState = new VotingState(5, 1);
            master.setState(newState);

            // wait
            Thread.sleep(3000);

            // check slaves
            for (int i = 0; i < 2; i++) {
                VotingState slaveState = (VotingState) this.slaves.get(i).getState();
                assertEquals(5, slaveState.getValue());
            }

        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Tests sending input from the clients to the server.
     * Has the client send a random number of up commands and then
     * ensures that the master's state reflects the number of 
     * commands that was sent.
     */
    @Test
    public void sendInputIntegration() {
        try {
            Random r = new Random();
            int expected = 0;
            for (int i = 0; i < 10; i++) {
                if (r.nextInt(2) == 0) {
                    client.sendInput("UP");
                    expected++;
                }
            }

            // wait
            Thread.sleep(1000);

            VotingState state = (VotingState)master.getState();
            assertEquals(expected, state.getValue());
        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Tests that slaves added in the middle of the game are properly integrated.
     * Crashes the original master and adds a new slave to the new master. Kills all
     * of the other original servers and then ensures that commands can still be sent
     * from the client to the new master, which is the slave that was added.
     */
    @Test
    public void newSlaveIntegration() {
        try {
            // wait
            Thread.sleep(2000);

            // kill master
            master.simulateCrash();

            // wait
            Thread.sleep(2000);

            // find new master
            GameServer newMaster = slaves.get(0).getMaster();

            // add new slave (to new master on localhost)
            GameClusterServer slaveServer = new GameClusterServer(processor, game);
            GameServer slaveStub = (GameServer) UnicastRemoteObject.exportObject(slaveServer, 0);
            slaveServer.setMaster(newMaster);

            // Register with the master
            newMaster.addPeer(slaveServer.getUUID(), slaveServer);

            // start leader election thread for slave
            LeaderElectThread slave_lt = new LeaderElectThread(slaveServer, 1000, this.localRegistry, this.name, slaveStub);
            slave_lt.start();

            // wait
            Thread.sleep(2000);

            // kill old slaves
            for (int i = 0; i < 2; i++)
                slaves.get(i).simulateCrash();

            // wait
            Thread.sleep(2000);

            // send commands
            Random r = new Random();
            int expected = 0;
            for (int i = 0; i < 10; i++) {
                if (r.nextInt(2) == 0) {
                    client.sendInput("UP");
                    expected++;
                }
            }

            VotingState state = (VotingState)slaveServer.getState();
            assertEquals(expected, state.getValue());

        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

}
