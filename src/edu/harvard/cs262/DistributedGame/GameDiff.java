package edu.harvard.cs262.DistributedGame;

public interface GameDiff {
	// Apply all changes from this diff to the game's state
	public long apply(Game game);
}