---
title: SparkStreaming之StreamingContext详解
categories: spark  
tags: [spark]
---



有两种创建StreamingContext

```
//通过SparkConf直接创建
val conf = new SparkConf().setAppName("Streaming").setMaster("local[2]")
val ssc = new StreamingContext(conf,Seconds(1))

//或者:通过SparkContext创建
val sc = new SparkContext(conf)
val ssc = new StreamingContext(sc,Durations.seconds(1))

```


一个StreamingContext定义之后,必须做以下几件事情:
1.通过创建输入DStream来创建输入数据源
2.通过对DStream定义Transformation和output算子操作,来定义实时计算逻辑
3.调用StreamingContext的start()方法,来开始实时处理数据
4.调用StreamingContext的waitTermination()方法来等待应用程序的终止,可以使用	Ctrl+c手动终止,或者就是让他持续不断的运行进行计算
5.也可以通过调用StreamingContext的stop()方法来停止应用程序的运行

需要主要的要点:
1.只要一个StreamingContext启动之后,就不能再往其中添加任何计算逻辑了,比如执行了start()方法之后,还给某个DStream执行一个算子,这样是无效的,所以一般start()方法之后就是waitTermination()方法,然后后面就没有任何执行逻辑了
2.一个StreamingContext停止之后,是肯定不能够重启的,调用stop()之后,不能再调用start()
3.一个JVM同时只能有一个StreamingContext启动,在你的应用程序中,不能创建两个StreamingContext
4.调用stop()方法时,会同时停止内部的SparkContext,如果不希望如此,还希望后面继续使用SparkContext创建其他类型的Context,比如SQLContext,那么就使用stop(false)
5.一个SparkContext可以创建多个StreamingContext,只要上一个先用stop(false)停止,在创建下一个即可

