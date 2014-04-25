package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameDiff;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import java.lang.UnsupportedOperationException;

import java.util.Random;

class BattleshipGame implements Game {
	final static int NUM_SHIPS = 5;
	final static int BOARD_SIZE = 10;

	// 0 indicates untried. 1 indicates miss. 2 indicates hit.
	private int[][] shotsBoard;
	private boolean[][] shipsBoard;
	private ShipLocation[] shipLocations;
	private int[] shipSizes;
	private long frameCount;

	// fires a shot at the board at the specified location
	// if the shot was a hit, returns true. else returns false.
	// if the shot was a hit, records the shot.
	private boolean fireShot(Position pos) {
		if (shipsBoard[pos.x][pos.y]) {
			shotsBoard[pos.x][pos.y] = 2;
			return true;
		} else {
			shotsBoard[pos.x][pos.y] = 1;
			return false;
		}
	}

	public BattleshipGame(){
		shotsBoard = new int[BOARD_SIZE][BOARD_SIZE];
		shipsBoard = new boolean[BOARD_SIZE][BOARD_SIZE];
		shipLocations = new ShipLocation[NUM_SHIPS];
		shipSizes = new int[]{2, 3, 3, 4, 5};
		frameCount = 0;

		Random r = new Random();

		for (int i = 0; i < NUM_SHIPS; i++) {
			Direction dir = Direction.VERTICAL;
			if (r.nextInt(2) == 0)
				dir = Direction.HORIZONTAL;

			int x = r.nextInt(BOARD_SIZE);
			int y = r.nextInt(BOARD_SIZE);

			// check that the ship is not off the board
			if ((dir == Direction.VERTICAL && y + shipSizes[i] > BOARD_SIZE) || 
				(dir == Direction.HORIZONTAL && x + shipSizes[i] > BOARD_SIZE)) {
				i--;
				continue;
			}

			// check that the ship is not overlapping with any ship
			boolean overlap = false;
			for (int j = 0; j < i; j++) {
				int size = shipSizes[j];
				int xPos = shipLocations[j].pos.x;
				int yPos = shipLocations[j].pos.y;

				for (int k = 0; k < size; k++) {
					if ((dir == Direction.VERTICAL && shipsBoard[xPos][yPos + k] == true) || 
						(dir == Direction.HORIZONTAL && shipsBoard[xPos + k][yPos] == true)) {
						overlap = true;
						break;
					}
				}
			}

			// save this location for this ship
			shipLocations[i] = new ShipLocation(new Position(x, y), dir);
			
			// update matrix of locations with ships on them
			for (int k = 0; k < shipSizes[i]; k++) {
				if (dir == Direction.VERTICAL)
					shipsBoard[x][y + k] = true;
				else
					shipsBoard[x + k][y] = true;
			}
		}
	}

	public long executeCommand(GameCommand command){
		if (command instanceof BattleshipCommand){
			BattleshipCommand bc = (BattleshipCommand) command;
			fireShot(bc.getPos());
		}

		return ++this.frameCount;
	}

	public GameState getState(){
		return new BattleshipState(shotsBoard, shipLocations, frameCount);
	}

	public GameSnapshot getSnapshot() {
		boolean[] sunkShips = new boolean[NUM_SHIPS];

		// for each ship, check if it has been sunk
		for (int i = 0; i < NUM_SHIPS; i++) {
			Direction dir = shipLocations[i].dir;
			int xOrigin = shipLocations[i].pos.x;
			int yOrigin = shipLocations[i].pos.y;

			boolean sunk = true;
			for (int k = 0; k < shipSizes[i]; k++) {
				if (dir == Direction.VERTICAL && shotsBoard[xOrigin][yOrigin + k] == 0 ||
				   (dir == Direction.HORIZONTAL && shotsBoard[xOrigin + k][yOrigin] == 0)) {
					sunk = false;
					break;
				}
			}

			sunkShips[i] = sunk;
		}

		return new BattleshipSnapshot(shotsBoard, sunkShips, frameCount);
	}

	public GameDiff getDiff(long start) {
		throw new UnsupportedOperationException();
	}
}
