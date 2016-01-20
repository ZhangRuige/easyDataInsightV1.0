## for mysql:
## 前缀m_表示是从hive同步来的数据
## 前缀t_表示永存临时表
##10 23 * * * ~/db_bk/mysql_db_bak.sh hive hive 122575688861167080414279946p963
##35 23 * * * ~/db_bk/mysql_db_bak.sh edi edi 1870356536615775580426p42231


##--正式表---------------------------------------------------------------------------------------------------
create table `m_prod_info` (
  `crawl_date` varchar(1000) default null comment '爬取时间',
  `source` varchar(1000) default null comment '数据来源，如jd.com，tmall.com',
  `prod_id` varchar(200) default null comment '商品id',
  `name` varchar(1000) default null comment '商品销售名',
  `price` float default null,
  `brand` varchar(200) default null comment '品牌',
  `model` varchar(200) default null comment '型号',
  `color` varchar(200) default null,
  `capacity` varchar(200) default null,
  `screen_size` varchar(200) default null,
  `comm_amount` int(11) default null comment '累计评价,如果不能爬到就先空，计算时自己统计',
  `comm_good_amount` int(11) default null comment '好评率 praise rate 与描述相符||好评度，存4位整数，2120 = 21.20%',
  `comm_middle_amount` int(11) default null comment '中评',
  `comm_bad_amount` int(11) default null comment '差评，存4位整数，2120 = 21.20%',
  `hot_tags` varchar(1000) default null,
  `created_dt` varchar(200) default null
) engine=innodb default charset=utf8mb4 comment='商品基本信息表，内部表，以写入当天日期分区存储（如20151107）';

create table `m_prod_comms` (
  `crawl_date` varchar(200) default null comment '爬取时间',
  `id` varchar(200) default null comment '评论id',
  `parent_id` varchar(200) default null comment '父id,本表自循环，关联id',
  `prod_id` varchar(200) default null comment '评论所属商品id',
  `comm_info` text comment '评论内容',
  `comm_time` varchar(200) default null comment '买家评论时间',
  `prod_info` varchar(1000) default null comment '该评论针对的购买信息',
  `publisher_info` varchar(200) default null comment '评论发布人信息',
  `comm_tags` varchar(1000) default null comment '评论标签',
  `comm_praise_amount` int(11) default null comment '评论被赞数量',
  `comm_reply_amount` int(11) comment '评论回复',
  `created_dt` varchar(200) default null
) engine=innodb default charset=utf8mb4 comment='商品评论表，内部表，以写入当天日期分区存储（如20151107）';

create table m_r_amount (
 brand varchar(200) comment '品牌',
 model varchar(200) comment '型号',
 aspect varchar(200) comment '评价纬度',
 attr varchar(200),
 category_val varchar(200) comment '评价得分值',
 amount varchar(200) comment 'sum总数',
 created_dt varchar(200)
) engine=innodb default charset=utf8mb4 comment '商品标签表，对同一商品从不同纬度的评价';

create table `m_model_mention` (
  brand_s varchar(200) comment '标准品牌',
  model_s varchar(200) comment '标准型号',
  brand_m varchar(200) comment '提及品牌',
  model_m varchar(200) comment '提及型号',
  amount int(11) comment '提及次数',
  created_dt varchar(200) default null
) engine=innodb default charset=utf8 comment='型号提及其他型号';

 create table m_prod_params (
 prod_id varchar(200) comment '商品id',
 param varchar(200) comment '规格参数',
 val varchar(1000) comment '参数值'
 ) engine=innodb default charset=utf8mb4 comment='商品规格';
 

##--SQOOP同步永存临时表---------------------------------------------------------------------------------------------------
create table `t_m_prod_comms` (
  `crawl_date` varchar(200) default null comment '爬取时间',
  `id` varchar(200) default null comment '评论id',
  `parent_id` varchar(200) default null comment '父id,本表自循环，关联id',
  `prod_id` varchar(200) default null comment '评论所属商品id',
  `comm_info` text comment '评论内容',
  `comm_time` varchar(200) default null comment '买家评论时间',
  `prod_info` varchar(1000) default null comment '该评论针对的购买信息',
  `publisher_info` varchar(200) default null comment '评论发布人信息',
  `comm_tags` varchar(1000) default null comment '评论标签',
  `comm_praise_amount` int(11) default null comment '评论被赞数量',
  `comm_reply_amount` int(11) comment '评论回复'
) engine=innodb default charset=utf8mb4 comment='商品评论表，内部表，以写入当天日期分区存储（如20151107）';

