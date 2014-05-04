package edu.harvard.cs262.DistributedGame;

/**
 * Game is an interface that is implemented by every game so that
 * it can interact with the client and server system.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 * 
 */
public interface Game {
    /**
     * Gets the current snapshot from the server to send back
     * to the client
     * 
     * @return A {@link GameSnapshot} that represents the current
     *         snapshot of the game
     */
    public GameSnapshot getSnapshot();

    /**
     * Gets an update of the current game state to sent to other
     * servers. The contents of GameState could be either a 
     * game state or a full log, depending on the game.
     * 
     * @return A {@link GameState} that represents the current
     *         state of the game.
     */
    public GameState getState();

    /**
     * Sets the state of the game to the passed-in game state
     * 
     * @param  gamestate A {@link GameState} that represents
     *         the current state of the game
     *         
     * @return A boolean that represents whether the gamestate 
     *         was properly set
     */
    public boolean setState(GameState gamestate);

    /**
     * Updates the state of the game by executing the 
     * passed-in command
     * 
     * @param  command  A {@link GameCommand} that represents
     *         the command to be executed
     *         
     * @return A long that represents the new frame number of 
     *         the game following the execution of the command
     */
    public long executeCommand(GameCommand command);
}