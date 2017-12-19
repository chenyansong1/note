---
title: SparkStreaming之updateStateByKey和WordCount全局统计
categories: spark  
tags: [spark]
---


updateStateByKey操作,可以让我们为每个key维护一份state,并持续不断的更新该state
1.首先,定义个state,可以是任意的数据类型
2.其次,要定义state更新函数---指定一个函数如何使用之前的state和新值来更新state

对于每个batch,spark都会为每个之前已经存在的key去应用一次state更新函数,无论这个key在batch中是否有新的数据,如果state更新函数返回none,那么key对应的state就会被删除

当然,对于每个新初出现的key,也会执行state更新操作

注意,updateStateByKey操作,要求必须开启checkpoint机制

案例:基于缓存的实时WordCount程序
```
  val conf = new SparkConf()
    .setAppName("Streaming")
    .setMaster("local[2]")

  // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
  val ssc = new StreamingContext(conf,Seconds(1))

  // 如果要使用updateStateByKey算子,就必须设置一个checkpoint目录,
  // 这样便于在内存数据丢失的时候,可以从checkpoint中恢复数据
  ssc.checkpoint("hdfs://spark1:9000/wordcount_checkpoint")

  val updateFunc = (iter: Iterator[(String, Seq[Int], Option[Int])]) => {
    //iter.flatMap(it=>Some(it._2.sum + it._3.getOrElse(0)).map(x=>(it._1,x)))
    iter.flatMap { case (x, y, z) => Some(y.sum + z.getOrElse(0)).map(i => (x, i)) }
  }

  val lines = ssc.socketTextStream("localhost",9999)
  val pairs = lines.flatMap(_.split(" ")).map((_,1))
  // 在之前的WordCount中,是直接使用pairs.reduceByKey
  // 得到的是每个时间段的batch对应的RDD,这样计算出来的是那个时间段的单词计数
  // 但是如果我们想要统计每个单词的全局的计数呢?
  // 就是说:统计出来从程序启动开始,到现在为止,统计一个单词出现的次数,那么之前的方式就不好实现了
  // 就必须基于redis缓存,或者mysql来实现累加
  // 但是我们的updateStateByKey就可以维护一份每个单词的全局的统计次数

  /*实际上,对于每个单词,每次batch的时候,都会调用这个函数
  第一个参数values:相当于这个batch中,这个key的新的值,可能有多个
  比如说:(hello,1) (hello,1),那么传入的是(1,1)
  第二个参数state:就是指的是这个key之前的状态,其中的泛型的类型是自己指定的
   */
  val func2 = (values:Seq[Int], state:Option[Int])=>{
    val newValue = state.getOrElse(0)//之前的状态不存在返回0
    Option(newValue + values.sum)//将本次新出现的值求和,然后再和state的值相加,就是这个key目前的全局统计
  }
  val wordCounts = pairs.updateStateByKey(func2)//其实内部就是调用的是下面的一种方式,只不过使用func2的方法更加的简洁
  //val wordCounts = pairs.updateStateByKey(updateFunc,new HashPartitioner(ssc.sparkContext.defaultParallelism), true)
  /*
  默认的情况:
  new HashPartitioner(ssc.sparkContext.defaultParallelism) 是指定分区函数,默认就是使用的是HashPartitioner
  true:Whether to remember the partitioner object in the generated RDDs.
   */
  wordCounts.print

  ssc.start()
  ssc.awaitTermination()
  ssc.stop()

```





