---
title: hive的几种交互式操作
categories: hive  
tags: [hive]
---


```
[root@hdp-node-01 hive]# ./bin/hive -help
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
[root@hdp-node-01 hive]# 
```


# bin/hive -e 

```

bin/hive -e <quoted-query-string>  # 带引号的查询字符串
#他是不会进入hive的交互式命令的
[root@hdp-node-01 hive]# ./bin/hive -e "select * from default.test_table ;"
....
Logging initialized using configuration in file:/home/hadoop/app/apache-hive-1.2.1-bin/conf/hive-log4j.properties
OK
test_table.id   test_table.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu
Time taken: 4.049 seconds, Fetched: 6 row(s)
[root@hdp-node-01 hive]# 


```

# bin/hive -f

在实际的开发过程中,就是使用这样方式

```
-f <filename> #将我们的sql语句放入一个文件中

touch hivef.sql
	select * from default.test_table ;

bin/hive -f hivef.sql


[root@hdp-node-01 hive]# bin/hive -f hivef.sql 
Logging initialized using configuration in file:/home/hadoop/app/apache-hive-1.2.1-bin/conf/hive-log4j.properties
OK
test_table.id   test_table.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu
Time taken: 2.28 seconds, Fetched: 6 row(s)



#将执行的结果写入一个文件中
[root@hdp-node-01 hive]# bin/hive -f hivef.sql > /tmp/hivef.txt
Logging initialized using configuration in file:/home/hadoop/app/apache-hive-1.2.1-bin/conf/hive-log4j.properties
OK
Time taken: 2.258 seconds, Fetched: 6 row(s)
[root@hdp-node-01 hive]# 


[root@hdp-node-01 hive]# cat /tmp/hivef.txt 
test_table.id   test_table.name
11      zhangsan
22      lisi
33      wangwu
11      zhangsan
22      lisi
33      wangwu
[root@hdp-node-01 hive]# 

```

# bin/hive -i

```

-i <filename>     Initialization SQL file (初始化的sql文件)

通常与用户自定义的udf相互使用

```







