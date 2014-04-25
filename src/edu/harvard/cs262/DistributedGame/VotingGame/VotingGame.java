package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

import java.lang.UnsupportedOperationException;

class VotingGame implements Game {
  private VotingState state;

  public VotingGame(int value) {
    this.state = new VotingState(value, 0);
  }

  public long executeCommand(GameCommand command) {
    if (command instanceof VotingCommand) {
      VotingCommand vc = (VotingCommand) command;
      if (vc.getVote()) {
        this.state = new VotingState(this.state.getValue()+1, this.state.getFrame()+1);
      } else {
        this.state = new VotingState(this.state.getValue()-1, this.state.getFrame()+1);
      }
    }
    return this.state.getFrame();
  }

  public GameState getState() {
    return this.state;
  }

  @Override
  public boolean setState(GameState state) {
    this.state = (VotingState) state;
    return true;
  }

  @Override
  public GameDiff getDiff(GameState state) {
    throw new UnsupportedOperationException();
  }

  public VotingSnapshot getSnapshot() {
    return new VotingSnapshot(this.state.getValue(), this.state.getFrame());
  }
}
