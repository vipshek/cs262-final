package edu.harvard.cs262.DistributedGame.VotingGame;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;

/**
 * The VotingClient class contains the code that the clients
 * run in the Voting Game. Clients see a display with the current 
 * number and can vote by pressing the up or down arrow keys.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingClient {
    /**
     * The main function that clients execute. Connects the clients
     * to the registry and shows them the game screen and allows them
     * to vote up or down on the number shown.
     * 
     * @param args  An array of strings given at the command line
     *                containing the host and port
     */
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
            UpdateableClient client = new UpdateableClient(display, parser, master);

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
