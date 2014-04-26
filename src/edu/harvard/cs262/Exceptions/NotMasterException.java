package edu.harvard.cs262.Exceptions;

import edu.harvard.cs262.ClusterServer.ClusterServer;
import edu.harvard.cs262.GameServer.GameServer;

import java.lang.Exception;

public class NotMasterException extends Exception {

  private ClusterServer master;

  public NotMasterException(GameServer master) {
    this.master = master;
  }

  public ClusterServer getMaster() {
    return this.master;
  }
}
