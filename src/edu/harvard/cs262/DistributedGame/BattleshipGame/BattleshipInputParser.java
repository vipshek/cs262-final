package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameCommand;

public class BattleshipInputParser implements GameInputParser {
	public BattleshipCommand parseInput(String input) {
		int x = input.charAt(0) - 'A';
		int y = input.charAt(1) - '0';
		return new BattleshipCommand(x,y);
	}
}