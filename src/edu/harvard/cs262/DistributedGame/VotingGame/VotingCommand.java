package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.GameCommand;

class VotingCommand implements GameCommand {
  private boolean isUp;

  public VotingCommand(boolean isUp) {
    this.isUp = isUp;
  }

  public boolean getVote() {
    return isUp;
  }
}
