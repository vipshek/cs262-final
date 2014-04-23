package edu.harvard.cs262.DistributedGame;
import java.io.Serializable;

public interface GameSnapshot extends Serializable {
	public long getFrame();
}