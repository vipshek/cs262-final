package edu.harvard.cs262.DistributedGame.VotingGame;

class VotingState implements GameState {
	private int value;
	private long frameCount;

	public VotingState(int value, long frameCount){
		this.value = value;
		this.frameCount = frameCount;
	}

	public int getValue() {
		return value;
	}
}
