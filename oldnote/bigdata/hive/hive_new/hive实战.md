---
title: hive实战
categories: hive  
tags: [hive]
---



# hive数据库创建时的编码问题

```
javax.jdo.option.connectionURL
jdbc:mysql://192.168.0.11:3306/metastore_hive_db?createDatabaseIfNotExist=true&amp;characterEncoding=UTF-8
```
如果数据库不存在,那么创建的库为utf-8,而hive不支持utf-8的格式,将会出现乱码的情况,此时我们需要手动在mysql库中创建metastore_hive_db,编码为latin1格式,这样就能解决这个问题

1.手动创建元数据库,设置编码为latin1
```
jdbc:mysql://192.168.0.11:3306/metastore_hive_db?createDatabaseIfNotExist=true&amp;characterEncoding=UTF-8
```
2.启动hive让其生成元数据库中的表


3.修改表字段注解和表注解

```
alter table COLUMNS_V2 modify column comment varchar(256) character set utf8;

alter table TABLE_PARAMS modify column PARAM_VALUE varchar(4000) character set utf8;

```

4.修改分区字段注解
```
alter table PARTITION_PARAMS modify column PARAM_VALUE varchar(4000) character set utf8;

alter table PARTITION_KEYS modify column PKEY_COMMENT varchar(4000) character set utf8;
```

5.修改索引注释
```
alter table INDEX_PARAMS modify column PARAM_VALUE varchar(4000) character set utf8;

```

6.去hive中创建建库,建表

```
create database ods;
create database dw;
create database dim;
create database app;


#然后去每个库下面建我们需要的表,如:

CREATE TABLE `dim.dim_category` (
  `tree_id` int COMMENT '分类树ID',
  `tree_name` varchar(64) COMMENT '分类树名称',
  `category_id` int COMMENT '分类ID',
  `category_name` varchar(64) COMMENT '分类名称',
  `category_type` int COMMENT '分类类型0后台分类1前台分类',
  `parent_id` int COMMENT '上级分类树根的是0',
  `parent_name` varchar(64) COMMENT '父分类名称',
  `layer` tinyint COMMENT '层级',
  `sort` int COMMENT '分类排序',
  `path` varchar(255) COMMENT '所有上级分类',
  `is_fmcg` tinyint comment '是否快消品1是0否',
  `is_open` tinyint COMMENT '是否打开1是0否',
  `is_show` tinyint COMMENT 'app是否显示1是0否',
  `visibility` tinyint COMMENT '商户端是否显示1是0否')
  comment '分类表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;

```


# 远程连接hive元数据库

启动元数据
#这个服务可以让远程去连接hive的元数据库
bin/hive --service metastore &

#如果是jdbc去连接hive的话需要启动这个服务
bin/hive --service hiveserver2 &


如果是本地去连接远程,则在本地客户端需要配置
```
<property>
	<name>hive.metastore.uris</name>
	<value>thrift://192.168.0.11:9083</value>
	<description>运行hive的主机地址及端口</description>
</property>
```


# hive-env.sh 配置

```
HADOOP_HOME=
export HIVE_CONF_DIR=

```

# hive的日志

```
hive -hiveconf hive.root.logger=DEBUG,console
#将DEBUG以上的所有级别的日志打印到控制台,在调试的时候可以使用
```


# 常用工作语句

