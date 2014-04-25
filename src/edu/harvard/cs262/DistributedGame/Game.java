package edu.harvard.cs262.DistributedGame;

public interface Game {
  // Get snapshot to send to client
  public GameSnapshot getSnapshot();

  // Get update to send to other servers
  // Get full update (either game state or full log)
  public GameState getState();

  public boolean setState(GameState state);

  // Get partial update (diff from start to current frame)
  public GameDiff getDiff(GameState state);

  // Update game state based on command argument
  public long executeCommand(GameCommand command);
}