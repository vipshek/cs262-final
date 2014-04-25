package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameCommand;

public class BattleshipInputParser implements GameInputParser {
	public BattleshipCommand parseInput(String input) {
		if (input.equals("UP")) {
			return new BattleshipCommand(true);
		} else if (input.equals("DOWN")) {
			return new VotingCommand(false);
		} else {
			return null;
		}
	}
}