#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT $cur_date"

cd /opt/running/edi/sbin/

if [ $# -ne 1 ];then
	echo "Usage:$0 crawl_host"
	echo "	crawl_host:the crawl host need to import to hive. eg:192.168.0.118 or crawl_jd."
	exit 1;
fi
CRAWL_HOST=$1

#1.hdfs to hive
echo "INFO:1.INFO:copy comment file from the source to local."
fromdir=/home/hadoop/output
newdir=/opt/running/edi/data/new

scp hadoop@$CRAWL_HOST:$fromdir/Comm/part-r-00000 $newdir/comment/
#skip if there is not new file
if [ $? -ne 0 ];then
	echo "WARNING:there is not new file named part-r-00000 .skip to next."
else
	echo "INFO:2.$CRAWL_HOST source file rename."
	ssh hadoop@$CRAWL_HOST mv "$fromdir/Comm" "$fromdir/comments_bk/Comm-$cur_date"
	if [ $? -ne 0 ];then
		echo "ERROR:remote part-r-00000 rename failed."
	fi

	echo "INFO:3.local file to hive."
	#sed -i 's/^\t//' $newdir/comment/part-r-00000
	hive -S -e "LOAD DATA LOCAL INPATH '$newdir/comment/part-r-00000' INTO TABLE EDI.EDI_N_PROD_COMMS partition (pt_date='$cur_date');"
	ecode=$?
	if [ $ecode -ne 0 ];then
		echo "ERROR:import new file to hive failed.exit $ecode"
	else	
		echo "INFO:4.EDI_N_PROD_COMMS 2 EDI_M_PROD_COMMS.pt >= $cur_date running..."
		hive -S -e "INSERT INTO TABLE EDI.EDI_M_PROD_COMMS PARTITION(pt_date='$cur_date') SELECT distinct '$cur_date',id,'NULL',referenceId,content,creationTime,referenceName,nickname,COMM_TAGS,usefulVoteCount,replyCount FROM EDI.EDI_N_PROD_COMMS nc WHERE nc.PT_DATE >= '$cur_date' and not exists (select 1 from EDI.EDI_M_PROD_COMMS mc where mc.id = nc.id);"

		ecode=$?
		if [ $ecode -ne 0 ];then
		        echo "ERROR:from table EDI_N_PROD_COMMS to EDI_M_PROD_COMMS failed.exit $ecode"
		fi
	fi
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"

echo "INFO:5.copy product from $CRAWL_HOST to local"
scp hadoop@$CRAWL_HOST:$fromdir/Pro/part-r-00000 $newdir/product/

#skip if there is not new file
if [ $? -ne 0 ];then
	echo "WARNING:there is not new file named part-r-00000 .exit 1."
else
	echo "INFO:6.product source file rename."
	ssh hadoop@$CRAWL_HOST mv "$fromdir/Pro" "$fromdir/products_bk/Pro-$cur_date"
	if [ $? -ne 0 ];then
		echo "WARNING:remote rename part-r-00000 failed."
	fi

	echo "INFO:7.local file to hive."
	#sed -i 's/^\t//' $newdir/product/part-r-00000
	hive -S -e "LOAD DATA LOCAL INPATH '$newdir/product/part-r-00000' INTO TABLE EDI.EDI_N_PROD_INFO PARTITION (PT_DATE='$cur_date');"
	ecode=$?
	if [ $ecode -ne 0 ];then
		echo "ERROR:import new file to hive failed.exit $ecode"
	else
		echo "INFO:8.prod_info new to normal."
		hive -S -e "USE edi;INSERT INTO TABLE EDI_M_PROD_INFO PARTITION(PT_DATE='$cur_date') SELECT CRAWL_DATE, SOURCE, PROD_ID, NAME, PRICE, BRAND, MODEL, COLOR, COMM_AMOUNT, COMM_GOOD_AMOUNT, COMM_MIDDLE_AMOUNT, COMM_BAD_AMOUNT, HOT_TAGS, PARAMS FROM EDI_N_PROD_INFO I WHERE NOT EXISTS (SELECT 1 FROM EDI_M_PROD_INFO MI WHERE MI.PROD_ID=I.PROD_ID) AND I.PT_DATE='$cur_date';"
		echo "INFO:9.append new prod_info records to MYSQL table ..."
		sh ./sqoop_to_mysql.sh m_prod_info -add $cur_date
		echo "INFO:10.update prod_params..."
		sh ../prod_params/edi_hive_parse_prod_params.sh $cur_date
	fi
fi

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
