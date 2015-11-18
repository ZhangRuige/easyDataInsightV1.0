#!/bin/sh
#######
#start edi hadoop
#######


echo "\n1.starting namenode/resourcemanager/nodemanager on HadoopNamenode...:"
ssh HadoopNamenode /opt/running/hadoop-2.6.0/sbin/hadoop-daemon.sh start namenode
ssh HadoopNamenode /opt/running/hadoop-2.6.0/sbin/yarn-daemon.sh start resourcemanager
ssh HadoopNamenode /opt/running/hadoop-2.6.0/sbin/yarn-daemon.sh start nodemanager


echo "\n2.starting secondarynamenode/datanode/nodemanager on HadoopDatanode1...:"
ssh HadoopDatanode1 /opt/running/hadoop-2.6.0/sbin/hadoop-daemon.sh start secondarynamenode
ssh HadoopDatanode1 /opt/running/hadoop-2.6.0/sbin/hadoop-daemon.sh start datanode
ssh HadoopDatanode1 /opt/running/hadoop-2.6.0/sbin/yarn-daemon.sh start nodemanager

echo "\n3.starting datanode/nodemanager on HadoopDatanode2...:"
ssh HadoopDatanode2 /opt/running/hadoop-2.6.0/sbin/hadoop-daemon.sh start datanode
ssh HadoopDatanode2 /opt/running/hadoop-2.6.0/sbin/yarn-daemon.sh start nodemanager