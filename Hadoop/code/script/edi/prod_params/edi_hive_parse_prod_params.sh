#!/bin/bash

start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START $0 AT $cur_date"
source /etc/profile

if [ $# -lt 1 ];then
	echo "Usage:$0 prod.pt_date."
	exit 0
fi

#filepath=$(cd "$(dirname "$0")"; pwd)
cd "$(dirname "$0")"

hive -S -e "use edi;add file ./prod_params_prase.py;insert into table edi_m_prod_params select transform(concat(A.prod_id,'\t',A.params)) USING 'prod_params_prase.py' as a,b,c from (select * from edi_m_prod_info where pt_date='$1') A;"
ecode=$?
if [ $ecode -ne 0 ];then
       	echo "ERROR:$0 ,error code=$ecode"
else
	sh ../sbin/sqoop_to_mysql.sh m_prod_params -overwrite	#optimize
fi

echo ">>>$0 DONE.spend time(s) :$(( $(date +%s) - $start_time ))"

