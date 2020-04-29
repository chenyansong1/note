---
title: hive常见属性配置
categories: hive  
tags: [hive]
---




# hive数据仓库位置配置

```

在conf/hive-default.xml.template 中有如下的配置,如果我们想要配置这个参数,可以在hive-site.xml中添加:

  <property>
    <name>hive.metastore.warehouse.dir</name>
    <value>/user/hive/warehouse</value>
    <description>location of default database for the warehouse</description>
  </property>

在该目录下,没有对默认的数据库(default)创建文件夹,如果某张表数据属于default数据库,那么直接在/user/hive/warehouse以表的名称创建文件夹



[root@hdp-node-01 hive]# hdfs dfs -ls /user/hive/warehouse
Found 5 items
drwxr-xr-x   - root supergroup          0 2016-11-27 23:34 /user/hive/warehouse/mytable1
drwxr-xr-x   - root supergroup          0 2016-11-28 13:49 /user/hive/warehouse/stu_buck
drwxr-xr-x   - root supergroup          0 2016-11-28 10:12 /user/hive/warehouse/student
drwxr-xr-x   - root supergroup          0 2017-04-23 16:05 /user/hive/warehouse/test_database.db
drwxr-xr-x   - root supergroup          0 2017-04-23 15:00 /user/hive/warehouse/test_table

上面的test_table表就是在default数据库下创建,所以直接在HDFS的/user/hive/warehouse目录下


而如果我们创建了一个数据库,如:test_database,然后在该数据库下创建表:test_table,那么就会在对应的数据库下创建表的目录,如下:

[root@hdp-node-01 hive]# hdfs dfs -ls /user/hive/warehouse/test_database.db
Found 1 items
drwxr-xr-x   - root supergroup          0 2017-04-23 16:05 /user/hive/warehouse/test_database.db/test_table

#test_database.db就是我们创建数据库对应的目录,而该目录下的test_table就是我们创建的表对应的目录




```


# hive运行日志的信息位置配置及运行日志配置
## hive运行日志的信息位置配置
```
#cp hive-log4j.properties.template hive-log4j.properties

# Define some default values that can be overridden by system properties
hive.log.threshold=ALL
#日志的级别
hive.root.logger=INFO,DRFA
#配置的是日志的存放目录,默认是在/tmp/当前用户/目录下面
hive.log.dir=${java.io.tmpdir}/${user.name}
#当前的日志文件
hive.log.file=hive.log


#默认存放日志文件的目录
[root@hdp-node-01 root]# ll /tmp/root/  
total 1228
drwx------ 2 root root   4096 Dec 17 20:54 09fbac45-c45a-45de-bc40-2aeb96a383fc
-rw-r--r-- 1 root root      0 Dec 17 20:54 09fbac45-c45a-45de-bc40-2aeb96a383fc3011878711548453.pipeout
drwx------ 2 root root   4096 Dec 17 20:35 3c42d672-333f-4a13-8251-3bd4426979d3
-rw-r--r-- 1 root root      0 Dec 17 20:35 3c42d672-333f-4a13-8251-3bd4426979d3415307667270826455.pipeout
drwx------ 2 root root   4096 Nov 28 10:36 95635832-e09c-47a2-a00c-7a30e7ad6a0f
-rw-r--r-- 1 root root      0 Nov 28 10:36 95635832-e09c-47a2-a00c-7a30e7ad6a0f7702146714156298728.pipeout
drwx------ 2 root root   4096 Dec 17 20:33 b024d3ea-59d7-4a68-a73a-e6d8b6f18ed8
-rw-r--r-- 1 root root      0 Dec 17 20:33 b024d3ea-59d7-4a68-a73a-e6d8b6f18ed87234035665673320168.pipeout
drwx------ 2 root root   4096 Nov 28 09:59 cb503457-399e-46c5-b3fe-8fa18c676db5
-rw-r--r-- 1 root root      0 Nov 28 08:19 cb503457-399e-46c5-b3fe-8fa18c676db52063284649208370642.pipeout
drwx------ 2 root root   4096 Dec 17 20:54 e45f3d4c-5ad9-48f5-99cf-2a2424b0883d
-rw-r--r-- 1 root root      0 Dec 17 20:54 e45f3d4c-5ad9-48f5-99cf-2a2424b0883d2746165294995632557.pipeout

#日志文件
-rw-r--r-- 1 root root  20054 Apr 23 15:25 hive.log 
-rw-r--r-- 1 root root  40632 Nov 23 00:30 hive.log.2016-11-23
-rw-r--r-- 1 root root 546704 Nov 27 23:41 hive.log.2016-11-27
-rw-r--r-- 1 root root 612388 Nov 28 13:50 hive.log.2016-11-28




hive.log.threshold=ALL
hive.root.logger=INFO,DRFA
#改变日志的目录
hive.log.dir=/home/hadoop/app/hive/log
hive.log.file=hive.log


[root@hdp-node-01 hive]# ll /home/hadoop/app/hive/log
total 4
-rw-r--r-- 1 root root 3065 Apr 23 15:33 hive.log



```
## 配置hive运行日志在控制台打印,方便调试
```


#查看启动hive命令的帮助
[root@hdp-node-01 root]# /home/hadoop/app/hive/bin/hive -help

usage: hive
 -d,--define <key=value>          Variable subsitution to apply to hive
                                  commands. e.g. -d A=B or --define A=B
    --database <databasename>     Specify the database to use
 -e <quoted-query-string>         SQL from command line
 -f <filename>                    SQL from files
 -H,--help                        Print help information
    --hiveconf <property=value>   Use value for given property
    --hivevar <key=value>         Variable subsitution to apply to hive
                                  commands. e.g. --hivevar A=B
 -i <filename>                    Initialization SQL file
 -S,--silent                      Silent mode in interactive shell
 -v,--verbose                     Verbose mode (echo executed SQL to the
                                  console)

#可以在启动的时候带参数
/home/hadoop/app/hive/bin/hive --hiveconf hive.root.logger=INFO,console

#这样就可以将log信息打印到控制台,我们可以详细的看到(INFO,console 是将INFO以上的log信息打印到console上)
#这样在我们程序出错的时候,我们可以通过这样的调试方式进行直观的错误排查


```


