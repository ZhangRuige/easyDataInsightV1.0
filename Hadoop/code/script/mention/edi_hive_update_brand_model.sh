#!/bin/bash
echo ">>>START.$0"
cur_dt=`date +%Y%m%d%H%M%S`
echo $cur_dt

start_time=$(date +%s)
source /etc/profile

echo "0.clear table EDI_M_BRAND_MODEL & insert new data."
hive -S -e "INSERT OVERWRITE TABLE EDI.EDI_M_BRAND_MODEL SELECT BRAND,MODEL,count(MODEL) FROM EDI.EDI_M_PROD_INFO GROUP BY BRAND,MODEL;"
ecode=$?
echo "INFO:hive update ,code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:hive error.exit=$ecode"
	exit $ecode
fi


echo "1.TRUNCATE mysql EDI.M_BRAND_MODEL."
ssh HadoopMySQL mysql -u edi -pedi@zy11 -e "use edi;delete from M_BRAND_MODEL;"

echo "2,overwrite EDI_M_BRAND_MODEL to mysql..."
file="/user/hive/warehouse/edi.db/edi_m_brand_model/"
echo "INFO:export file:$file"
sqoop export --connect 'jdbc:mysql://HadoopMySQL/edi?useUnicode=true&characterEncoding=utf-8' --table 'M_BRAND_MODEL' --username edi --password edi@zy11 --export-dir $file --input-fields-terminated-by '\t' --input-null-string NULL --input-null-non-string NULL 
ecode=$?
echo "INFO:sqoop exit code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:sqoop error.skip $table ,code=$ecode"
fi

echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
