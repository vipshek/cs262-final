package edu.harvard.cs262.DistributedGame.VotingGame;

import edu.harvard.cs262.DistributedGame.Game;
import edu.harvard.cs262.DistributedGame.GameCommand;
import edu.harvard.cs262.DistributedGame.GameState;


/**
 * The VotingGame class represents and instance of the voting game.
 * It holds the current state of the game, and it has methods for 
 * modifying the state and to run commands.
 *
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
class VotingGame implements Game {
    private VotingState state;

    /**
     * The constructor for a VotingGame object. Sets the initial frame
     * to 0 and the value of the number to the passed-in value.
     * 
     * @param  value  An int that represents the initial value of the game
     * 
     * @return A VotingGame object whose state is on frame 0 with the value
     *         equal to the passed-in value
     */
    public VotingGame(int value) {
        this.state = new VotingState(value, 0);
    }

    /**
     * The executeCommand function takes a command and executes it, updating 
     * the value and frame of the state accordingly.
     * 
     * @param  command  A {@link GameCommand} that represents whether the number
     *         should be incremented or decremented
     *         
     * @return A long that represents the current frame number of the game
     */
    public long executeCommand(GameCommand command) {
        if (command instanceof VotingCommand) {
            VotingCommand vc = (VotingCommand) command;
            if (vc.getVote()) {
                this.state = new VotingState(this.state.getValue()+1, this.state.getFrame()+1);
            } else {
                this.state = new VotingState(this.state.getValue()-1, this.state.getFrame()+1);
            }
        }
        return this.state.getFrame();
    }

    /**
     * Gets the current state of the game.
     * 
     * @return A {@link GameState} that represents the current state of game.
     */
    public GameState getState() {
        return this.state;
    }

    /**
     * Sets the current state of the game with the passed-in state.
     * 
     * @param  state  A {@link GameState} that represents the state of the game
     *         that this instance should be set to.
     *         
     * @return A boolean that is true if the state of the game is successfully set
     *         and false if it isn't successful.
     */
    @Override
    public boolean setState(GameState state) {
        this.state = (VotingState) state;
        return true;
    }

    /**
     * Creates a snapshot of the current game from the current state of the game.
     * 
     * @return A {@link VotingSnapshot} that holds the current value and frame of the game.
     */
    public VotingSnapshot getSnapshot() {
        return new VotingSnapshot(this.state.getValue(), this.state.getFrame());
    }

}
