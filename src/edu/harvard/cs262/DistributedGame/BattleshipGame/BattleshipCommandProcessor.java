package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameCommand;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class BattleshipCommandProcessor implements GameCommandProcessor {
	private ArrayList<Position> positions;
	private Position output;
	private final Semaphore available = new Semaphore(0, true);

	private class CommandUpdate implements Runnable {
		BattleshipCommandProcessor parent;
		public CommandUpdate(BattleshipCommandProcessor parent){
			this.parent = parent;
		}

		public void run() {
			while(true){
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO
				}

				synchronized (positions){
					if(this.parent.output == null){
						int rowSum = 0;
						int columnSum = 0;
						for(int i = 0; i < positions.size(); i++){
							rowSum += positions.get(i).row;
							columnSum += positions.get(i).column;
						}
						if(positions.size() > 0){
							int rowPos = rowSum / positions.size();
							int columnPos = columnSum / positions.size();
							Position p = new Position (rowPos, columnPos);
							this.parent.output = p;
							this.parent.available.release();
						} 
					}
				}
			}

		}
	}


	public BattleshipCommandProcessor() {
		positions = new ArrayList<Position>();
		this.output = null;
		new Thread(new CommandUpdate(this)).start();
	}

	// Add to the processor's command queue
	public void addCommand(GameCommand command) {
		BattleshipCommand cmd = (BattleshipCommand) command;
		synchronized(positions) {
			positions.add(cmd.getPos());
		}
	}

	// Retrieve the command decided upon by the processor
	public GameCommand getCommand() {
		try {
			this.available.acquire();
		} catch (InterruptedException e) {
			// TODO
		}

		synchronized(positions){
			this.positions.clear();
			BattleshipCommand cmd = new BattleshipCommand(this.output.row, this.output.column);
			this.output = null;
			return cmd;
		}
	}
}
