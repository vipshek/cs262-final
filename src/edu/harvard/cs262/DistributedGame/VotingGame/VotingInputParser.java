package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameCommand;

/**
 * The VotingInputParser class parses the input given
 * from the clients into VotingCommand objects
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingInputParser implements GameInputParser {
    /**
     * Parses the input string into a VotingCommand. Input
     * options are either "UP" or "DOWN" to represent incrementing
     * or decrementing the number of the game.
     * 
     * @param  input A String that is "UP" to represent incrementing
     *         the voting game number and "DOWN" to represent 
     *         decrementing the number
     * @return A GameCommand object for the server to process
     */
    public GameCommand parseInput(String input) {
        if (input.equals("UP")) {
            return new VotingCommand(true);
        } else if (input.equals("DOWN")) {
            return new VotingCommand(false);
        } else {
            return null;
        }
    }
}