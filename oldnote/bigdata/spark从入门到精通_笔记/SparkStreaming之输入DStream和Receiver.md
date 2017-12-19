---
title: SparkStreaming之输入DStream和Receiver
categories: spark  
tags: [spark]
---



输入DStream代表了来自数据源的输入数据流,在之前的WordCount例子中,lines就是一个输入DStream(SocketInputDStream),代表了从socket服务接收到的数据流,除了文件数据流之外,所有的输入DStream都会绑定一个Receiver对象,该对象是一个关键的组件,用来从数据源接收数据,并将其存储在spark的内存中,以供后续处理


spark Streaming提供了两种内置的数据源的支持
1.基础数据源:StreamingContext API(如:StreamingContext.socketTextStream()方法)中直接提供了对这些数据源的支持,比如:文件,socket,Akka Actor等,
2.高级数据源:诸如Kafka,flume,Kinesis,Twitter等书卷,通过第三方工具类提供支持,这些数据源的使用,需要引用其依赖
3.自定义数据源:我们可以自己定义数据源,来决定如何接受和存储数据


输入DStream和Receiver详解

要注意的是,如果你想要在实时计算应用中并行接收多条数据流,可以创建多个输入DStream,这样就会创建多个Receiver,从而并行的接收多个数据流,但是要注意的是,一个spark Streaming Application的Executor是一个长时间运行的任务,因此,他会独占分配给spark streaming Application的cpu core,从而只要spark streaming运行起来以后,这个节点上的cpu core,就没法给其他应用使用了

使用本地模式,运行程序时,绝对不能使用local或者是local[1],因为那样的话,只会给执行输入DStream的executor分配一个线程,而spark streaming底层的原理是,至少要有两条线程,一个线程用来分配给Receiver接收数据,一条线程用来处理接收到的数据,因此必须使用local[n],n>=2的模式

如果不设置Master,也就是直接将spark streaming应用提交到集群上运行,那么首先,必须要求集群节点上,有>1个cpu core,其次给spark streaming的每个executor分配的core,必须>1,这样,才能保证分配到executor上运行的的输入DStream,两条线程并行,一条运行Receiver,接收数据,一条处理数据,否则的话,只会接收数据,不会处理数据

因此,在实际工作中,都要给每个executor的cpu core设置超过1个即可


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/Receiver.png)


