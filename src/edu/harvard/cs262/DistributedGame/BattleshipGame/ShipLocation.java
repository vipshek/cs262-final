package edu.harvard.cs262.DistributedGame.BattleshipGame;

public class ShipLocation {
	public Position pos;
	public Direction dir;
	public ShipLocation(Position pos, Direction dir) {
		this.pos = pos;
		this.dir = dir;
	}

	@Override public String toString() {
		String s = "";

		if (dir == Direction.HORIZONTAL)
			s += "Horizontal: ";
		else
			s += "Vertical: ";

		s += "(" + pos.row + ", " + pos.column + ")";

		return s;
	}
}