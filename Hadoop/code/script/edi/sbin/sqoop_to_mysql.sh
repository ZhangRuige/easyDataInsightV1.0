#!/bin/bash

cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt
echo "$0 >>>START at $cur_dt"
start_time=$(date +%s)
source /etc/profile

if [ $# -lt 2 ];then
	echo "$0 USEAGE:$0 tablename [overwrite|add|partition] [partition name] \
	tablename : lowercase mysql table name,must has a table named edi_$table in hive;"
fi

#mysql_url='jdbc:mysql://hadoopmysql/edi?useUnicode=true&characterEncoding=utf-8'
#hmsl='177374746095048029837425627581083108134044272033849682643281162P18'       #name hive --password hive
#msl='8272367958314031600942076814602P8272'      #hive -p hive

param_a=edi
param_b=edi@zy11
table=`echo "$1" |tr A-Z a-z`
t_table=$table
action=$2
partition=$3

if [ "$action" = "-overwrite" ];then
	echo "$0 INFO:truncate mysql table $table."
	mysql -h hadoopmysql -u $param_a -p$param_b -e "use edi;delete from $table ;"
	if [ "$partition" = "" ];then
		file=/user/hive/warehouse/edi.db/edi_$table
	else
		file=/user/hive/warehouse/edi.db/edi_"$table"/pt_date=$partition
		t_table=t_$table
	fi
elif [ "$action" = "-add" ];then	
	if [ "$partition" = "" ];then
		file=/user/hive/warehouse/edi.db/edi_$table
	else
		file=/user/hive/warehouse/edi.db/edi_"$table"/pt_date=$partition
	fi
elif [ "$action" = "-partition" ];then
	if [ "$partition" = "" ];then
		echo "$0 INFO:please input partition name."
		exit 0
	fi
	file=/user/hive/warehouse/edi.db/edi_"$table"/pt_date=$partition
	t_table=t_$table
fi

echo "$0 INFO:export file:$file"
sqoop export --connect "jdbc:mysql://hadoopmysql/edi?useUnicode=true&characterEncoding=utf-8" \
	     --table "$t_table" --username $param_a --password $param_b --export-dir $file --input-fields-terminated-by '\t' \
	     --mapreduce-job-name "export hive table $t_table to mysql" -m 1 \
	     --input-null-string NULL --input-null-non-string NULL 
ecode=$?
if [ $ecode -ne 0 ];then
       	echo "$0 ERROR:sqoop error.skip $table ,code=$ecode"
fi


if [ "$table" != "$t_table" ];then
	mysql -h hadoopmysql -u $param_a -p$param_b -e "USE edi;INSERT INTO $table SELECT T.*,'$partition' AS CREATED_DT FROM $t_table T;TRUNCATE TABLE $t_table;"
fi

echo "$0 DONE. spend time(s) :"$(( $(date +%s) - $start_time ))


