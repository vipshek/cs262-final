import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.cs262.DistributedGame.BattleshipGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.BattleshipGame.BattleshipCommand;
import edu.harvard.cs262.DistributedGame.BattleshipGame.BattleshipSnapshot;
import edu.harvard.cs262.DistributedGame.BattleshipGame.BattleshipState;
import edu.harvard.cs262.DistributedGame.BattleshipGame.Direction;

/**
 * Tests for the BattleshipGame
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class BattleshipGameTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    /**
     * Tests that after the board is created, there are 17 spaces that
     * ships should occupy. Ensures the ships do not overlap and are
     * all present.
     */
    public void boardTest() {
        BattleshipGame game = new BattleshipGame();

        int num_squares = 0;
        for (int i = 0; i < BattleshipGame.BOARD_SIZE; i++) {
            for (int j = 0; j < BattleshipGame.BOARD_SIZE; j++) {
                if (game.shipsBoard[i][j]) {
                    num_squares++;
                }
            }
        }

        BattleshipState state = (BattleshipState)game.getState();
        assertEquals(17, num_squares);
    }


    @Test
    /**
     * Tests that if every ship's location is hit with a command,
     * every ship is marked as sunk at the end.
     */
    public void sunkTest() {
        BattleshipGame game = new BattleshipGame();

        BattleshipState state = (BattleshipState)game.getState();

        for (int i = 0; i < BattleshipGame.NUM_SHIPS; i++) {
            Direction dir = state.getShipLocations()[i].dir;
            int rowOrigin = state.getShipLocations()[i].pos.row;
            int columnOrigin = state.getShipLocations()[i].pos.column;

            for (int k = 0; k < game.shipSizes[i]; k++) {
                if (dir == Direction.HORIZONTAL)
                    game.executeCommand(new BattleshipCommand(rowOrigin, columnOrigin + k));
                else
                    game.executeCommand(new BattleshipCommand(rowOrigin + k, columnOrigin));
            }

            BattleshipSnapshot snapshot = (BattleshipSnapshot)game.getSnapshot();
            boolean[] sunkShips = snapshot.getSunkShips();
            assert(sunkShips[i]);
        }
    }
}
