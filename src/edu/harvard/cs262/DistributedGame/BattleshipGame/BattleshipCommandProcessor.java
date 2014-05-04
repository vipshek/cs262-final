package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommandProcessor;
import edu.harvard.cs262.DistributedGame.GameCommand;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * BattleShipCommandProcessor is a GameCommand Processor for Battleship
 * At a high level, it takes user input which the server has received
 * aggregates it over a quarter of a second, and then produces a position
 * representing where the users have collectively chosen to launch a projectile.(Selected
 * Via Average)
 *
 * @author Twitch Plays Battleship Group
 *
 * @version 1.0, April 2014
 */

public class BattleshipCommandProcessor implements GameCommandProcessor {
    /**
     * Positions is a list containing all the user input received since the last frame
     * The BattleshipCommandProcessor uses this both as a literal store of data, but also 
     * as a means of synchronizing the multiple threads adding user commands and requesting
     * output
     **/
    private ArrayList<Position> positions;
    /**
     * This is the output position which is packaged as an {@link GameCommand} and given back to the 
     * Server once getCommand returns/unblocks. This variable being null means that a new output has
     * to be generated for the next frame. While this variable is not null, the BattleShipCommandProcessor
     * does not actually produce new output. The rationale for this being that we want to bind users to 
     * inputing commands for only one frame at a time, and not pre-load commands for frames yet to come
     * if the rest of the server is somehow slow or unresponsive
     **/
    private Position output;
    /**
     ** @see #getCommand
     ** @see CommandUpdate
     ** This semaphore represents whether or not output is available to a server calling getCommand.
     ** Thus, it is used as a barrier. The server blocks on this semaphore when it calls getCommand, and 
     ** unblocks when released by the {@link CommandUpdate}, allowing the processor to move onto a new frame
     ** and also allowing the server which called {@link #getCommand} to return and reset position
     **/
    private final Semaphore available = new Semaphore(0, true);

    /**
     ** CommandUpdate is a class representing the thread which actually aggregates the user input and waits until
     ** the specified time for client input in a frame (hardcoded as .25 seconds in this game, but this could easily
     ** be parameterized or changed). The basic strategy is that CommandUpdate synchronizes upon positions variable
     ** in the parent of this class and sleeps until we have reached our timeout, and then compute the average of all the 
     ** points before we set that as our output
     **/
    private class CommandUpdate implements Runnable {
        BattleshipCommandProcessor parent;
        public CommandUpdate(BattleshipCommandProcessor parent){
            this.parent = parent;
        }
        /**
         * The actual running thread. This handles averaging the user input
         * and producing the output which the server application uses to calculate the 
         * next frame
         * @see BattleShipCommandProcessor#getCommand
        **/
        public void run() {
            //This thread runs forever
            while(true){
                try {
                    //we sleep for a quarter of a second
                    Thread.sleep(250);
                //We ignore being woken up early
                } catch (InterruptedException e) {
                }
                //We synch on our positions
                synchronized (positions){
                    //It we don't have any sort of input
                    //We compute the average of all of our user inputs
                    if(this.parent.output == null){
                        int rowSum = 0;
                        int columnSum = 0;
                        for(int i = 0; i < positions.size(); i++){
                            rowSum += positions.get(i).row;
                            columnSum += positions.get(i).column;
                        }
                        //If We actually had any user input at all
                        if(positions.size() > 0){
                            int rowPos = rowSum / positions.size();
                            int columnPos = columnSum / positions.size();
                            //Now we add this to our output 
                            Position p = new Position (rowPos, columnPos);
                            this.parent.output = p;

                            /** Unblock any threads which are waiting on input in {@link BattleCommandProcessor#getCommand}
                             */
                            this.parent.available.release();
                        } 
                    }
                }
            }

        }
    }
    /**
     * BattleshipCommandProcessor Constructor
     * This launches the CommandUpdate thread and sets up the instance variables
     * for the command processor
     **/
    public BattleshipCommandProcessor() {
        positions = new ArrayList<Position>();
        this.output = null;
        new Thread(new CommandUpdate(this)).start();
    }

    // Add to the processor's command queue
    // This method is synchronized
    public void addCommand(GameCommand command) {
        BattleshipCommand cmd = (BattleshipCommand) command;
        synchronized(positions) {
            positions.add(cmd.getPos());
        }
    }

    /**
     * 
     * @see CommandUpdate
     ** This attempts to wait on the object's semaphore, which will be unblocked when there is input for calling Server
     ** Once we unblock, we graph the position, use it to make a BattleShipCommand, and then return after setting the output
     ** variable to null
     **/
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
