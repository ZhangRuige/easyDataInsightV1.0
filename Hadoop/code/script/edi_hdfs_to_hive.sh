#!/bin/bash

echo "1111111"
cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

#products
hive -S -e "LOAD DATA LOCAL INPATH '../data/product.txt' INTO TABLE edi.EDI_M_PROD_INFO PARTITION (PT_DATE='$cur_date');"

# comments
hive -S -e "LOAD DATA LOCAL INPATH '../data/comments.txt' INTO TABLE edi.EDI_M_PROD_COMMS PARTITION (PT_DATE='$cur_date');"
