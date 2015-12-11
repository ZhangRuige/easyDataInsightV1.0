--for hive edi;
-- 前缀EDI_M_表示要同步到mysql去的数据
-- 有前缀R_表示是计算结果数据
-- 前缀N_表示是new data数据源接口表


USE EDI:

--数据源表加S_前缀
--临时表加T_前缀
--结果表加R_前缀
--需跨DB的表加M_前缀
--然后hive表都以项目简写前缀，如EDI_XX


LOAD DATA LOCAL INPATH '/opt/running/edi/op/data/part-r-00000_2.5w' INTO TABLE EDI_N_PROD_COMMS PARTITION (PT_DATE='20151207102010');

INSERT OVERWRITE LOCAL DIRECTORY '/opt/d3' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
select key_,id,guid,content,creationTime,isTop,referenceId,referenceImage,referenceName,referenceTime,referenceType,referenceTypeId,firstCategory,secondCategory,thirdCategory,replyCount,score,status,usefulVoteCount,uselessVoteCount,userImage,userImageUrl,userLevelId,userProvince,userRegisterTime,viewCount,orderId,isReplyGrade,nickname,userClient,productColor,productSize,integral,anonymousFlag,userLevelName,recommend,userLevelColor,userClientShow,isMobile,days,COMM_TAGS
from EDI_N_PROD_COMMS1
group by key_,id,guid,content,creationTime,isTop,referenceId,referenceImage,referenceName,referenceTime,referenceType,referenceTypeId,firstCategory,secondCategory,thirdCategory,replyCount,score,status,usefulVoteCount,uselessVoteCount,userImage,userImageUrl,userLevelId,userProvince,userRegisterTime,viewCount,orderId,isReplyGrade,nickname,userClient,productColor,productSize,integral,anonymousFlag,userLevelName,recommend,userLevelColor,userClientShow,isMobile,days,COMM_TAGS



--###
--0.EDI_N_PROD_COMMS
CREATE TABLE IF NOT EXISTS EDI_N_PROD_COMMS (
  key_ STRING,
  id STRING,
  guid STRING,
  content STRING,
  creationTime STRING,
  isTop STRING,
  referenceId STRING,
  referenceImage STRING,
  referenceName STRING,
  referenceTime STRING,
  referenceType STRING,
  referenceTypeId INT,
  firstCategory INT,
  secondCategory INT,
  thirdCategory INT,
  replyCount INT,
  score INT,
  status INT,
  usefulVoteCount INT,
  uselessVoteCount INT,
  userImage STRING,
  userImageUrl STRING,
  userLevelId STRING,
  userProvince STRING,
  userRegisterTime STRING,
  viewCount INT,
  orderId INT,
  isReplyGrade STRING,
  nickname STRING,
  userClient INT,
  productColor STRING,
  productSize STRING,
  integral INT,
  anonymousFlag INT,
  userLevelName STRING,
  recommend STRING,
  userLevelColor STRING,
  userClientShow STRING,
  isMobile STRING,
  days INT,
  COMM_TAGS STRING
)COMMENT 'JD商品评论新数据接口表'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;


-------------------------------------------------------------------------

--1.EDI_S_PROD_DATA --deleted

--####LOAD DATA LOCAL INPATH '/opt/running/edi/op/data/product.txt' INTO TABLE EDI_M_PROD_INFO PARTITION (PT_DATE='20151207102010');

--2.EDI_M_PROD_INFO --DELETED
CREATE TABLE IF NOT EXISTS EDI_M_PROD_INFO (
 CRAWL_DATE STRING COMMENT '爬取时间',
 SOURCE STRING COMMENT '数据来源，如JD.COM，TMALL.COM',
 PROD_ID STRING COMMENT '商品ID',
 NAME STRING COMMENT '商品销售名',
 PRICE FLOAT,
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号',
 COLOR STRING,
 COMM_AMOUNT INT COMMENT '累计评价,如果不能爬到就先空，计算时自己统计',
 COMM_GOOD_AMOUNT INT COMMENT '好评率 PRAISE RATE 与描述相符||好评度，存4位整数，2120 = 21.20%',
 COMM_BAD_AMOUNT INT COMMENT '差评，存4位整数，2120 = 21.20%',
 COMM_MIDDLE_AMOUNT INT COMMENT '中评',
 HOT_TAGS STRING COMMENT '商品热点标签',
 PARAMS STRING COMMENT '规格参数')
COMMENT '商品基本信息表，内部表，以写入当天日期分区存储（如20151107）'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;	



--###LOAD DATA LOCAL INPATH '/opt/running/edi/op/data/comments.txt' INTO TABLE EDI_M_PROD_COMMS PARTITION (PT_DATE='20151207102010');
--###导入的文件数据应该是UTF-8的UNIX编码

