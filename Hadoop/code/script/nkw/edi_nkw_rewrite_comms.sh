#!/bin/bash

#1.hive输出评论及评论对象位置列表到文件
#2.分词、词性标注、观点对象标注、分块
#3.参数export_dir为输出分块文件的存储路径

cd /opt/running/edi/op
echo "START.$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`


if [ $# -ne 1 ] ; then
	echo "USAGE: $0 export_dir,use default export_dir 'data/blocks'"
	#exit 0;
	export_dir=data/blocks
else
	export_dir=$1
fi 

tmp_dir="tmp/nkw$cur_date"

#create export dir if not exists
if [ ! -d "$tmp_dir" ];then
	mkdir -p "$tmp_dir"
fi

echo "tmp_dir=$tmp_dir ,export_dir=$export_dir,pwd=`pwd`"

echo "INFO:executing hiveQL :"
hql="INSERT OVERWRITE LOCAL DIRECTORY '$tmp_dir' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
SELECT CONCAT(C.COMM_TAGS,',',C.COMM_INFO) AS COMM_INFO,B.CTS
FROM EDI_M_PROD_COMMS C JOIN(
 SELECT A.CID,concat_ws('\;',collect_set(A.CT)) AS CTS FROM (
  SELECT COMM_ID AS CID,concat(ASPT_START,',',ASPT_END) AS CT FROM EDI_R_COMM_TAG 
 ) A GROUP BY A.CID
)B ON C.ID=B.CID;"

#TODO add where condition partition

echo $hql

/opt/running/apache-hive-1.2.1-bin/bin/hive -e "use edi;$hql"

if [ $? -ne 0 ];then
	echo "ERROR:hiveQL exec failure."
	exit 0
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"


echo "INFO:java -classpath com.xxx.jar CRFsUtil $1 $2"
infile=`ls -t "$tmp_dir" | head -n 1`
echo $infile
java -classpath lib/ansj_seg-2.0.8.jar:lib/word2vec.jar:lib/nlp-lang-1.0.jar:lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar: com.zhongyitech.edi.NLP.test.ToCVBlocks $tmp_dir/$infile $export_dir

echo $?

echo "DONE.$0"
echo "time cost(s) :$(( $(date +%s) - $start_time ))"
