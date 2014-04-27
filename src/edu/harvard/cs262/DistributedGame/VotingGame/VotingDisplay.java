package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.TerminalSize;

public class VotingDisplay implements GameDisplay {
<<<<<<< HEAD
	private Screen screen;
	private ScreenWriter writer;
	private TerminalSize size;

	public VotingDisplay(Screen screen) {
		this.screen = screen;
		writer = new ScreenWriter(screen);
		screen.startScreen();
		size = screen.getTerminalSize();
		writer.drawString(size.getColumns() / 2, 0, "THE VOTING GAME!");
		screen.refresh();
	}

	public long render(GameSnapshot snapshot) {
		writer.drawString(size.getColumns() / 2, 
						 size.getRows() / 2, 
						 Integer.toString(((VotingSnapshot) snapshot).getValue()));
		screen.refresh();
		return snapshot.getFrame();
	}
=======
  public long render(GameSnapshot snapshot) {
    System.out.println(((VotingSnapshot) snapshot).getValue());
    return snapshot.getFrame();
  }
>>>>>>> quad
}