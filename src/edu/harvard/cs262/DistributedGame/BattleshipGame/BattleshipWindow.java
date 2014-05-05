package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.Table;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.gui.Interactable;

/**
 * This class extends the Window compoenent of the Lanterna library
 * to render the Battleship Game on the client side. It is 
 * instantiated by {@link BattleshipDisplay} to render the game.
 * 
 * @author Twitch Plays Battleship Group
 * 
 * @version 1.0, April 2014
 */
public class BattleshipWindow extends Window {
    private UpdateableClient client;
    /**
     * Keep track of the currently focused box, for use in event callback.
     */
    public Interactable focusedBox;
    /**
     * Table containing ship position buttons.
     * @see BattleshipSquare
     */
    public Table table;
    /**
     * Simple text labels that display which ships are still on the board.
     */
    public Label[] shipLabels;
    /**
     * Panel to group together shipLabels.
     */
    public Panel shipLabelPanel;

    /**
     * Creates the window for the Battleship game, including a lanterna
     * Table that represents the board, navigable buttons to fill the grid,
     * and labels that display which ships remain. Event handling is
     * located in {@link BattleshipWindowAdapter}.
     * 
     * @param client An updateable game client, used in the {@link sendInput}
     * method to create a command and send input to server on key press.
     */
    public BattleshipWindow(UpdateableClient client) {
        super("Battleship!");
        this.client = client;
        this.focusedBox = null;

        // Create main panel and add to window
        Panel mainPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        addComponent(mainPanel);

        // Create table, fill with BattleshipSquares and add to main panel
        table = new Table(10);
        BattleshipSquare firstButton = null;
        for (int i = 0; i < 10; i++) {
            BattleshipSquare[] buttons = new BattleshipSquare[10];
            for (int j = 0; j < 10; j++) {
                buttons[j] = new BattleshipSquare(i, j, " ");
                if (i == 0 && j == 0)
                    firstButton = buttons[j];
            }
            table.addRow(buttons);
        }
        mainPanel.addComponent(table);

        // Create label for each ship
        shipLabels = new Label[5];
        shipLabels[0] = new Label("Patrol (2)");
        shipLabels[1] = new Label("Destroyer (3)");
        shipLabels[2] = new Label("Submarine (3)");
        shipLabels[3] = new Label("Battleship (4)");
        shipLabels[4] = new Label("Carrier (5)");

        // Create panel to store ship labels, and add to main panel
        shipLabelPanel = new Panel("Remaining Ships",new Border.Standard(),Panel.Orientation.HORISONTAL);
        for (int i = 0; i < 5; i++)
            shipLabelPanel.addComponent(shipLabels[i]);
        mainPanel.addComponent(shipLabelPanel);

        // Set window's focus on the top-left button
        setFocus(firstButton);
    }

    /**
     * Wrapper for client's sendInput function. This allows the 
     * {@link BattleshipWindowAdapter} to pass input to the client.
     */
    public void sendInput(int row, int column) {
        String s = Character.toString((char) (row + 'A')) + Character.toString((char) (column + '0'));
        client.sendInput(s);
    }

    /**
     * This method overrides the lanterna window onKeyPressed method,
     * because the library does not support navigating left or right on
     * the grid.
     */
    @Override
    public void onKeyPressed(Key key) {
        // Keep track of whether we've moved
        boolean moved = false;

        if (focusedBox != null) {
            Interactable.Result result = focusedBox.keyboardInteraction(key);
            // Handle moving right
            if (result == Interactable.Result.NEXT_INTERACTABLE_RIGHT) {
                Interactable nextItem = focusedBox;
                for (int i = 0; i < 10; i++) {
                    nextItem = table.nextFocus(nextItem);
                }
                setFocus(nextItem, Interactable.FocusChangeDirection.RIGHT);
                moved = true;
            }
            // Handle moving left
            else if (result == Interactable.Result.PREVIOUS_INTERACTABLE_LEFT) {
                Interactable prevItem = focusedBox;
                for (int i = 0; i < 10; i++) {
                    prevItem = table.previousFocus(prevItem);
                }
                setFocus(prevItem, Interactable.FocusChangeDirection.LEFT);
                moved = true;
            }
        }
        // If we didn't move left or right, just call the parent method
        if (!moved)
            super.onKeyPressed(key);
    }
}
