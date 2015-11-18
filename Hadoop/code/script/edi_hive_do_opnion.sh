#!/bin/bash
cd /opt/running/edi/op
echo "DO NLP START."

cur_date=`date +%Y%m%d%H%M%S`
echo $cur_date
echo "export ..."

hive -S -e "use edi;add jars /opt/running/edi/op/lib/ansj_seg-2.0.8.jar 
/opt/running/edi/op/lib/nlp-lang-1.0.jar 
/opt/running/edi/op/lib/word2vec.jar 
/opt/running/edi/op/lib/com.zhongyitech.edi.NLP.omsa-1.5.jar 
/opt/running/edi/op/lib/com.zhongyitech.edi.hive.udf.donlp-1.0.jar;

create temporary function OPUDF as 'com.zhongyitech.edi.hive.udf.OpUDF';

SELECT 	A.ARR[0] AS COMM_ID,
	A.ARR[1] AS ASPECT,
	A.ARR[2] AS ATTR, 
	A.ARR[3] AS CATEGORY,
	A.ARR[4] AS CONTEXT_INDEX 
FROM (SELECT EXPLODE(OPUDF(ID,CONCAT(COMM_TAGS,'\\t',COMM_INFO),PROD_INFO)) AS ARR FROM EDI_M_PROD_COMMS 
) A;" > tmp/edi_r_comm_tag_`echo $cur_date`.td

echo "import ..."
hive -S -e "use edi;LOAD DATA LOCAL INPATH 'tmp/edi_r_comm_tag_`echo $cur_date`.td' INTO TABLE EDI_R_COMM_TAG PARTITION (PT_DATE='`echo $cur_date`');"

#rm tmp/edi_r_comm_tag_`echo $cur_date`.td

echo "DO NLP END."
