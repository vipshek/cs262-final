package edu.harvard.cs262.DistributedGame.VotingGame;

class VotingState implements GameState {
	private int value;

	public VotingState(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
