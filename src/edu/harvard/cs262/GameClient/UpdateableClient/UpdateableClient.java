package edu.harvard.cs262.GameClient.UpdateableClient;

import edu.harvard.cs262.GameServer.GameServer;
import edu.harvard.cs262.GameClient.SimpleClient.SimpleClient;
import edu.harvard.cs262.DistributedGame.GameDisplay;
import edu.harvard.cs262.DistributedGame.GameInputParser;
import edu.harvard.cs262.DistributedGame.GameRequestThread;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

/**
 * Extends SimpleClient to allow updating without sending input.
 * This allows another thread to periodically ping the server and
 * render the latest game state even if the client is not sending commands.
 */
public class UpdateableClient extends SimpleClient {
    private long currentFrame;
    GameRequestThread thread;

    /**
     * In addition to initializing a peer request thread, this method
     * starts a thread that simply polls the master server for the latest
     * snapshot.
     * 
     * @see GameRequestThread
     */
    public UpdateableClient(GameDisplay display, GameInputParser inputParser, GameServer master) {
        super(display,inputParser,master);
        this.currentFrame = 0;
        this.thread = new GameRequestThread(master,this);
        this.thread.start();
    }

    /**
     * Given a snapshot, renders snapshot without needing to send a command.
     * Synchronized to avoid rendering race conditions. Rejects input
     * snapshot if its game frame is earlier than the client's current frame.
     * 
     * @param snapshot A {@link GameSnapshot} to be rendered.
     */
    public synchronized void updateDisplay(GameSnapshot snapshot) {
        if (snapshot == null)
            return;
        if (snapshot.getFrame() > this.currentFrame) {
            this.display.render(snapshot);
        }
    }
}
