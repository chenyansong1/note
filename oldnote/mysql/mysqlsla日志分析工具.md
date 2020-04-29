---
title: mysqlsla日志分析工具
categories: mysql   
tags: [mysql]
---



&emsp;一款帮助语句分析、过滤、分析和排序MySQL慢日志、查询日志、二进制日志和microslow patched日志的分析工具。整体来说, 功能非常强大. 数据报表,非常有利于分析慢查询的原因, 包括执行频率, 数据量, 查询消耗等。



# 1.下载mysqlsla
```
[root@localhost tmp]# wget http://hackmysql.com/scripts/mysqlsla-2.03.tar.gz

#在目录下已经下载好了，直接上传即可。

```


# 2.解压
```
[root@localhost tmp]# tar -zxvf mysqlsla-2.03.tar.gz
[root@localhost tmp]# cd mysqlsla-2.03

```

# 3.执行Perl脚本检查包依赖关系
```
[root@localhost mysqlsla-2.03]# perl Makefile.PL
Checking if your kit is complete...
Looks good
Writing Makefile for mysqlsla
 
@如果依赖检查报错：
[root@localhost mysqlsla-2.03]# perl Makefile.PL
Can't locate ExtUtils/MakeMaker.pm in @INC (@INC contains: /usr/local/lib64/perl5 /usr/local/share/perl5 /usr/lib64/perl5/vendor_perl /usr/share/perl5/vendor_perl /usr/lib64/perl5 /usr/share/perl5 .) at Makefile.PL line 2.
BEGIN failed--compilation aborted at Makefile.PL line 2.
@错误2
[root@MySQL mysqlsla-2.03]# mysqlsla -lt slow /data/3309/slow.log
Can't locate Time/HiRes.pm in @INC (@INC contains: /usr/local/lib/perl5 /usr/local/share/perl5 /usr/lib/perl5/vendor_perl /usr/share/perl5/vendor_perl /usr/lib/perl5 /usr/share/perl5 .) at /usr/local/bin/mysqlsla line 2095.
BEGIN failed--compilation aborted at /usr/local/bin/mysqlsla line 2095.
 
解决方法如下：
yum -y install perl-devel
yum -y install perl-CPAN
yum install perl-Time-HiRes -y


```


# 4.安装

```
[root@localhost mysqlsla-2.03]# make && make install;
```

# 5.简单使用
```
语法：
Slow log: mysqlsla -lt slow slow.log
General log: mysqlsla -lt general general.log
Binary log: mysqlbinlog bin.log | mysqlsla -lt binary -
 
这里以slow log为例：
[root@localhost mysqlsla-2.03]# mysqlsla -lt slow /tmp/127_slow.log | more
——————————————————————————————————
Report for slow logs: /tmp/127_slow.log
24 queries total, 6 unique
Sorted by 't_sum'
Grand Totals: Time 16 s, Lock 1 s, Rows sent 18, Rows Examined 2.10M
 
 
______________________________________________________________________ 001 ___
Count         : 18  (75.00%)
Time          : 15 s total, 833.333 ms avg, 0 to 8 s max  (93.75%)
  95% of Time : 7 s total, 411.765 ms avg, 0 to 4 s max
Lock Time (s) : 0 total, 0 avg, 0 to 0 max  (0.00%)
  95% of Lock : 0 total, 0 avg, 0 to 0 max
Rows sent     : 0 avg, 0 to 0 max  (0.00%)
Rows examined : 116.51k avg, 8 to 1.05M max  (99.99%)
Database      :
Users         :
        root@localhost  : 100.00% (18) of query, 100.00% (24) of all users
 
Query abstract:
INSERT INTO t2 SELECT * FROM t2;
 
Query sample:
insert into t2 select * from t2;
........


```

各个字段说明
```
总查询次数 (queries total)， 去重后的sql数量 (unique)
输出报表的内容排序(sorted by)
最重大的慢sql统计信息, 包括 平均执行时间, 等待锁时间, 结果行的总数, 扫描的行总数.
Count, sql的执行次数及占总的slow log数量的百分比.
Time, 执行时间, 包括总时间, 平均时间, 最小, 最大时间, 时间占到总慢sql时间的百分比.
95% of Time, 去除最快和最慢的sql, 覆盖率占95%的sql的执行时间.
Lock Time, 等待锁的时间.
95% of Lock , 95%的慢sql等待锁时间.
Rows sent, 结果行统计数量, 包括平均, 最小, 最大数量.
Rows examined, 扫描的行数量.
Database, 属于哪个数据库
Users, 哪个用户,IP, 占到所有用户执行的sql百分比
Query abstract, 抽象后的sql语句
Query sample, sql语句

```
# 6.sla常用参数
```
mysqlsla常用参数说明：
1) -log-type (-lt) type logs:
通过这个参数来制定log的类型，主要有slow, general, binary, msl, udl,分析slow log时通过制定为slow.
 
2) -sort:
制定使用什么参数来对分析结果进行排序，默认是按照t_sum来进行排序。
t_sum:按总时间排序
c_sum:按总次数排序
c_sum_p: sql语句执行次数占总执行次数的百分比。
 
3) -top:
显示sql的数量，默认是10,表示按规则取排序的前多少条
 
4) –statement-filter (-sf) [+-][TYPE]:
过滤sql语句的类型，比如select、update、drop.
[TYPE]有SELECT, CREATE, DROP, UPDATE, INSERT，例如"+SELECT,INSERT"，不出现的默认是-，即不包括。
 
5) db：要处理哪个库的日志：
 
例如，只取backup库的select语句、按c_sum_p排序的前2条记录
 
[root@localhost mysqlsla-2.03]# mysqlsla -lt slow -sort c_sum_p  -sf  "+select" -db backup -top 2  /tmp/127_slow.log
Report for slow logs: /tmp/127_slow.log
4 queries total, 3 unique
Sorted by 'c_sum_p'
Grand Totals: Time 1 s, Lock 1 s, Rows sent 18, Rows Examined 195
______________________________________________________________________ 001 ___
Count         : 2  (50.00%)
Time          : 0 total, 0 avg, 0 to 0 max  (0.00%)
Lock Time (s) : 0 total, 0 avg, 0 to 0 max  (0.00%)
Rows sent     : 1 avg, 1 to 1 max  (11.11%)
Rows examined : 86 avg, 77 to 94 max  (87.69%)
Database      :
Users         :
        root@localhost  : 100.00% (2) of query, 100.00% (4) of all users
 
Query abstract:
SELECT SUM(format(duration,N)) AS duration FROM information_schema.profiling WHERE query_id=N;
 
Query sample:
select sum(format(duration,6)) as duration from information_schema.profiling where query_id=7;
______________________________________________________________________ 002 ___
Count         : 1  (25.00%)
Time          : 1 s total, 1 s avg, 1 s to 1 s max  (100.00%)
Lock Time (s) : 1 s total, 1 s avg, 1 s to 1 s max  (100.00%)
Rows sent     : 4 avg, 4 to 4 max  (22.22%)
Rows examined : 12 avg, 12 to 12 max  (6.15%)
Database      :
Users         :
        root@localhost  : 100.00% (1) of query, 100.00% (4) of all users
//。。。。。。。。。。。。。。。


```



