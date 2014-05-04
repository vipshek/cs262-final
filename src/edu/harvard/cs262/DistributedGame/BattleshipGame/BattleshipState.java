package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameState;

/**
 * The BattleshipState class represents the state of the Battleship
 * game.  It encompasses all information needed to run the game,
 * including the board of where shots were fired, the location of the
 * ships, the current frame, and the number of hits and misses.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class BattleshipState implements GameState {
	private static final long serialVersionUID = 1L;
	
    private int[][] shotsBoard;
    private ShipLocation[] shipLocations;
    private long frameCount;
    private int numHits;
    private int numMisses;

    /**
     * Constructor for the BattleshipState class. Sets the state's shots board,
     * ship locations, and frame to the passed-in arguments and initializes the 
     * number of hits and misses to 0.
     * 
     * @param  shotsBoard  A 2-D int array that represents where shots were
     *         fired on the board
     *         
     * @param  shipLocations  An array of {@link ShipLocation} objects that 
     *         represents the lcoations of all the ships on the board
     * 
     * @param  frameCount  A long that represents the frame number of the state
     * 
     * @return A BattleshipState object that encompasses the above variables
     */
    public BattleshipState(int[][] shotsBoard, ShipLocation[] shipLocations, long frameCount){
        this.shotsBoard = shotsBoard;
        this.shipLocations = shipLocations;
        this.frameCount = frameCount;
        this.numHits = 0;
        this.numMisses = 0;
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
     * Gets the array that represents where all the ships are located
     *
     * @return An array of {@link ShipLocation} objects that represents
     *         the location of all ships on the board
     */
    public ShipLocation[] getShipLocations() {
        return shipLocations;
    }

    /**
     * Gets the current frame of the state
     * 
     * @return A long that represents the frame of the state
     */
    public long getFrame() {
        return frameCount;
    }

    /**
     * Increments the counter that represents the number of hits
     * 
     * @return A boolean that is always true and shows that the 
     *         hit counter was successfully incremented
     */
    public boolean addHit() {
        this.numHits++;
        return true;
    }

    /**
     * Increments the counter that represents the number of misses
     * 
     * @return A boolean that is always true and shows that the
     *         miss counter was successfully incremented
     */
    public boolean addMiss() {
        this.numMisses++;
        return true;
    }
    
    /**
     * Sets the frame of the state of the game. If the frame to be set
     * is less than the current frame, the request is ignored.
     * 
     * @param  f  A long that represents the frame of the game to set
     *         the state's frame to
     *         
     * @return A boolean that is true if the frame is successfully set
     *         to the passed-in frame and is false if the argument frame
     *         is less than the current frame 
     */
    public boolean setFrame(long f) {
        if (f < frameCount)
            return false;

        frameCount = f;
        return true;
    }

    /**
     * Prints the frame number, number of hits, and number of misses of
     * the state
     * 
     * @return A string that includes frame, number of hits, and number
     *         of misses
     */
    @Override
    public String toString() {
        return String.format("BattleshipState - Frame: %d, Hits: %d, Misses: %d", this.frameCount, this.numHits, this.numMisses);
    }
}
