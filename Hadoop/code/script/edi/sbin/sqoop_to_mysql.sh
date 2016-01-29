#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START.$0 AT $cur_date"

if [ $# -lt 3 ];then
	echo "USEAGE:$0 tablename [overwrite|add|partition] [partition name] \
	tablename : lowercase mysql table name,must has a table named edi_xxx in hive;"
fi

#mysql_url='jdbc:mysql://hadoopmysql/edi?useUnicode=true&characterEncoding=utf-8'
#hmsl='177374746095048029837425627581083108134044272033849682643281162P18'       #name hive --password hive
#msl='8272367958314031600942076814602P8272'      #hive -p hive

PARAM_A=edi
PARAM_B=edi@zy11
RDB_HOST=$1
TABLE=`echo "$2" |tr A-Z a-z`
ACTION=$3
PARTITION=$4

T_TABLE=$TABLE

if [ "$ACTION" = "-overwrite" ];then
	echo "INFO:truncate mysql table $TABLE."
	ssh $RDB_HOST "mysql -u $PARAM_A -p$PARAM_B -e 'use edi;delete from '$TABLE';'"
	if [ "$PARTITION" = "" ];then
		file=/user/hive/warehouse/edi.db/edi_$TABLE
	else
		file=/user/hive/warehouse/edi.db/edi_"$TABLE"/pt_date=$PARTITION
		T_TABLE=t_$TABLE
	fi
elif [ "$ACTION" = "-add" ];then	
	if [ "$PARTITION" = "" ];then
		file=/user/hive/warehouse/edi.db/edi_$TABLE
	else
		file=/user/hive/warehouse/edi.db/edi_"$TABLE"/pt_date=$PARTITION
	fi
elif [ "$ACTION" = "-partition" ];then
	if [ "$PARTITION" = "" ];then
		echo "INFO:please input partition name."
		exit 0
	fi
	file=/user/hive/warehouse/edi.db/edi_"$TABLE"/pt_date=$PARTITION
	T_TABLE=t_$TABLE
fi

echo "INFO:export file:$file"
sqoop export --connect "jdbc:mysql://$RDB_HOST/edi?useUnicode=true&characterEncoding=utf-8" \
	    --table "$T_TABLE" --username $PARAM_A --password $PARAM_B \
	    --export-dir $file --input-fields-terminated-by '\t' \
	    --mapreduce-job-name "export hive table $T_TABLE to mysql" -m 1 \
	    --input-null-string NULL --input-null-non-string NULL 
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:sqoop error.skip $TABLE ,code=$ecode"
fi

echo "INFO:mysql tmp2normal.TABLE=$TABLE ,T_TABLE=$T_TABLE"
if [ "$TABLE" != "$T_TABLE" ];then
	ssh $RDB_HOST "mysql -u $PARAM_A -p$PARAM_B -e 'use edi;insert into $TABLE select t.*,'$PARTITION' as created_dt from '$T_TABLE' t;truncate table '$T_TABLE';'"
fi

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"

