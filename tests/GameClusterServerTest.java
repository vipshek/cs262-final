/**
 * 
 */
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameServer.GameClusterServer.LeaderElectThread;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
/**
 * @author rjaquino
 *
 */
public class GameClusterServerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
        try {
            // Connect to registry
            String hostname = "localhost";
            String name = "master";
            int port = 1099;

            Registry registry = LocateRegistry.getRegistry(hostname, port);
            Registry localRegistry = LocateRegistry.getRegistry(port);

            // set up master
            GameClusterServer masterServer = new GameClusterServer(null, null);
            GameServer stub = (GameServer) UnicastRemoteObject.exportObject(masterServer, 0);
            localRegistry.rebind(name, stub);
            masterServer.setMaster(masterServer);
            masterServer.addPeer(masterServer.getUUID(), masterServer);
            LeaderElectThread lt = new LeaderElectThread(masterServer, 1000, localRegistry, name, stub);
            lt.start();

            // set up two slaves
            ArrayList<GameClusterServer> slaves = new ArrayList<GameClusterServer>();
            GameServer master = (GameServer) registry.lookup(name);
            for (int i = 0; i < 2; i++) {
                GameClusterServer slaveServer = new GameClusterServer(null, null);
                GameServer slaveStub = (GameServer) UnicastRemoteObject.exportObject(slaveServer, 0);
                slaveServer.setMaster(masterServer);

                // Register with the master
                master.addPeer(slaveServer.getUUID(), slaveServer);
                slaves.add(slaveServer);

                // start leader election thread for slave
                LeaderElectThread slave_lt = new LeaderElectThread(slaveServer, 1000, localRegistry, name, slaveStub);
                slave_lt.start();
            }

            // wait
            Thread.sleep(2000);

            // kill master
            master.simulateCrash();

            // wait
            Thread.sleep(3000);

            // check that exactly one slave is now the master
            int numMasters = 0;
            for (int i = 0; i < 2; i++) {
                if (slaves.get(i).isMaster())
                    numMasters++;
            }
            assertEquals(1, numMasters);
        }
        catch (Exception e) {
            fail("Exception " + e.toString());
        }
	}

}
