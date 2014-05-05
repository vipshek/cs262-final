package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommand;

/**
 * Wrapper for the data sent from a client to the master. Instantiates
 * a {@link Position} which is stored as an instance variable.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class BattleshipCommand implements GameCommand {
	private static final long serialVersionUID = 1L;
	
	private Position pos;

	/**
	 * Creates a {@link Position} based on the input row and column.
	 */
    public BattleshipCommand(int row, int column){
        this.pos = new Position(row,column);
    }

    public Position getPos() {
        return this.pos;
    }
}
