---
title: Spark2.0新特性之Structured Streaming
categories: spark  
tags: [spark]
---


# Structured Streaming介绍

<!--more-->

## 流式计算的现状
大多数的流式计算引擎（比如storm、spark streaming等）都仅仅关注流数据的计算方面：比如使用一个map函数对一个流中每条数据都进行转换，或者是用reduce函数对一批数据进行聚合。但是，实际上在大部分的流式计算应用中，远远不只是需要一个流式计算引擎那么简单。相反的，流式计算仅仅在流式应用中占据一个部分而已。因此现在出现了一个新的名词，叫做持续计算/应用，continuous application。比如以下一些持续应用的例子：

1、更新需要以服务形式实时提供出去的数据：例如，我们可能需要更新一份数据，然后其他用户会通过web应用来实时查询这些数据。这种情况下，一个技术难题就是实时计算应用如何与实时数据服务进行交互，比如说，当实时计算应用在更新数据的时候，如果用户通过实时数据服务来进行查询，此时该如何处理？因此为了处理这种场景下的技术难题，就必须以一个完整的持续计算应用的方式来构建整个系统，而不是站在实时计算的角度，仅仅考虑实时更新数据。
2、实时ETL（Extract、Transform和Load）：实时计算领域一个常见的应用就是，将一个存储系统中的数据转换后迁移至另外一个存储系统。例如说，将JSON格式的日志数据迁移到Hive表中。这种场景下的技术难题就在于，如何与两边的存储系统进行交互，从而保证数据不会丢失，同时也不会发生重复。这种协调逻辑是非常复杂的。
3、为一个已经存在的批量计算作业开发一个对应的实时计算作业：这个场景的技术难题在于，大多数的流式计算引擎都无法保证说，它们计算出的结果是与离线计算结果相匹配的。例如说，有些企业会通过实时计算应用来构建实时更新的dashboard，然后通过批量计算应用来构建每天的数据报表，此时很多用户就会发现并且抱怨，离线报表与实时dashboard的指标是不一致的。
4、在线机器学习：这类持续计算应用，通常都包含了大型的静态数据集以及批处理作业，还有实时数据流以及实时预测服务等各个组件。

以上这些例子就表明了在一个大型的流式计算应用中，流式计算本身其实只是占据了一个部分而已，其他部分还包括了数据服务、存储以及批处理作业。但是目前的现状是，几乎所有的流式计算引擎都仅仅是关注自己的那一小部分而已，仅仅是做流式计算处理。这就使得开发人员需要去处理复杂的流式计算应用与外部存储系统之间的交互，比如说管理事务，同时保证他们的流式计算结果与离线批处理计算结果保持一致。这就是目前流式计算领域急需要解决的难题与现状。


持续计算应用可以定义为，对数据进行实时处理的整套应用系统。spark社区希望能够让开发人员仅仅使用一套api，就可以完整持续计算应用中各个部分涉及的任务和操作，而这各个部分的任务和操作目前都是通过分离的单个系统来完成的，比如说实时数据查询服务，以及与批处理作业的交互等。举例来说，未来对于解决这些问题的一些设想如下：

1、更新那些需要被实时提供服务的数据：开发人员可以开发一个spark应用，来同时完成更新实时数据，以及提供实时数据查询服务，可能是通过jdbc相关接口来实现。也可以通过内置的api来实现事务性的、批量的数据更新，对一些诸如mysql、redis等存储系统。
2、实时ETL：开发人员仅仅需要如同批处理作业一样，开发一样的数据转换操作，然后spark就可以自动完成针对存储系统的操作，并且保证数据的一次且仅一次的强一致性语义。
3、为一个批处理作业开发一个实时版本：spark可以保证实时处理作业与批处理作业的结果一定是一致的。
4、在线机器学习：机器学习的api将会同时支持实时训练、定期批量训练、以及实时预测服务。




## Structured Streaming

Spark 2.0中，引入的structured streaming，就是为了实现上述所说的continuous application，也就是持续计算的。首先，structured streaming是一种比spark更高阶的api，主要是基于spark的批处理中的高阶api，比如dataset/dataframe。此外，structured streaming也提供很多其他流式计算应用所无法提供的功能：

