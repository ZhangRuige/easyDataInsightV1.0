#!/bin/bash

echo "############################# 0.init #############################"
RUNNING_EDI='hadoop@hadoopnamenode'		#edi
RUNNING_SPD='hadoop@crawljd'			#edi deploy host
HADOOPMYSQL='hadoop@hadoopmysql'

JENKINS_EDI_WD=/var/lib/jenkins/workspace/edi/default		#edi
JENKINS_SPD_WD=/var/lib/jenkins/workspace/edi_crawl/default	#spider
RUNNING_EDI_DIR=$RUNNING_EDI:/opt/running/edi				#edi deploy

echo "############################# 1.deploy edi(Hadoop,NLP) #############################"
cd $JENKINS_EDI_WD
echo "#1) edi script overwrite..."
scp -r Hadoop/code/script/edi/* $RUNNING_EDI_DIR/
scp -r NLP/code/edi_nlp_common/corpus $RUNNING_EDI_DIR/op/
scp -r NLP/code/edi_nlp_common/dicts $RUNNING_EDI_DIR/op/
scp -r NLP/code/edi_nlp_common/library $RUNNING_EDI_DIR/op/
scp -r NLP/code/edi_nlp_common/model $RUNNING_EDI_DIR/op/
ssh $RUNNING_EDI chmod -R +x /opt/running/edi

echo "#2) edi jars overwrite..."
scp -r Hadoop/code/edi_hadoop_common/lib $RUNNING_EDI_DIR/op/
scp -r NLP/code/edi_nlp_common/lib/* $RUNNING_EDI_DIR/op/lib/
scp -r Hadoop/code/edi_hadoop_common/dist/*.jar $RUNNING_EDI_DIR/op/lib/
scp -r NLP/code/edi_nlp_common/dist/*.jar $RUNNING_EDI_DIR/op/lib/

echo "#3) Mysql data backup script overwrite..."
scp -r Hadoop/code/script/db_bk $HADOOPMYSQL:./
ssh $HADOOPMYSQL chmod -R +x ./db_bk

echo "############################# 2.deploy spider #############################"
echo "#1) nutch-crawl overwrite..."
cd $JENKINS_SPD_WD/nutch-crawl
tar -czf ~/runtime.tar runtime
scp ~/runtime.tar $RUNNING_SPD:./soft/
ssh $RUNNING_SPD "cd soft;tar -xf runtime.tar"

echo "#2) ParseCrawlData overwrite..."
cd $JENKINS_SPD_WD/ParseCrawlData
tar -cf ~/ParseCrawl.tar ParseCrawl
scp ~/ParseCrawl.tar $RUNNING_SPD:./soft/
ssh $RUNNING_SPD "cd soft;tar -xf ParseCrawl.tar"


