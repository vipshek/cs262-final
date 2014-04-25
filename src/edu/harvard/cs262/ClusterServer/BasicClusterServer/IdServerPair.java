// adapted from http://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java
package edu.harvard.cs262.ClusterServer.BasicClusterServer;

import java.util.UUID;
import java.io.Serializable;

import edu.harvard.cs262.ClusterServer.ClusterServer;

public class IdServerPair<UUID, ClusterServer> implements Serializable {
  public final UUID id;
  public final ClusterServer server;

  public IdServerPair(UUID id, ClusterServer server) {
    this.id = id;
    this.server = server;
  }
}


