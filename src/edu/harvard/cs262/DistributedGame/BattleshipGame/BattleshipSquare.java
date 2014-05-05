package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.component.Button;

/**
 * Extends lanterna's Button class, which is a convenient abstraction
 * for each position in our Battleship grid.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class BattleshipSquare extends Button {
    public int row;
    public int column;

    /**
     * Constructor takes an int row and column that the BattleshipSquare
     * is in. This allows the {@link BattleshipWindowAdapter} to access
     * the square's position to easily send to client.
     */
    public BattleshipSquare(int row, int column, String text) {
        super(text);
        this.row = row;
        this.column = column;
    }
}
