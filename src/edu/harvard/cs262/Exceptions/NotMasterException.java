package edu.harvard.cs262.Exceptions;

import edu.harvard.cs262.ClusterServer.ClusterServer;
import java.lang.Exception;

public class NotMasterException extends Exception {

	private ClusterServer master;

    public NotMasterException(ClusterServer master) {
        this.master=master;
    }

    public ClusterServer getMaster() {
    	return this.master;
    }
}
