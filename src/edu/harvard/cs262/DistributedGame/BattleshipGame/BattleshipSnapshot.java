package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameSnapshot;

class BattleshipSnapshot implements GameSnapshot {
    private int[][] shotsBoard;
    private boolean[] sunkShips;
    private long frame;

    public BattleshipSnapshot(int[][] shotsBoard, boolean[] sunkShips, long frame){
        this.shotsBoard = shotsBoard;
        this.sunkShips=sunkShips;
        this.frame = frame;
    }

    public int[][] getShotsBoard() {
        return shotsBoard;
    }

    public boolean[] getSunkShips() {
        return sunkShips;
    }

    public long getFrame() {
        return frame;
    }
}
