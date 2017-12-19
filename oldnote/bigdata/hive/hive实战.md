---
title: hive实战
categories: hive   
toc: true  
tag: [hive]
---




# 1.分桶表示例
```
#创建分桶表
drop table stu_buck;
create table stu_buck(Sno int,Sname string,Sex string,Sage int,Sdept string)
clustered by(Sno)                #以Sno来分桶
sorted by(Sno DESC)            #每个桶内使用Sno排序
into 4 buckets            #分成4个桶
row format delimited
fields terminated by ',';


#设置变量,设置分桶为true, 设置reduce数量是分桶的数量个数
set hive.enforce.bucketing = true;
set mapreduce.job.reduces=4;



#插入数据
#开会往创建的分通表插入数据(插入数据需要是已分桶, 且排序的)
#可以使用distribute by(sno) sort by(sno asc)   或是排序和分桶的字段相同的时候使用Cluster by(字段)
#注意使用cluster by  就等同于分桶+排序(sort)
insert into table stu_buck
select Sno,Sname,Sex,Sage,Sdept from student distribute by(Sno) sort by(Sno asc);
 
insert overwrite table stu_buck
select * from student distribute by(Sno) sort by(Sno asc);
 
insert overwrite table stu_buck
select * from student cluster by(Sno);


```

<!--more-->

# 2.保存select查询结果的几种方式
```
'1、将查询结果保存到一张新的hive表中'
create table t_tmp
as
select * from t_p;


'2、将查询结果保存到一张已经存在的hive表中'
insert into  table t_tmp
select * from t_p;

'3、将查询结果保存到指定的文件目录（可以是本地，也可以是hdfs）'
#本地
insert overwrite local directory '/home/hadoop/test'
select * from t_p;
 
#hdfs
insert overwrite directory '/user/test'
select * from t_p;


```

# 3.关于hive中的各种join
## 3.1.准备数据
```
#a.txt
1,a
2,b
3,c
4,d
7,y
8,u
 
#b.txt
2,bb
3,cc
7,yy
9,pp
```


## 3.2.建表
```
create table a(id int,name string)
row format delimited fields terminated by ',';
 
create table b(id int,name string)
row format delimited fields terminated by ',';

```

## 3.3.导入数据
```
load data local inpath '/home/hadoop/a.txt' into table a;
load data local inpath '/home/hadoop/b.txt' into table b;

```


## 3.4.实验
```
#inner join

select * from a inner join b on a.id=b.id;
+-------+---------+-------+---------+--+
| a.id  | a.name  | b.id  | b.name  |
+-------+---------+-------+---------+--+
| 2     | b       | 2     | bb      |
| 3     | c       | 3     | cc      |
| 7     | y       | 7     | yy      |
+-------+---------+-------+---------+--+




#left join

select * from a left join b on a.id=b.id;
+-------+---------+-------+---------+--+
| a.id  | a.name  | b.id  | b.name  |
+-------+---------+-------+---------+--+
| 1     | a       | NULL  | NULL    |
| 2     | b       | 2     | bb      |
| 3     | c       | 3     | cc      |
| 4     | d       | NULL  | NULL    |
| 7     | y       | 7     | yy      |
| 8     | u       | NULL  | NULL    |
+-------+---------+-------+---------+--+


#right join
select * from a right join b on a.id=b.id;
 
# full outer join 
select * from a full outer join b on a.id=b.id;
+-------+---------+-------+---------+--+
| a.id  | a.name  | b.id  | b.name  |
+-------+---------+-------+---------+--+
| 1     | a       | NULL  | NULL    |
| 2     | b       | 2     | bb      |
| 3     | c       | 3     | cc      |
| 4     | d       | NULL  | NULL    |
| 7     | y       | 7     | yy      |
| 8     | u       | NULL  | NULL    |
| NULL  | NULL    | 9     | pp      |
+-------+---------+-------+---------+--+

#semi join    只是返回inner join 的前半
select * from a left semi join b on a.id = b.id;
+-------+---------+--+
| a.id  | a.name  |
+-------+---------+--+
| 2     | b       |
| 3     | c       |
| 7     | y       |
+-------+---------+--+


#重写以下子查询为LEFT SEMI JOIN
  SELECT a.key, a.value
  FROM a
  WHERE a.key exist in
   (SELECT b.key
    FROM B);
#可以被重写为：
   SELECT a.key, a.val
   FROM a LEFT SEMI JOIN b on (a.key = b.key)

```


