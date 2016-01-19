#!/bin/bash

# This is mysql mysqlfullbak scripts

#2015-11-23

#shizenghua

user=edi

passwd=edi@zy11

databak_dir=/opt/backup/mysql   #备份的目录

bk_host_dir=/opt/backup/mysql_bk

DATE=`date +%Y%m%d`

logFile=$databak_dir/mysqldump.log

database=edi

dumpFile=$database$DATE.sql

GZDumpFile=$database$DATE.tar.gz

options="-u $user -p$passwd -B $database"
#options="-u $user -p$passwd --opt --extended-insert=false --triggers=false -R --hex-blob --flush-logs --delete-master-logs -B $database"

mysqldump $options > $dumpFile  #导出数据文件

if [[ $? == 0 ]]; then

  tar cvzf $GZDumpFile $dumpFile 

  scp $GZDumpFile $bk_host_dir   #传送备份文件到另一台计算机，需要做好ssh信任

  rm -f $dumpFile           #删除备份的文件

  echo "DataBase Backup Success;备份文件$GZDumpFile 已移到 $bk_host_dir ." >> $logFile
else

  echo "DataBase Backup Fail!" >> $logFile

fi

echo "$DATE--------------------------------------------------------" >> $logFile

