#!/bin/bash
echo ">>>START.$0"

cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt
start_time=$(date +%s)

mysql_url='jdbc:mysql://HadoopMySQL/edi?useUnicode=true&characterEncoding=utf-8'

#hive2mysql
echo "INFO:loop start."
for table in {'M_PROD_INFO','M_PROD_COMMS','M_R_AMOUNT'}
do
		
	file="/user/hive/warehouse/edi.db/edi_`echo $table |tr A-Z a-z`/"
	echo "INFO:export file:$file"

	sqoop export --connect $mysql_url --table 'T_'$table --username edi --password edi@zy11 --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string NULL 
	echo "INFO:sqoop exit code=$?"
	if [ $? -ne 0 ];then
		echo "ERROR:sqoop error.skip $table ,code=$?"
		continue
	fi

done
echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