# 4.多重插入
```
from student
insert into table student_p partition(part='a')
select * where Sno<95011;
insert into table student_p partition(part='a')
select * where Sno<95011;

```

# 5.导出数据到本地
```
insert overwrite local directory '/home/hadoop/student.txt'
select * from student;

```

# 6.UDF（自定义函数）案例
```
########################文件：rating.json ###############################
{"movie":"1193","rate":"5","timeStamp":"978300760","uid":"1"}
{"movie":"661","rate":"3","timeStamp":"978302109","uid":"1"}
{"movie":"914","rate":"3","timeStamp":"978301968","uid":"1"}
{"movie":"3408","rate":"4","timeStamp":"978300275","uid":"1"}
#######################################################


create table rat_json(line string) row format delimited;
load data local inpath '/home/hadoop/rating.json' into table rat_json;
 
drop table if exists t_rating;
create table t_rating(movieid string,rate int,timestring string,uid string)
row format delimited fields terminated by '\t';
 
insert overwrite table t_rating
select split(parsejson(line),'\t')[0]as movieid,split(parsejson(line),'\t')[1] as rate,split(parsejson(line),'\t')[2] as timestring,split(parsejson(line),'\t')[3] as uid from rat_json limit 10;



```

# 7.内置jason函数
```
select get_json_object(line,'$.movie') as moive,get_json_object(line,'$.rate') as rate  from rat_json limit 10;

```

# 8.transform案例
```
'1、先加载rating.json文件到hive的一个原始表 rat_json'
create table rat_json(line string) row format delimited;
load data local inpath '/home/hadoop/rating.json' into table rat_json;
 
'2、需要解析json数据成四个字段，插入一张新的表 t_rating'
insert overwrite table t_rating
select get_json_object(line,'$.movie') as moive,get_json_object(line,'$.rate') as rate  from rat_json;
 
'3、使用transform+python的方式去转换unixtime为weekday'
先编辑一个python脚本文件
########python######代码
vi weekday_mapper.py
#!/bin/python
import sys
import datetime
 
for line in sys.stdin:
  line = line.strip()
  movieid, rating, unixtime,userid = line.split('\t')
  weekday = datetime.datetime.fromtimestamp(float(unixtime)).isoweekday()
  print '\t'.join([movieid, rating, str(weekday),userid])
 
#保存文件
#然后，将文件加入hive的classpath：
hive>add FILE /home/hadoop/weekday_mapper.py;
hive>create TABLE u_data_new as
SELECT
  TRANSFORM (movieid, rate, timestring,uid)
  USING 'python weekday_mapper.py'
  AS (movieid, rate, weekday,uid)
FROM t_rating;
 
select distinct(weekday) from u_data_new limit 10;


```


# 8.数据ETL

## 8.1.需求
* 对web点击流日志基础数据表进行etl（按照仓库模型设计）
* 按各时间维度统计来源域名top10
* 已有数据表 “t_orgin_weblog” ：
```
+------------------+------------+----------+--+
|     col_name     | data_type  | comment  |
+------------------+------------+----------+--+
| valid            | string     |          |
| remote_addr      | string     |          |
| remote_user      | string     |          |
| time_local       | string     |          |
| request          | string     |          |
| status           | string     |          |
| body_bytes_sent  | string     |          |
| http_referer     | string     |          |
| http_user_agent  | string     |          |
+------------------+------------+----------+--+

```




