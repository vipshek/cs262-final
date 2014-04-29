package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.GameServer.GameClusterServer.GameClusterServer;
import edu.harvard.cs262.GameServer.GameServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.UUID;

public class VotingClusterServer {
  public static void main(String args[]) {
    try {
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
      }

      // check args
      if (args.length < 5) {
        System.out.println("Usage: VotingClusterServer remotehost remoteport localport serverName master?");
        System.exit(1);
      }

      VotingCommandProcessor processor = new VotingCommandProcessor();
      VotingGame game = new VotingGame(0);

      GameClusterServer mySrv = new GameClusterServer(processor, game);
      GameServer stub = (GameServer) UnicastRemoteObject.exportObject(mySrv, 0);

      GameServer master;

      // Connect to registry and find master server (getting name/host from args)
      String hostname = args[0];
      String name = args[3];
      Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(args[1]));
      Registry localRegistry = LocateRegistry.getRegistry(Integer.parseInt(args[2]));

      if (args[4].equals("true")) {
        localRegistry.rebind(name, stub);
        mySrv.setMaster(mySrv);
        mySrv.addPeer(mySrv.getUUID(), mySrv);
        System.out.format("Master ready (id: %s)\n", mySrv.getUUID().toString());
      } else {
        master = (GameServer) registry.lookup(name);
        mySrv.setMaster(master);

        // Register with the queueing server
        master.addPeer(mySrv.getUUID(), mySrv);
        System.out.format("Slave ready (id: %s)\n", mySrv.getUUID().toString());
      }

      // ping queue server
      Hashtable<UUID, GameServer> peers;
      // XXX need to check some field (leader election ongoing)
      while (true) {
        Thread.sleep(1000);
        if (mySrv.isMaster()) {
          localRegistry.rebind(name, stub);
          break;
        }
        try {
          master = mySrv.getMaster();
          peers = master.getPeers();
          mySrv.setPeers(peers);
//          System.out.format("Master (%s) still up (%d workers)\n", mySrv.getMaster().getUUID().toString(), peers.size());
        } catch (RemoteException e) {
          System.out.println("Master down");
          mySrv.runLeaderElection();
        }
      }

    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
    }
  }
}
