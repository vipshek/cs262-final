package edu.harvard.cs262.DistributedGame;

import java.io.Serializable;

/**
 * The GameState interface represents the current
 * state of the game, which is used by servers to 
 * execute commands on the game.  It is also what is
 * to replicated on slave servers.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public interface GameState extends Serializable {
    /**
     * Gets the current frame of the state
     * 
     * @return A long that represents the current
     *         frame of the state
     */
    public long getFrame();
}