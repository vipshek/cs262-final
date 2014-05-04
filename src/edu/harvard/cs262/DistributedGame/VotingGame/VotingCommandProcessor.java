package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameCommand;

/**
 * The VotingCommandProcessor class processes the commands the clients
 * send and chooses the ones to execute.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingCommandProcessor implements GameCommandProcessor {
    private VotingCommand command;

    /**
     * Sets the processor's command to the passed-in command.
     * The Voting Game has a simple processor that simply runs all received
     * commands.
     * 
     * @param command A {@link GameCommand} that a client wants to
     *        execute
     */
    public void addCommand(GameCommand command) {
        this.command = (VotingCommand) command;
    }

    /**
     * A getter for the processor's current command
     * 
     * @return A {@link GameCommand} that is the processor's current
     *         command 
     */
    public GameCommand getCommand() {
        return this.command;
    }
}