package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

/**
 * The BattleshipSnapshot class represents the snapshot of the Battleship
 * game.  It encompasses all information needed for clients to display the
 * game, including the board of where shots were fired, which ships were sunk,
 * and the current frame
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class BattleshipSnapshot implements GameSnapshot {
	private static final long serialVersionUID = 1L;
	
    private int[][] shotsBoard;
    private boolean[] sunkShips;
    private long frame;

    /**
     * Constructor for the BattleshipSnapshot class. Sets the snapshot's
     * board of where shots were fired, list of sunk ships, and frame.
     * 
     * @param  shotsBoard  A 2-D int array that represents where shots were
     *         fired on the board
     *         
     * @param  sunkShips  A boolean array that represents which ships have
     *         been sunk
     * 
     * @param  A long that represents the frame number of the state
     * 
     * @return A BattleshipSnapshot object that encompasses the above variables
     */
    public BattleshipSnapshot(int[][] shotsBoard, boolean[] sunkShips, long frame){
        this.shotsBoard = shotsBoard;
        this.sunkShips=sunkShips;
        this.frame = frame;
    }

    /**
     * Gets the matrix that represents what parts of the board were shot at
     *
     * @return A 2-D array of ints that holds whether that space on the
     *         board was untouched, a hit, or a miss
     */
    public int[][] getShotsBoard() {
        return shotsBoard;
    }

    /**
     * Gets the array that represents which ships were sunk
     *
     * @return An array of booleans that holds whether the ship at 
     *         that index was sunk or not
     */
    public boolean[] getSunkShips() {
        return sunkShips;
    }

    /**
     * Gets the current frame of the state
     * 
     * @return A long that represents the frame of the state
     */
    public long getFrame() {
        return frame;
    }
}
