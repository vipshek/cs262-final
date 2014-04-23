package edu.harvard.cs262.DistributedGame;

// Represents a mapping from input strings to GameCommands
public interface GameInputParser {
	public GameCommand parseInput(String input);
}