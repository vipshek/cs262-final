package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;

public class VotingDisplay implements GameDisplay {
	public long render(VotingSnapshot snapshot) {
		System.out.println(snapshot.get);
	}
}