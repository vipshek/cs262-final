package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameInputParser;

/**
 * A BattleshipInputParser takes input from the client and parses
 * it into a {@link BattleshipCommand}
 */
public class BattleshipInputParser implements GameInputParser {

    /**
     * parseInput takes the input string from the client and parses
     * out the separate x and y coordinate components
     * @param  input A string that contains the x and y coordinates of
     *         the spot on the Battleship grid that was pressed
     * @return a {@link BattleshipCommand} that represents that spot
     *         on the Battleship grid
     */
    public BattleshipCommand parseInput(String input) {
        int x = input.charAt(0) - 'A';
        int y = input.charAt(1) - '0';
        return new BattleshipCommand(x,y);
    }
}
