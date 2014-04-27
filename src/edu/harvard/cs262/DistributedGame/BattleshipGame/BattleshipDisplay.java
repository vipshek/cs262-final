package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import java.util.Arrays;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.terminal.Terminal;

import java.awt.Desktop;
import java.net.URI;

public class BattleshipDisplay implements GameDisplay {
	private GUIScreen gui;
	private boolean won;

	public BattleshipDisplay(GUIScreen gui) {
		this.gui = gui;
		this.won = false;
		gui.getScreen().startScreen();
		gui.setTitle("Battleship");
	}

	public long render(GameSnapshot snapshot) {
		int[][] shotsBoard = ((BattleshipSnapshot) snapshot).getShotsBoard();
		boolean[] sunkShips = ((BattleshipSnapshot) snapshot).getSunkShips();

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

				((BattleshipSquare) ((BattleshipWindow) 
					gui.getActiveWindow()).table.getRow(i)[j]).setText(newLabel);
			}
		}

		// Update ship labels
		for (int i = 0; i < sunkShips.length; i++) {
			if (sunkShips[i])
				((BattleshipWindow) gui.getActiveWindow()).shipLabels[i].setTextColor(Terminal.Color.WHITE);
		}

		// Check if victory
		boolean $winning = true;
		for (int i = 0; i < sunkShips.length; i++)
			$winning = $winning && sunkShips[i];

		if ($winning && !won) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.youtube.com/watch?v=dQw4w9WgXcQ"));
				} catch (Exception e) {
					// TODO
				}
			}
			
			MessageBox.showMessageBox(gui,"Victory","THE SHIPS HAVE BEEN SUCCESSFULLY BATTLED");

			won = true;
		}

		gui.invalidate();

		return snapshot.getFrame();
	}
}