package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import java.lang.UnsupportedOperationException;

import java.util.Arrays;

import java.util.Random;

class BattleshipGame implements Game {
	final static int NUM_SHIPS = 5;
	final static int BOARD_SIZE = 10;

	// 0 indicates untried. 1 indicates miss. 2 indicates hit.
    private BattleshipState state;
	private boolean[][] shipsBoard;
	private int[] shipSizes;

	// fires a shot at the board at the specified location
	// if the shot was a hit, returns true. else returns false.
	// if the shot was a hit, records the shot.
	private boolean fireShot(Position pos) {
		if (shipsBoard[pos.row][pos.column]) {
			state.getShotsBoard()[pos.row][pos.column] = 2;
            state.numHits++;
			return true;
		} else {
			state.getShotsBoard()[pos.row][pos.column] = 1;
            state.numMisses++;
			return false;
		}
	}

	public BattleshipGame(){
        state = new BattleshipState(new int[BOARD_SIZE][BOARD_SIZE], new ShipLocation[NUM_SHIPS], 0);
		shipsBoard = new boolean[BOARD_SIZE][BOARD_SIZE];
		shipSizes = new int[]{2, 3, 3, 4, 5};

		Random r = new Random();

		for (int i = 0; i < NUM_SHIPS; i++) {
			Direction dir = Direction.VERTICAL;
			if (r.nextInt(2) == 0)
				dir = Direction.HORIZONTAL;

			int row = r.nextInt(BOARD_SIZE);
			int column = r.nextInt(BOARD_SIZE);

			// check that the ship is not off the board
			if ((dir == Direction.HORIZONTAL && column + shipSizes[i] > BOARD_SIZE) || 
				(dir == Direction.VERTICAL && row + shipSizes[i] > BOARD_SIZE)) {
				i--;
				continue;
			}

			// check that the ship is not overlapping with any ship
			boolean overlap = false;
			for (int j = 0; j < i; j++) {
				for (int k = 0; k < shipSizes[j]; k++) {
					if (column + k < 10) {
						if (dir == Direction.HORIZONTAL && shipsBoard[row][column + k] == true) {
							overlap = true;
							break;
						}
					}

					if (row + k < 10) {
						if (row + k < BOARD_SIZE && dir == Direction.VERTICAL && shipsBoard[row + k][column] == true) {
							overlap = true;
							break;
						}
					}
				}

				if (overlap)
					break;
			}

			if (overlap) {
				i--;
				continue;
			}

			// save this location for this ship
			state.getShipLocations()[i] = new ShipLocation(new Position(row, column), dir);

			// update matrix of locations with ships on them
			for (int k = 0; k < shipSizes[i]; k++) {
				if (dir == Direction.HORIZONTAL)
					shipsBoard[row][column + k] = true;
				else
					shipsBoard[row + k][column] = true;
			}
		}
	}

	public long executeCommand(GameCommand command){
		if (command instanceof BattleshipCommand){
			BattleshipCommand bc = (BattleshipCommand) command;
			fireShot(bc.getPos());
		}

        state.setFrame(state.getFrame() + 1);
		return state.getFrame();
	}

	public GameState getState(){
        return this.state;
	}

	public boolean setState(GameState gameState){
        this.state = (BattleshipState)gameState;

		shipsBoard = new boolean[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < NUM_SHIPS; i++) {

            int row = state.getShipLocations()[i].pos.row;
            int column = state.getShipLocations()[i].pos.column;
            Direction dir = state.getShipLocations()[i].dir;

			// update matrix of locations with ships on them
			for (int k = 0; k < shipSizes[i]; k++) {
				if (dir == Direction.HORIZONTAL)
					shipsBoard[row][column + k] = true;
				else
					shipsBoard[row + k][column] = true;
			}
        }

        return true;
	}
	public GameSnapshot getSnapshot() {
		boolean[] sunkShips = new boolean[NUM_SHIPS];

		// for each ship, check if it has been sunk
		for (int i = 0; i < NUM_SHIPS; i++) {
			Direction dir = state.getShipLocations()[i].dir;
			int rowOrigin = state.getShipLocations()[i].pos.row;
			int columnOrigin = state.getShipLocations()[i].pos.column;

			boolean sunk = true;
			for (int k = 0; k < shipSizes[i]; k++) {
				if (dir == Direction.HORIZONTAL && state.getShotsBoard()[rowOrigin][columnOrigin + k] == 0 ||
				   (dir == Direction.VERTICAL && state.getShotsBoard()[rowOrigin + k][columnOrigin] == 0)) {
					sunk = false;
					break;
				}
			}

			sunkShips[i] = sunk;
		}

		return new BattleshipSnapshot(state.getShotsBoard(), sunkShips, state.getFrame());
	}
}
