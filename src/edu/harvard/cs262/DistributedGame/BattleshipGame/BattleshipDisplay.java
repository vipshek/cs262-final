package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;


import com.googlecode.lanterna.gui.GUIScreen;

import com.googlecode.lanterna.terminal.Terminal;

import java.awt.Desktop;
import java.net.URI;

/**
 * Takes BattleshipSnapshots from the servers and renders them. Instantiates
 * a {@link BattleshipWindow} for this purpose, updating the BattleshipSquares
 * and text labels to render input for the client.
 *
 * @author Twitch Plays Battleship Group
 *
 * @version 1.0, April 2014
 */
public class BattleshipDisplay implements GameDisplay {
    private GUIScreen gui;
    private boolean won;
    private long currentFrame;

    /**
     * Starts the input GUIScreen and sets its title.
     *
     * @param gui A lanterna GUIScreen, initialized in BattleshipClient.
     */
    public BattleshipDisplay(GUIScreen gui) {
        this.gui = gui;
        this.won = false;
        gui.getScreen().startScreen();
        gui.setTitle("Battleship");
    }

    /**
     * Renders a snapshot passed back from the server. The BattleshipClient
     * needs to initialize a window and attach it to the gui before this
     * method will successfully render anything.
     *
     * @param snapshot A BattleshipSnapshot received from the server.
     */ 
    public long render(GameSnapshot snapshot) {
        // Retrieve relevant variables from the snapshot
        int[][] shotsBoard = ((BattleshipSnapshot) snapshot).getShotsBoard();
        boolean[] sunkShips = ((BattleshipSnapshot) snapshot).getSunkShips();

        // Check that the window exists
        BattleshipWindow window = (BattleshipWindow) gui.getActiveWindow();
        if (window == null)
            return currentFrame;

        // Update labels for each BattleshipSquare on the board
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String newLabel;
                if (shotsBoard[i][j] == 0) {
                    newLabel = " ";
                } else if (shotsBoard[i][j] == 1) {
                    newLabel = "O";
                } else if (shotsBoard[i][j] == 2) {
                    newLabel = "X";
                } else {
                    newLabel = " ";
                }

                ((BattleshipSquare) window.table.getRow(i)[j]).setText(newLabel);
            }
        }

        // Update ship labels to be invisible if ship has been sunk
        for (int i = 0; i < sunkShips.length; i++) {
            if (sunkShips[i])
                window.shipLabels[i].setTextColor(Terminal.Color.WHITE);
        }

        // Check if victory
        boolean winning = true;
        for (int i = 0; i < sunkShips.length; i++)
            winning = winning && sunkShips[i];

        if (winning && !won) {
            // Rickroll
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (Exception e) {
                    // If we cannot Rickroll, just do the other display modification
                }
            }

            // Remove ship labels and display "Victory"
            window.shipLabelPanel.setTitle("Victory");
            for (int i = 1; i < window.shipLabels.length; i++) {
                window.shipLabels[i].setText("");
            }
            window.shipLabels[0].setTextColor(Terminal.Color.DEFAULT);
            window.shipLabels[0].setText("THE SHIPS HAVE BEEN SUCCESSFULLY BATTLED");

            won = true;
        }

        // Invalidate GUI so that it gets automatically redrawn
        gui.invalidate();

        currentFrame = snapshot.getFrame();

        return currentFrame;
    }
}
