#!/bin/bash

#1.数据交叉，为训练提供不同组合的数据
#2.发起MR
source /etc/profile
cd /opt/running/edi/nkw/
echo "START.$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

echo "running ..."
echo "INFO:run edi_nkw_rewrite_comms.sh"
./edi_nkw_rewrite_comms.sh
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:exec edi_nkw_rewrite_comms.sh failed.exit $ecode"
	exit $ecode
fi

mv data/blocks/block_1 data/blocks/_block_1
train_block_a=_block_1
train_block_b=block_0

#0.crf_learn训练得到模型train_model_a
echo "INFO:executting :crf_learn -f 3000 -c 2 template_file $train_block_a model_file"
rm -rf tmp/result/* tmp/bfilenames tmp/train_model_*
/usr/local/bin/crf_learn -c 2 -f 3000 data/crf_template data/blocks/$train_block_a tmp/train_model_a > /dev/null

#3.crf_learn训练得到模型train_model_b,用train_model_b对train_block_a做测试
echo "executting :crf_learn -f 3000 -c 2 template_file $train_block_b model_file"
echo "executting :crf_test -m train_model_b $train_block_a"
/usr/local/bin/crf_learn -c 2 -f 3000 data/crf_template data/blocks/$train_block_b tmp/train_model_b > /dev/null && /usr/local/bin/crf_test -m tmp/train_model_b data/blocks/$train_block_a > tmp/result/result_$train_block_a &   # !!! run in background


#1.初始化计算环境:将本地blocks\train_model_a\bfilenames 覆盖到hdfs
hdfs dfs -rm -r -skipTrash /edi/nkw/blocks/* /edi/nkw/result/* /edi/nkw/tmp/bfilenames /edi/nkw/tmp/train_model_a
hdfs dfs -put data/blocks/[^_]* /edi/nkw/blocks/ 
ls -1 data/blocks/[^_]* | cut -d '/' -f3 > tmp/bfilenames && hdfs dfs -put tmp/bfilenames tmp/train_model_a /edi/nkw/tmp/
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:init failed.skip code=$ecode"
	exit $ecode
fi
	
bcount=`cat tmp/bfilenames |wc -l`
echo "mappers count=$bcount"
if [ $bcount -eq 0 ];then
	echo "$line/blocks count = 0 skip ."
	exit $ecode
fi
let "bcount-=1"  #except train block

#2.发起crf_test的MR
echo "INFO:exec hadoop M/R for train new keywords."
hadoop jar /opt/running/hadoop-2.6.0/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar \
	-D mapreduce.job.reduces=0 \
	-D mapreduce.job.maps=$bcount \
	-D mapreduce.task.timeout=3600000 \
	-D mapreduce.tasktracker.map.tasks.maximum=1 \
	-D mapreduce.map.memory.mb=1224 \
	-D mapred.child.java.opts=-Xmx600M \
	-files edi_nkw_train_test_mapper.sh \
	-input /edi/nkw/tmp/bfilenames \
	-mapper edi_nkw_train_test_mapper.sh \
	-output "/edi/nkw/tmp/train_$cur_date";
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:M/R train failed.exit code=$ecode"
	exit $ecode
fi
hdfs dfs -get /edi/nkw/result/* tmp/result/
echo "time cost(s) :$(( $(date +%s) - $start_time ))"


wait 	#important!!!


#4.filter new keywords
echo "INFO:filter new keywords.ToNewWords"
cd /opt/running/edi/op/
java -classpath lib/word2vec.jar:lib/ansj_seg-2.0.8.jar:lib/nlp-lang-1.0.jar:lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar: \
	com.zhongyitech.edi.NLP.test.ToNewWords \
	/opt/running/edi/nkw/tmp/result/ 0.5;
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:filter and merge failed.skip code=$ecode"
	exit $ecode
fi

#5.merge to dicts
echo "INFO:merge to dicts"
#cat dicts/newwords.txt >> dicts/dict2.txt
mv dicts/newwords.txt "dicts/newwords.txt_$cur_date"
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:merge to dicts failed,skip code=$ecode"
	exit $ecode
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"

echo ">>>------------------------------------------->" >> log/edi_nkw_do_train.log
hdfs dfs -cat "/edi/nkw/tmp/train_$cur_date/*" >> log/edi_nkw_do_train.log 


