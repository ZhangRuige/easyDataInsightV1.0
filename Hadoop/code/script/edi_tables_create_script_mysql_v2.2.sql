-- for MYSQL:
-- 前缀M_表示是从hive同步来的数据
-- 前缀T_表示永存临时表
--10 23 * * * ~/db_bk/mysql_db_bak.sh hive hive 122575688861167080414279946P963
--35 23 * * * ~/db_bk/mysql_db_bak.sh edi edi 1870356536615775580426P42231



--1.m_r_amount
CREATE TABLE IF NOT EXISTS M_R_AMOUNT (
 BRAND VARCHAR(200) COMMENT '品牌',
 MODEL VARCHAR(200) COMMENT '型号',
 ASPECT VARCHAR(200) COMMENT '评价纬度',
 ATTR VARCHAR(200),
 CATEGORY_VAL VARCHAR(200) COMMENT '评价得分值',
 AMOUNT VARCHAR(200) COMMENT 'sum总数',
 CREATED_DT VARCHAR(200)
) COMMENT '商品标签表，对同一商品从不同纬度的评价';


--2.m_prod_comms
CREATE TABLE IF NOT EXISTS `M_PROD_COMMS` (
  `CRAWL_DATE` varchar(200) DEFAULT NULL COMMENT '爬取时间',
  `ID` varchar(200) DEFAULT NULL COMMENT '评论ID',
  `PARENT_ID` varchar(200) DEFAULT NULL COMMENT '父ID,本表自循环，关联ID',
  `PROD_ID` varchar(200) DEFAULT NULL COMMENT '评论所属商品ID',
  `COMM_INFO` text COMMENT '评论内容',
  `COMM_TIME` varchar(200) DEFAULT NULL COMMENT '买家评论时间',
  `PROD_INFO` varchar(1000) DEFAULT NULL COMMENT '该评论针对的购买信息',
  `PUBLISHER_INFO` varchar(200) DEFAULT NULL COMMENT '评论发布人信息',
  `COMM_TAGS` varchar(1000) DEFAULT NULL COMMENT '评论标签',
  `COMM_PRAISE_AMOUNT` int(11) DEFAULT NULL COMMENT '评论被赞数量',
  `COMM_REPLY_AMOUNT` int(11) COMMENT '评论回复',
  `CREATED_DT` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 
COMMENT='商品评论表，内部表，以写入当天日期分区存储（如20151107）';


--3.m_prod_info
CREATE TABLE `M_PROD_INFO` (
  `CRAWL_DATE` varchar(1000) DEFAULT NULL COMMENT '爬取时间',
  `SOURCE` varchar(1000) DEFAULT NULL COMMENT '数据来源，如JD.COM，TMALL.COM',
  `PROD_ID` varchar(200) DEFAULT NULL COMMENT '商品ID',
  `NAME` varchar(1000) DEFAULT NULL COMMENT '商品销售名',
  `PRICE` float DEFAULT NULL,
  `BRAND` varchar(200) DEFAULT NULL COMMENT '品牌',
  `MODEL` varchar(200) DEFAULT NULL COMMENT '型号',
  `COLOR` varchar(200) DEFAULT NULL,
  `CAPACITY` varchar(200) DEFAULT NULL,
  `SCREEN_SIZE` varchar(200) DEFAULT NULL,
  `COMM_AMOUNT` int(11) DEFAULT NULL COMMENT '累计评价,如果不能爬到就先空，计算时自己统计',
  `COMM_GOOD_AMOUNT` int(11) DEFAULT NULL COMMENT '好评率 PRAISE RATE 与描述相符||好评度，存4位整数，2120 = 21.20%',
  `COMM_MIDDLE_AMOUNT` int(11) DEFAULT NULL COMMENT '中评',
  `COMM_BAD_AMOUNT` int(11) DEFAULT NULL COMMENT '差评，存4位整数，2120 = 21.20%',
  `HOT_TAGS` varchar(1000) DEFAULT NULL,
  `CREATED_DT` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品基本信息表，内部表，以写入当天日期分区存储（如20151107）';



--=====================================================

--1.t_m_r_amount
CREATE TABLE IF NOT EXISTS T_M_R_AMOUNT (
 BRAND VARCHAR(200) COMMENT '品牌',
 MODEL VARCHAR(200) COMMENT '型号',
 ASPECT VARCHAR(200) COMMENT '评价纬度',
 ATTR VARCHAR(200),
 CATEGORY_VAL VARCHAR(200) COMMENT '评价得分值',
 AMOUNT VARCHAR(200) COMMENT 'sum总数'
) COMMENT '商品标签表，对同一商品从不同纬度的评价';


--2.t_m_prod_comms
CREATE TABLE `T_M_PROD_COMMS` (
  `CRAWL_DATE` varchar(200) DEFAULT NULL COMMENT '爬取时间',
  `ID` varchar(200) DEFAULT NULL COMMENT '评论ID',
  `PARENT_ID` varchar(200) DEFAULT NULL COMMENT '父ID,本表自循环，关联ID',
  `PROD_ID` varchar(200) DEFAULT NULL COMMENT '评论所属商品ID',
  `COMM_INFO` text COMMENT '评论内容',
  `COMM_TIME` varchar(200) DEFAULT NULL COMMENT '买家评论时间',
  `PROD_INFO` varchar(1000) DEFAULT NULL COMMENT '该评论针对的购买信息',
  `PUBLISHER_INFO` varchar(200) DEFAULT NULL COMMENT '评论发布人信息',
  `COMM_TAGS` varchar(1000) DEFAULT NULL COMMENT '评论标签',
  `COMM_PRAISE_AMOUNT` int(11) DEFAULT NULL COMMENT '评论被赞数量',
  `COMM_REPLY_AMOUNT` int(11) COMMENT '评论回复'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表，内部表，以写入当天日期分区存储（如20151107）';


--3.t_m_prod_info
CREATE TABLE `T_M_PROD_INFO` (
  `CRAWL_DATE` varchar(1000) DEFAULT NULL COMMENT '爬取时间',
  `SOURCE` varchar(1000) DEFAULT NULL COMMENT '数据来源，如JD.COM，TMALL.COM',
  `PROD_ID` varchar(200) DEFAULT NULL COMMENT '商品ID',
  `NAME` varchar(1000) DEFAULT NULL COMMENT '商品销售名',
  `PRICE` float DEFAULT NULL,
  `BRAND` varchar(200) DEFAULT NULL COMMENT '品牌',
  `MODEL` varchar(200) DEFAULT NULL COMMENT '型号',
  `COLOR` varchar(200) DEFAULT NULL,
  `CAPACITY` varchar(200) DEFAULT NULL,
  `SCREEN_SIZE` varchar(200) DEFAULT NULL,
  `COMM_AMOUNT` int(11) DEFAULT NULL COMMENT '累计评价,如果不能爬到就先空，计算时自己统计',
  `COMM_GOOD_AMOUNT` int(11) DEFAULT NULL COMMENT '好评率 PRAISE RATE 与描述相符||好评度，存4位整数，2120 = 21.20%',
  `COMM_MIDDLE_AMOUNT` int(11) DEFAULT NULL COMMENT '中评',
  `COMM_BAD_AMOUNT` int(11) DEFAULT NULL COMMENT '差评，存4位整数，2120 = 21.20%',
  `HOT_TAGS` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品基本信息表，内部表，以写入当天日期分区存储（如20151107）';


CREATE TABLE `PROD_BRAND_MODEL` (
  `MID` int(11) NOT NULL AUTO_INCREMENT,
  `BRAND` varchar(200) DEFAULT NULL,
  `MODEL` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`MID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='本系统中所有商品品牌及型号';

CREATE TABLE `MODEL_ALIAS` (
  `mid` int(11) NOT NULL COMMENT '品牌型号ID',
  `ALIAS` varchar(200) DEFAULT NULL COMMENT '型号别名',
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '型号别名';

CREATE TABLE `MODEL_MENTION` (
  `MID` int(11) NOT NULL COMMENT '型号ID',
  `MENTION_MID` int(11) DEFAULT NULL COMMENT '提及的型号ID',
  PRIMARY KEY (`MID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='型号提及其他型号';

CREATE TABLE `M_COMM_MENTION` (
  `CID` int(11) NOT NULL COMMENT '型号ID',
  `BRAND` varchar(200) DEFAULT NULL,
  `MODEL` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`MID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论提及型号原始数据';

CREATE TABLE IF NOT EXISTS M_CONSUMER_DIST (
 BRAND varchar(200) COMMENT '品牌',
 MODEL varchar(200) COMMENT '型号',
 AREA varchar(200) COMMENT '地域',
 AMOUNT int(11) COMMENT '数量'
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费者地域分布统计'
 
 
 CREATE TABLE IF NOT EXISTS COMMENT_MOUNTH_AMOUNT (
 BRAND varchar(200) COMMENT '品牌',
 MODEL varchar(200) COMMENT '型号',
 COMM_DAY varchar(200) COMMENT '月份',
 AMOUNT int(11) COMMENT '数量'
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月份销量分布统计'
 
 
 
 
 
 
 
-------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------

CREATE ALGORITHM = UNDEFINED DEFINER = `edi`@`%` SQL SECURITY DEFINER VIEW `V_MODEL_COMMS_AMOOUNT` AS SELECT
	`d`.`BRAND` AS `BRAND`,
	`d`.`MODEL` AS `MODEL`,
	sum(`d`.`AMOUNT`) AS `AMOUNT`
FROM
	`M_CONSUMER_DIST` `d`
GROUP BY
	`d`.`BRAND`,
	`d`.`MODEL`;
	
	
 
CREATE ALGORITHM = UNDEFINED DEFINER = `edi`@`%` SQL SECURITY DEFINER VIEW `V_PROD_BRAND_MODEL` AS SELECT
	`p`.`BRAND` AS `BRAND`,
	`p`.`MODEL` AS `model`
FROM
	`M_PROD_INFO` `p`
GROUP BY
	`p`.`BRAND`,
	`p`.`MODEL`;
 
 
-- PROCEDURE
CREATE DEFINER=`edi`@`%` PROCEDURE `porc_edi_brand_model_update`()
BEGIN
	#Routine body goes here...
	INSERT into PROD_BRAND_MODEL(BRAND,MODEL )
	SELECT BRAND,MODEL 
	FROM (
			SELECT BRAND,MODEL FROM M_PROD_INFO GROUP BY BRAND,MODEL 
	) A WHERE NOT EXISTS (
			SELECT 1 FROM PROD_BRAND_MODEL WHERE BRAND=A.BRAND AND MODEL=A.MODEL
	);
END
/

-- EVENT
CREATE DEFINER = `edi`@`%` EVENT `event_edi_brand_model_update` 
ON SCHEDULE 
EVERY 1 DAY 
STARTS '2015-12-09 17:45:30' ON COMPLETION NOT PRESERVE ENABLE 
DO
	CALL porc_edi_brand_model_update ();
/


CREATE ALGORITHM=UNDEFINED DEFINER=`edi`@`%` SQL SECURITY DEFINER VIEW `V_COMM_DAY` AS 
select substr(`c`.`COMM_TIME`,1,7) AS `COMM_DAY`,`c`.`PROD_ID` AS `PROD_ID` 
from `M_PROD_COMMS` `c`
/


SELECT i.BRAND,i.MODEL,COMM_DAY as MONTH,count(COMM_DAY) as AMOUNT 
FROM V_COMM_DAY c
LEFT JOIN M_PROD_INFO  i on i.PROD_ID=c.PROD_ID
GROUP BY i.BRAND,i.MODEL,COMM_DAY 
ORDER BY i.BRAND,i.MODEL,COMM_DAY;


