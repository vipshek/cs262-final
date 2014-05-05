package edu.harvard.cs262.DistributedGame.BattleshipGame;

import java.io.Serializable;

/**
 * The ShipLocation class represents the location of a ship on the 
 * Battleship board.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class ShipLocation implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public Position pos;
    public Direction dir;

    /**
     * Constructor for a ShipLocation object. Sets the position and direction
     * of each ship.
     * 
     * @param  pos  A {@link Position} object that represents the location
     *         where the ship starts
     * 
     * @param  dir  A {@link Direction} that represents the direction in which
     *         the ship is facing
     */
    public ShipLocation(Position pos, Direction dir) {
        this.pos = pos;
        this.dir = dir;
    }

    /**
     * Creates a string that shows whether a ship is horizontal or vertical
     * and also its row and column position that it starts at.
     * 
     * @return A string that encompasses the ship's direction and position
     */
    @Override public String toString() {
        String s = "";

        if (dir == Direction.HORIZONTAL)
            s += "Horizontal: ";
        else
            s += "Vertical: ";

        s += "(" + pos.row + ", " + pos.column + ")";

        return s;
    }
}
