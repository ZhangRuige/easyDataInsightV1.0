#!/bin/bash
start_time=$(date +%s)
echo "START.$0"

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date
echo "running ..."

hive -S -e "USE EDI;INSERT OVERWRITE TABLE EDI_M_R_AMOUNT PARTITION(PT_DATE='`echo $cur_date`')
SELECT P.BRAND,P.MODEL,R.ASPECT,R.ATTR,sum(R.CATEGORY) ,count(R.CATEGORY) 
from EDI_R_COMM_TAG R 
 left join EDI_M_PROD_COMMS C on R.COMM_ID=C.ID
 left join EDI_M_PROD_INFO P on P.PROD_ID=C.PROD_ID
group by P.BRAND,P.MODEL,R.ASPECT,R.ATTR;" 


echo "END.reture code=$?"
echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
