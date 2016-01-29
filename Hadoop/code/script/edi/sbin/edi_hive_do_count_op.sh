#!/bin/bash
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START .$0 AT $cur_date"
source /etc/profile

hive -S -e "USE EDI;INSERT OVERWRITE TABLE EDI_M_R_AMOUNT 
SELECT P.BRAND,P.MODEL,R.ASPECT,R.ATTR,sum(case R.CATEGORY when 1 then 1 else 0 end)/count(R.CATEGORY) ,count(R.CATEGORY) 
from EDI_R_COMM_TAG R 
 left join EDI_M_PROD_COMMS C on R.COMM_ID=C.ID
 left join EDI_M_PROD_INFO P on P.PROD_ID=C.PROD_ID
group by P.BRAND,P.MODEL,R.ASPECT,R.ATTR;" 


sh ./sqoop_to_mysql.sh m_r_amount -overwrite
ecode=$?
echo "INFO:sqoop exit code=$ecode"
if [ $ecode -ne 0 ];then
        echo "ERROR:sqoop error.skip $table ,code=$ecode"
fi

echo "END.reture code=$?"
echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
