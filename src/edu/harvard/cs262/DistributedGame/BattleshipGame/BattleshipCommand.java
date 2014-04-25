package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommand;

class BattleshipCommand implements GameCommand {
	private boolean isUp;

	public VotingCommand(boolean isUp){
		this.isUp = isUp;
	}

	public boolean getVote(){
		return isUp;
	}
}
