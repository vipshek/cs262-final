package edu.harvard.cs262.DistributedGame.BattleshipGame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.nio.charset.Charset;
import java.lang.Thread;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;

public class BattleshipClient {
    public static void main(String args[]){
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            // check args
            if (args.length < 2) {
                System.out.println("Usage: BattleshipClient host port");
                System.exit(1);
            }

            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            GameServer master = (GameServer) registry.lookup("master");

            GUIScreen gui = TerminalFacade.createGUIScreen();

            BattleshipDisplay display = new BattleshipDisplay(gui);
            BattleshipInputParser parser = new BattleshipInputParser();
            UpdateableClient client = new UpdateableClient(display, parser, master);

            BattleshipWindow window = new BattleshipWindow(client);
            BattleshipWindowAdapter adapter = new BattleshipWindowAdapter();
            window.addWindowListener(adapter);
            gui.showWindow(window, GUIScreen.Position.CENTER);

            BattleshipRequestThread thread = new BattleshipRequestThread(master,client);
            thread.start();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
        }
    }
}
