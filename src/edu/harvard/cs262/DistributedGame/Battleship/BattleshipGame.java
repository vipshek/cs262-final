package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

import java.lang.UnsupportedOperationException;

final int NUM_SHIPS = 6;
final int BOARD_SIZE = 10;

private enum Direction {
	HORIZONTAL,
	VERTICAL
}

private class ShipLocation {
	public int x;
	public int y;
	public Direction dir;
	public ShipLocation(int x, int y, Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
}

class BattleshipGame implements Game {
	private int[][] shots = new int[BOARD_SIZE][BOARD_SIZE];
	private ShipLocation[] ships = new ShipLocation[NUM_SHIPS];
	private long frameCount;

	public BattleshipGame(){
		for (int i = 0; i < NUM_SHIPS; i++) {
			// PLACE SHIPS HERE
			ships[i] = 
		}

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
