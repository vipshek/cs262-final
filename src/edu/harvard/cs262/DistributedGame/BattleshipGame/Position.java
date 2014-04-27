package edu.harvard.cs262.DistributedGame.BattleshipGame;
import java.io.Serializable;

public class Position implements Serializable {
        
    public int row;
    public int column;
        
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
}