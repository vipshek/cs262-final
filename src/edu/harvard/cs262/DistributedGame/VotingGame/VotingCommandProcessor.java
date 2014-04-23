package edu.harvard.cs262.DistributedGame.VotingGame;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;

public class VotingCommandProcessor implements GameCommandProcessor {
	private VotingCommand command;

	// Clear previous queue of commands
	public void startProcessor() {
		return;
	}

	// Add to the processor's command queue
	public void addCommand(VotingCommand command) {
		this.command = command;
	}

	// Retrieve the command decided upon by the processor
	public GameCommand getCommand() {
		return this.command;
	}
}