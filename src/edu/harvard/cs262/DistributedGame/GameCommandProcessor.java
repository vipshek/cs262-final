package edu.harvard.cs262.DistributedGame;

/**
 * 
 * GameCommandProcessor is an interface that represents
 * the system by which commands are chosen to be run
 * for a given game.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 * 
 */
public interface GameCommandProcessor {

  /**
   * Adds a command to the list of commands that 
   * the game processor will choose from/aggregate
   * to decide the command that's executed
   * 
   * @param command  The {@link GameCommand} to be added
   *        to the command queue
   */
  public void addCommand(GameCommand command);

  /**
   * Retrieves the command decided on by the processor
   * 
   * @return A {@link GameCommand} that represents the 
   *         command chosen to be executed.
   */
  public GameCommand getCommand();
}