# 显示当前数据库以及表头的信息

```


#打印表头
#需要在hive-site.xml中添加

<property>
<name>hive.cli.print.current.db</name>
<value>true</value>
<description>password to use against metastore database</description>
</property>

#默认的数据库时default
hive (default)> show databases;
OK
database_name
default
hadoop_hive

hive (default)> use hadoop_hive;
OK

#改变数据库为hadoop_hive
hive (hadoop_hive)> 



#打印当前的数据库信息
#需要在hive-site.xml中添加
<property>
<name>hive.cli.print.header</name>
<value>true</value>
<description>password to use against metastore database</description>
</property>


hive (hadoop_hive)> select *from default.test_table;
OK
#会将表名+字段名带上
test_table.id   test_table.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu


```


# 启动hive时指定参数
```
bin/hive --hiveconf <property=value>
```

```
#查看启动hive命令的帮助
[root@hdp-node-01 root]# /home/hadoop/app/hive/bin/hive -help

usage: hive
 -d,--define <key=value>          Variable subsitution to apply to hive
                                  commands. e.g. -d A=B or --define A=B
    --database <databasename>     Specify the database to use
 -e <quoted-query-string>         SQL from command line
 -f <filename>                    SQL from files
 -H,--help                        Print help information
    --hiveconf <property=value>   Use value for given property
    --hivevar <key=value>         Variable subsitution to apply to hive
                                  commands. e.g. --hivevar A=B
 -i <filename>                    Initialization SQL file
 -S,--silent                      Silent mode in interactive shell
 -v,--verbose                     Verbose mode (echo executed SQL to the
                                  console)

#可以在启动的时候带参数
/home/hadoop/app/hive/bin/hive --hiveconf hive.root.logger=INFO,console

#这样就可以将log信息打印到控制台,我们可以详细的看到(INFO,console 是将INFO以上的log信息打印到console上)
#这样在我们程序出错的时候,我们可以通过这样的调试方式进行直观的错误排查

```


# 查看当前所有的命令行配置信息

```

#查看所有的设置的环境变量的信息
hive (default)> set;

system:sun.boot.library.path=/home/hadoop/app/jdk1.7.0_80/jre/lib/amd64
system:sun.cpu.endian=little
system:sun.cpu.isalist=
system:sun.io.unicode.encoding=UnicodeLittle
system:sun.java.command=org.apache.hadoop.util.RunJar /home/hadoop/app/hive/lib/hive-cli-1.2.1.jar org.apache.hadoop.hive.cli.CliDriver
system:sun.java.launcher=SUN_STANDARD
system:sun.jnu.encoding=UTF-8
system:sun.management.compiler=HotSpot 64-Bit Tiered Compilers
system:sun.os.patch.level=unknown
system:user.country=US
system:user.dir=/tmp/root
system:user.home=/root
system:user.language=en
system:user.name=root
system:user.timezone=Asia/Shanghai

hive (default)> set;



```

