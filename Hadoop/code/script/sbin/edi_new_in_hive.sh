#!/bin/bash


echo ">>>START .$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo "running ..."

cd /opt/running/edi/sbin/

#1.hdfs to hive

echo "1.INFO:from source to local."
newdir=/opt/running/edi/data/new

#TODO file name 

scp HadoopMySQL:$newdir/part-r-00000 $newdir
#skip if there is not new file
if [ $? -ne 0 ];then
	echo "WARNING:there is not new file named part-r-00000 .exit 1."
	exit 1
fi

echo "2.INFO:source file rename."
ssh HadoopMySQL mv "$newdir/part-r-00000" "$newdir/part-r-00000-$start_time"
if [ $? -ne 0 ];then
	echo "WARNING:HadoopMySQL:/opt/running/edi/data/new/part-r-00000 rename failed."
fi

echo "3.INFO:local file to hive."
hive -S -e "use edi;LOAD DATA LOCAL INPATH '$newdir/part-r-00000' INTO TABLE EDI_N_PROD_COMMS PARTITION (PT_DATE='$cur_date');"
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:import new file to hive failed.exit $ecode"
	exit $ecode;
fi


echo "4.INFO:hive new data from table EDI_N_PROD_COMMS to EDI_M_PROD_COMMS."
./edi_new_to_normal.sh $cur_date
ecode=$?
if [ $ecode -ne 0 ];then
        echo "ERROR:from table EDI_N_PROD_COMMS to EDI_M_PROD_COMMS failed.exit $ecode"
        exit $ecode
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE.$0"