1、保证与批处理作业的强一致性：开发人员可以通过dataset/dataframe api以开发批处理作业的方式来开发流式处理作业，进而structured streaming可以以增量的方式来运行这些计算操作。在任何时刻，流式处理作业的计算结果，都与处理同一份batch数据的批处理作业的计算结果，是完全一致的。而大多数的流式计算引擎，比如storm、kafka stream、flink等，是无法提供这种保证的。
2、与存储系统进行事务性的整合：structured streaming在设计时就考虑到了，要能够基于存储系统保证数据被处理一次且仅一次，同时能够以事务的方式来操作存储系统，这样的话，对外提供服务的实时数据才能在任何时刻都保持一致性。目前spark 2.0版本的structured streaming，仅仅支持hdfs这一种外部存储，在未来的版本中，会加入更多的外部存储的支持。事务性的更新是流式计算开发人员的一大痛点，其他的流式计算引擎都需要我们手动来实现，而structured streaming希望在内核中自动来实现。
3、与spark的其他部分进行无缝整合：structured steaming在未来将支持基于spark sql和jdbc来对streaming state进行实时查询，同时提供与mllib进行整合。spark 2.0仅仅开始做这些整合的工作，在未来的版本中会逐渐完善这些整合。

除了这些独一无二的特性以外，structured streaming还会提供其他feature来简化流式应用的开发，例如对event time的支持，从而可以自动处理延迟到达的数据，以及对滑动窗口和会话的更多的支持。目前structured streaming还停留在beta阶段，因此官方声明，仅供用户学习、实验和测试。



## Structured Streaming的未来

spark官方对structured streaming未来的计划是非常有野心的：希望spark的所有组件（core、sql、dataset、mllib等）都能够通过structured steaming，以增量的方式来运行，进而支持更丰富的实时计算操作。structured streaming会设计为让其计算结果与批处理计算结果是强一致的。大数据用户的一个非常大的痛点，就是需要一个完全统一的编程接口。例如说，之前用户进行大数据开发时，需要整合使用多种计算引擎，比如mapreduce来进行etl，hive来执行sql查询，giraph来进行图计算，storm来进行实时计算，等等。而spark则可以完全统一这些操作。此外，structured streaming也希望能够完全涵盖一个持续计算应用中的方方面面。



## Structured Streaming与其他流式计算应用的对比


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming.png)




# wordcount入门案例

structured streaming是一种可伸缩的、容错的、基于Spark SQL引擎的流式计算引擎。你可以使用，与针对静态数据的批处理计算操作一样的方式来编写流式计算操作。随着数据不断地到达，Spark SQL引擎会以一种增量的方式来执行这些操作，并且持续更新结算结果。可以使用java、scala等编程语言，以及dataset/dataframe api来编写计算操作，执行数据流的聚合、基于event的滑动窗口、流式数据与离线数据的join等操作。所有这些操作都与Spark SQL使用一套引擎来执行。此外，structured streaming会通过checkpoint和预写日志等机制来实现一次且仅一次的语义。简单来说，对于开发人员来说，根本不用去考虑是流式计算，还是批处理，只要使用同样的方式来编写计算操作即可，structured streaming在底层会自动去实现快速、可伸缩、容错、一次且仅一次语义。

```

import org.apache.spark.sql.SparkSession

object StructuredNetworkWordCount {
  
  def main(args: Array[String]) {
    val spark = SparkSession
        .builder()
        .appName("StructuredNetworkWordCount")  
        .getOrCreate()
        
    import spark.implicits._
    
    val lines = spark.readStream
        .format("socket")
        .option("host", "localhost")  
        .option("port", "9999")  
        .load()
    val words = lines.as[String].flatMap(_.split(" "))  
    val wordCounts = words.groupBy("value").count() 
    
    val query = wordCounts.writeStream
        .outputMode("complete") 
        .format("console")
        .start()
        
    query.awaitTermination()  
  }
  
}

```


测试
```
yum install -y nc

nc -lk 9999

```



# 编程模型

## 编程模型

structured streaming的核心理念，就是将数据流抽象成一张表，而源源不断过来的数据是持续地添加到这个表中的。这就产生了一种全新的流式计算模型，与离线计算模型是很类似的。你可以使用与在一个静态表中执行离线查询相同的方式来编写流式查询。spark会采用一种增量执行的方式来对表中源源不断的数据进行查询。我们可以将输入数据流想象成是一张input table。数据流中每条新到达的数据，都可以想象成是一条添加到表中的新数据。画图讲解。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_1.png)



