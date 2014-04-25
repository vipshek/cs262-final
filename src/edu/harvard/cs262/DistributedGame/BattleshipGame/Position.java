package edu.harvard.cs262.DistributedGame.BattleshipGame;

public class Position {
        
    public int x;
    public int y;
        
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
        
    public void getX() {
        return this.x;
    }
        
    public void getY() {
        return this.y;
    }
}