create table `t_m_prod_info` (
  `crawl_date` varchar(1000) default null comment '爬取时间',
  `source` varchar(1000) default null comment '数据来源，如jd.com，tmall.com',
  `prod_id` varchar(200) default null comment '商品id',
  `name` varchar(1000) default null comment '商品销售名',
  `price` float default null,
  `brand` varchar(200) default null comment '品牌',
  `model` varchar(200) default null comment '型号',
  `color` varchar(200) default null,
  `capacity` varchar(200) default null,
  `screen_size` varchar(200) default null,
  `comm_amount` int(11) default null comment '累计评价,如果不能爬到就先空，计算时自己统计',
  `comm_good_amount` int(11) default null comment '好评率 praise rate 与描述相符||好评度，存4位整数，2120 = 21.20%',
  `comm_middle_amount` int(11) default null comment '中评',
  `comm_bad_amount` int(11) default null comment '差评，存4位整数，2120 = 21.20%',
  `hot_tags` varchar(1000) default null
) engine=innodb default charset=utf8mb4 comment='商品基本信息表，内部表，以写入当天日期分区存储（如20151107）';

create table t_m_prod_params (
 prod_id varchar(200) comment '商品id',
 param varchar(200) comment '规格参数',
 val varchar(1000) comment '参数值'
) engine=innodb default charset=utf8mb4 comment='商品规格';

create table t_m_r_amount (
 brand varchar(200) comment '品牌',
 model varchar(200) comment '型号',
 aspect varchar(200) comment '评价纬度',
 attr varchar(200),
 category_val varchar(200) comment '评价得分值',
 amount varchar(200) comment 'sum总数'
) engine=innodb default charset=utf8mb4 comment '商品标签表，对同一商品从不同纬度的评价';

create table `t_m_model_mention` (
  brand_s varchar(200) comment '标准品牌',
  model_s varchar(200) comment '标准型号',
  brand_m varchar(200) comment '提及品牌',
  model_m varchar(200) comment '提及型号',
  amount int(11) comment '提及次数'
) engine=innodb default charset=utf8 comment='型号提及其他型号';


#全量更新--------------------------------------------------------------------
create table `prod_brand_model` (
  `brand` varchar(200) ,
  `model` varchar(200) 
) engine=innodb default charset=utf8 comment='本系统中所有商品品牌及型号';

##--全量更新---------------------------------------------------------------------------------------------------
create table if not exists m_consumer_dist (
 brand varchar(200) comment '品牌',
 model varchar(200) comment '型号',
 area varchar(200) comment '地域',
 amount int(11) comment '数量'
 ) engine=innodb default charset=utf8mb4 comment='消费者地域分布统计';
  
 create table if not exists comms_mounth_amount (
 brand varchar(200) comment '品牌',
 model varchar(200) comment '型号',
 comm_day varchar(200) comment '月份',
 amount int(11) comment '数量'
 ) engine=innodb default charset=utf8mb4 comment='月份销量分布统计';

create table `model_alias` (
  brand_s varchar(200) comment '标准品牌',
  model_s varchar(200) comment '标准型号',
  alias varchar(200) comment '型号别名',
  is_brand int(1)
) engine=innodb default charset=utf8 comment '型号别名';
 
 
##--其他---------------------------------------------------------------------------------------------------
 
create algorithm=undefined definer=`edi`@`%` sql security definer view `v_comm_day` as 
select substr(`c`.`comm_time`,1,7) as `comm_day`,`c`.`prod_id` as `prod_id` 
from `m_prod_comms` `c`;



#select i.brand,i.model,comm_day as month,count(comm_day) as amount 
#from v_comm_day c left join m_prod_info  i on i.prod_id=c.prod_id
#group by i.brand,i.model,comm_day order by i.brand,i.model,comm_day;


