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
}
