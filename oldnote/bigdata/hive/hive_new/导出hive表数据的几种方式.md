---
title: 导出hive表数据的几种方式
categories: hive  
tags: [hive]
---


# 将hive表中的数据导入到本地

```
#方式一(导出到本地目录下)

insert overwrite local directory '/tmp/datas/hive_exp_emp'
row format delimited fields terminated by ','
select * from default.emp ;


#方式二

bin/hive -e "select * from default.emp ;" > /tmp/datas/exp_res.txt


#方式三(导出到hdfs中)

insert overwrite directory '/tmp/datas/hive_exp_emp'
row format delimited fields terminated by ','
select * from default.emp ;


#到hdfs上查看导出的数据
hive (default)> dfs -ls /tmp/datas/hive_exp_emp ;
Found 1 items
-rwx-wx-wx   3 root supergroup       1322 2017-04-24 01:32 /tmp/datas/hive_exp_emp/000000_0

hive (default)> dfs -text /tmp/datas/hive_exp_emp/000000_0 ;
7369,SMITH,CLERK,7902,1980-12-17,800.0,\N,20
7499,ALLEN,SALESMAN,7698,1981-2-20,1600.0,300.0,30
7521,WARD,SALESMAN,7698,1981-2-22,1250.0,500.0,30
7566,JONES,MANAGER,7839,1981-4-2,2975.0,\N,20
7654,MARTIN,SALESMAN,7698,1981-9-28,1250.0,1400.0,30
7698,BLAKE,MANAGER,7839,1981-5-1,2850.0,\N,30
7782,CLARK,MANAGER,7839,1981-7-9,2450.0,\N,10


#然后我们可以将hdfs上的数据拿到本地
hdfs dfs -get /tmp/datas/hive_exp_emp/000000_0 /user/datas/test.txt 

```
