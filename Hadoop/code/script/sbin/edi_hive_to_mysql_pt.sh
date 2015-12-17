#!/bin/bash
#mysql_url="jdbc:mysql://hadoop-1/edi --username hive --password hive"
echo ">>>START.$0"
source /etc/profile
mysql_url='jdbc:mysql://HadoopMySQL/edi?useUnicode=true&characterEncoding=utf-8'
hmsl='177374746095048029837425627581083108134044272033849682643281162P18'	#name hive --password hive
msl='8272367958314031600942076814602P8272'	#hive -p hive

cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt

start_time=$(date +%s)

#change work dir
cd /opt/running/edi/op/tmp/

#check if /edi/edi_conf exists.
/opt/running/hadoop-2.6.0/bin/hdfs dfs -test -e /edi/edi_conf
if [ $? -ne 0 ];then
	/opt/running/hadoop-2.6.0/bin/hdfs dfs -mkdir -p /edi/edi_conf
	echo "WARNING:path /edi/conf is not exists. have been created."
fi

#hive2mysql
echo "INFO:loop start."
#for table in {'M_PROD_INFO','M_PROD_COMMS','M_R_AMOUNT','M_MODEL_MENTION'}
for table in {'M_PROD_INFO','M_PROD_COMMS'}
do
	#>>>1.get the last sync partition
	export_pt=`hdfs dfs -ls /edi/edi_conf/ | grep $table".last_push_mysql" | tail -n 1 | cut -f2 -d "="`
	echo "INFO:table:$table ,export_pt:$export_pt"

	#>>>2.get all partitions which need to export	
	if [ -z $export_pt ];then	#-z str empty
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_\`echo $table|tr A-Z a-z\`|grep pt_date|cut -f2 -d '='`
	else
		awk_fun='{if( '$export_pt' < $1){ print $1}}'
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_\`echo $table|tr A-Z a-z\`|grep pt_date|cut -f2 -d '=' | awk "$awk_fun"`
	fi
	echo "INFO:$folders"
	
	OLD_IFS="$IFS" 
	IFS=" " 
	arr=($folders) 
	IFS="$OLD_IFS" 
	for folder in ${arr[@]} 
	do 
		
		#>>>3.do export to temp table in mysql
		echo "INFO:do for partition=$folder" 
		file="/user/hive/warehouse/edi.db/edi_`echo $table |tr A-Z a-z`/pt_date=$folder"

		echo "INFO:export file:$file"
		/opt/soft/sqoop-1.4.6.bin/bin/sqoop export --connect $mysql_url --table 'T_'$table --username edi --password edi@zy11 --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string '\\N' --input-null-non-string 'NULL' 
		echo "INFO:sqoop exit code=$?"
		if [ $? -ne 0 ];then
			echo "ERROR:sqoop error.skip $table ,code=$?"
			continue
		fi

		#>>>4.update mysql crate_dt(partition)
		msql="USE edi;INSERT INTO $table SELECT T.*,'$folder' AS CREATED_DT FROM T_$table T;TRUNCATE TABLE T_$table;"
		echo "INFO:executing sql=$msql"

		#TODO password !!!
		mysql -h HadoopMySQL -u edi -pedi@zy11 -e "$msql"
		
		if [ $? -ne 0 ];then
			echo "ERROR:mysql transfer data error.skip code=$?"
			continue
		fi
		#>>>5.update last sync
		/opt/running/hadoop-2.6.0/bin/hdfs dfs -rm -r -skipTrash /edi/edi_conf/$table".last_push_mysql=*"
		/opt/running/hadoop-2.6.0/bin/hdfs dfs -mkdir -p "/edi/edi_conf/$table"".last_push_mysql=$folder"
		echo "updating... edi_conf key:$table"".last_push_mysql=$folder"
	done

done
echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
