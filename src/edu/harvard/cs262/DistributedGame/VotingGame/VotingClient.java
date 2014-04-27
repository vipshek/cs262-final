package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.GameClient.SimpleClient.SimpleClient;
import edu.harvard.cs262.GameServer.GameServer;

import java.io.Console;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VotingClient {
  public static void main(String args[]) {
    try {
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
      }

      // check args
      if (args.length < 2) {
        System.out.println("Usage: VotingClient host port");
        System.exit(1);
      }

      Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
      GameServer master = (GameServer) registry.lookup("master");
      Console console = System.console();

      VotingDisplay display = new VotingDisplay();
      VotingInputParser parser = new VotingInputParser();
      SimpleClient client = new SimpleClient(display, parser, master);

      while (true) {
        String input = console.readLine("> ");
        client.sendInput(input);
      }

    } catch (Exception e) {
      System.err.println("Client exception: " + e.toString());
    }
  }
}
