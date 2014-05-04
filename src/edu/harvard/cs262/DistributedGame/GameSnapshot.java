package edu.harvard.cs262.DistributedGame;

import java.io.Serializable;

/**
 * The GameSnapshot interface represents the current 
 * snapshot of the game, which clients can render to 
 * display and view the current progression of the game.
 * A GameSnapshot only contains as much information as 
 * clients need to render the game.  It does not include
 * hidden information used by servers.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public interface GameSnapshot extends Serializable {
    /**
     * Gets the current frame of the snapshot
     * 
     * @return A long that represents the current
     *         frame of the snapshot
     */
    public long getFrame();
}