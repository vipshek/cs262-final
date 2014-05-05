package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

import java.util.Random;

/**
 * Stores the data and provides the gameplay logic for a battleship game. 
 * Initializes the game board, randomly generates locations for the ships, 
 * keeps track of shots and misses, and provides other support for games.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class BattleshipGame implements Game {
    final static int NUM_SHIPS = 5;
    final static int BOARD_SIZE = 10;

    // BattleshipState variable that stores all of the information associated with this game
    private BattleshipState state;
    // Boolean matrix to mark each space on the board on which there is a ship
    private boolean[][] shipsBoard;
    // Preset sizes for the ships in a battleship game
    private int[] shipSizes;

    /* Fires a shot at the board at the specified location
     * If the shot was a hit, returns true, else returns false.
     * If the shot was a hit, records the shot on the shotsBoard. */
    private boolean fireShot(Position pos) {
        if (shipsBoard[pos.row][pos.column]) {
            state.getShotsBoard()[pos.row][pos.column] = 2;
            state.addHit();
            return true;
        } else {
            state.getShotsBoard()[pos.row][pos.column] = 1;
            state.addMiss();
            return false;
        }
    }

    /* Constructor for the BattleshipGame */
    public BattleshipGame() {
        state = new BattleshipState(new int[BOARD_SIZE][BOARD_SIZE], new ShipLocation[NUM_SHIPS], 0);
        shipsBoard = new boolean[BOARD_SIZE][BOARD_SIZE];
        shipSizes = new int[]{2, 3, 3, 4, 5};

        Random r = new Random();

        /* Generate each of the ships on the board, making sure that they do not
         * fall off the board or overlap with one another */
        for (int i = 0; i < NUM_SHIPS; i++) {
            // Pick a random direction at first
            Direction dir = Direction.VERTICAL;
            if (r.nextInt(2) == 0)
                dir = Direction.HORIZONTAL;

            // Pick a random location on the board
            int row = r.nextInt(BOARD_SIZE);
            int column = r.nextInt(BOARD_SIZE);

            // Check that the ship is not off the board. If it is, pick a different location
            if ((dir == Direction.HORIZONTAL && column + shipSizes[i] > BOARD_SIZE) || 
                (dir == Direction.VERTICAL && row + shipSizes[i] > BOARD_SIZE)) {
                i--;
                continue;
            }

            // Check that the ship we're currently is not overlapping with any other 
            // ship already on the board. If its, pick a different location.
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

            // This ship is ready to go! Save the location.
            state.getShipLocations()[i] = new ShipLocation(new Position(row, column), dir);

            // Update the matrix of locations with ships on them
            for (int k = 0; k < shipSizes[i]; k++) {
                if (dir == Direction.HORIZONTAL)
                    shipsBoard[row][column + k] = true;
                else
                    shipsBoard[row + k][column] = true;
            }
        }
    }

    /* Execute a command by firing a shot at the board and storing the results of that shot. */
    public long executeCommand(GameCommand command) {
        if (command instanceof BattleshipCommand){
            BattleshipCommand bc = (BattleshipCommand) command;
            fireShot(bc.getPos());
        }

        state.setFrame(state.getFrame() + 1);
        return state.getFrame();
    }

    /* Returns the current game state. */
    public GameState getState() {
        return this.state;
    }

    /* Sets the game state for a Battleship Game. Upon receipt of a game state, set up other
     * bookkeeping variables such as shipsBoard from the Battleship game state. */
    public boolean setState(GameState gameState){
        this.state = (BattleshipState)gameState;

        // Create the ships board from the game state.
        shipsBoard = new boolean[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < NUM_SHIPS; i++) {

            int row = state.getShipLocations()[i].pos.row;
            int column = state.getShipLocations()[i].pos.column;
            Direction dir = state.getShipLocations()[i].dir;

            // Update matrix of locations with ships on them
            for (int k = 0; k < shipSizes[i]; k++) {
                if (dir == Direction.HORIZONTAL)
                    shipsBoard[row][column + k] = true;
                else
                    shipsBoard[row + k][column] = true;
            }
        }

        return true;
    }

    /* Returns a snapshot for the current game. Each time, we must calculate which of the ships on
     * the board have already been sunk. */
    public GameSnapshot getSnapshot() {
        // Informs which of the ships on the board have already been sunk
        boolean[] sunkShips = new boolean[NUM_SHIPS];

        // For each ship, check if it has been sunk
        for (int i = 0; i < NUM_SHIPS; i++) {
            // Get all the squares that the current ship occupies
            Direction dir = state.getShipLocations()[i].dir;
            int rowOrigin = state.getShipLocations()[i].pos.row;
            int columnOrigin = state.getShipLocations()[i].pos.column;

            // If all those squares have been hit, the ship has been sunk
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

    // Run all the tests for this class
    public boolean run_tests() {
        // Test 
        return false;
    }
}
