package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameCommand;

import java.util.List
import java.util.concurrent.Semaphore

public class BattleshipCommandProcessor implements GameCommandProcessor {
	private List<Position> positions;
	private Position output;
	private final Semaphore available = new  Semaphore(0, true);

	private class CommandUpdate implements Runnable {
		BattleshipCommandProcessor parent;
		public CommandUpdate(BattleshipCommandProcessor parent){
			this.parent = parent;
			output = null;
		}

		public void run() {
			while(true){
				Thread.sleep(250);
				synchronized (positions){
					if(output != null){
						int xSum = 0;
						int ySum = 0;
						for(int i = 0; i < positions.count(); i++){
							xSum += positions.get(i).x;
							ySum += positions.get(i).y;
						}
						if(positions.count() > 0){
							int xPos = xSum / positions.count();
							int yPos = ySum / positions.count();
							Position p = new Position (xPos, yPos);
							this.parent.output = p;
							this.parent.available.release();
						} 
					}
				}
			}

		}
	}


	public BattleshipCommandProcessor() {
		positions = new List<Position> ();
		output = null;
		new Thread(new CommandUpdate(this)).start()
	}

	// Clear previous queue of commands
	public void startProcessor() {
		return;
	}

	// Add to the processor's command queue
	public void addCommand(GameCommand command) {
		BattleshipCommand cmd = (BattleshipCommand) command;
		synchronized(positions) {
			positions.add(cmd.getPosition());
		}
	}

	// Retrieve the command decided upon by the processor
	public GameCommand getCommand() {
		this.available.acquire();
		synchronized(positions){
			this.positions.clear();
			BattleshipCommand cmd = new BattleshipCommand(this.output.x, this.output.y);
			this.output = null;
			return cmd;
		}
	}
}