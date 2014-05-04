package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameSnapshot;

/**
 * A VotingSnapshot represents a snapshot of the Voting Game.
 * Clients use the snapshot to render the game. It contains the
 * current value of the number to vote on and the frame number.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class VotingSnapshot implements GameSnapshot {
  private int value;
  private long frame;

  /**
   * The constructor for a VotingSnapshot. Sets the value 
   * and the frame number of the snapshot.
   * 
   * @param  value  An int that represents the current value
   *                to be voted on.
   *                
   * @param  frame  A long representing the frame of the game
   *                the snapshot represents
   *                
   * @return A VotingSnapshot object that encompasses the value
   *         and frame arguments and can be used to display the
   *         snapshot of the game
   */
  public VotingSnapshot(int value, long frame) {
    this.value = value;
    this.frame = frame;
  }

  /**
   * Gets the current value of the int that is voted on
   * 
   * @return An int representing the value of the game
   */
  public int getValue() {
    return value;
  }

  /**
   * Gets the frame of the current snapshot
   * 
   * @return An int representing the frame of the snapshot
   */
  public long getFrame() {
    return frame;
  }
}
