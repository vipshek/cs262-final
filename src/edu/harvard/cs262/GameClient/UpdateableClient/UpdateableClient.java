package edu.harvard.cs262.GameClient.UpdateableClient;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.GameClient;
import edu.harvard.cs262.GameClient.SimpleClient.SimpleClient;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameRequestThread;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

public class UpdateableClient extends SimpleClient {
	private long currentFrame;
    GameRequestThread thread;

	public UpdateableClient(GameDisplay display, GameInputParser inputParser, GameServer master) {
		super(display,inputParser,master);
		this.currentFrame = 0;
        this.thread = new GameRequestThread(master,this);
        this.thread.start();
	}

	public synchronized void updateDisplay(GameSnapshot snapshot) {
		if (snapshot == null)
			return;
		if (snapshot.getFrame() > this.currentFrame) {
			this.display.render(snapshot);
		}
	}

    public void setMaster(GameServer server) {
      this.master = server;
      this.thread = new GameRequestThread(this.master,this);
      this.thread.start();
    }
}
