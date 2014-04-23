package edu.harvard.cs262.DistributedGame.VotingGame;


class VotingGame implements Game {
	private int value;
	private long frameCount;

	public VotingGame(int value){
		this.value = value;
		this.frameCount = 0;
	}

	public long executeCommmand(GameCommand command){
		if(command instanceof VotingCommand){
			VotingCommand vc = (VotingCommand) command;
			if(vc.getVote()){
				this.value++;
			} else {
				this.value--;
			}
		}
		this.frameCount++;
		return this.frameCount
	}

	public GameState getState(){
		return new VotingState(this.value);
	}
}
