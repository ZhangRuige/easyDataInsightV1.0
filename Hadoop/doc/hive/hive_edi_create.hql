
USE hive;

--源表用S_为前缀
--临时表用T_为前缀
--结果表用R_为前缀
--跨SQL的临时表用M_为前缀
--然后以项目名开头，如S_EDI_XX
--1.EDI_S_PROD_DATA
CREATE TABLE IF NOT EXISTS EDI_S_PROD_DATA (
 SOURCE STRING COMMENT '数据来源，如JD.COM，TMALL.COM',
 PROD_ID STRING COMMENT '商品ID,在抓取到的ID前+来源，如JD2035688,TMALL523814861817',
 NAME STRING COMMENT '商品销售名',
 PRICE FLOAT,
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号',
 COLOR STRING,
 CAPACITY STRING,
 SCREEN_SIZE STRING,
 COMMENT_INFO STRING,
 COMMENT_AMOUNT INT COMMENT '累计评价,如果不能爬到就先空，计算时自己统计',
 COMMENT_REPLY STRING,
 COMMENT_TIME STRING,
 PRAISE_AMOUNT INT COMMENT '评论被赞数量',
 COMMENT_TAGS STRING COMMENT '销售网站的买家印象标签',
 COMMENT_GOOD_AMOUNT INT COMMENT '好评率 PRAISE RATE 与描述相符||好评度，存4位整数，2120 = 21.20%',
 COMMENT_BAD_AMOUNT INT COMMENT '差评，存4位整数，2120 = 21.20%',
 COMMENT_MIDDLE_AMOUNT INT COMMENT '中评')
COMMENT '商品基本信息表，内部表，以写入当天日期分区存储（如20151107）'
PARTITIONED BY (PT_DATE STRING)
STORED AS TEXTFILE;


--2.EDI_PRODUCT_INFO --DELETED
CREATE TABLE IF NOT EXISTS EDI_PRODUCT_INFO (
 SOURCE STRING COMMENT '数据来源，如JD.COM，TMALL.COM',
 PROD_ID STRING COMMENT '商品ID,在抓取到的ID前+来源，如JD2035688,TMALL523814861817',
 NAME STRING COMMENT '商品销售名',
 PRICE FLOAT,
 BRAND STRING COMMENT '品牌',
 MODEL STRING COMMENT '型号',
 COLOR STRING,
 CAPACITY STRING,
 SCREEN_SIZE STRING,
 COMMENT_AMOUNT INT COMMENT '累计评价,如果不能爬到就先空，计算时自己统计',
 COMMENT_GOOD_AMOUNT INT COMMENT '好评率 PRAISE RATE 与描述相符||好评度，存4位整数，2120 = 21.20%',
 COMMENT_BAD_AMOUNT INT COMMENT '差评，存4位整数，2120 = 21.20%',
 COMMENT_MIDDLE_AMOUNT INT COMMENT '中评')
COMMENT '商品基本信息表，内部表，以写入当天日期分区存储（如20151107）'
PARTITIONED BY (PT_DATE STRING)
STORED AS TEXTFILE;


--3.EDI_PROD_COMMENTS
CREATE TABLE IF NOT EXISTS EDI_PROD_COMMENTS (
 ID STRING COMMENT '评论ID',
 PARENT_ID STRING COMMENT '父ID,本表自循环，关联ID',
 PROD_ID STRING COMMENT '评论所属商品ID',
 COMMENT_INFO STRING COMMENT '评论内容',
 COMMENT_TIME STRING COMMENT '买家评论时间',
 PROD_INFO STRING COMMENT '该评论针对的购买信息',
 PUBLISHER_INFO STRING COMMENT '评论发布人信息',
 PRAISE_COUNT INT COMMENT '评论被赞数量',
 COMMENT_TAGS STRING COMMENT '评论标签',
 COMMENT_REPLY STRING COMMENT '评论回复')
COMMENT '商品评论表，内部表，以写入当天日期分区存储（如20151107）'
PARTITIONED BY (PT_DATE STRING)
STORED AS TEXTFILE;



--4.EDI_R_COMMENT_TAG,该表数据量最大
CREATE TABLE IF NOT EXISTS EDI_R_COMMENT_TAG (
 COMM_ID STRING COMMENT '评论ID',
 ASPECT STRING COMMENT '评价纬度',
 CATEGORY INT COMMENT '评价得分',
 CONTEXT_INDEX STRING COMMENT '上下文 观点句位置（START,END）')
COMMENT '评论标签表，从不同纬度评价该条评论的结果数据'
PARTITIONED BY (PT_DATE STRING)
STORED AS TEXTFILE;



--5.EDI_M_PROD_TAG
CREATE TABLE IF NOT EXISTS EDI_M_PROD_TAG (
 PROD_ID STRING COMMENT '商品ID',
 ASPECT STRING COMMENT '评价纬度',
 CATEGORY INT COMMENT '评价得分'
 ) COMMENT '商品标签表，对同一商品从不同纬度的评价'
PARTITIONED BY (PT_DATE STRING)
STORED AS TEXTFILE;
