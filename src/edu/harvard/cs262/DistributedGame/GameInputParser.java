package edu.harvard.cs262.DistributedGame;

/**
 * The GameInputParser interface represents a mapping from
 * input strings to {@link GameCommand} objects
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public interface GameInputParser {
    /**
     * Parses the input string and changes it into 
     * a {@link GameCommand} which can be executed
     * by the game
     * 
     * @param  input  A string that the client produces
     *         to represent a command it wants to execute
     * 
     * @return A {@link GameCommand} that represents the 
     *         client's command in a form readable by the Game
     */
    public GameCommand parseInput(String input);
}