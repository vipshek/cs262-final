package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

import java.lang.UnsupportedOperationException;

class VotingGame implements Game {
	private int value;
	private long frameCount;

	public VotingGame(int value){
		this.value = value;
		this.frameCount = 0;
	}

	public long executeCommand(GameCommand command){
		if(command instanceof VotingCommand){
			VotingCommand vc = (VotingCommand) command;
			if(vc.getVote()){
				this.value++;
			} else {
				this.value--;
			}
		}
		this.frameCount++;
		return this.frameCount;
	}

	public GameState getState(){
		return new VotingState(this.value, this.frameCount);
	}

	public GameDiff getDiff(long start) {
		throw new UnsupportedOperationException();
	}

	public VotingSnapshot getSnapshot() {
		return new VotingSnapshot(this.value, this.frameCount);
	}
}
