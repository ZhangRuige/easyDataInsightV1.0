#!/bin/bash

cur_dt=`date +%Y%m%d%H%M%S`
echo ">>>START.$0 at $cur_dt"
start_time=$(date +%s)

source /etc/profile

#change work dir
cd "$(dirname "$0")"

#check if /edi/edi_conf exists.
hdfs dfs -test -e /edi/edi_conf
if [ $? -ne 0 ];then
	hdfs dfs -mkdir -p /edi/edi_conf
	echo "WARNING:path /edi/conf is not exists. have been created."
fi

#hive2mysql
echo "INFO:table loop start."
for table in {'M_PROD_COMMS','M_MODEL_MENTION'}
do
	#>>>1.get the last sync partition
	export_pt=`hdfs dfs -ls /edi/edi_conf/ | grep $table".last_push_mysql" | tail -n 1 | cut -f2 -d "="`
	echo "INFO:table:$table ,last_export_pt:$export_pt"

	#>>>2.get all partitions which need to export	
	if [ -z $export_pt ];then	#-z str empty
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_$(echo $table|tr A-Z a-z)|grep pt_date|cut -f2 -d '='`
	else
		awk_fun='{if( '$export_pt' < $1){ print $1}}'
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_$(echo $table|tr A-Z a-z)|grep pt_date|cut -f2 -d '=' | awk "$awk_fun"`
	fi
	echo "INFO:folders=$folders"
	
	OLD_IFS="$IFS" 
	IFS=" " 
	arr=($folders) 
	IFS="$OLD_IFS" 
	for folder in ${arr[@]} ### partitions loop start
	do 
		#>>>do export to temp table in mysql
		echo "INFO:do for partition=$folder"
		sh ../sbin/sqoop_to_mysql.sh "$table" -partition "$folder"
		if [ $? -ne 0 ];then
			echo "ERROR:sqoop error.skip $table ,code=$?"
			#continue
			break
		else
			#>>>update last sync
			hdfs dfs -rm -r -skipTrash /edi/edi_conf/$table".last_push_mysql=*"
			hdfs dfs -mkdir -p "/edi/edi_conf/$table"".last_push_mysql=$folder"
			echo "updating... edi_conf key:$table"".last_push_mysql=$folder"
		fi
	done
	####
done

echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
