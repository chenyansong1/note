---
title: spark的提交命令在shell中组织
categories: spark   
toc: true  
tag: [spark]
---


```

/*
/usr/local/spark-1.5.2-bin-hadoop2.6/bin/spark-submit \
--class cn.itcast.spark.WordCount \
--master spark://node1.itcast.cn:7077 \
--executor-memory 2G \
--total-executor-cores 4 \
/root/spark-mvn-1.0-SNAPSHOT.jar \
hdfs://node1.itcast.cn:9000/words.txt \
hdfs://node1.itcast.cn:9000/out

其中:
--class cn.itcast.spark.WordCount \         //是启动类
/root/spark-mvn-1.0-SNAPSHOT.jar \        //jar包
hdfs://node1.itcast.cn:9000/words.txt \        //输入参数1 ,要读取的文件
hdfs://node1.itcast.cn:9000/out        //输入参数2 , 内容输出的文件
*/

```