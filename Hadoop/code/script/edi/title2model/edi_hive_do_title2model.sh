#!/bin/bash
start_time=$(date +%s)
echo "START.$0"

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date

if [ -z $1 ];then
	echo "*****Input the necessary parameters: pt_date"
        exit
fi

pt_date=$1

source /etc/profile
cd /opt/running/edi/op
tmp_file=tmp/edi_new_prod_info_"$cur_date".td

echo "running ..."
echo "INFO:update dictionary of title2model"
hive -S -e "USE EDI;INSERT OVERWRITE LOCAL DIRECTORY '/opt/running/edi/tmp/brand_models_$pt_date' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n' select brand,collect_list(model) from edi_m_brand_model where brand!=model group by brand;"

cat /opt/running/edi/tmp/brand_models_$pt_date/000000_0 > /opt/running/edi/op/dicts/brand_models.txt
rm -r /opt/running/edi/tmp/brand_models_$pt_date

echo "INFO:extract model from product name."
hive -e "USE EDI;
add jars /opt/running/edi/op/lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar
/opt/running/edi/op/lib/com.zhongyitech.edi.hive.udf.donlp-1.1.jar;
create temporary function Title2ModelUDF as 'com.zhongyitech.edi.hive.udf.Title2ModelUDF';
select crawl_date,source,prod_id,name,price,brand,if(model=brand,Title2ModelUDF(name,brand),model),
color,comm_amount,comm_good_amount,comm_middle_amount,comm_bad_amount,hot_tags,params
from edi_m_prod_info where pt_date='$pt_date';" > $tmp_file
ecode=$?
if [ $ecode -ne 0 ];then
        echo "ERROR:hive error.code=$ecode"
fi

if [ ! -s $tmp_file ];then  #string length is zero
        echo "0 records.exit."
        exit 1
fi

echo "run import ..."
hive -S -e "use edi;LOAD DATA LOCAL INPATH '$tmp_file' OVERWRITE INTO TABLE edi_m_prod_info PARTITION (PT_DATE='$pt_date');"
rm tmp/edi_new_prod_info_"$cur_date".td


echo 'spend time(s) :'$(( $(date +%s) - $start_time ))
