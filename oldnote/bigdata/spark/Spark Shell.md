---
title: Spark Shell
categories: spark  
tags: [spark]
---


# 1.启动shell
&emsp;spark-shell是Spark自带的交互式Shell程序，方便用户进行交互式编程，用户可以在该命令行下用scala编写spark程序

```

/export/servers/spark/bin/spark-shell \
--master spark://hdp-node-01:7077 \
--executor-memory 1g \
--total-executor-cores 2


/*

参数说明：
--master spark://node1.itcast.cn:7077 指定Master的地址
--executor-memory 2g 指定每个worker可用内存为2G
--total-executor-cores 2 指定整个集群使用的cup核数为2个


注意：
如果启动spark shell时没有指定master地址，但是也可以正常启动spark shell和执行spark shell中的程序，其实是启动了spark的local模式，该模式仅在本机启动一个进程，没有与集群建立联系。
 
Spark Shell中已经默认将SparkContext类初始化为对象sc。用户代码如果需要用到，则直接应用sc即可
exit 退出shell


*/


```


# 2.在spark shell中编写WordCount程序


1.首先启动hdfs
2.向hdfs上传一个文件到

words.txt

```
[root@hdp-node-02 export]# cat words.txt

tom jerry
chenyansong zhangsan
tom jerry
wo shi who
chenyansong

```
 
上传文件

```
hdfs dfs -put words.txt /wordcount/
```

3.在spark shell中用scala语言编写spark程序

```
scala> sc.textFile("hdfs://hdp-node-01:9000/wordcount/words.txt").flatMap(_.split(" ")).map((_ ,1)).reduceByKey(_+_).saveAsTextFile("hdfs://hdp-node-01:9000/wordcount/out")

```
 
4.使用hdfs命令查看结果

```
[root@hdp-node-02 export]# hdfs dfs -ls /wordcount/out3
Found 3 items
-rw-r--r--   3 root supergroup          0 2016-12-17 21:13 /wordcount/out3/_SUCCESS
-rw-r--r--   3 root supergroup         54 2016-12-17 21:13 /wordcount/out3/part-00000
-rw-r--r--   3 root supergroup         16 2016-12-17 21:13 /wordcount/out3/part-00001

[root@hdp-node-02 export]# hdfs dfs -cat /wordcount/out3/p*
(zhangsan,1)
(shi,1)
(tom,2)
(wo,1)
(who,1)
(jerry,2)
(chenyansong,2)

```
 
 说明：

* sc是SparkContext对象，该对象时提交spark程序的入口
* textFile(hdfs://node1.itcast.cn:9000/words.txt)是hdfs中读取数据
* flatMap(_.split(" "))先map在压平
* map((_,1))将单词和1构成元组
* reduceByKey(_+_)按照key进行reduce，并将value累加
* saveAsTextFile("hdfs://node1.itcast.cn:9000/out")将结果写入到hdfs中




