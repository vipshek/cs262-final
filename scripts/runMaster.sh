java -cp ../src -Djava.rmi.server.codebase=http://10.251.41.127:8888/cs262.jar -Djava.security.policy=server.policy -Djava.rmi.server.useCodebaseOnly=false edu.harvard.cs262.ClusterServer.BasicClusterServer.BasicMaster $@
