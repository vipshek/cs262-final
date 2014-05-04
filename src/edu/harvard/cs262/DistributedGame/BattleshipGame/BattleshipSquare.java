package edu.harvard.cs262.DistributedGame.BattleshipGame;
import com.googlecode.lanterna.gui.component.Button;

public class BattleshipSquare extends Button {
    public int row;
    public int column;

    public BattleshipSquare(int row, int column, String text) {
        super(text);
        this.row = row;
        this.column = column;
    }
}
