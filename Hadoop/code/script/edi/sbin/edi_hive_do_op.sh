#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START .$0 AT $cur_date"
source /etc/profile

cd /opt/running/edi/op

tmp_file=tmp/edi_r_comm_tag_"$cur_date".td

#>>>1.get the last partition
last_donlp_pt=`hdfs dfs -ls /edi/edi_conf |grep 'last_donlp_pt' |tail -n 1|cut -f2 -d '='`
echo "last_donlp_pt=$last_donlp_pt"

echo "run export ..."

#if [ "$1"x = "distinct"x ];then
#	hql="(SELECT * FROM (SELECT *,ROW_NUMBER() OVER(DISTRIBUTE  BY ID SORT BY PT_DATE DESC ) RN  FROM EDI_M_PROD_COMMS) T WHERE T.RN=1)"
#else
#	hql="EDI_M_PROD_COMMS"
#fi

condition=""
if [ "x$last_donlp_pt" != "x" ];then
	condition=" WHERE B.PT_DATE > '$last_donlp_pt'"
fi

hive -S -e "use edi;
add jars /opt/running/edi/op/lib/ansj_seg-2.0.8.jar
/opt/running/edi/op/lib/nlp-lang-1.0.jar
/opt/running/edi/op/lib/word2vec.jar
/opt/running/edi/op/lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar
/opt/running/edi/op/lib/com.zhongyitech.edi.hive.udf.donlp-1.1.jar;
create temporary function OPUDF as 'com.zhongyitech.edi.hive.udf.OpUDF';
SELECT 	A.ARR[0] AS COMM_ID,
    A.ARR[1] AS ASPECT,
    A.ARR[2] AS ATTR, 
    A.ARR[3] AS CATEGORY,
    A.ARR[4] AS CONTEXT_START,
    A.ARR[5] AS CONTEXT_END,
    A.ARR[6] AS ATTR_START,
    A.ARR[7] AS ATTR_START,
    A.ARR[8] AS ASPT_START,
    A.ARR[9] AS ASPT_END,
    A.ARR[10] AS SENTI_START,
    A.ARR[11] AS SENTI_END
FROM (
    SELECT EXPLODE(OPUDF(B.ID,CONCAT(B.COMM_TAGS,'\\t',B.COMM_INFO),B.PROD_INFO)) AS ARR 
    FROM EDI_M_PROD_COMMS B $condition ) A;
" >> $tmp_file

ecode=$?
if [ $ecode -ne 0 ];then
	echo "hiveQL exec failed.exit $ecode"
	exit $ecode
fi

if [ ! -s $tmp_file ];then  #string length is zero
	echo "0 records.exit."
	exit 1
fi

echo "run import ..."
hive -e "use edi;LOAD DATA LOCAL INPATH '$tmp_file' INTO TABLE EDI_R_COMM_TAG PARTITION (PT_DATE='$cur_date');"
#hive -S -e "use edi;LOAD DATA LOCAL INPATH '$tmp_file' INTO TABLE EDI_R_COMM_TAG PARTITION (PT_DATE='$cur_date');"
ecode=$?
rm tmp/edi_r_comm_tag_"$cur_date".td

#>>>update last ptA
if [ $ecode -ne 0 ];then
	echo "ERROR:hiveQL exec failed.exit $ecode"
	exit $ecode
else
	hdfs dfs -rm -r -skipTrash /edi/edi_conf/last_donlp_pt=*
	hdfs dfs -mkdir -p /edi/edi_conf/last_donlp_pt=$cur_date
	echo "updating... edi_conf key:last_donlp_pt=$cur_date"
fi

echo "END.$0"
echo "spend time(s) :$(( $(date +%s) - $start_time ))"
