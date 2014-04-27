package edu.harvard.cs262.DistributedGame;

public interface GameCommandProcessor {
  // Clear previous queue of commands
  public void startProcessor();

  // Add to the processor's command queue
  public void addCommand(GameCommand command);

  // Retrieve the command decided upon by the processor
  public GameCommand getCommand();
}