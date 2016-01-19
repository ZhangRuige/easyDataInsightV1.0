#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START AT `hostname`.$cur_date"
source /etc/profile
echo "mapper running ..."

read testfile

echo "testfile=$testfile"
if [ -z "$testfile" ];then
	echo "input testfile is empty!! exit 0, sikp this."
	exit 0;
fi

echo "pwd=`pwd`"

hdfs dfs -get /edi/nkw/tmp/train_model_a /edi/nkw/blocks/$testfile ./

echo "3.executting :crf_test -m model_file test_files"
/usr/local/bin/crf_test -m train_model_a $testfile > result_$testfile
echo $?
#TODO fail skip

echo "put result to hdfs."
hdfs dfs -put result_$testfile /edi/nkw/result/
echo $?

echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE."

