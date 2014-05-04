package edu.harvard.cs262.DistributedGame;

/**
 * The GameDisplay interface represents the display of 
 * each game that the client sees when they connect to
 * the servers.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 * 
 */
public interface GameDisplay {
    /**
     * Renders the passed-in snapshot to the client,
     * displaying it and returning the latest frame number
     * the client has seen.
     * 
     * @param  snapshot  A {@link GameSnapshot} that 
     *         represents the snapshot of the game to 
     *         be rendered
     *         
     * @return A long that represents the latest frame number
     *         that has been seen.
     */
    public long render(GameSnapshot snapshot);
}