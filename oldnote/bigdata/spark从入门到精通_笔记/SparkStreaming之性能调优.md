---
title: SparkStreaming之性能调优
categories: spark  
tags: [spark]
---



# 数据接收的并行度调优

通过网络接收数据时(比如kafka,flume),会将数据反序列化,并存储在spark的内存中,如果数据接收称为系统的瓶颈,那么可以考虑并行化的数据接收,每一个输入DStream都会在某个Worker的Executor上,启动一个Receiver,该Receiver接收一个数据流,因此可以通过创建多个输入DStream,并且配置他们接收数据源不同的分区数据,达到多个数据流的效果,比如说,一个接收两个kafka topic的输入DStream,可以被拆分为两个DStream,每个分别接收一个topic的数据,这样就会创建两个Receiver,从而并行的接收数据,进而提升吞吐量,读个DStream可以使用union算子进行聚合,从而形成一个DStream,然后后续的Transformation算在操作都针对一个聚合后的DStream即可

```
int numStreams = 5
List<DStream> kafkaStreams = new ArrayList<DStream>(numStreams)

for(int i=0;i<DStream; i++){
	kafkaStreams.add(KafkaUtils.createStream(...))
}


unionedDStream = streamingContext.union(kafkaStreams.get(0),kafkaStreams.get(2)...)


unionedDStream.print()


```


数据接收并行度调优,除了创建更多输入DStream和Receiver以外,还可以考虑调节block interval参数,"spark.streaming.blockInterval"可以设置block interval(默认是200ms),对于大多数Receiver来说,在将接收到的数据保存到Spark的BlockManager之前,都会将数据切分为一个一个的block,而每个batch中的block数量,则决定了该batch对应的RDD的partition的数量,以及针对该RDD执行Transformation操作时,创建的task的数量,每个batch对应的task数量是可以大约估计的:
batch interval / (block interval)

例如说:batch interval为2s,block interval为200ms,会创建10个task,如果你认为每个batch的task的数量太少,即低于每台机器的cpu core数量,那么就说明batch的task数量是不够的,因为所有的cpu资源无法完全被利用起来,要为batch增加block的数量,那么就减小block interval,而然,推荐的block interval最小值是50ms,如果低于这个数值,那么大量task的启动时间,可能会变成一个性能开销点



除了上述说的两个提升设局接收并行度的方式,还有一种方法,技术显示的对输入数据流进行重分区,使用
inputStream.reparation(num of partitions)即可,这样就可以将接收到的batch,分布到指定的数量的机器上,然后再进行进一步的操作


# 任务启动调优
如果每秒钟启动的task过多,比如每秒启动50个,那么发送这些task到Worker节点上的Executor的性能开销会比较大,而且此时基本就很难达到毫秒级的延迟了,使用下面的操作可以减少这方面的性能开销;
1.Task序列化:使用Kryo序列化类库来序列化task,可以减小task的大小,从而减少发送这些task到各个Worker节点上的Executor的时间
2.执行模式:在Strandalone模式下,运行spark,可以达到更少的task启动时间



# 数据处理的并行度调优
如果在计算的任何stage中使用并行task的数量没有足够多,那么集群资源时无法被充分利用的,举例说:对于分布式的reduce操作,比如reduceByKey和reduceByKeyAndWindow,默认的并行task的数量是由"spark.default.parallelism"参数决定的,你可以在reduceByKey等操作中,传入第二个参数,手动指定该参数的并行度,也可以调节全局的"spark.default.parallelism"参数



# 数据序列化的调优
数据序列化造成的系统开销可以由序列化的优化来减小,在流式计算的场景下,有两种类型的数据需要序列化:
1.输入数据,默认情况下,接收到的输入数据,是存储在Executor的内存中的,使用的持久化级别是StorageLevel.MEMORY_AND_DISK_SER_2,这意味着,数据被序列化为字节从而减少GC开销,并且会复制以进行Executor失败的容错,因此数据首先会存储在内存中,然后在内存不足时会溢写到磁盘上,从而为流式计算来保存所有需要的数据,这里的序列化有明显的性能开销---Receiver必须反序列化从网络接收到的数据,然后再使用spark的序列化格式序列化数据

3.流式计算操作生成的持久化RDD,流式计算操作生成的持久化RDD可能会持久化到内存中,例如:窗口操作默认就会将数据持久化在内存章,因为这些数据后面可能会在多个窗口中被使用,并被处理多次,然而,不像spark core的默认持久化级别,StorageLevel.MEMORY_ONLY,流式计算操作生成的RDD的默认持久化级别是:StorageLevel.MEMORY_ONLY_SER,默认就会减小GC开销


