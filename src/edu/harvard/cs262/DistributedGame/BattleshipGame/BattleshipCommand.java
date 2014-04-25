package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommand;

class BattleshipCommand implements GameCommand {
	private Position pos;

	public BattleshipCommand(int x, int y){
		this.pos = new Position(x,y);
	}

	public boolean getPos() {
		return this.pos;
	}
}
