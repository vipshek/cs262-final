package edu.harvard.cs262.DistributedGame.BattleshipGame;
import edu.harvard.cs262.DistributedGame.GameState;

class BattleshipState implements GameState {
	private int[][] shotsBoard;
	private ShipLocation[] shipLocations;
	private long frameCount;

	public BattleshipState(int[][] shotsBoard, ShipLocation[] shipLocations, long frameCount){
		this.shotsBoard = shotsBoard;
		this.shipLocations=shipLocations;
		this.frameCount = frameCount;
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

    // XXX counting hits/misses should be handled in private vars with state accessing methods To: R.J.; From: R.J.
    @Override
    public String toString() {
        int numHits = 0;
        int numMisses = 0;
        for (int i = 0; i < shotsBoard.length; i++) {
            for (int j = 0; j < shotsBoard[0].length; j++) {
                if (shotsBoard[i][j] == 1)
                    numMisses++;
                else if (shotsBoard[i][j] == 2)
                    numHits++;
            }
        }

        return String.format("BattleshipState - Frame: %d, Hits: %d, Misses: %d", this.frameCount, numHits, numMisses);
    }
}
