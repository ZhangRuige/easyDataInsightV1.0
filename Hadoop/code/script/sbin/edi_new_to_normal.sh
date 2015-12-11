#!/bin/bash
#mysql_url="jdbc:mysql://hadoop-1/edi --username hive --password hive"
echo ">>>START.$0"
cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt

start_time=$(date +%s)

last_pt=$cur_dt
if [ $# = 1 ];then
	last_pt=$1
fi
echo "INFO:EDI_N_PROD_COMMS 2 EDI_M_PROD_COMMS.pt >= $last_pt running..."
hive -S -e "INSERT INTO TABLE EDI.EDI_M_PROD_COMMS PARTITION(pt_date='$last_pt') SELECT '$last_pt',id,'NULL', referenceId, content, creationTime, referenceName, nickname, COMM_TAGS, usefulVoteCount,  replyCount FROM EDI.EDI_N_PROD_COMMS WHERE PT_DATE >= '$last_pt';"

	
echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