针对输入数据执行的查询，会产生一张result table。每个trigger interval，比如说1秒钟，添加到input table中的新数据行，都会被增量地执行我们定义的查询操作，产生的结果会更新到结果表中。当结果表被更新的时候，我们可能会希望将结果表中变化的行写入一个外部存储中。画图讲解。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_2.png)


我们可以定义每次结果表中的数据更新时，以何种方式，将哪些数据写入外部存储。我们有多种模式的output：
complete mode，被更新后的整个结果表中的数据，都会被写入外部存储。具体如何写入，是根据不同的外部存储自身来决定的。
append mode，只有最近一次trigger之后，新增加到result table中的数据，会被写入外部存储。只有当我们确定，result table中已有的数据是肯定不会被改变时，才应该使用append mode。
update mode，只有最近一次trigger之后，result table中被更新的数据，包括增加的和修改的，会被写入外部存储中。spark 2.0中还不支持这种mode。这种mode和complete mode不同，没有改变的数据是不会写入外部存储的。

我们可以以上一讲的wordcount例子作为背景来理解，lines dataframe是一个input table，而wordcounts dataframe就是一个result table。当应用启动后，spark会周期性地check socket输入源中是否有新数据到达。如果有新数据到达，那么spark会将之前的计算结果与新到达的数据整合起来，以增量的方式来运行我们定义的计算操作，进而计算出最新的单词计数结果。


这种模型跟其他很多流式计算引擎都不同。大多数流式计算引擎都需要开发人员自己来维护新数据与历史数据的整合并进行聚合操作。然后我们就需要自己去考虑和实现容错机制、数据一致性的语义等。然而在structured streaming的这种模式下，spark会负责将新到达的数据与历史数据进行整合，并完成正确的计算操作，同时更新result table，不需要我们去考虑这些事情。画图讲解。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_3.png)



## event-time和late-data process

event-time指的是嵌入在数据自身内部的一个时间。在很多流式计算应用中，我们可能都需要根据event-time来进行处理。例如，可能我们需要获取某个设备每分钟产生的事件的数量，那么我们就需要使用事件产生时的时间，而不是spark接受到这条数据的时间。设备产生的每个事件都是input table中的一行数据，而event-time就是这行数据的一个字段。这就可以支持我们进行基于时间窗口的聚合操作（例如每分钟的事件数量），只要针对input table中的event-time字段进行分组和聚合即可。每个时间窗口就是一个分组，而每一行都可以落入不同行的分组内。因此，类似这样的基于时间窗口的分组聚合操作，既可以被定义在一份静态数据上，也可以被定义在一个实时数据流上。

此外，这种模型也天然支持延迟到达的数据，late-data。spark会负责更新result table，因此它有决定的控制权来针对延迟到达的数据进行聚合结果的重新计算。虽然目前在spark 2.0中还没有实现这个feature，但是未来会基于event-time watermark（水印）来实现这个late-data processing的feature。

## 容错语义


structured streaming的核心设计理念和目标之一，就是支持一次且仅一次的语义。为了实现这个目标，structured streaming设计将source、sink和execution engine来追踪计算处理的进度，这样就可以在任何一个步骤出现失败时自动重试。每个streaming source都被设计成支持offset，进而可以让spark来追踪读取的位置。spark基于checkpoint和wal来持久化保存每个trigger interval内处理的offset的范围。sink被设计成可以支持在多次计算处理时保持幂等性，就是说，用同样的一批数据，无论多少次去更新sink，都会保持一致和相同的状态。这样的话，综合利用基于offset的source，基于checkpoint和wal的execution engine，以及基于幂等性的sink，可以支持完整的一次且仅一次的语义。

总结:
1.需要跟历史数据整合起来进行聚合的操作,启动完成
2.基于event-time自定进行延迟到达数据的聚合结果的纠正计算
3.自动完成一次且一次的语义


# 创建流式的dataset和dataframe

流式dataframe可以通过DataStreamReader接口来创建，DataStreamReader对象是通过SparkSession的readStream()方法返回的。与创建静态dataframe的read()方法类似，我们可以指定数据源的一些配置信息，比如data format、schema、option等。spark 2.0中初步提供了一些内置的source支持。

