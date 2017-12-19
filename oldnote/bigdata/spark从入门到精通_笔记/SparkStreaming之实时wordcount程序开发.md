---
title: SparkStreaming之实时wordcount程序开发
categories: spark  
tags: [spark]
---


```
 // local后面必须跟一个方括号,里面填写一个数字,代表了用几个线程来执行我们的spark streaming程序
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")
 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(1))

 // 首先,创建输入DStream,代表了一个从数据源(kafka,socket)来的持续不断的实时数据流
 // 这里创建的数据源是socket:参数:监听的主机和端口
 val lines = ssc.socketTextStream("localhost", 9999)
 // 返回的是一个DStream,表示每隔一秒会有一个RDD,其中封装了这一秒发送过来的数据
 // RDD的元素类型为String,即一行一行的文本

 // 开始对接收到的数据,对DStream执行算子操作
 // 在底层实际上会对DStream中的一个一个的RDD,执行我们应用在DStream上的算子
 // 产生的新的RDD会作为新DStream中的RDD
 val words = lines.flatMap(_.split(" "))
 val pairs = words.map((_,1))
 val wordCounts = pairs.reduceByKey(_ + _)

 // 可以看到spark streaming开发程序和spark core很像
 // 因为DStream是对Rdd的封装,那么DStream操作,就是对Rdd的操作

 // 休眠,打印(测试用)
 Thread.sleep(50000)
 wordCounts.print

 ssc.start()
 ssc.awaitTermination()

 /*总结:
 1.每秒钟发送到指定socket端口中的数据,都会被lines DStream接收到
 2.lines DStream会把每秒的数据,也就是一行一行的文本,诸如"hello world", 封装成一个RDD
 3.然后对每秒钟中对应的RDD执行后续的一系列的算子操作
 4.最终就得到了每秒钟发送过来的单词统计
 5.可以将最后计算出的wordcount中的一个一个的RDD,写入外部的缓存,或者持久化DB
  */

```

