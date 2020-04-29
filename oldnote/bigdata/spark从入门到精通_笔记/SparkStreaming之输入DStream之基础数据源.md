---
title: SparkStreaming之输入DStream之基础数据源
categories: spark  
tags: [spark]
---

基础数据源
1.socket:StreamingContext.socketTextStream()

2.HDFS文件
StreamingContext.fileStream()
基于HDFS的文件实时计算,其实就是监控一个HDFS目录,只要其中有新文件出现,就实时处理,相当于处理实时的文件流
```
def fileStream[
  K: ClassTag,
  V: ClassTag,
  F <: NewInputFormat[K, V]: ClassTag
] (directory: String): InputDStream[(K, V)] 

```

spark Streaming会监视指定的HDFS目录,并且处理出现在目录中的文件,要注意的是:
1.所有放入HDFS目录中的文件,都必须有相同的格式,
2.必须使用移动或者重命名的方式
3.将文件移入目录,一旦处理之后,文件的内容即使改变了,也不会再处理了
4.基于HDFS文件的数据源是没有Receiver的,因此不会占用一个cpu core

```
 // local后面必须跟一个方括号,里面填写一个数字,代表了用几个线程来执行我们的spark streaming程序
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")

 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(1))

 // 针对HDFS目录创建DStream
 val lines = ssc.textFileStream("hdfs:spark1:9000/wordcount_dir")
 /* 其实在textFileStream底层是调用了fileStream
   def textFileStream(directory: String): DStream[String] = withNamedScope("text file stream") {
     fileStream[LongWritable, Text, TextInputFormat](directory).map(_._2.toString)
   }
  */

 // 执行WordCount逻辑
 val words = lines.flatMap(_.split(" "))
 val pairs = words.map((_,1))
 val wordcount = pairs.reduceByKey(_ + _)

 // 打印测试
 wordcount.print

 ssc.start()
 ssc.awaitTermination()
 ssc.stop()

```

