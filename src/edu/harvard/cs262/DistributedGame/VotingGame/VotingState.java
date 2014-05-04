package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameState;

/**
 * The VotingState class represents the current state of the game,
 * which consists of the value of the game and the frame number
 * associated with that value.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class VotingState implements GameState {
    private int value;
    private long frameCount;

    /**
     * The constructor for a VotingState object. Sets the value and 
     * frame of the state.
     * 
     * @param  value  An int that represents the value of the number to be 
     *                voted on
     * 
     * @param  frameCount  A long that represents the frame number of the 
     *                     game associated with the value
     * 
     * @return  A VotingState object that encompasses the value and frame
     */
    public VotingState(int value, long frameCount) {
        this.value = value;
        this.frameCount = frameCount;
    }

    /**
     * Gets the value of the state
     * 
     * @return  An int that represents the value of the state
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the frame of the state
     * 
     * @return  A long that represents the frame of the state
     */
    public long getFrame() {
        return frameCount;
    }

    /**
     * Prints a string with the frame and value of the state
     * 
     * @return  A string that holds the frame and value of the state
     */
    @Override
    public String toString() {
        return String.format("Frame: %d, Value: %d", this.frameCount, this.value);
    }
}