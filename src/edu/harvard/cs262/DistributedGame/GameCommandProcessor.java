package edu.harvard.cs262.DistributedGame;

public interface GameCommandProcessor {

  // Add to the processor's command queue
  public void addCommand(GameCommand command);

  // Retrieve the command decided upon by the processor
  public GameCommand getCommand();
}