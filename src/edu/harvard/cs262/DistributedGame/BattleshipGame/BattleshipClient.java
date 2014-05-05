package edu.harvard.cs262.DistributedGame.BattleshipGame;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.TerminalFacade;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;

/**
 * Executable class that actually runs the Battleship game on client machine.
 * Initializes {@link UpdateableClient} and other classes in BattleshipGame
 * package.
 */
public class BattleshipClient {
    public static void main(String args[]){
        try {
            // Get security manager
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            // Check arguments
            if (args.length < 2) {
                System.out.println("Usage: BattleshipClient host port");
                System.exit(1);
            }

            // Find registry and master server based on input host and port
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            GameServer master = (GameServer) registry.lookup("master");

            // Initialize lanterna GUI; creates new system window
            GUIScreen gui = TerminalFacade.createGUIScreen();

            // Construct our classes to run the client code
            BattleshipDisplay display = new BattleshipDisplay(gui);
            BattleshipInputParser parser = new BattleshipInputParser();
            UpdateableClient client = new UpdateableClient(display, parser, master);

            // Create lanterna window and event handler, and attach to window
            BattleshipWindow window = new BattleshipWindow(client);
            BattleshipWindowAdapter adapter = new BattleshipWindowAdapter();
            window.addWindowListener(adapter);
            gui.showWindow(window, GUIScreen.Position.CENTER);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }
}
