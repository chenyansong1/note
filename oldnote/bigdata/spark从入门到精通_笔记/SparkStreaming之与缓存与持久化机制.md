---
title: SparkStreaming之与缓存与持久化机制
categories: spark  
tags: [spark]
---


与RDD类似,spark streaming也可以让开发人员手动控制,将数据流中的数据持久化到内存中,对DStream调用persist()方法,就可以让spark streaming自动将该数据流中的所有产生的RDD,都持久化到内存中

如果要对一个DStream多次执行操作,那么对DStream持久化是非常有用的,因为多次操作,可以共享使用内存中的一份缓存数据,对于基于窗口的操作,比如reduceByKeyAndWindow,以及基于状态的操作,比如updateStateByKey,默认就隐式开启了持久化的机制,即spark streaming默认就会将上述操作产生的数据,缓存到内存中,不需要开发人员手动调用persist()方法


对于通过网络接收数据的输入流,比如:socket,kafka,flume等,默认的持久化级别是将数据复制一份,以便于容错

与RDD不同的是,默认的持久化级别,统一都是要序列化的