--3.EDI_M_PROD_COMMS
CREATE TABLE IF NOT EXISTS EDI_M_PROD_COMMS (
 CRAWL_DATE STRING COMMENT '爬取时间',
 ID STRING COMMENT '评论ID',
 PARENT_ID STRING COMMENT '父ID,本表自循环，关联ID',
 PROD_ID STRING COMMENT '评论所属商品ID',
 COMM_INFO STRING COMMENT '评论内容',
 COMM_TIME STRING COMMENT '买家评论时间',
 PROD_INFO STRING COMMENT '该评论针对的购买信息',
 PUBLISHER_INFO STRING COMMENT '评论发布人信息',
 COMM_TAGS STRING COMMENT '评论标签',
 COMM_PRAISE_AMOUNT INT COMMENT '评论被赞数量',
 COMM_REPLY_AMOUNT INT COMMENT '评论回复数量')
COMMENT '商品评论表，内部表，以写入当天日期分区存储（如20151107）'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;



--###select A.arr[0],A.arr[1],A.arr[2],A.arr[3] from (SELECT explode(OPUDF(COMM_INFO,PROD_INFO)) as arr FROM EDI_PROD_COMMENTS ) A;
--###ALTER TABLE table_name PARTITION (dt='2008-08-08') RENAME TO PARTITION (dt='20080808');

--4.EDI_R_COMM_TAG,该表数据量最大
CREATE TABLE IF NOT EXISTS EDI_R_COMM_TAG (
 COMM_ID STRING COMMENT '评论ID',
 ASPECT STRING COMMENT '评价纬度',
 ATTR STRING,
 CATEGORY INT COMMENT '评价得分',
 CONTEXT_START STRING COMMENT '上下文 观点句位置（START,END）',
 CONTEXT_END   STRING COMMENT '上下文 观点句位置（START,END）',
 ATTR_START STRING,
 ATTR_END   STRING,
 ASPT_START STRING,
 ASPT_END   STRING,
 SENTI_START STRING,
 SENTI_END   STRING
 )
COMMENT '评论标签表，从不同纬度评价该条评论的结果数据'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;

--####生成CRF入口数据
--INSERT OVERWRITE LOCAL DIRECTORY '/opt/d1' ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'
--SELECT CONCAT(C.COMM_TAGS,"",C.COMM_INFO) AS COMM_INFO,B.CTS
--FROM EDI_M_PROD_COMMS C JOIN(
-- SELECT A.CID,concat_ws('\;',collect_set(A.CT)) AS CTS FROM (
--  SELECT COMM_ID AS CID,CONCAT(ASPT_START,",",ASPT_END) AS CT FROM EDI_R_COMM_TAG 
-- ) A GROUP BY A.CID
--)B ON C.ID=B.CID WHERE C.PT_DATE='20151123111610';

--####去重复
--SELECT * FROM (SELECT *,ROW_NUMBER() OVER(DISTRIBUTE  BY ID SORT BY PT_DATE DESC ) RN  FROM EDI_M_PROD_COMMS) T WHERE T.RN=1;

--###INSERT OVERWRITE [LOCAL] DIRECTORY directory1 [ROW FORMAT row_format] SELECT ... FROM ...
--###[INSERT OVERWRITE TABLE tablename2 [PARTITION ... [IF NOT EXISTS]] select_statement2][INSERT INTO TABLE tablename2 [PARTITION ...] select_statement2]

--5.EDI_M_R_AMOUNT
CREATE TABLE IF NOT EXISTS EDI_M_R_AMOUNT (
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号',
 ASPECT STRING COMMENT '评价纬度',
 ATTR STRING,
 CATEGORY_VAL INT COMMENT '评价得分值',
 AMOUNT INT COMMENT 'sum总数'
 ) COMMENT '评论标签汇总'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;


--6.EDI_M_COMM_MENTION
CREATE TABLE IF NOT EXISTS EDI_M_COMM_MENTION (
 CID STRING COMMENT '评论ID',
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号'
 ) COMMENT '评论中提及型号表'
PARTITIONED BY (PT_DATE STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;


--INSERT OVERWRITE TABLE EDI_M_CONSUMER_DIST SELECT * FROM EDI_M_CONSUMER_DIST LIMIT 0;

--INSERT OVERWRITE TABLE EDI_M_CONSUMER_DIST
--SELECT A.BRAND,A.MODEL,REGEXP_REPLACE(A.AREA,"^$","其他"),A.AMOUNT FROM (
--SELECT I.BRAND,I.MODEL,C.USERPROVINCE as AREA,COUNT(C.ID) as AMOUNT FROM EDI_N_PROD_COMMS C 
--LEFT JOIN EDI_M_PROD_INFO I ON C.REFERENCEID=I.PROD_ID
--GROUP BY I.BRAND,I.MODEL,C.USERPROVINCE ) A
--ORDER BY A.AMOUNT DESC;


--7.EDI_M_CONSUMER_DISTRIBUTION
CREATE TABLE IF NOT EXISTS EDI_M_CONSUMER_DIST (
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号',
 AREA STRING COMMENT '地域',
 AMOUNT INT COMMENT '数量'
 ) COMMENT '消费者地域分布统计'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;


