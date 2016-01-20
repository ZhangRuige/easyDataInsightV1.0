#!/bin/sh
#######
# make(compile|jar) and deploy edi FROM jenkins
#######

## 0.init
DEPLORY_BASE_DIR=/opt/running/edi/
JENKINS_WD=/var/lib/jenkins/workspace/edi/default

## 1.make jar com.zhongyitech.edi.NLP.omsa-v1.25.jar
sudo ant make-jar
ecode=$?
if [ $ecode -ne 0 ];then
	echo "make-jar NLP failed.exit $ecode"
	exit $ecode
fi

## 2.make jar com.zhongyitech.edi.hive.udf.donlp-1.1.jar
cd $JENKINS_WD
sudo ant make-jar
ecode=$?
if [ $ecode -ne 0 ];then
	echo "make-jar HADOOP failed.exit $ecode"
	exit $ecode
fi



## 3.deploy
sudo scp $JENKINS_WD/Hadoop/code/edi_hadoop_common/dist/*.jar $DEPLORY_BASE_DIR/op/lib/
sudo scp $JENKINS_WD/Hadoop/code/edi_nlp_common/dist/*.jar $DEPLORY_BASE_DIR/op/lib/

sudo scp $JENKINS_WD/Hadoop/code/edi_hadoop_common/lib/*.jar $DEPLORY_BASE_DIR/op/lib/
sudo scp $JENKINS_WD/Hadoop/code/edi_nlp_common/lib/*.jar $DEPLORY_BASE_DIR/op/lib/

