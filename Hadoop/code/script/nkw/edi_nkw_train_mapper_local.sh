#!/bin/bash

#处理一个map任务，输入是blocks中作为测试集的文件名，输出

echo ">>>START AT `hostname`."
start_time=$(date +%s)

echo "mapper running ..."

read testfile

echo "testfile=$testfile"

if [ -z "$testfile" ];then
	echo "input testfile is empty!! exit 0, sikp this."
	exit 0;
fi

echo "test file is $testfile"

#for no in {0..9}
#do
#	if [ $no != $testfile ];then
#		echo "is $no"
#	fi
#done

workdir=/opt/running/edi/nkw/tmp/$testfile

if [ -d $workdir ];then
	rm -r $workdir/*
else
	mkdir -p $workdir	
fi

cd $workdir

pwd

echo "0.get train & test file is $testfile"
/opt/running/hadoop-2.6.0/bin/hdfs dfs -get "/edi/nkw/crf_template" "/edi/nkw/blocks/" $workdir
echo $?

echo "1.merging train files to train_$testfile ..."
#hadoop jar /opt/running/hadoop-2.6.0/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar \
#    -input "/edi/nkw/blocks/block0[^$testfile]*" \
#    -mapper cat \
#    -output /edi/nkw/train/train_$testfile \
#    -reducer cat;

cat blocks/*[^$testfile] > train_$testfile
echo $?
#TODO fail skip
echo "time cost(s) :$(( $(date +%s) - $start_time ))"


echo "2.executting :crf_learn -f 3 -c 1.5 template_file train_file model_file"
/usr/local/bin/crf_learn -c 100 crf_template train_$testfile model_$testfile > crf_log_$testfile
echo $?
#TODO fail skip
echo "time cost(s) :$(( $(date +%s) - $start_time ))"

echo "3.executting :crf_test -m model_file test_files"
/usr/local/bin/crf_test -m model_$testfile blocks/$testfile > result_$testfile
echo $?
#TODO fail skip

echo "4.put result to hdfs."
/opt/running/hadoop-2.6.0/bin/hdfs dfs -put result_$testfile /edi/nkw/result/
echo $?

echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE."

