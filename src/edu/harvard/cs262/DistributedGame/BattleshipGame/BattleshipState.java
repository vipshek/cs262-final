package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameState;

class BattleshipState implements GameState {
    private int[][] shotsBoard;
    private ShipLocation[] shipLocations;
    private long frameCount;
    private int numHits;
    private int numMisses;

    public BattleshipState(int[][] shotsBoard, ShipLocation[] shipLocations, long frameCount){
        this.shotsBoard = shotsBoard;
        this.shipLocations = shipLocations;
        this.frameCount = frameCount;
        this.numHits = 0;
        this.numMisses = 0;
    }

    public int[][] getShotsBoard() {
        return shotsBoard;
    }

    public ShipLocation[] getShipLocations() {
        return shipLocations;
    }

    public long getFrame() {
        return frameCount;
    }

    public boolean setFrame(long f) {
        if (f < frameCount)
            return false;

        frameCount = f;
        return true;
    }

    @Override
    public String toString() {
        return String.format("BattleshipState - Frame: %d, Hits: %d, Misses: %d", this.frameCount, this.numHits, this.numMisses);
    }
}
