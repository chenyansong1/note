---
title: hive项目实战之服访问日志处理
categories: hive  
tags: [hive]
---


# 思路

* 创建原表
* 针对不同的业务创建不同的子表
	* 数据存储格式:orcfile/parquet
	* 数据压缩:snappy
	* map output 数据压缩:snappy
	* 外部表
	* 分区表


# 创建原表

```

create table if not exists default.log_src
(
remote_add string,
remote_user string,
request string,
host string
//...其他的字段
)
comment 'access log'
row format delimited fields terminated by ' '
stored as textfile ;


```

# 加载数据

```
load data local inpath '/datas/access.log' into table default.log_src ;

```


会发现,我们的数据是不会正确的加载到表中的,因为使用空格的时候会出现错乱

hive的官网对web log推荐使用正则表达式


这里是hive官网的例子:

https://issues.apache.org/jira/browse/HIVE-662

```
CREATE TABLE serde_regex(
  host STRING,
  identity STRING,
  user STRING,
  time STRING,
  request STRING,
  status STRING,
  size STRING,
  referer STRING,
  agent STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "([^ ]*) ([^ ]*) ([^ ]*) (-|\\[[^\\]]*\\]) ([^ \"]*|\"[^\"]*\") (-|[0-9]*) (-|[0-9]*)(?: ([^ \"]*|\"[^\"]*\") ([^ \"]*|\"[^\"]*\"))?",
  "output.format.string" = "%1$s %2$s %3$s %4$s %5$s %6$s %7$s %8$s %9$s"
)
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH "../data/files/apache.access.log" INTO TABLE serde_regex;
LOAD DATA LOCAL INPATH "../data/files/apache.access.2.log" INTO TABLE serde_regex;

SELECT * FROM serde_regex ORDER BY time;

```

对于不同的日志格式,我们可能需要些不同的正则表达式去匹配,下面的网站是校验你的正则表达式是否正确的网站,需要提供一条日志和你写的正则表达式

http://wpjam.qiniudn.com/tool/regexpal/

写正则表达式的规律:一个()就是一个字段,每个字段之间使用空格隔开,这样针对每一个字段在括号中写需要匹配的正则表达式

如果没有正则表达式,那么我们要写MapReduce做预处理了


# 根据不同的业务创建不同的子表

```
create table if not exists default.log_comm
(
remote_add string,
remote_user string,
request string,
host string
)
comment 'access log common'
row format delimited fields terminated by '\t'
stored as orc tblproperties ("orc.compress"="SNAPPY") ;


insert into table default.log_comm 
select remote_add, remote_user, request, host from default.log_src ;
```

# 自定义UDF进行数据清洗

定义UDF对原表数据进行清洗

## 定义UDF去除引号

1.UDF编码

```

#下面是伪代码

class RemoveQuotesUDF extends UDF{
	
	public TEXT evaluate(Text str){
		if(null==str.toString()){
			return null;
		}

		//将所有的引号替换为空
		return new Text(str.toString().replaceAll("\"", ""));
	}
}

```

2.将上面的代码打包导出/opt/jars/udf.jar

3.add jar /opt/jars/udf.jar ;


4.create temporary function my_removequotes as "com.study.udf.RemoveQuotesUDF" ;

5.list jars ;

6.
```
insert overwrite  table default.log_comm 
select my_removequotes(remote_add), remote_user, request, host from default.log_src ;
```

## 定义UDF日期转换

源数据中的日期的格式为:31/Aug/2015:00:04:37 +0800
udf之后的数据为:20150110437

udf代码
```

SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss", Locale.ENGLISH);
SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);

public Text evalue(Text input){
	
	Text output = new Text();
	

	if(null==str||null==str.toString()){
		return null;
	}
	
	String inputDate = input.toString().trim();
	if(null == inputDate){
		return null;
	}
	
	try{
		//parse
		Date parseDate = inputFormat.parse(inputDate);
		//transform
		String outputDate = outputFormat.format(parseDate);
		//set
		output.set(outputDate);

	}catch{Exception e){
		e.printStackTrace();
	}
	return output;
}

```


2.将上面的代码打包导出/opt/jars/udf2.jar

3.add jar /opt/jars/udf2.jar ;


4.create temporary function my_datetransform as "com.study.udf.DateTransformUDF" ;

5.list jars ;

6.
```
insert overwrite  table default.log_comm 
select my_removequotes(remote_add), my_datetransform(time_local), remote_user, request, host from default.log_src ;
```



# 根据业务编写hiveQL

统计每天每小时的访问量,并排序
```
select substring(time_local,9,2) hour from default.log_comm;


select t.hour, count(*) cnt from 
(
select substring(time_local,9,2) hour from default.log_comm
) t
group by t.hour order by cnt desc ;

```

根据访问的ip地址进行地域统计

```
select t.prex_ip, count(*) cnt from 
(
select substring(ip, 1, 7) prex_ip from log_comm 
) t
group by t.prex_ip order by cnt desc ;

```

# 使用python脚本进行数据清洗和统计

```
#数据的格式
userid	movieid	rate	time
196		33		4		881250949


#创建表
create table if not exists u_data
(
userid int,
movieid int,
rating	int,
unixtime string
)
row format delimited
fields terminated by '\t'
stored as textfile



#导入数据
load data local inpath '/datas/u.data' overwrite into table u_data ;


#创建一个新的表(此时时间变成weekday星期几)
create table if not exists u_data_new
(
userid int,
movieid int,
rating	int,
weekday int
)
row format delimited
fields terminated by '\t'
stored as textfile


#加载python文件
add file weekday_mapper.py ;

#向新表中插入数据
insert overwrite table u_data_new 
select 
	transform(userid,movieid, rating, unixtime)
	using 'python weekday_mapper.py'
	as(user, movieid, rating, weekday)
from u_data ;

/*
transform方法中传递过去的是u_data表中的指定的字段
using 表示使用的是哪个python脚本
as表示调用脚本传递出来的数据
*/

#按星期分组统计
select weekd, count(*) from u_data_new  group by weekday ;

```

python脚本
```
import sys
import datetime

for line in sys.stdin:
	line = line.strip()
	userid,movieid, rating, unixtime = line.split('\t')
	weekday = datetime.datetime.fromtimestamp(float(unixtime))
	print '\t'.join([userid,movieid, rating, str(weekday)])	
	
```







