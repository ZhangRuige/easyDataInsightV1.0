#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT `hostname`.$cur_date"
echo "INFO:mapper running ..."
source /etc/profile

read testfile

echo "INFO:testfile=$testfile"
if [ -z "$testfile" ];then
	echo "INFO:input testfile is empty!! exit 0, sikp this."
	exit 0;
fi

echo "INFO:pwd=`pwd`"

hdfs dfs -get /edi/nkw/tmp/train_model_a /edi/nkw/blocks/$testfile ./

echo "INFO:3.executting :crf_test -m model_file test_files"
/usr/local/bin/crf_test -m train_model_a $testfile > result_$testfile
echo $?
#TODO fail skip

echo "INFO:put result to hdfs."
hdfs dfs -put result_$testfile /edi/nkw/result/
echo $?

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
