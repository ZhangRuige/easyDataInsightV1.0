#!/bin/bash
echo ">>>START.$0"
cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt

start_time=$(date +%s)


echo "0.clear table EDI_M_CONSUMER_DIST & insert new data."
hive -S -e "INSERT OVERWRITE TABLE EDI.EDI_M_CONSUMER_DIST SELECT A.BRAND,A.MODEL,REGEXP_REPLACE(A.AREA,'^$','其他'),A.AMOUNT FROM (SELECT I.BRAND,I.MODEL,C.USERPROVINCE as AREA,COUNT(C.ID) as AMOUNT FROM EDI.EDI_N_PROD_COMMS C LEFT JOIN EDI.EDI_M_PROD_INFO I ON C.REFERENCEID=I.PROD_ID GROUP BY I.BRAND,I.MODEL,C.USERPROVINCE ) A ORDER BY A.AMOUNT DESC;"
ecode=$?
echo "INFO:hive update ,code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:hive error.exit=$ecode"
	exit $ecode
fi


echo "1.TRUNCATE mysql EDI.M_CONSUMER_DIST."
ssh HadoopMySQL mysql -u edi -pedi@zy11 -e "USE edi;DELETE FROM M_CONSUMER_DIST;"

echo "2,overwrite EDI_M_CONSUMER_DIST to mysql..."
file="/user/hive/warehouse/edi.db/edi_m_consumer_dist/"
echo "INFO:export file:$file"
sqoop export --connect 'jdbc:mysql://HadoopMySQL/edi?useUnicode=true&characterEncoding=utf-8' --table 'M_CONSUMER_DIST' --username edi --password edi@zy11 --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string NULL 
ecode=$?
echo "INFO:sqoop exit code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:sqoop error.skip $table ,code=$ecode"
fi

echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
