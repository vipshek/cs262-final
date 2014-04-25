package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import com.googlecode.lanterna.gui.GUIScreen;

public class BattleshipDisplay implements GameDisplay {
	private GUIScreen gui;

	public BattleshipDisplay(GUIScreen gui) {
		this.gui = gui;
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

		gui.invalidate();

		return snapshot.getFrame();
	}
}