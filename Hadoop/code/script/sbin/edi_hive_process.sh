#!/bin/bash

#处理一个map任务，输入是blocks中作为测试集的文件名，输出
echo ""
echo ">>>START .$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
source /etc/profile
echo "running ..."

cd /opt/running/edi/sbin/

#1.hdfs to hive
echo "INFO:from source to local."
./edi_new_in_hive.sh
#skip if there is not new file
if [ $? -ne 0 ];then
	echo "INFO:there is not new file named part-r-00000 .exit 0."
	exit 0
fi


#2.edi_hive_do_op.sh 4 after the last do_op pt
echo "INFO:do op."
./edi_hive_do_op.sh

#3.edi_hive_do_count_op.sh
echo "INFO:do op count."
./edi_hive_do_count_op.sh


#4.edi_hive_to_mysql.sh
echo "INFO:hive to mysql."
./edi_hive_to_mysql.sh


echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE.$0"

