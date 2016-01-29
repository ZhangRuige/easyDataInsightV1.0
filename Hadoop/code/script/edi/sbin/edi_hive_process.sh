#!/bin/bash

#处理一个map任务，输入是blocks中作为测试集的文件名，输出
echo "=============================================="
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT $cur_date"
source /etc/profile

cd /opt/running/edi/sbin/

echo "INFO:1.exec edi_new_in_hive_process.sh."
./edi_new_in_hive_process.sh  
if [ $? -ne 0 ];then
	echo "INFO:there is not new file named part-r-00000 .exit 0."
	exit 0
fi

echo "INFO:2.exec edi_hive_do_op.sh"
./edi_hive_do_op.sh

echo "INFO:3.exec edi_hive_do_count_op.sh"
./edi_hive_do_count_op.sh

echo "INFO:4.exec edi_hive_do_mention.sh"
../mention/edi_hive_mention_process.sh

echo "INFO:5.exec edi_hive_to_mysql.sh"
./edi_hive_to_mysql_pt.sh

echo "INFO:6.exec edi_hive_update_brand_model.sh."
./edi_hive_update_brand_model.sh

echo "INFO:7.exec edi_update_consumer_dist.sh"
./edi_update_consumer_dist.sh


echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"

