package edu.harvard.cs262.DistributedGame;

import java.io.Serializable;

public interface GameState extends Serializable {
  public long getFrame();
}