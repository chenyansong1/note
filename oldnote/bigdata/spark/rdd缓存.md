---
title: rdd缓存
categories: spark  
tags: [spark]
---

# 1.Spark RDD缓存源码分析
我们知道,spark相比Hadoop最大的一个优势就是可以将数据cache到内存,以供后面的计算使用,我们可以通过rdd.persist()或rdd.cache()来缓存RDD中的数据
cache()其实就是调用的persist()实现的,persist()支持下面的几种存储级别:
```
val NONE = new StorageLevel(false, false, false, false)
val DISK_ONLY = new StorageLevel(true, false, false, false)
val DISK_ONLY_2 = new StorageLevel(true, false, false, false, 2)
val MEMORY_ONLY = new StorageLevel(false, true, false, true)
val MEMORY_ONLY_2 = new StorageLevel(false, true, false, true, 2)
val MEMORY_ONLY_SER = new StorageLevel(false, true, false, false)
val MEMORY_ONLY_SER_2 = new StorageLevel(false, true, false, false, 2)
val MEMORY_AND_DISK = new StorageLevel(true, true, false, true)
val MEMORY_AND_DISK_2 = new StorageLevel(true, true, false, true, 2)
val MEMORY_AND_DISK_SER = new StorageLevel(true, true, false, false)
val MEMORY_AND_DISK_SER_2 = new StorageLevel(true, true, false, false, 2)
val OFF_HEAP = new StorageLevel(false, false, true, false)

```

&emsp;而cache()最终调用的是persist(StorageLevel.MEMORY_ONLY)，也就是默认的缓存级别。我们可以根据自己的需要去设置不同的缓存级别，这里各种缓存级别的含义我就不介绍了，可以参见官方文档说明。 
通过调用rdd.persist()来缓存RDD中的数据，其最终调用的都是下面的代码：

```
private def persist(newLevel: StorageLevel, allowOverride: Boolean): this.type = {
  // TODO: Handle changes of StorageLevel
  if (storageLevel != StorageLevel.NONE && newLevel != storageLevel && !allowOverride) {
    throw new UnsupportedOperationException(
      "Cannot change storage level of an RDD after it was already assigned a level")
  }
  // If this is the first time this RDD is marked for persisting, register it
  if (storageLevel == StorageLevel.NONE) {
    sc.cleaner.foreach(_.registerRDDForCleanup(this))
    sc.persistRDD(this)
  }
  storageLevel = newLevel
  this
}

```
&emsp; 这段代码的最主要作用其实就是将storageLevel设置为persist()函数传进来的存储级别，而且一旦设置好RDD的存储级别之后就不能再对相同RDD设置别的存储级别，否则将会出现异常。<font color=red>设置好存储级别在之后除非触发了action操作，否则不会真正地执行缓存操作</font>。当我们触发了action，它会调用sc.runJob方法来真正的计算，而这个方法最终会调用org.apache.spark.scheduler.Task#run，而这个方法最后会调用ResultTask或者ShuffleMapTask的runTask方法，runTask方法最后会调用org.apache.spark.rdd.RDD#iterator方法，iterator的代码如下：
```
final def iterator(split: Partition, context: TaskContext): Iterator[T] = {
  if (storageLevel != StorageLevel.NONE) {
    SparkEnv.get.cacheManager.getOrCompute(this, split, context, storageLevel)
  } else {
    computeOrReadCheckpoint(split, context)
  }
}
```
&emsp;如果当前RDD设置了存储级别（也就是通过上面的rdd.persist()设置的），那么会从cacheManager中判断是否有缓存数据。如果有，则直接获取，如果没有则计算。getOrCompute的代码如下：

