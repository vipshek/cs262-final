package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.*;

public class BattleshipSquare extends Button {
	public x;
	public y;

	public BattleshipSquare(int x, int y, String text) {
		super(text);
		this.x = x;
		this.y = y;
	}
}