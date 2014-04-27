package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.Table;
import com.googlecode.lanterna.gui.component.Panel;

public class BattleshipWindow extends Window {
	private UpdateableClient client;
	public Interactable focusedBox;
	public Table table;

	public BattleshipWindow(UpdateableClient client) {
		super("Battleship!");
		this.client = client;
		this.focusedBox = null;
		Panel mainPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORISONTAL);
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
		setFocus(firstButton);
	}

	public void sendInput(int row, int column) {
		String s = Character.toString((char) (row + 'A')) + Character.toString((char) (column + '0'));
		client.sendInput(s);
	}
}