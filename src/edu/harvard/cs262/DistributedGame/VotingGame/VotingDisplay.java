package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * The VotingDisplay class is the display that the clients see
 * when they play the Voting Game. It consists of a screen that
 * displays the current number.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingDisplay implements GameDisplay {
    private Screen screen;
    private ScreenWriter writer;
    private TerminalSize size;

    /**
     * The constructor for the VotingDisplay. Initializes the
     * screen to display 0
     * @param  screen  A Screen to display the information
     *
     */
    public VotingDisplay(Screen screen) {
        this.screen = screen;
        writer = new ScreenWriter(screen);
        screen.startScreen();
        size = screen.getTerminalSize();
        writer.drawString(size.getColumns() / 2, 0, "THE VOTING GAME!");
        screen.refresh();
    }

    /**
     * Renders the display of the discreen given a snapshot of the game,
     * which contains the integer value of a number. Displays that number
     * on the screen
     * @param  snapshot  A {@link GameSnapshot} that represents the snapshot
     *         of the game to be displayed
     * @return A long that represents the current frame of the snapshot
     */
    public long render(GameSnapshot snapshot) {
        writer.drawString(size.getColumns() / 2, 
                         size.getRows() / 2, 
                         Integer.toString(((VotingSnapshot) snapshot).getValue()));
        screen.refresh();
        return snapshot.getFrame();
    }
}
