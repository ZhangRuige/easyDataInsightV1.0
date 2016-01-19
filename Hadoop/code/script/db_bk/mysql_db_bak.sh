#!/bin/bash

# This is mysql mysqlfullbak scripts

#2015-11-23

#shizenghua

if [ $# < 3 ] ; then
	echo "USAGE: $0 database user passwd"
	exit 1;
fi 

databak_dir=`dirname $0`   #备份的目录
remote_bk_dir=hdfs://HadoopNamenode:9100/data/mysql_bk

DATE=`date +%Y%m%d%H%M`

logFile=$databak_dir/mysqldump.log

dumpFile=$databak_dir/$1$DATE.sql

GZDumpFile=$databak_dir/$1$DATE.tar.gz

#options="-u $user -p$passwd --opt --extended-insert=false --triggers=false -R --hex-blob --flush-logs --delete-master-logs -B $database"
options="-u $2 -p`dc -e $3` -B $1" #1870356536615775580426
mysqldump $options > $dumpFile  #导出数据文件

if [[ $? == 0 ]]; then

  tar cvzf $GZDumpFile $dumpFile 

  hdfs dfs -put $GZDumpFile $remote_bk_dir >> $logFile  #传送备份文件到HDFS
  echo "put backup file to hdfs location $remote_bk_dir return code=$?" >> $logFile

  rm -f $dumpFile           #删除备份的文件

  echo "DataBase Backup Success;备份文件$GZDumpFile 已移到HDFS: $remote_bk_dir ." >> $logFile
else

  echo "DataBase Backup Fail!" >> $logFile

fi

echo "$DATE--------------------------------------------------------" >> $logFile

