package edu.harvard.cs262.DistributedGame.BattleshipGame;
package edu.harvard.cs262.GameClient.UpdateableClient.UpdateableClient;
import com.googlecode.lanterna.gui.*;

public class BattleshipWindow extends Window {
	private UpdateableClient client;
	public Interactable focusedBox;
	public Table table;

	public BattleshipWindow(UpdateableClient client) {
		super("Battleship!");
		this.client = client;
		this.focusedBox = null;
		Panel mainPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORIZONTAL);
		addComponent(mainPanel);
		table = new Table(10);
		for (int i = 0; i < 10; i++) {
			BattleshipSquare[] buttons = new BattleshipSquare[10];
			for (int j = 0; j < 10; j++) {
				buttons[j] = new BattleshipSquare(i, j, " ");
			}
			table.addRow(buttons);
		}
		mainPanel.addComponent(table);
	}

	public void sendInput(int x, int y) {
		String s = Character.toString(((char) x + 'A')) + Character.toString(((char) y + '0'));
		client.sendInput(s);
	}
}