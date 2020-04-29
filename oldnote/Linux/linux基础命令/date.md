---
title: Linux基础命令之date
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 1.date +FORMAT 获取格式化的时间字符串

&emsp;<font color=red > 注意：date 和+之间的空格 </font>

```
#语法格式
date [OPTION]... [+FORMAT]
```

|格式|说明|
|---|---|
|%F|full date; same as %Y-%m-%d  eg:date +%F --> 2016-07-19|
|%d|day of month (e.g, 01) |
|%D|date; same as %m/%d/%y|
|%m|month (01..12)|
|%M|minute (00..59)|
|%s|seconds since 1970-01-01 00:00:00 UTC|
|%S|second (00..60)|
|%y|last two digits of year (00..99)|
|%Y|year      date +%Y--->2016|
|%h|same as %b|
|%H|hour (00..23)|
|%s|seconds since 1970-01-01 00:00:00 UTC      |
|%S|second (00..60)|
|%t|a tab|
|%T|time; same as %H:%M:%S|
|%w|day of week (0..6); 0 is Sunday|
|%W|week number of year, with Monday as first day of week (00..53)|



# 2. date -s 临时修改时间
```
[root@linux-study ~]# date -s "2016-07-20 15:52:49"
2016年 07月 20日 星期三 15:52:49 CST
[root@linux-study ~]# 
```

# 3.date -d 获取过去或者未来的时间
```shell
[root@lamp01 ~]# date
2017年 02月 12日 星期日 17:24:17 CST

#后2天的日期
[root@lamp01 ~]# date +%F -d "2day"
2017-02-14

#前2天的日期
[root@lamp01 ~]# date +%F -d "-2day"
2017-02-10


[root@lamp01 ~]# date +%F-%H
2017-02-12-17
[root@lamp01 ~]# date +%F-%H -d "+2hour"
2017-02-12-19

#当前日志的前多少天
[root@soc60 elasticsearch-2.4.3]# echo $(date +"%Y%m%d" -d "-1day")
20190805

#类似的还有分钟,秒
-d "-2min"
-d "2min"
-d "-2sec"

```


# 4.举例
```
#打印“YYYY-mm-dd HH:mm:ss”
[root@lamp01 ~]# date +"%Y-%m-%d %H:%M:%S"
2017-02-12 17:29:30
#or
[root@lamp01 ~]# date +%F\ %T
2017-02-12 17:29:43


#按照时间打包
[root@lamp01 ~]# ll
-rw-r--r--  1 root root     0 7月  26 2016 f.103

[root@lamp01 ~]# tar zcvf cys_test_`date +%F`.tar.gz ./f.103    #反引号中是命令
./f.103
[root@lamp01 ~]# ll
-rw-r--r--  1 root root     0 7月  26 2016 f.103
-rw-r--r--  1 root root   111 2月  12 17:31 cys_test_2017-02-12.tar.gz

#or
[root@lamp01 ~]# tar zcvf cys_test_$(date +%F).tar.gz ./f.103

#解析命令的方式:反引号`` 或 $()

```


