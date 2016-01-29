#!/bin/bash
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
echo ">>>START .$0 AT $cur_date"

cd /opt/running/edi/op

#>>>1.get the last partition
last_do_pt=`hdfs dfs -ls /edi/edi_conf |grep 'last_transfer_mention_pt' |tail -n 1|cut -f2 -d '='`
echo "last_do_pt=$last_do_pt"
echo "run export ..."

condition=""
if [ "$last_do_pt" != "" ];then
	condition=" WHERE cm.PT_DATE > '$last_do_pt'"
fi

hive -S -e "use edi;INSERT INTO TABLE EDI_M_MODEL_MENTION PARTITION(PT_DATE='$cur_date')
select B.mbs,B.mms,B.abs,B.ams,sum(B.amt) from (
select mm.brand_s as mbs,mm.model_s as mms,ma.brand_s as abs,ma.model_s as ams,mm.amt as amt from (
  select A.brand_s,A.model_s,A.BRAND_m,A.model_m,count(1) as amt
  FROM (
    SELECT i.brand as brand_s,i.model as model_s,cm.BRAND as BRAND_m,cm.model as model_m,c.PROD_ID
    FROM EDI_R_COMM_MENTION cm 
    LEFT JOIN EDI_M_PROD_COMMS c on cm.cid=c.id
    LEFT JOIN EDI_M_PROD_INFO i on i.PROD_ID=c.PROD_ID $condition
  )A GROUP BY A.brand_s,A.model_s,A.BRAND_m,A.model_m 
) mm JOIN edi_m_model_alias ma on upper(mm.model_m)=upper(ma.ALIAS) and ma.IS_BRAND=0 
) B WHERE B.mms!=B.ams
GROUP BY B.mbs,B.mms,B.abs,B.ams;"
if [ $? -ne 0 ];then
	echo "ERROR:hiveQL exec failed.exit $ecode"
	exit $ecode
else
	hdfs dfs -rm -r /edi/edi_conf/last_transfer_mention_pt=*
	hdfs dfs -mkdir -p /edi/edi_conf/last_transfer_mention_pt=$cur_date
	echo "updating... edi_conf key:last_transfer_mention_pt=$cur_date"
fi

echo "END.$0"
echo "spend time(s) :$(( $(date +%s) - $start_time ))"
