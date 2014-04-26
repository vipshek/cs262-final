package edu.harvard.cs262.DistributedGame;

public interface Game {
  // Get snapshot to send to client
  public GameSnapshot getSnapshot();

  // Get update to send to other servers
  // Get full update (either game state or full log)
  public GameState getState();

  // Get partial update (diff from start to current frame)
  public GameDiff getDiff(GameState gamestate);

  public boolean setState(GameState gamestate);

  // Update game state based on command argument
  public long executeCommand(GameCommand command);
}