```
create database ods;
create database dw;
create database app;
create database dim;

show databases;

use ods;

show tables;
show tables like 'ods_*';

#查看表的信息
desc formatted ods.ods_order;
可以看表的字段, 分区信息, 表所在的location, 表的类型

#可以看文件的数据
hive>dfs -ls /user/hive/warehourse/ods.db/ods_order

#查看文件的大小
hive (ods)> dfs -du -h /user/hive/warehouse/ods.db/;


#查看执行计划
explain select * from ods.ods_order ;


#查看执行计划中的执行了哪些分区(对于查询语句中,分区没有卡死的情况下,我们可以很容易的知道)
explain dependency select * from test where dt='20151010' ;

hive (ods)> explain dependency select * from ods_order where dt='20151010';
OK
Explain
{"input_partitions":[{"partitionName":"ods@ods_order@dt=20151010"}],"input_tables":[{"tablename":"ods@ods_order","tabletype":"MANAGED_TABLE"}]}
#可以看到输入分区,表,表类型



#查看表的分区的信息
show partitions app_order_city_d;

#查看hadoop的任务
yarn application -list|grep 用户名
#有时候,知道hive中出错了,不要让他跑了,此时手动杀掉任务
yarn application -kill application_143432432432423_86621



#查看hive的函数
show functions;

#找到某个我们不记得名字的函数
show functions like 'xpath*';

#查看某个函数的用法,其中有例子
desc function extended upper ;


#hive查看表存储实际路径,找到location
desc extended dw_order ;



#添加分区
alter table ods_order add partition (dt='20151111');

#删除分区
alter table ods_order drop if exists partition (dt='20151111');


#开启mapreduce
set hive.fetch.task.conversion=minimal,more 

```

# 创建分区加载数据

```
hive (ods)> alter table ods_customer add partition (dt='20151210');
OK
Time taken: 0.19 seconds
hive (ods)> show partitions ods_customer;
OK
partition
dt=20151210
Time taken: 0.151 seconds, Fetched: 1 row(s)
hive (ods)> 


load data local inpath '/home/hadoop/app/hive/script/t_customer.txt' overwrite into table ods_customer partition (dt='20151210');

hive (ods)> dfs -du -h /user/hive/warehouse/ods.db/ods_customer/ ;
7.6 M  /user/hive/warehouse/ods.db/ods_customer/dt=20151210


#有的时候采用追加的方式去加载数据

load data local inpath '/home/hadoop/app/hive/script/t_customer.txt' into table ods_customer partition (dt='20151210');

```

# 一些业务逻辑

```
select 
city_id,
sum(case when order_status=5 then 1 else 0 end) as cnt_ord_succ_d,
sum(case when order_status=3 then 1 else 0 end) as cnt_ord_cacel_d,
sum(1) as cnt_ord_d,  <----每天的每个城市的下单数
count(distinct CUST_ID) as cnt_ord_user <----- 下单的用户数
FROM dw.dw_order 
WHERE dt='${day_01}' and city_id is not null
group by city_id ;


1.以城市id作为分组
2.case when 进行转换
	case when order_status=5 then 1 else 0 end
3.sum对case when的结果进行求和
4.统计每天下单的用户数,因为用户存在多次购买,所以使用distinct去重,然后使用count去统计去重之后的个数
count(distinct CUST_ID) 

```

求商品的复购率

```
需求列出的商品的7日,15日,30复购率，目的了解这几款商品的周期.
计算口径:当日购买部分商品的用户数/7日重复购买此商品的用户数。
每天查看每个城市每个商品当日购买用户数，7日15日30日复购率。

SELECT t3.atdate AS cdate,t3.city_id,t3.goods_id,
COUNT(DISTINCT CASE WHEN days=0 THEN t3.cust_id END) AS cnt_buy_cust_d,		#取当前的购买人数
COUNT(DISTINCT CASE WHEN days>0 AND days<=7 THEN t3.cust_id END) AS cnt_buy_cust_7_d, 	#取7天的购买人数,同一个用户多次购买,只取一次
COUNT(DISTINCT CASE WHEN days>0 AND days<=15 THEN t3.cust_id END) AS cnt_buy_cust_15_d, 	#取15天的购买人数
COUNT(DISTINCT CASE WHEN days>0 AND days<=30 THEN t3.cust_id END) AS cnt_buy_cust_30_d
FROM (
	SELECT t1.atdate,t1.city_id,t1.cust_id,t1.goods_id,
	DATEDIFF(t2.atdate, t1.atdate) days ###第3步
	FROM (###第一步
		SELECT o.order_date AS atdate,o.city_id,
		o.cust_id,og.goods_id
		FROM dw.dw_order o INNER JOIN dw.dw_order_goods og
		ON o.order_id=og.order_id 
		AND o.ORDER_STATUS = 5
		AND og.source_id=1
		AND o.dt = '20151010' 
	) t1 INNER JOIN (###第2步
		SELECT o.order_date AS atdate,o.city_id,
		o.cust_id,og.goods_id,
		og.goods_name
		FROM dw.dw_order o INNER JOIN dw.dw_order_goods og
		ON o.order_id=og.order_id 
		AND o.ORDER_STATUS = 5
		AND og.source_id=1
	) t2 ON t1.cust_id=t2.cust_id AND t1.goods_id=t2.goods_id
) t3 GROUP BY t3.atdate,t3.city_id,t3.goods_id;


#这样在做数据展现的时候,求7日复购率
select round(cnt_buy_cust_d/cnt_buy_cust_7_d) from t1

其实在O2O中复购率可以看一些像:蔬菜,水果,生鲜等的上架周期

```