## 8.2.数据实例
```
| true|1.162.203.134| - | 18/Sep/2013:13:47:35| /images/my.jpg             | 200| 19939 | "http://www.angularjs.cn/A0d9"           | "Mozilla/5.0 (Windows   |
 
| true|1.202.186.37 | - | 18/Sep/2013:15:39:11| /wp-content/uploads/2013/08/windjs.png| 200| 34613 | "http://cnodejs.org/topic/521a30d4bee8d3cb1272ac0f" | "Mozilla/5.0 (Macintosh;|

```


## 8.3.实现步骤
```
'1、对原始数据进行抽取转换'
#--将来访url分离出host  path  query  query id
drop table if exists t_etl_referurl;
create table t_etl_referurl as
SELECT a.*,b.*
FROM t_orgin_weblog a LATERAL VIEW parse_url_tuple(regexp_replace(http_referer, "\"", ""), 'HOST', 'PATH','QUERY', 'QUERY:id') b as host, path, query, query_id    
#使用了hive的内置函数 parse_url_tuple

'2.从前述步骤进一步分离出日期时间形成ETL明细表“t_etl_detail”    day tm  '
drop table if exists t_etl_detail;
create table t_etl_detail as
select b.*,substring(time_local,0,11) as daystr,
substring(time_local,13) as tmstr,
substring(time_local,4,3) as month,
substring(time_local,0,2) as day,
substring(time_local,13,2) as hour
from t_etl_referurl b;

'3.对etl数据进行分区(包含所有数据的结构化信息)'
drop table t_etl_detail_prt;
create table t_etl_detail_prt(
valid                   string,
remote_addr            string,
remote_user            string,
time_local               string,
request                 string,
status                  string,
body_bytes_sent         string,
http_referer             string,
http_user_agent         string,
host                   string,
path                   string,
query                  string,
query_id               string,
daystr                 string,
tmstr                  string,
month                  string,
day                    string,
hour                   string)
partitioned by (mm string,dd string);


'4.导入数据'
insert into table t_etl_detail_prt partition(mm='Sep',dd='18')
select * from t_etl_detail where daystr='18/Sep/2013';
 
insert into table t_etl_detail_prt partition(mm='Sep',dd='19')
select * from t_etl_detail where daystr='19/Sep/2013';

'5.分个时间维度统计各referer_host的访问次数并排序'
create table t_refer_host_visit_top_tmp as
select referer_host,count(*) as counts,mm,dd,hh from t_display_referer_counts group by hh,dd,mm,referer_host order by hh asc,dd asc,mm asc,counts desc;

'6.来源访问次数topn各时间维度URL, 取各时间维度的referer_host访问次数topn'
select * from (select referer_host,counts,concat(hh,dd),row_number() over (partition by concat(hh,dd) order by concat(hh,dd) asc) as od from t_refer_host_visit_top_tmp) t where od<=3;



```

# 9.级联求和
## 9.1.需求
有如下访客访问次数统计表 t_access_times


访客|   月份|	  访问次数
:--:|:--:|:--:	
A	|2015-01|	5
A	|2015-01|	15
B	|2015-01|	5
A	|2015-01|	8
B	|2015-01|	25
A	|2015-01|	5
A	|2015-02|	4
A	|2015-02|	6
B	|2015-02|	10
B	|2015-02|	5
……	|   ……	|	  ……


 需要输出报表：t_access_times_accumulate

访客|   月份|	  访问次数
:--:|:--:|:--:	
A|2015-01|33|33
A |2015-02|10|43
...|....|...|...
B|2015-01|30|30
B|2015-02|22|52
...|...|...|...

## 9.2.实现步骤
详见《面试用神sql--套路--累计报表.md》
```
select A.username,A.month,max(A.salary) as salary,sum(B.salary) as accumulate
from
(select username,month,sum(salary) as salary from t_access_times group by username,month) A
inner join
(select username,month,sum(salary) as salary from t_access_times group by username,month) B
on
A.username=B.username
where B.month <= A.month
group by A.username,A.month
order by A.username,A.month;

```




