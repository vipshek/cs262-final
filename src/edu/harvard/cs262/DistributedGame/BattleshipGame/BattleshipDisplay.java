package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;


import com.googlecode.lanterna.gui.GUIScreen;

import com.googlecode.lanterna.terminal.Terminal;

import java.awt.Desktop;
import java.net.URI;

public class BattleshipDisplay implements GameDisplay {
    private GUIScreen gui;
    private boolean won;
    private long currentFrame;

    public BattleshipDisplay(GUIScreen gui) {
        this.gui = gui;
        this.won = false;
        gui.getScreen().startScreen();
        gui.setTitle("Battleship");
    }

    public long render(GameSnapshot snapshot) {
        int[][] shotsBoard = ((BattleshipSnapshot) snapshot).getShotsBoard();
        boolean[] sunkShips = ((BattleshipSnapshot) snapshot).getSunkShips();

        BattleshipWindow window = (BattleshipWindow) gui.getActiveWindow();
        if (window == null)
            return currentFrame;

        // Update board
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

        // Update ship labels
        for (int i = 0; i < sunkShips.length; i++) {
            if (sunkShips[i])
                window.shipLabels[i].setTextColor(Terminal.Color.WHITE);
        }

        // Check if victory
        boolean winning = true;
        for (int i = 0; i < sunkShips.length; i++)
            winning = winning && sunkShips[i];

        if (winning && !won) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (Exception e) {
                    // TODO
                }
            }

            window.shipLabelPanel.setTitle("Victory");
            for (int i = 1; i < window.shipLabels.length; i++) {
                window.shipLabels[i].setText("");
            }
            window.shipLabels[0].setTextColor(Terminal.Color.DEFAULT);
            window.shipLabels[0].setText("THE SHIPS HAVE BEEN SUCCESSFULLY BATTLED");


            won = true;
        }

        gui.invalidate();

        currentFrame = snapshot.getFrame();

        return currentFrame;
    }
}