```
def getOrCompute[T](
    rdd: RDD[T],
    partition: Partition,
    context: TaskContext,
    storageLevel: StorageLevel): Iterator[T] = {
 
  val key = RDDBlockId(rdd.id, partition.index)
  logDebug(s"Looking for partition $key")
  blockManager.get(key) match {
    case Some(blockResult) =>
      // Partition is already materialized, so just return its values
      val existingMetrics = context.taskMetrics
        .getInputMetricsForReadMethod(blockResult.readMethod)
      existingMetrics.incBytesRead(blockResult.bytes)
 
      val iter = blockResult.data.asInstanceOf[Iterator[T]]
      new InterruptibleIterator[T](context, iter) {
        override def next(): T = {
          existingMetrics.incRecordsRead(1)
          delegate.next()
        }
      }
    case None =>
      // Acquire a lock for loading this partition
      // If another thread already holds the lock, wait for it to finish return its results
      val storedValues = acquireLockForPartition[T](key)
      if (storedValues.isDefined) {
        return new InterruptibleIterator[T](context, storedValues.get)
      }
 
      // Otherwise, we have to load the partition ourselves
      try {
        logInfo(s"Partition $key not found, computing it")
        val computedValues = rdd.computeOrReadCheckpoint(partition, context)
 
        // If the task is running locally, do not persist the result
        if (context.isRunningLocally) {
          return computedValues
        }
 
        // Otherwise, cache the values and keep track of any updates in block statuses
        val updatedBlocks = new ArrayBuffer[(BlockId, BlockStatus)]
        val cachedValues = putInBlockManager(key, computedValues, storageLevel, updatedBlocks)
        val metrics = context.taskMetrics
        val lastUpdatedBlocks = metrics.updatedBlocks.getOrElse(Seq[(BlockId, BlockStatus)]())
        metrics.updatedBlocks = Some(lastUpdatedBlocks ++ updatedBlocks.toSeq)
        new InterruptibleIterator(context, cachedValues)
 
      } finally {
        loading.synchronized {
          loading.remove(key)
          loading.notifyAll()
        }
      }
  }
}
```
&emsp;首先通过RDD的ID和当前计算的分区ID构成一个key，并向blockManager中查找是否存在相关的block信息。如果能够获取得到，说明当前分区已经被缓存了；否者需要重新计算。如果重新计算，我们需要获取到相关的锁，因为可能有多个线程对请求同一分区的数据。如果获取到相关的锁，则会调用rdd.computeOrReadCheckpoint(partition, context)计算当前分区的数据，并放计算完的数据放到BlockManager中，如果有相关的线程等待该分区的计算，那么在计算完数据之后还得通知它们（loading.notifyAll()）。
 
&emsp;如果获取锁失败，则说明已经有其他线程在计算该分区中的数据了，那么我们就得等（loading.wait()），获取锁的代码如下：
```
private def acquireLockForPartition[T](id: RDDBlockId): Option[Iterator[T]] = {
  loading.synchronized {
    if (!loading.contains(id)) {
      // If the partition is free, acquire its lock to compute its value
      loading.add(id)
      None
    } else {
      // Otherwise, wait for another thread to finish and return its result
      logInfo(s"Another thread is loading $id, waiting for it to finish...")
      while (loading.contains(id)) {
        try {
          loading.wait()
        } catch {
          case e: Exception =>
            logWarning(s"Exception while waiting for another thread to load $id", e)
        }
      }
      logInfo(s"Finished waiting for $id")
      val values = blockManager.get(id)
      if (!values.isDefined) {
        /* The block is not guaranteed to exist even after the other thread has finished.
         * For instance, the block could be evicted after it was put, but before our get.
         * In this case, we still need to load the partition ourselves. */
        logInfo(s"Whoever was loading $id failed; we'll try it ourselves")
        loading.add(id)
      }
      values.map(_.data.asInstanceOf[Iterator[T]])
    }
  }
}

```
&emsp;等待的线程（也就是没有获取到锁的线程）是通过获取到锁的线程调用loading.notifyAll()唤醒的，唤醒之后之后调用new InterruptibleIterator[T](context, storedValues.get)获取已经缓存的数据。以后后续RDD需要这个RDD的数据我们就可以直接在缓存中获取了，而不需要再计算了。后面我会对checkpoint相关代码进行分析。


# 2.示例代码

```


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("UrlCountPartition").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //rdd1将数据切分，元组中放的是（URL， 1）
    val rdd1 = sc.textFile("c://itcast.log").map(line => {
      val f = line.split("\t")
      (f(1), 1)
    })
    val rdd2 = rdd1.reduceByKey(_ + _)

   '步骤1'
    val rdd3 = rdd2.map(t => {
      val url = t._1
      val host = new URL(url).getHost
      (host, (url, t._2))
    }).cache()//cache会将数据缓存到内存当中，cache是一个Transformation，lazy

   '步骤2'
    val ints = rdd3.map(_._1).distinct().collect()

    val hostParitioner = new HostParitioner(ints)

   '步骤3'
    val rdd4 = rdd3.partitionBy(hostParitioner).mapPartitions(it => {
      it.toList.sortBy(_._2._2).reverse.take(2).iterator
    })

    rdd4.saveAsTextFile("c://out4")


    //println(rdd4.collect().toBuffer)
    sc.stop()

  }

/*
因为transaction是延迟加载,transaction只是保存了所有动作的执行轨迹,并没有真正的执行,所以只有当有action的动作的时候,才会有真正的执行的动作,
在步骤1的最后进行了cache()标记,在进行步骤2(action)的时候会将步骤1中的结果进行缓存,所以在进行步骤3操作的时候,会利用步骤1的缓存的结果直接进行计算,但是如果步骤1没有进行cache操作,那么在进行步骤3的时候,会重新计算前面所有的结果

*/

```







