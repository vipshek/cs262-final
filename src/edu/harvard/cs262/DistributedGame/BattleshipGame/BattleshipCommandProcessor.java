package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameCommand;

public class BattleshipCommandProcessor implements GameCommandProcessor {
	private BattleshipCommand command;

	// Clear previous queue of commands
	public void startProcessor() {
		return;
	}

	// Add to the processor's command queue
	public void addCommand(GameCommand command) {
		this.command = (BattleshipCommand) command;
	}

	// Retrieve the command decided upon by the processor
	public GameCommand getCommand() {
		return this.command;
	}
}