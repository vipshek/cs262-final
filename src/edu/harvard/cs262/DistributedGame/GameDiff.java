package edu.harvard.cs262.DistributedGame;

import java.io.Serializable;

public interface GameDiff extends Serializable {
  // Apply all changes from this diff to the game's state
  public long apply(Game game);
}