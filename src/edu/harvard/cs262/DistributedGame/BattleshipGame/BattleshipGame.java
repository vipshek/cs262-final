package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;

import java.lang.UnsupportedOperationException;

import java.util.Random;

public enum Direction {
	HORIZONTAL,
	VERTICAL
}

public class ShipLocation {
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
	final int NUM_SHIPS = 5;
	final int BOARD_SIZE = 10;

	private int[][] shots;
	private ShipLocation[] ships;
	private int[] shipSizes;
	private long frameCount;

	public BattleshipGame(){
		shots = new int[BOARD_SIZE][BOARD_SIZE];
		ships = new ShipLocation[NUM_SHIPS];
		shipSizes = {2, 3, 3, 4, 5};

		Random r = new Random();

		for (int i = 0; i < NUM_SHIPS; i++) {
			Direction dir = VERTICAL;
			if (r.nextInt(2) == 0)
				dir = HORIZONTAL;

			int x = r.nextInt(BOARD_SIZE);
			int y = r.nextInt(BOARD_SIZE);

			for (int j = 0; j < i; j++) {

			}

			ships[i] = 
		}

		this.frameCount = 0;
	}

	public long executeCommand(GameCommand command){
		if (command instanceof VotingCommand){
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
