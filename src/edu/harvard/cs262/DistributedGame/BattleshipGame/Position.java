package edu.harvard.cs262.DistributedGame.BattleshipGame;
import java.io.Serializable;

/**
 * The Position class represents a row-column pair that indicates the 
 * position of a cell in the Battleship board
 * 
 * @author  Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 * 
 */

public class Position implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public int row;
    public int column;
    
    /**
     * Constructor for Position that sets the row and column variables.
     * @param  row  An int that represents the row of the position
     * @param  column  An int that represents the column of the position
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
}