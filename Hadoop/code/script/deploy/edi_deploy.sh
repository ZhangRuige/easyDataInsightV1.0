#!/bin/bash
##
# 本脚本用来对已经构建编译成功的文件的部署，在完后edi所有编译打包后被执行。
# 本脚步只负责将指定的文件或目录copy到指定目标位置，不负责对被复制文件的检验。
##

## 0.init
JENKINS_EDI_WD=/var/lib/jenkins/workspace/edi/default		#edi
JENKINS_SPD_WD=/var/lib/jenkins/workspace/edi_crawl/default	#spider
RUNNING_EDI_DIR=hadoop@hadoopnamenode:/opt/running/edi	#edi 部署路径
RUNNING_SPD=hadoop@crawljd					#edi 部署路径

############################# 1.deploy edi(Hadoop,NLP) #############################
#1) edi script overwrite
sudo scp -r $JENKINS_EDI_WD/Hadoop/code/script/edi/* $RUNNING_EDI_DIR/
sudo scp -r $JENKINS_EDI_WD/NLP/code/edi_nlp_common/corpus $RUNNING_EDI_DIR/op/
sudo scp -r $JENKINS_EDI_WD/NLP/code/edi_nlp_common/dicts $RUNNING_EDI_DIR/op/
sudo scp -r $JENKINS_EDI_WD/NLP/code/edi_nlp_common/library $RUNNING_EDI_DIR/op/
sudo scp -r $JENKINS_EDI_WD/NLP/code/edi_nlp_common/model $RUNNING_EDI_DIR/op/

#2) edi jars overwrite
sudo scp $JENKINS_EDI_WD/Hadoop/code/edi_hadoop_common/dist/*.jar $RUNNING_EDI_DIR/op/lib/
sudo scp $JENKINS_EDI_WD/Hadoop/code/edi_nlp_common/dist/*.jar $RUNNING_EDI_DIR/op/lib/
sudo scp $JENKINS_EDI_WD/Hadoop/code/edi_hadoop_common/lib/*.jar $RUNNING_EDI_DIR/op/lib/
sudo scp $JENKINS_EDI_WD/Hadoop/code/edi_nlp_common/lib/*.jar $RUNNING_EDI_DIR/op/lib/

#3) Mysql database backup script overwrite	
sudo scp -r $JENKINS_EDI_WD/Hadoop/code/script/db_bk hadoop@hadoopmysql:./1


############################# 2.deploy spider #############################
#1) nutch-crawl 
sudo tar -czf $JENKINS_SPD_WD/nutch-crawl/runtime $JENKINS_SPD_WD/nutch-crawl/dist/runtime.tar
sudo scp $JENKINS_SPD_WD/nutch-crawl/dist/runtime.tar $RUNNING_SPD_DIR:./soft/
sudo ssh $RUNNING_SPD_DIR tar -zxf ./soft/runtime.tar ./soft/

#2) ParseCrawlData
sudo tar -czf $JENKINS_SPD_WD/ParseCrawlData/ParseCrawl $JENKINS_SPD_WD/ParseCrawlData/ParseCrawl.tar
sudo scp $JENKINS_SPD_WD/ParseCrawlData/ParseCrawl.tar $RUNNING_SPD_DIR:./soft/
sudo ssh $RUNNING_SPD_DIR tar -zxf ./soft/ParseCrawl.tar ./soft/
