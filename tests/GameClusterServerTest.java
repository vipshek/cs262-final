import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameServer.GameClusterServer.LeaderElectThread;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingGame;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingCommand;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingState;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
/**
 * Tests the GameClusterServer
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class GameClusterServerTest {

    private ArrayList<GameClusterServer> slaves;
    private GameServer master;
	/**
	 * Connects to the registry and sets up a master, two slaves, and client
     * 
     * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
        // create game (no processor needed)
        VotingGame game = new VotingGame(0);

        // Connect to registry
        String hostname = "localhost";
        String name = "master";
        int port = 1099;

        Registry registry = LocateRegistry.getRegistry(hostname, port);
        Registry localRegistry = LocateRegistry.getRegistry(port);

        // set up master
        GameClusterServer masterServer = new GameClusterServer(null, game);
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
            GameClusterServer slaveServer = new GameClusterServer(null, game);
            GameServer slaveStub = (GameServer) UnicastRemoteObject.exportObject(slaveServer, 0);
            slaveServer.setMaster(masterServer);

            // Register with the master
            master.addPeer(slaveServer.getUUID(), slaveServer);
            slaves.add(slaveServer);

            // start leader election thread for slave
            LeaderElectThread slave_lt = new LeaderElectThread(slaveServer, 1000, localRegistry, name, slaveStub);
            slave_lt.start();
        }

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

}
