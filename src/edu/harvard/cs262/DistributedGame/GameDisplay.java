package edu.harvard.cs262.DistributedGame;

public interface GameDisplay {
	// Render the snapshot, returning latest frame number
	public long render(GameSnapshot snapshot);
}