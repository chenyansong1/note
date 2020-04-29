---
title: SparkStreaming之checkpoint机制
categories: spark  
tags: [spark]
---

每一个spark streaming应用,正常来说,都是要7*24小时运转的,这就是实时计算程序的特点,因为要持续不断的对数据进行计算,因此,读实时计算应用的要求,应该是必须要能够对与应用程序逻辑无关的失败进行容错

如果要实现这个目标,spark streaming程序就必须将足够的信息checkpoint到容错的存储系统上,从而让他能够从失败中进行恢复,有两种数据需要被进行checkpoint

1.元数据checkpoint---将定义了流式计算逻辑的信息,把偶才能到容错的存储系统上,如HDFS,当运行spark streaming应用程序的driver进程所在节点失败时,该信息可以用于进行行恢复,元数据信息包括了
1.1.配置信息---创建spark streaming的应用程序的配置信息,比如sparkConf中的信息
1.2.DStream的操作信息---定义了spark stream应用程序的计算逻辑的DStream操作信息
1.3.未处理的batch信息---那些job正在排队,还没处理的batch信息


2.数据checkpoint----将实时计算过程中产生的RDD的数据保存到可靠的存储系统中

对于一些存在多个batch的数据进行聚合的,有状态的Transformation操作,这是非常有用的,在这种Transformation操作中,生成的RDD是依赖于之前的batch的RDD的,这会导致随着时间的推移,RDD的依赖链条变得越来越长

要避免由于依赖链条越来越长,导致的一起变得越来越长的失败恢复时间,有状态的Transformation操作执行过程中间产生的RDD,会定期被checkpoint到可靠的存储系统上,比如HDFS,从而削减RDD的依赖链条,进而缩短失败恢复时,RDD的恢复时间

一句话概括,元数据checkpoint主要是为了从driver失败中进行恢复,而RDD的checkpoint主要是为了使用到有状态的Transformation操作时,能够在其生产出的数据丢失时,进行快速恢复


何时启用checkpoint机制?
1.使用了有状态的Transformation操作--比如updateStateByKey,或者reduceByKeyAndWindow操作,被使用了,那么checkpoint目录要求是必须提供的,也就是必须开启checkpoint机制,从而进行周期性的RDD checkpoint

2.要保证可以从Driver失败中进行恢复----元数据checkpoint需要启用,来进行	这种情况的恢复

要注意的是,并不是说,所有的spark streaming应用程序,都要启用checkpoint机制,如果即不强制要求从Driver失败中自动进行恢复,又没有有状态的Transformation操作,那么就不需要启用checkpoint,事实上,这么做反而有助于提升性能


如何启用checkpoint机制?
1.对于有状态的Transformation操作,启用checkpoint机制,定期将其产生的RDD数据checkpoint,是比较简单的

可以通过配置一个容错的,可靠的文件系统(比如HDFS)的目录,来启用checkpoint机制,checkpoint数据就会写入该目录,使用StreamingContext的checkpoint()方法即可,然后,你就可以放心使用有状态的Transformation操作了

2.如果为了要从Driver失败中进行恢复,那么启用checkpoint机制,是比较复杂的,需要改写spark streaming应用程序

当应用程序第一次启动的时候,需要创建一个新的StreamingContext,并且调用其start()方法,进行启动,当Driver从失败中恢复过来的时候,需要从checkpoint目录中记录的元数据中,恢复出来一个StreamingContext

代码
```
def func2CreateConext():StreamingContext = {
	val ssc = new StreamingContext(...)
	val lines = ssc.socketTextStream(...)
	//我们要写的代码逻辑
	ssc.checkpoint(checkpointDir)

	ssc
}


val context =StreamingContext.getOrCreate(checkpointDir,func2CreateConext _)

context.start()
context.awaitTermination()

/*
在func2CreateConext中的ssc.checkpoint(checkpointDir)中的checkpointDir
和
val context =StreamingContext.getOrCreate(checkpointDir,func2CreateConext _)的目录名要一致

*/
```


按照上诉方法,进行spark streaming应用程序的重写后,当第一次运行程序时,如果发现checkpoint目录不存在,那么就使用定义的函数来第一次创建一个StreamingContext,并将其元数据写入checkpoint指定的目录,当从Driver失败中恢复过来的时候,发现checkpoint目录已经存在了,那么会使用该目录中的元数据创建一个StreamingContext

但是上面的重写应用程序的过程,只是实现Driver失败自动恢复的第一步,第二部是:必须确保Driver可以在失败时,会自动被重启

要能够自动从Driver失败中恢复过来,运行spark Streaming应用程序的集群,就必须监控Driver运行的过程,并且在他失败时将它重启,对于spark自身的Standalone模式,需要进行一些配置去supervise driver,在他失败时将其重启

首先,要在spark-submit中,添加--deploy-mode参数,默认其值为client,即在提交应用的机器上启动Driver,但是要能够自动重启Driver,就必须将其值设置为cluster(在集群中的某个节点启动Driver),此外,需要添加--supervise参数(自动重启)

使用了上述第二部提交应用之后,就可以让Driver在失败时自动被重启,并且通过checkpoint目录的元数据恢复StreamingContext

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/checkpoint_1.png)