月平均日客户数

```
目前有一个合作资源，北京某度假酒店，价值几百到8000不等的酒店套房，一共100套，可以给到购买200元以上订单用户，用于抽奖奖品，比如设置的获奖条件：凡在9月,10月,11月的用户，下单200元以上的订单，即可获得北京某度假酒店。目的带动销量，刺激用户参与活动，同时给合作方导流。合作方需要知道我们订单金额在200以上的每天平均的用户量是多少.
#客户id是int类型 需注意用count
SELECT 
SUM(CASE WHEN t.COMPLETION_DATE>='20151001' AND t.COMPLETION_DATE<='20151031' THEN 1 ELSE 0 END) AS cnt_ord_10_m		//返回的是订单数量
,COUNT(DISTINCT CASE WHEN t.COMPLETION_DATE>='20151001' AND t.COMPLETION_DATE<='20151031' THEN CUST_ID END) AS cnt_cust_10_m //返回的是客户数量
FROM dw.dw_order t 
WHERE t.COMPLETION_DATE>='20151001' 
AND t.COMPLETION_DATE<='20151031' 
AND CITY_ID=2
AND ORDER_TYPE <>6
AND PAYABLE_AMOUNT>100
AND t.ORDER_STATUS=5;

#注意这里的sum和count分别的作用

```

求每个用户累计订单数，累计应付金额

```
#1.在dw_customer表中有累计的订单数和累计的订单总额,这样就可以避免去全表扫描dw_order表,因为订单表很大的,这样很耗时;
#2.为什么要用full join?因为dw_customer表中并不是最新的用户表,如果当天有新增的用户,而新增的用户还不存在于dw_customer表中,而此时新增用户还有购买行为,那么如果使用left join就会将新增的用户过滤掉了,所以此处使用的是full join
select nvl(t1.cust_id,t2.cust_id), 
nvl(t2.order_cnt,0)+nvl(t1.order_cnt,0) as order_cnt,
nvl(t2.amount_sum,0)+nvl(t1.amount_sum,0) as amount_sum
from dw.dw_customer t1
full outer join (
select cust_id,count(1) as order_cnt,sum(payable_amount) as amount_sum from 
dw.dw_order where dt='20151011' and order_status=5
group by cust_id
) t2 on t1.cust_id=t2.cust_id
and t1.dt=20151210 limit 100;



```


新用户统计信息Hql分析(日粒度)
```
#查看所有的时间函数
show functions like "*time*";

/*
这里涉及到两个时间的转换:
1.将时间字符串转成毫秒数
2015/5/1 21:46  转成毫秒数

2.将毫秒数转成指定格式的时间字符串
from_unixtime将毫秒数转成指定格式的字符串

其实一般处理时间会使用我们写的UDF函数去统一处理

*/

select count(1) from dw.dw_customer
where dt='20151210' and  
from_unixtime(unix_timestamp(register_time,'yyyy/MM/dd HH:mm'),'yyyyMMdd')='20140610';
```


