package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameState;

class VotingState implements GameState {
  private int value;
  private long frameCount;

  public VotingState(int value, long frameCount) {
    this.value = value;
    this.frameCount = frameCount;
  }

  public int getValue() {
    return value;
  }

  public long getFrame() {
    return frameCount;
  }

  @Override
  public String toString() {
    return String.format("Frame: %d, Value: %d", this.frameCount, this.value);
  }
}
