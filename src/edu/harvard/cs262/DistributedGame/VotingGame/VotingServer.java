package edu.harvard.cs262.DistributedGame.VotingGame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.Console;

import edu.harvard.cs262.GameClient.SimpleServer.SimpleServer;

public class VotingServer {
    public static void main(String args[]){
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            // check args
            if (args.length < 2) {
                System.out.println("Usage: VotingServer host port");
                System.exit(1);
            }

            VotingCommandProcessor processor = new VotingCommandProcessor();
            VotingGame game = new VotingGame();

            SimpleServer server = new SimpleServer(processor, game);
            GameServer stub = (GameServer)UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            registry.rebind("master", stub);

            System.out.println("Master ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
    }
}
