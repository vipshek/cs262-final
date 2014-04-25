package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

public class VotingDisplay implements GameDisplay {
	public long render(GameSnapshot snapshot) {
		System.out.println(((VotingSnapshot) snapshot).getValue());
		return snapshot.getFrame();
	}
}