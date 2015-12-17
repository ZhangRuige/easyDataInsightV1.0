#!/bin/bash

echo ">>>START .$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo "running ..$cur_date."
source /etc/profile
cd /opt/running/edi/sbin/

#1.hdfs to hive

echo "1.INFO:copy comment from source to local."
fromdir=/home/hadoop/output/
newdir=/opt/running/edi/data/new/

scp hadoop@HadoopMySQL:$fromdir/Comm/part-r-00000 $newdir/comment/
#skip if there is not new file
if [ $? -ne 0 ];then
        echo "WARNING:there is not new file named part-r-00000 .skip to next."
else
	echo "2.INFO:source file rename."
	ssh hadoop@HadoopMySQL mv "$fromdir/Comm" "$fromdir/comments_bk/Comm-$cur_date"
	if [ $? -ne 0 ];then
		echo "ERROR:remote part-r-00000 rename failed."
	fi

	echo "3.INFO:local file to hive."
	hive -S -e "LOAD DATA LOCAL INPATH '$newdir/comment/part-r-00000' INTO TABLE EDI.EDI_N_PROD_COMMS PARTITION (PT_DATE='$cur_date');"
	ecode=$?
	if [ $ecode -ne 0 ];then
		echo "ERROR:import new file to hive failed.exit $ecode"
	else	
		echo "4.INFO:hive new data from table EDI_N_PROD_COMMS to EDI_M_PROD_COMMS."
		echo "INFO:EDI_N_PROD_COMMS 2 EDI_M_PROD_COMMS.pt >= $cur_date running..."
		hive -S -e "INSERT INTO TABLE EDI.EDI_M_PROD_COMMS PARTITION(pt_date='$cur_date') SELECT '$cur_date',id,'NULL', referenceId, content, creationTime, referenceName, nickname, COMM_TAGS, usefulVoteCount,  replyCount FROM EDI.EDI_N_PROD_COMMS WHERE PT_DATE >= '$cur_date';"

		ecode=$?
		if [ $ecode -ne 0 ];then
		        echo "ERROR:from table EDI_N_PROD_COMMS to EDI_M_PROD_COMMS failed.exit $ecode"
		fi
	fi

fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"

echo "5.INFO:copy product from source to local"
scp hadoop@HadoopMySQL:$fromdir/Pro/part-r-00000 $newdir/product/
#skip if there is not new file
if [ $? -ne 0 ];then
        echo "WARNING:there is not new file named part-r-00000 .exit 1."
else
	echo "6.INFO:product source file rename."
	ssh hadoop@HadoopMySQL mv "$fromdir/Pro" "$fromdir/products_bk/Pro-$cur_date"
	if [ $? -ne 0 ];then
        	echo "WARNING:HadoopMySQL:remote part-r-00000 rename failed."
	fi

	echo "7.INFO:local file to hive."
	sed -i 's/^\t//' $newdir/product/part-r-00000
	hive -S -e "LOAD DATA LOCAL INPATH '$newdir/product/part-r-00000' INTO TABLE EDI.EDI_M_PROD_INFO PARTITION (PT_DATE='$cur_date');"
	ecode=$?
	if [ $ecode -ne 0 ];then
        	echo "ERROR:import new file to hive failed.exit $ecode"
	fi
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"
echo ">>>DONE.$0"

