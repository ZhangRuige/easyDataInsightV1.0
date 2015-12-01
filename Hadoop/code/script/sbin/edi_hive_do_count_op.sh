#!/bin/bash
start_time=$(date +%s)
echo "START.$0"

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date
echo "running ..."

hive -S -e "use edi;INSERT OVERWRITE TABLE EDI_M_R_AMOUNT PARTITION(PT_DATE='`echo $cur_date`')
SELECT p.BRAND,P.MODEL,R.ASPECT,R.ATTR,sum(R.CATEGORY) ,count(R.CATEGORY) 
from EDI_R_COMM_TAG R 
 join EDI_M_PROD_COMMS C on R.COMM_ID=C.ID
 join EDI_M_PROD_INFO P on P.PROD_ID=C.PROD_ID
group by p.BRAND,P.MODEL,R.ASPECT,R.ATTR;" 


echo "END.$0"
echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
