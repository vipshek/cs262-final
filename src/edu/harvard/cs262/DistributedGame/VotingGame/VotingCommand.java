package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameCommand;

/**
 * The VotingCommand class represents a command in the 
 * Voting Game. Commands are represented by a boolean,
 * where true represents incrementing the number and 
 * false represents decrementing the number.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class VotingCommand implements GameCommand {
    private boolean isUp;

    /**
     * The constructor for a VotingCommand. Sets
     * the boolean that represents a command.
     * 
     * @param  isUp  A boolean that represents 
     *         whether the number will be incremented
     *         or decremented
     * @return A VotingCommand object 
     */
    public VotingCommand(boolean isUp) {
        this.isUp = isUp;
    }

    /**
     * Getter for the vote variable
     * 
     * @return A boolean that represents the vote
     *         encompassed by the VotingCommand
     */
    public boolean getVote() {
        return isUp;
    }
}
