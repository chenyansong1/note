---
title: 远程dug
categories: spark  
tags: [spark]
---


# 1.master使用远程debug
```
#步骤1
在Master端的spark-env.sh文件中添加如下参数
export SPARK_MASTER_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=10000"

#步骤2
start-master.sh
执行完这个脚本
jps
4828 -- main class information unavailable
4860 Jps
 

#步骤3
通过一个IDE  建立一个remote application
172.16.0.11 10000
在本地的代码打断点
debug按钮开始调试
```


# 2.worker使用远程debug

```
#步骤1
在Worker所在的配置文件中添加一个环境变量
export SPARK_WORKER_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=10001"

 
#步骤2
start-slave.sh spark://node-1.itcast.cn:7077
执行jps命令
2891 -- main class information unavailable
2923 Jps
 
#步骤3
用一个IDE工具连接 建立一个remote application
172.16.0.12 10001
在本地的代码打断点
debug按钮开始调试
```
# 3.远程dug自己的一个工程
```
#步骤1
#任务提交
#--driver-java-options就是配置远程dug
bin/spark-submit --class cn.itcast.spark.WC --master spark://node-1.itcast.cn:7077 --driver-java-options "-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=10002" /root/bigdata-2.0.jar hdfs://node-1.itcast.cn:9000/words.txt hdfs://node-1.itcast.cn:9000/wordsout
 


#步骤2
用一个IDE工具连接 建立一个remote application
172.16.0.13 10002
在本地的代码打断点
debug按钮开始调试

```



