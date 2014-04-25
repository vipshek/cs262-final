package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

class VotingSnapshot implements GameSnapshot {
	private int value;
	private long frame;

	public VotingSnapshot(int value, long frame){
		this.value = value;
		this.frame = frame;
	}

	public int getValue() {
		return value;
	}

	public long getFrame() {
		return frame;
	}
}
