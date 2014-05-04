package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.Table;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.gui.Interactable;

public class BattleshipWindow extends Window {
    private UpdateableClient client;
    public Interactable focusedBox;
    public Table table;
    public Label[] shipLabels;
    public Panel shipLabelPanel;

    public BattleshipWindow(UpdateableClient client) {
        super("Battleship!");
        this.client = client;
        this.focusedBox = null;
        Panel mainPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        addComponent(mainPanel);
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

        shipLabels = new Label[5];
        shipLabels[0] = new Label("Patrol (2)");
        shipLabels[1] = new Label("Destroyer (3)");
        shipLabels[2] = new Label("Submarine (3)");
        shipLabels[3] = new Label("Battleship (4)");
        shipLabels[4] = new Label("Carrier (5)");

        shipLabelPanel = new Panel("Remaining Ships",new Border.Standard(),Panel.Orientation.HORISONTAL);
        
        for (int i = 0; i < 5; i++)
            shipLabelPanel.addComponent(shipLabels[i]);

        mainPanel.addComponent(shipLabelPanel);

        setFocus(firstButton);
    }

    public void sendInput(int row, int column) {
        String s = Character.toString((char) (row + 'A')) + Character.toString((char) (column + '0'));
        client.sendInput(s);
    }

    // Making left and right navigation work, because the library doesn't
    @Override
    public void onKeyPressed(Key key) {
        boolean moved = false;
        if (focusedBox != null) {
            Interactable.Result result = focusedBox.keyboardInteraction(key);
            if (result == Interactable.Result.NEXT_INTERACTABLE_RIGHT) {
                Interactable nextItem = focusedBox;
                for (int i = 0; i < 10; i++) {
                    nextItem = table.nextFocus(nextItem);
                }
                setFocus(nextItem, Interactable.FocusChangeDirection.RIGHT);
                moved = true;
            }
            else if (result == Interactable.Result.PREVIOUS_INTERACTABLE_LEFT) {
                Interactable prevItem = focusedBox;
                for (int i = 0; i < 10; i++) {
                    prevItem = table.previousFocus(prevItem);
                }
                setFocus(prevItem, Interactable.FocusChangeDirection.LEFT);
                moved = true;
            }
        }
        if (!moved)
            super.onKeyPressed(key);
    }
}
