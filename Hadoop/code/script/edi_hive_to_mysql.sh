#!/bin/bash
#mysql_url="jdbc:mysql://hadoop-1/edi --username hive --password hive"

mysql_url='jdbc:mysql://hadoop-1/edi'
hmsl='177374746095048029837425627581083108134044272033849682643281162P18'	#name hive --password hive
msl='8272367958314031600942076814602P8272'	#hive -p hive

cur_dt=`date +%Y%m%d%H%M`

tmp_tab_sql=("BRAND,MODEL,ASPECT,ATTR,CATEGORY_VAL,AMOUNT","","")

echo 'loop start.'
#hive2mysql
#for table in {'M_PROD_INFO','M_PROD_COMMS','M_R_AMOUNT'}
for table in {'M_R_AMOUNT','M_PROD_COMMS'}
do

	#>>>1.get the last sync partition
	export_pt=`hdfs dfs -ls /edi/edi_conf | grep $table'.last_sync_dt'|tail -n 1|cut -f2 -d '='`
	#export_pt=`hdfs dfs -cat /edi/edi_conf.txt | grep $table'.last_sync_dt' |head -n 1|cut -f2`	#from file,not good
	echo $export_pt
	if [ -z $export_pt];then
		export_pt=$cur_dt
	fi
	echo 'table:'$table',export_pt:'$export_pt

	#>>>2.get all partitions which need to export	
	export_pt='20151110101010'
	awk_fun='{if("'$export_pt'"<$0){print $0}}'
	echo $awk_fun
	#awk_fun="hdfs dfs -ls /user/hive/warehouse/edi.db/edi_`echo $table |tr A-Z a-z`|grep pt_date|cut -f2 -d '='|awk '$awk_fun'"
	folders=`hdfs dfs -ls /user/hive/warehouse/edi.db/edi_\`echo $table|tr A-Z a-z\`|grep pt_date|cut -f2 -d '='|awk "$awk_fun"`
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
		#sqoop export --connect $mysql_url --table $table --user`dc -e $hmsl` --export-dir $file --input-fields-terminated-by '\t' --null-non-string '0' --null-string '0'
		sqoop export --connect $mysql_url --table 'T_'$table --username  hive --password hive --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string NULL  || continue


		#>>>4.update mysql crate_dt(partition)
		#msql='USE EDI;UPDATE '$table' SET CREATED_DT="'$folder'" WHERE CREATED_DT IS NULL;'
		msql="USE edi;INSERT INTO $table SELECT T.*,'$folder' AS CREATED_DT FROM T_$table T;TRUNCATE TABLE T$table;"
		echo $msql
		mysql -u hive -phive -e "$msql"
		
		#>>>5.update last sync
		hdfs dfs -rm -r /edi/edi_conf/$table'.last_push_mysql=*'
		hdfs dfs -mkdir -p /edi/edi_conf/$table'.last_push_mysql='$folder
		echo 'updating... edi_conf key:'$table'.last_push_mysql='$folder
	done

done
echo 'done!'