* file source：以数据流的方式读取一个目录中的文件。支持text、csv、json、parquet等文件类型。文件必须是被移动到目录中的，比如用mv命令。
* socket source：从socket连接中读取文本内容。driver是负责监听请求的server socket。socket source只能被用来进行测试。


```
val socketDF = spark
    .readStream
    .format("socket")
    .option("host", "localhost")
    .option("port", 9999)
    .load()

socketDF.isStreaming    
socketDF.printSchema 

val userSchema = new StructType().add("name", "string").add("age", "integer")
val csvDF = spark
    .readStream
    .option("sep", ";")
    .schema(userSchema)      
    .csv("/path/to/directory")    

```

上面的例子都是产生untyped类型的dataframe，这就意味着在编译时是无法检查其schema的，只有在计算被提交并运行时才会进行检查。一些操作，比如map、flatMap等，需要在编译时就知道具体的类型。为了使用一些typed类型的操作，我们可以将dataframe转换为typed类型的dataset，比如df.as[String]。


# 对流式的dataset和dataframe执行计算操作

## 基础操作：选择、映射、聚合

```
我们可以对流式dataset/dataframe执行所有类型的操作，包括untyped操作，SQL类操作，typed操作。

case class DeviceData(device: String, type: String, signal: Double, time: DateTime)

val df: DataFrame = ... // streaming DataFrame with IOT device data with schema { device: string, type: string, signal: double, time: string }
val ds: Dataset[DeviceData] = df.as[DeviceData]    // streaming Dataset with IOT device data

// Select the devices which have signal more than 10
df.select("device").where("signal > 10")      // using untyped APIs   
ds.filter(_.signal > 10).map(_.device)         // using typed APIs

// Running count of the number of updates for each device type
df.groupBy("type").count()                          // using untyped API

// Running average signal for each device type
Import org.apache.spark.sql.expressions.scalalang.typed._
ds.groupByKey(_.type).agg(typed.avg(_.signal))    // using typed API


```

## 滑动窗口：基于event-time
```
import spark.implicits._

val words = ... // streaming DataFrame of schema { timestamp: Timestamp, word: String }

// Group the data by window and word and compute the count of each group
val windowedCounts = words.groupBy(
	//使用timestamp作为event-time,每隔5分钟去统计最近10分钟内的滑动窗口
  window($"timestamp", "10 minutes", "5 minutes"),
  $"word"
).count()


```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_4.png)

如果有延迟到达的数据,如下有一条延迟10min到达的数据

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_5.png)


## join操作

```
structured streaming，支持将一个流式dataset与一个静态dataset进行join。

val staticDf = spark.read. ...
val streamingDf = spark.readStream. ... 

streamingDf.join(staticDf, “type”)          // inner equi-join with a static DF
streamingDf.join(staticDf, “type”, “right_join”)  // right outer join with a static DF

```


## 不支持的操作

```
//不允许对一个dataframe连续进行聚合操作
streaming dataframe的chain aggregation

//不允许尽心limit和take操作
limit and take

//不允许尽心distinct
distinct

//sort仅在聚合过后，同时使用complete output mode时可用
sort

streaming dataframe和static dataframe的outer join
	full outer join是不支持的
	streaming dataframe在左侧时，left outer join是不支持的
	streaming dataframe在右侧时，right outer join是不支持的

两个streaming dataframe的join是不支持的

//不能对一个dataframe直接进行count,要先进行分组之后在进行count
count() -> groupBy().count()

//不能对一个dataframe直接进行foreach,要用df.writeStream.foreach()
foreach() -> df.writeStream.foreach()

//不能对一个dataframe直接进行show,要用 console output sink
show() -> console output sink

```

# output mode、sink以及foreach sink详解


## output操作

定义好了各种计算操作之后，就需要启动这个应用。此时就需要使用DataStreamWriter，通过spark.writeStream()方法返回。此时需要指定以下一些信息：

