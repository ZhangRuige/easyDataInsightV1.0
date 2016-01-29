#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START .$0 AT $cur_date"
source /etc/profile

echo "0.clear table EDI_M_BRAND_MODEL & insert new data."
hive -S -e "INSERT OVERWRITE TABLE EDI.EDI_M_BRAND_MODEL SELECT BRAND,MODEL,count(MODEL) FROM EDI.EDI_M_PROD_INFO GROUP BY BRAND,MODEL;"
ecode=$?
echo "INFO:hive update ,code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:hive error.exit=$ecode"
	exit $ecode
fi

cd "$(dirname "$0")"
sh ../sbin/sqoop_to_mysql.sh m_brand_model -overwrite
ecode=$?
echo "INFO:sqoop exit code=$ecode"
if [ $ecode -ne 0 ];then
	echo "ERROR:sqoop error.skip $table ,code=$ecode"
fi

echo "done!$0"
echo "spend time(s) :"$(( $(date +%s) - $start_time ))
