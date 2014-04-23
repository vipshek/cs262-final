package edu.harvard.cs262.DistributedGame.VotingGame;

class VotingSnapshot implements GameSnapshot {
	private int value;

	public VotingSnapshot(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