* output sink的一些细节：数据格式、位置等。
* output mode：以哪种方式将result table的数据写入sink。
* query name：指定查询的标识。
* trigger interval：如果不指定，那么默认就会尽可能快速地处理数据，只要之前的数据处理完，就会立即处理下一条数据。如果上一个数据还没处理完，而这一个trigger也错过了，那么会一起放入下一个trigger再处理。
* checkpoint地址：对于某些sink，可以做到一次且仅一次的语义，此时需要指定一个目录，进而可以将一些元信息写入其中。一般会是类似hdfs上的容错目录。



##　output mode

目前仅仅支持两种output mode
append mode：仅适用于不包含聚合操作的查询。
complete mode：仅适用于包含聚合操作的查询。


## output sink

目前有一些内置支持的sink

* file sink：在spark 2.0中，仅仅支持parquet文件，以及append模式
* foreach sink
* console sink：仅供调试
* memory sink：仅供调试



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/structed_streaming_6.png)

```
val noAggDF = deviceDataDf.select("device").where("signal > 10")   

noAggDF
   .writeStream
   .format("console")
   .start()

noAggDF
   .writeStream
   .parquet("path/to/destination/directory")
   .start()
   
val aggDF = df.groupBy(“device”).count()

aggDF
   .writeStream
   .outputMode("complete")
   .format("console")
   .start()

aggDF
   .writeStream
   .queryName("aggregates")    // this query name will be the table name
   .outputMode("complete")
   .format("memory")
   .start()

spark.sql("select * from aggregates").show()   // interactively query in-memory table


```

## foreach sink详解
```
使用foreach sink时，我们需要自定义ForeachWriter，并且自定义处理每条数据的业务逻辑。每次trigger发生后，根据output mode需要写入sink的数据，就会传递给ForeachWriter来进行处理。使用如下方式来定义ForeachWriter：

datasetOfString.write.foreach(new ForeachWriter[String] {
  def open(partitionId: Long, version: Long): Boolean = {
    // open connection
  }
  def process(record: String) = {
    // write string to connection
  }
  def close(errorOrNull: Throwable): Unit = {
    // close the connection
  }
})

```

需要有如下一些注意点：

* ForeachWriter必须支持序列化，因为该对象会被序列化后发送到executor上去执行。
* open、process和close这三个方法都会给executor调用。
* ForeachWriter所有的初始化方法，必须创建数据库连接，开启一个事务，打开一个IO流等等，都必须在open方法中完成。必须注意，如果在ForeachWriter的构造函数中进行初始化，那么这些操作都是在driver上发生的。
* open中有两个参数，version和partition，可以唯一标识一批需要处理的数据。每次发生一次trigger，version就会自增长一次。partition是要处理的结果数据的分区号。因为output操作是分布式执行的，会分布在多个executor上并行执行。
* open可以使用version和partition来决定，是否要处理这一批数据。此时可以选择返回true或false。如果返回false，那么process不会被调用。举个例子来说，有些partition的数据可能已经被持久化了，而另外一些partiton的处理操作由于失败被重试，此时之前已经被持久化的数据可以不再次进行持久化，避免重复计算。
* close方法中，需要处理一些异常，以及一些资源的释放。




# 管理streaming query
```
val query = df.writeStream.format("console").start()   // get the query object
query.id          // get the unique identifier of the running query
query.name        // get the name of the auto-generated or user-specified name
query.explain()   // print detailed explanations of the query
query.stop()      // stop the query 
query.awaitTermination()   // block until query is terminated, with stop() or with error
query.exception()    // the exception if the query has been terminated with error
query.sourceStatus()  // progress information about data has been read from the input sources
query.sinkStatus()   // progress information about data written to the output sink


val spark: SparkSession = ...
spark.streams.active    // get the list of currently active streaming queries
spark.streams.get(id)   // get a query object by its unique id
spark.streams.awaitAnyTermination()   // block until any one of them terminates


```


## 基于checkpoint的容错机制

如果实时计算作业遇到了某个错误挂掉了，那么我们可以配置容错机制让它自动重启，同时继续之前的进度运行下去。这是通过checkpoint和wal机制完成的。可以给query配置一个checkpoint location，接着query会将所有的元信息（比如每个trigger消费的offset范围、至今为止的聚合结果数据），写入checkpoint目录。

```
aggDF
   .writeStream
   .outputMode("complete")
   .option(“checkpointLocation”, “path/to/HDFS/dir”)
   .format("memory")
   .start()


```


