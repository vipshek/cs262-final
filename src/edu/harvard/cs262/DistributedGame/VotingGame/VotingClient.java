
package edu.harvard.cs262.DistributedGame.VotingGame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.SimpleClient.SimpleClient;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;

public class VotingClient {
    public static void main(String args[]){
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

            Screen screen = TerminalFacade.createScreen();

            VotingDisplay display = new VotingDisplay(screen);
            VotingInputParser parser = new VotingInputParser();
            SimpleClient client = new SimpleClient(display, parser, master);

            while (true) {
                Key key = screen.readInput();
                String input;
                if (key == null)
                    continue;
                else if (key.getKind() == Key.Kind.ArrowUp)
                    input = "UP";
                else if (key.getKind() == Key.Kind.ArrowDown)
                    input = "DOWN";
                else
                    continue;
                client.sendInput(input);
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }
}
