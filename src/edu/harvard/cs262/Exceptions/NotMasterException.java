package edu.harvard.cs262.Exceptions;

import edu.harvard.cs262.GameServer.GameServer;

import java.lang.Exception;

/**
 *	This is a class for our own exception NotMasterException. This exception is thrown
 *  when a call is made to a {@link GameServer} but not to the master
 *	{@link GameServer}. The invoking agent should take this exception and redirect
 *	its method to the actual master.
 *
 *	
 */

public class NotMasterException extends Exception {

  private GameServer master;

  /**
   *  This is the implementation of the NotMasterException class. It sets this 
   *  exception's master to be the input {@link GameServer} master.
   *
   */
  public NotMasterException(GameServer master) {
    this.master = master;
  }

  /**
   *  This method is just used to get the master as far as this exception is aware.
   *
   */  
  public GameServer getMaster() {
    return this.master;
  }
}
