#!/bin/bash

#1.hive输出评论及评论对象位置列表到文件
#2.分词、词性标注、观点对象标注、分块
#3.参数export_dir为输出分块文件的存储路径

echo "START.$0"
start_time=$(date +%s)
cur_date=`date +%Y%m%d%H%M%S`
source /etc/profile

if [ $# -ne 1 ] ; then
	echo "USAGE: $0 export_dir,use default export_dir 'data/blocks'"
	#exit 0;
	export_dir=/opt/running/edi/nkw/data/blocks
else
	export_dir=$1
fi 

tmp_dir=/opt/running/edi/nkw/tmp/nkw
#create export dir if not exists
if [ ! -d "$tmp_dir" ];then
	mkdir -p "$tmp_dir"
fi
rm -rf $export_dir/*

#>>>1.get the last partition
last_donlp_pt=`hdfs dfs -ls /edi/edi_conf |grep 'last_train_rewrite_pt' |tail -n 1|cut -f2 -d '='`
echo "last_train_rewrite_pt=$last_train_rewrite_pt"
condition=""
if [ "$last_train_rewrite_pt" != "" ];then
        condition=" WHERE PT_DATE > '$last_train_rewrite_pt'"
fi

echo "INFO:hive overwrite dir..."
hive -S -e "use edi;INSERT OVERWRITE LOCAL DIRECTORY '$tmp_dir' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
SELECT CONCAT(C.COMM_TAGS,',',C.COMM_INFO) AS COMM_INFO,B.CTS
FROM EDI_M_PROD_COMMS C JOIN(
 SELECT A.CID,concat_ws('\;',collect_set(A.CT)) AS CTS FROM (
  SELECT COMM_ID AS CID,concat(ATTR_START,',',ATTR_END) AS CT FROM EDI_R_COMM_TAG $condition
 ) A GROUP BY A.CID
)B ON C.ID=B.CID;"
if [ $? -ne 0 ];then
	echo "ERROR:hiveQL exec failure."
	exit 0
fi

echo "time cost(s) :$(( $(date +%s) - $start_time ))"


#echo "INFO:java -classpath com.xxx.jar CRFsUtil $1 $2"
rm -r $tmp_dir/.*

cat $tmp_dir/* > $tmp_dir/infile

cd ../op/
java -classpath lib/ansj_seg-2.0.8.jar:lib/word2vec.jar:lib/nlp-lang-1.0.jar:lib/com.zhongyitech.edi.NLP.omsa-v1.25.jar: com.zhongyitech.edi.NLP.test.ToCVBlocks $tmp_dir/infile $export_dir
if [ $? -ne 0 ];then
	echo "ERROR:java ToCVBlocks failed.code $?"
else
	fc=`ls $export_dir | wc -l`
	echo "INFO:output file count:$fc"
	if [ $fc -lt 4 ];then
		echo "ERROR:output block files count is $fc ,less than 4,give up and delay to next time process.exit with -1"
		echo "time cost(s) :$(( $(date +%s) - $start_time ))"
		exit -1
	else
		hdfs dfs -rm -r -skipTrash /edi/edi_conf/last_train_rewrite_pt=*
		hdfs dfs -mkdir -p /edi/edi_conf/last_train_rewrite_pt=$cur_date
		echo "updating... edi_conf key:last_train_rewrite_pt=$cur_date"
	fi
fi


echo "DONE.$0"
echo "time cost(s) :$(( $(date +%s) - $start_time ))"
