package edu.harvard.cs262.DistributedGame.VotingGame;

class VotingCommand implements GameCommand {
	private boolean isUp;

	public VotingCommand(boolean isUp){
		this.isUp = isUp;
	}

	public getVote(){
		return isUp;
	}
}
