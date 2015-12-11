#!/bin/bash

#1.数据交叉，为训练提供不同组合的数据
#2.发起MR

source /home/edi/.bashrc

cd /opt/running/edi/nkw/
echo "START.$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

echo "running ..."
echo "INFO:run edi_nkw_rewrite_comms.sh"
./edi_nkw_rewrite_comms.sh `pwd`/data/blocks/
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:exec edi_nkw_rewrite_comms.sh failed.exit $ecode"
	exit $ecode
fi


#1.将本地block 覆盖到hdfs,初始化计算环境
/opt/running/hadoop-2.6.0/bin/hdfs dfs -rm -r -skipTrash /edi/nkw/blocks/* /edi/nkw/result/* 
/opt/running/hadoop-2.6.0/bin/hdfs dfs -put data/blocks/block* /edi/nkw/blocks/
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:init failed.exit $ecode"
	exit $ecode
fi


#2.发起crf训练的MR
#bcount="`ls blocks/block* |cat |wc -l`"
#echo "mappers count=$bcount"

#if [ "$bcount" = "" ];then
#	echo "blocks empty! exit 1."
#	exit 1
#fi
echo "INFO:exec hadoop M/R for train new key word."
/opt/running/hadoop-2.6.0/bin/hadoop jar /opt/running/hadoop-2.6.0/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar \
    -D mapreduce.job.reduces=0 \
    -D mapreduce.job.maps=10 \
    -D mapreduce.task.timeout=3600000 \
    -D mapreduce.map.memory.mb=1200 \
    -D mapreduce.tasktracker.map.tasks.maximum=1 \
    -files edi_nkw_train_mapper.sh \
    -input /edi/nkw/bfilenames \
    -mapper edi_nkw_train_mapper.sh \
    -output "/edi/nkw/tmp/train_$cur_date";
    #-output "/edi/nkw/tmp/train_$cur_date" -verbose ;

ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:M/R train failed.exit $ecode"
	exit $ecode	
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"




#3.filter new keywords
echo "INFO:filter new keywords."
cd /opt/running/edi/op/
rm -r tmp/result/

/opt/running/hadoop-2.6.0/bin/hdfs dfs -get /edi/nkw/result tmp/
java -classpath lib/word2vec.jar:lib/ansj_seg-2.0.8.jar:lib/nlp-lang-1.0.jar:lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar: \
	com.zhongyitech.edi.NLP.test.ToNewWords \
	tmp/result/ 0.5;
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:filter and merge failed.exit $ecode"
	exit $ecode
fi

#4.merge
echo "INFO:merge to dicts"
cat dicts/newwords.txt >> dicts/dict2.txt
mv dicts/newwords.txt "dicts/newwords.txt$cur_date"
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:merge to dicts failed,exit $ecode"
	exit $ecode
fi

echo "DONE."
echo "time cost(s) :$(( $(date +%s) - $start_time ))"

/opt/running/hadoop-2.6.0/bin/hdfs dfs -cat "/edi/nkw/tmp/train_$cur_date""/*" >> log/edi_nkw_do_train.log 
