package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.Button;

public class BattleshipSquare extends Button {
	public int x;
	public int y;

	public BattleshipSquare(int x, int y, String text) {
		super(text);
		this.x = x;
		this.y = y;
	}
}