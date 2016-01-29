#!/bin/bash
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT $cur_date"
source /etc/profile

echo "INFO:0.clear table EDI_M_CONSUMER_DIST & insert new data."
#hive -S -e "INSERT OVERWRITE TABLE EDI.EDI_M_CONSUMER_DIST SELECT A.BRAND,A.MODEL,REGEXP_REPLACE(A.AREA,'^$','其他'),A.AMOUNT FROM (SELECT I.BRAND,I.MODEL,C.USERPROVINCE as AREA,COUNT(C.ID) as AMOUNT FROM EDI.EDI_N_PROD_COMMS C LEFT JOIN EDI.EDI_M_PROD_INFO I ON C.REFERENCEID=I.PROD_ID GROUP BY I.BRAND,I.MODEL,C.USERPROVINCE ) A ORDER BY A.AMOUNT DESC;"
hive -S -e 'INSERT OVERWRITE TABLE EDI.EDI_M_CONSUMER_DIST 
SELECT A.BRAND,A.MODEL,A.AREA,A.DEAL_DATE,A.AMOUNT 
FROM ( SELECT I.BRAND,I.MODEL,REGEXP_REPLACE(C.USERPROVINCE,"^$","其他") as AREA,SUBSTR(C.referenceTime,1,7) as DEAL_DATE,COUNT(C.ID) as AMOUNT FROM EDI.EDI_N_PROD_COMMS C 
LEFT JOIN EDI.EDI_M_PROD_INFO I ON C.REFERENCEID=I.PROD_ID 
GROUP BY I.BRAND,I.MODEL,SUBSTR(C.referenceTime,1,7),C.USERPROVINCE ) A 
ORDER BY A.BRAND,A.MODEL,A.AREA,A.DEAL_DATE;'
ecode=$?
echo "INFO:hive update ,code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:hive error.exit=$ecode"
	exit $ecode
fi

sh sqoop_to_mysql.sh m_consumer_dist -overwrite
ecode=$?
if [ $ecode -ne 0 ];then
	echo "ERROR:sqoop error.skip m_consumer_dist ,code=$ecode"
	exit 1
fi

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"
