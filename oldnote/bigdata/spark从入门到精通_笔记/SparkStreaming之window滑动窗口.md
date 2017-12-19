---
title: SparkStreaming之window滑动窗口
categories: spark  
tags: [spark]
---



spark streaming提供了滑动窗口操作的支持,从而让我们可以对一个滑动窗口内的数据执行计算操作,每次掉落在窗口内的RDD的数据,会被聚合起来执行计算操作,然后生成的RDD,会作为window DStream的一个RDD,比如下图中,就是对每三秒钟的数据执行一次滑动窗口计算,这3秒内的3个RDD会被聚合起来进行处理,然后过了2秒钟,又会对最近3秒内的数据执行滑动窗口计算,所以每个滑动窗口操作,都必须指定2个参数,窗口长度以及滑动间隔,而且这两个参数值都必须是batch间隔的整数倍

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/window.png)



window滑动窗口的操作

Transformation|含义
:-------------|:--------
window|对每个滑动窗口的数据执行自定义的计算
countByWindow|对每个滑动窗口的数据执行count操作
reduceByWindow|对每个滑动窗口的数据执行reduceByKey操作
reduceByKeyAndWindow|对每个滑动窗口的数据执行reduceByKey操作
countByValueAndWindow|对每个滑动窗口的数据执行countByValue操作


案例:热点搜索词滑动统计,每隔5秒钟,统计最近10秒钟的搜索词的搜索频次,并打印出来排名最靠前的3个搜索词以及出现次数

```
 val conf = new SparkConf()
   .setAppName("Streaming")
   .setMaster("local[2]")

 // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
 val ssc = new StreamingContext(conf,Seconds(5))

 // 搜素日志的格式: username searchWord
 val searchLog = ssc.socketTextStream("spark1", 9999)
 // (searchWord,1)
 val searchWordPairDStream = searchLog.map(_.split(" ")(2)).map((_,1))

 /*
 第一个参数是:reduceByKey中的需要指定的函数
 第二个参数是:窗口长度,这里是60秒
 第三个参数:滑动间隔,这是是10秒
 也就是说:每隔10秒钟,将最近60秒的数据,作为一个窗口,
 进行内部的RDD的聚合,统一成一个RDD,然后进行后续计算

 每隔10秒钟,就会滑动一下,会将之前60秒的RDD(因为一个batch的间隔是5秒,
 所以之前60秒就有12个RDD)给聚合起来统一执行reduceByKey操作,
 所以这里的reduceByKeyAndWindow是针对每隔窗口执行计算的,而不是针对某个DStream中的RDD
  */
 val searchWordCountsDStream = searchWordPairDStream.reduceByKeyAndWindow((x:Int,y:Int)=>x+y,Durations.seconds(60),Durations.seconds(10))
 /*
  @param reduceFunc associative and commutative reduce function
  @param windowDuration width of the window; must be a multiple of this DStream's batching interval(必须是batch的整数倍)
  @param slideDuration  sliding interval of the window
  */

 // 执行transform,因为一个窗口就是一个60秒钟的数据,会变成一个RDD,
 // 然后对这一个RDD根据每个搜索词出现的频率进行排序,
 // 然后获取排名前3的热点搜索词
 val finalDStream = searchWordCountsDStream.transform{
   searchWordCountsRdd=>
     // 执行搜索词和频率的反转,格式为:(count, searchWord)
     val searchCountsWordRdd = searchWordCountsRdd.map(t=>(t._2, t._1))
     val searchCountsWordSortedRdd = searchCountsWordRdd.sortByKey(false)
     // 格式为:(searchWord, count)
     val searchWordCountsSortedRdd = searchCountsWordSortedRdd.map(t=>(t._2, t._1))
     // 然后take(),获取排名前3的特点搜索词
     val top3SearchWordCounts = searchWordCountsSortedRdd.take(3)

     top3SearchWordCounts.foreach(println)

     null
 }

 // 触发
 finalDStream.print

 ssc.start()
 ssc.awaitTermination()
 ssc.stop()
 /*
 其实window函数就是对一个时间段中的数据进行统计,比如:看过去60秒钟内的热点搜索词
  */

```




