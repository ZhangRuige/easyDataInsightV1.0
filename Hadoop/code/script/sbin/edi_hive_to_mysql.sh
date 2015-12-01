#!/bin/bash
#mysql_url="jdbc:mysql://hadoop-1/edi --username hive --password hive"
echo "START.$0"

mysql_url='jdbc:mysql://HadoopMySQL/edi?useUnicode=true&characterEncoding=utf-8'
hmsl='177374746095048029837425627581083108134044272033849682643281162P18'	#name hive --password hive
msl='8272367958314031600942076814602P8272'	#hive -p hive

cur_dt=`date +%Y%m%d%H%M`
echo $cur_dt

start_time=$(date +%s)

#change work dir
cd /opt/running/edi/op/tmp/

#check if /edi/edi_conf exists.
hdfs dfs -test -e /edi/edi_conf
if [ $? -ne 0 ];then
	hdfs dfs -mkdir -p /edi/edi_conf
	echo 'path /edi/conf is not exists. have been created.'
fi

#hive2mysql
echo 'loop start.'
for table in {'M_PROD_INFO','M_PROD_COMMS','M_R_AMOUNT'}
do

	#>>>1.get the last sync partition
	export_pt=`hdfs dfs -ls /edi/edi_conf | grep $table'.last_sync_dt'|tail -n 1|cut -f2 -d '='`
	echo 'table:'$table',export_pt:'$export_pt

	#>>>2.get all partitions which need to export	
	if [ -z $export_pt];then	#-z str empty
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_\`echo $table|tr A-Z a-z\`|grep pt_date|cut -f2 -d '='`
	else
	#	export_pt='20151110101010'
		awk_fun='{if("'$export_pt'"<$0){print $0}}'
		folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_\`echo $table|tr A-Z a-z\`|grep pt_date|cut -f2 -d '='|awk "$awk_fun"`
	fi
	echo $folders
	
	OLD_IFS="$IFS" 
	IFS=" " 
	arr=($folders) 
	IFS="$OLD_IFS" 
	for folder in ${arr[@]} 
	do 
		
		#>>>3.do export to temp table in mysql
		echo "$folder" 
		file="/user/hive/warehouse/edi.db/edi_`echo $table |tr A-Z a-z`/pt_date=$folder"
		echo "export file:$file"
		sqoop export --connect $mysql_url --table 'T_'$table --username edi --password edi@zy11 --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string NULL  || continue


		#>>>4.update mysql crate_dt(partition)
		msql="USE edi;INSERT INTO $table SELECT T.*,'$folder' AS CREATED_DT FROM T_$table T;TRUNCATE TABLE T_$table;"
		echo $msql
		mysql -h HadoopMySQL -u edi -pedi@zy11 -e "$msql"
		#msql="TRUNCATE TABLE T_$table;"
		#echo $msql
		#mysql -h HadoopMySQL -u edi -pedi@zy11 -e "$msql"
		
		
		#>>>5.update last sync
		hdfs dfs -rm -r /edi/edi_conf/$table".last_push_mysql=*"
		hdfs dfs -mkdir -p /edi/edi_conf/$tablei".last_push_mysql=$folder"
		echo "updating... edi_conf key:$table"".last_push_mysql=$folder"
	done

done
echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
