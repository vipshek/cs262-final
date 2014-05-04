package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameCommand;

class BattleshipCommand implements GameCommand {
    private Position pos;

    public BattleshipCommand(int row, int column){
        this.pos = new Position(row,column);
    }

    public Position getPos() {
        return this.pos;
    }
}
