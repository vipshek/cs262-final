import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.cs262.DistributedGame.VotingGame.VotingGame;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingCommand;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingState;

/**
 * Tests for the Voting Game
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class VotingGameTest {

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

    /**
     * Tests that if a command is sent to increment the voting game
     * variable on a new game where the variable is initialized to 0,
     * the game correctly increments the variable to 1.
     */
	@Test
	public void simpleCommandTest() {
        VotingGame game = new VotingGame(0);
        VotingCommand command = new VotingCommand(true);
        game.executeCommand(command);

        VotingState state = (VotingState) game.getState();

        assertEquals(state.getValue(), 1);
        assertEquals(state.getFrame(), 1);
	}

}
