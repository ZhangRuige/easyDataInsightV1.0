#!/bin/bash

# This is mysql mysqlfullbak scripts

#2015-11-23

#shizenghua

if [ $# < 3 ] ; then
	echo "USAGE: $0 database user passwd"
	exit 1;
fi 

source /etc/profile

databak_dir=`dirname $0` 
remote_bk_dir='hdfs://HadoopNamenode:9100/data/mysql_bk'

DATE=`date +%Y%m%d%H%M`

logFile=$databak_dir/mysqldump.log
dumpFile=$databak_dir/$1$DATE.sql
GZDumpFile=$databak_dir/$1$DATE.tar.gz

options="-u $2 -p`dc -e $3` -B $1" #1870356536615775580426
mysqldump $options > $dumpFile  

if [[ $? == 0 ]]; then
	tar cvzf $GZDumpFile $dumpFile 
	hdfs dfs -put $GZDumpFile $remote_bk_dir >> $logFile
	ecode=$?
	if [ $ecode -ne 0 ];then
		echo "put to hdfs failed.backup to local fs."
	else
		echo "put backup file to hdfs location $remote_bk_dir return code=$ecode" >> $logFile
  		rm -f $dumpFile $GZDumpFile
	fi
else
	echo "DataBase Backup Fail!" >> $logFile
fi

echo "$DATE--------------------------------------------------------" >> $logFile