在上述的场景中,使用Kryo序列化类库可以减小cpu和内存的性能开销,使用Kryo时,一定要考虑注册自定义的类,并且禁用对应引用的tracking(spark.Kryo.referenceTracking)

在写特殊的场景下,比如需要为流式应用保持的数据总量并不是很多,也许可以将数据以非序列化的方式进行持久化,从而减少序列化和反序列化的cpu开销,而且又不会有太昂贵的GC开销,那么你可以考虑通过显示的设置持久化级别,来禁止持久化时对数据进行序列化,这样就减少用于序列化和反序列化的cpu性能开销,并且不用承担太多的gc开销




# batch interval 

如果想让一个运行在集群上的spark streaming应用程序可以稳定,他就必须尽可能快的处理接收到的数据,换句话说,batch应该在生成之后,就尽可能的处理掉,对于一个应用来说,可以通过观察spark UI上的batch的处理时间来定,batch处理时间必须小于batch interval时间,不然上一个batch还没有处理成功,那么下一个batch就来了,这样会造成数据堆积

基于流式计算的本质,batch interval对于,在固定集群资源条件下,应用能保持的数据接收速率,会有巨大的影响,例如:在WordCount例子中,对于一个特定的数据接收速率,应用业务可以保证每2秒打印一次单词计数,而不是每500ms,因为batch interval 需要被设置的让与其的数据接收速率可以在生产环境中保持住

为你的应用计算正确的batch大小的比较好的方法,是在一个很保守的batch interval ,比如5-10s,以很慢的数据接收速率进行测试,要检查应用是否跟得上这个数据速率,可以检查每个batch的处理时间的延迟,如果处理时间与batch interval基本吻合,那么应用就是稳定的,否则,如果batch调度的延迟持续增加,那么就意味着无法跟得上这个速率,也就是不稳定的,因此,你要想有一个稳定的配置,可以尝试提升数据处理的速度,或者增加batch interval,记住,由于临时性的数据增长导致的暂时的延迟,可以合理的,只要延迟情况可以在短时间内恢复即可



# 内存调优

Transformation操作会决定你的内存的使用:
spark streaming应用需要的集群内UC你资源,是由使用的Transformation操作类型决定的,举例来说,如果想要使用一个窗口长度为10分钟的window操作,那么集群就必须有足够的内存来保存10分钟的数据,如果想要使用updateStateByKey来维护许多key的state,那么你的内存资源就必须足够大,返货来说,如果想要做一个简单的map-filter-sotre操作,那么需要使用的内存就很少


通常来说,通过Receiver接收到的数据,会使用StorageLevel.MEMORY_AND_DISK_SER_2持久化级别来进行存储,因此无法保存在内存中的数据会溢写到磁盘上,而溢写到磁盘上,是会降低应用的性能的,因此,通常是建议为应用提供他需要的足够的内存资源,建议在一个小规模的场景下测试内存的使用量,并进行评估


内存调优的另外一个方面是垃圾回收,对于流式应用来说,如果要获得低延迟的,肯定不想要有因为JVM垃圾回收导致的长时间延迟,有很多参数可以帮助降低内存使用和GC开销:
1.DStream的持久化级别:
输入数据和某些操作产生的中间RDD,默认持久化时都会序列化为字节,与非序列化的方式相比,这会降低内存和GC开销,使用Kryo序列化机制可以进一步减少内存使用和GC开销,进一步降低内存使用率,可以对数据进行压缩,由"spark.rdd.compress"参数控制(默认false)

2.清理旧数据:
默认情况下,所有输入数据和通过DStream Transformation操作生成的持久化的RDD,会自动被清理,spark streaming会决定何时清理这些数据,取决于Transformation操作类型,例如:你在使用窗口长度为10分钟的window操作,spark会保持10分钟以内的数据,时间过了以后会清理旧数据,但是在某些特殊场景下,比如spark sql和spark streaming整合使用时,在异步开启的线程中,使用spark streaming针对batch RDD进行执行查询,那么就㤇让spark 保持更长时间的数据,知道sparksql查询结束,可以使用:streamingContext.remember()方法来实现

3.CMS垃圾回收:
使用并行的mark-sweep垃圾回收机制,被推荐使用,用来保持GC开销,虽然并行的GC会降低吞吐量,但是还是建议使用它,来减少batch的处理时间(降低处理过程中的gc开销),如果要使用,那么要在driver端和Executor端都开启,在spark-submit中使用--driver-java-options设置,使用spark.executor.extra.javaOptions参数设置
XX:+UseConMarkSweepGC














