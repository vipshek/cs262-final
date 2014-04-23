package edu.harvard.cs262.Exceptions;

import edu.harvard.cs262.GameServer.GameServer;
import java.lang.Exception;

public class NotMasterException extends Exception {

	private GameServer master;

    public NotMasterException(GameServer master) {
        this.master=master;
    }

    public GameServer getMaster() {
    	return this.master;
    }
}