求5,6月各个渠道带来的新用户(注册时间)，以此来考核运营部门的kpi

```
select source_no,count(1) from dw.dw_customer
where dt=20151211 and
from_unixtime(unix_timestamp(register_time,'yyyy/MM/dd HH:mm'),'yyyyMMdd')>='20141201'
and from_unixtime(unix_timestamp(register_time,'yyyy/MM/dd HH:mm'),'yyyyMMdd')<='20150131' 
and source_no is not null
group by source_no;

```


统计各个渠道带来的用户，top10完成订单数

```
#求分组中的top10,使用row_number()函数:partition by 表示分组的字段, order by 表示组内排序的字段

select source_no,mobile,order_cnt,rn from (
select source_no,order_cnt,mobile,
row_number() over(partition by source_no order by order_cnt desc) as rn 
from dw.dw_customer 
where dt=20151211 and source_no is not null and order_cnt is not null
) t2 where rn <10;

```


# etl开发模板

```
#!/bin/bash
#这里是需要用到的函数
. /home/anjianbing/soft/functions/wait4FlagFile.sh
# ===========================================================================
# 程序名称:     
# 功能描述:     城市每日完成订单数
# 输入参数:     运行日期 20151010
# 目标表名:     app.app_order_city_d 到哪张表来
# 数据源表:     dw.dw_order	从哪张表来
#   创建人:     安坚兵
# 创建日期:     2015-12-21
# 版本说明:     v1.0
# 代码审核:     
# 修改人名:
# 修改日期:
# 修改原因:
# 修改列表: 
# ===========================================================================
### 1.参数加载
exe_hive="hive"
# 因为有的时候,可能会运行脚本的时候指定参数:sh test.sh 20151010,此时就是运行指定天的数,否则默认是运行前一天的数据
if [ $# -eq 1 ]
then
    day_01=`date --date="${1}" +%Y-%m-%d`
else
    day_01=`date -d'-1 day' +%Y-%m-%d`
fi

#拿到时间的年月日
syear=`date --date=$day_01 +%Y`
smonth=`date --date=$day_01 +%m`
sday=`date --date=$day_01 +%d`

#目标数据库和表
TARGET_DB=app.db
TARGET_TABLE=app_order_city_d

### 2.定义执行HQL
HQL="insert overwrite table app.app_order_city_d partition (dt='${day_01}')
SELECT city_id,COUNT(1) FROM dw.dw_order WHERE dt='${day_01}' 
AND order_status=5 GROUP BY city_id;
"
### 3.检查依赖 (因为只要等ods层的表跑完了,才能跑dw层的表,同样只有等dw层的表跑完了,才能跑app层的表)
wait4FlagFile HDFS /user/hive/warehouse/dw.db/dw_order/dt=${day_01} _SUCCESS 15801152142

#### 4.执行HQL
$exe_hive -e "$HQL"

#### 5. 判断代码是否执行成功，touch控制文件
result=`hadoop fs -ls /user/hive/warehouse/${TARGET_DB}/${TARGET_TABLE}/dt=${day_01} | wc -l`
if [[ $result -gt 0 ]]; then
	#为下面的依赖做准备
    hadoop fs -touchz /user/hive/warehouse/${TARGET_DB}/${TARGET_TABLE}/dt=${day_01}/_SUCCESS
else
    echo "失败发送预警短信和邮件"
fi


```




# 给已经排好序的表添加序列号的字段

```
#给已经排好序的表添加序列号的字段

select 
student_nu, score, row_number() over() as num
from student order by score;

```


# 行转列

```
name		class		score
zhangsan	math		99
zhangsan	chinese		88
lisi		math		99
lisi		chinese		100

#行转列
select
name,
max(case when class='math' then score) as math_score,
max(case when class='chinese' then score) as math_score,
from student group by name ; 

```







