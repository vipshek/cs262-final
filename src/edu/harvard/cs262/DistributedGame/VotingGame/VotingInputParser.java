package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameCommand;

public class VotingInputParser implements GameInputParser {
	public GameCommand parseInput(String input) {
		if (input.equals("UP")) {
			return new VotingCommand(true);
		} else if (input.equals("DOWN")) {
			return new VotingCommand(false);
		} else {
			return null;
		}
	}
}