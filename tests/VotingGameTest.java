/**
 * 
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.cs262.DistributedGame.VotingGame.VotingGame;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingCommand;
import edu.harvard.cs262.DistributedGame.VotingGame.VotingState;
/**
 * @author rjaquino
 *
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

	@Test
	public void test() {
        VotingGame game = new VotingGame(0);
        VotingCommand command = new VotingCommand(true);
        game.executeCommand(command);

        VotingState state = (VotingState) game.getState();

        assertEquals(state.getValue(), 1);
        assertEquals(state.getValue(), 1);
	}

}
