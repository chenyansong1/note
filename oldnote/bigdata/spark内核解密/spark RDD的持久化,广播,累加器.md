---
title: spark RDD的持久化,广播,累加器
categories: spark   
toc: true  
tag: [spark]
---


# 常见的Action
凡是Action级别的操作都会触发:sc.runJob

reduce
```
val numbers = sc.parallelize(1 to 100)
numbers.reduce(_+_)	//会将上一次计算的结果作为下一次的第一个参数


def reduce(f: (T, T) => T): T = withScope {
	//....
	sc.runJob(this, reducePartition, mergeResult)
	jobResult.getOrElse(throw new UnsupportedOperationException("empty collection"))
}



```


collect

```

numbers.collect		

def collect(): Array[T] = withScope {
	val results = sc.runJob(this, (iter: Iterator[T]) => iter.toArray)
	Array.concat(results: _*)
}
```

![](/assert/img/bigdata/spark内核解密/collect工作机制.png)



count

```
val numbers = sc.parallelize(1 to 100)
numbers.count	

/*
//Return the number of elements in the RDD.
def count(): Long = sc.runJob(this, Utils.getIteratorSize _).sum
*/
```


take:取结果的一批元素
```
val numbers = sc.parallelize(1 to 10)
val arr = numbers.map(_*2).take(5)
arr.foreach(println)
/*打印结果:
2
4
6
8
10
 */

源码:
  def take(num: Int): Array[T] = withScope {
    if (num == 0) {
      new Array[T](0)
    } else {
		//......
        val res = sc.runJob(this, (it: Iterator[T]) => it.take(left).toArray, p)

        res.foreach(buf ++= _.take(num - buf.size))
        partsScanned += numPartsToTry
      }
      buf.toArray
    }
  }

```


countByKey:统计Tuple中key的次数
```
val numbers = sc.parallelize(Seq(1,2,3,5,1,3,5))
val rdd = numbers.map((_,1)).countByKey()
rdd.foreach(println)

/*打印结果:
(1,2)
(3,2)
(5,2)
(2,1)

*/

//源码:
  def countByKey(): Map[K, Long] = self.withScope {
    self.mapValues(_ => 1L).reduceByKey(_ + _).collect().toMap
  }

因为因countByKey也是进行了collect,而collect是Action,所以countByKey也是Action

```


saveAsTextFile
```
在源码中有这样的一句:self.context.runJob(self, writeToFile)

```

# RDD持久化
1.某步骤计算特别耗时
2.计算链条特别长的情况
3.checkpoint所在的RDD也一定要持久化数据(checkpoint之前persist或cache)
4.Shuffle之后要进行persist(因为Shuffle要进行网络传输,如果失败,数据丢失,那么又要进行网络传输)
5.Shuffle之前(框架默认帮我们把数据持久化到磁盘)

```

/** Persist this RDD with the default storage level (`MEMORY_ONLY`).默认是内存中缓存 */
def persist(): this.type = persist(StorageLevel.MEMORY_ONLY)

/** Persist this RDD with the default storage level (`MEMORY_ONLY`). */
def cache(): this.type = persist()

```


```
几种持久化的方式

object StorageLevel {
 	 //不持久化
  val NONE = new StorageLevel(false, false, false, false)

	//持久化到磁盘2分
  val DISK_ONLY_2 = new StorageLevel(true, false, false, false, 2)
  val DISK_ONLY = new StorageLevel(true, false, false, false)


	//只是缓存到内存
  val MEMORY_ONLY = new StorageLevel(false, true, false, true)
  val MEMORY_ONLY_2 = new StorageLevel(false, true, false, true, 2)

	//缓存到内存并序列化,这样存储就会小,但是反序列化的时候,耗CPU
  val MEMORY_ONLY_SER = new StorageLevel(false, true, false, false)
  val MEMORY_ONLY_SER_2 = new StorageLevel(false, true, false, false, 2)
	
	//优先考虑内存,然后再写入到磁盘,这样防止内存溢出
  val MEMORY_AND_DISK = new StorageLevel(true, true, false, true)
  val MEMORY_AND_DISK_2 = new StorageLevel(true, true, false, true, 2)

  val MEMORY_AND_DISK_SER = new StorageLevel(true, true, false, false)
  val MEMORY_AND_DISK_SER_2 = new StorageLevel(true, true, false, false, 2)
  val OFF_HEAP = new StorageLevel(false, false, true, false)

}


//上述情况可以看到,有些情况下有2分副本,为什么要有两份?
因为如果一份内存崩溃掉了,那么另外一份可以立即顶上,虽然2分副本占用了空间,但这就是使用空间(2份副本)换时间
```


cache:是persist的一种特殊情况

```
cache实际上是调用的是下面的persist:即只是缓存在内存中,并且只是缓存一份
def persist(): this.type = persist(StorageLevel.MEMORY_ONLY)

```

执行的效果说明:
```
val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\test.txt")
val rdd2 = rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).cache
rdd2.collect.foreach(println)

下面是连续2次执行上述代码所用的时间:
//第一次
17/03/24 14:41:55 INFO DAGScheduler: Job 0 finished: collect at MakeActionRDD.scala:30, took 1.117844 s

//第二次
17/03/24 14:42:46 INFO DAGScheduler: Job 0 finished: collect at MakeActionRDD.scala:30, took 0.600663 s



cache之后一定不能立即有其他算子
val rdd2 = rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).cache.count
是没有对计算结果缓存

```

cache从内存中清除
1.使用unpersist,强制从内存中去掉



# Spark广播

广播的应用场景:
1.大变量: 每次task在执行任务的时候都要拷贝数据副本,因为函数式编程,即变量不变val,因为要将变量拷贝一份到task中,一个Executor保存一份,Executor中所有的task只读共享这个大变量  
广播是由Driver发给当前Application分配的所有Executor内存级别的全局只读变量,Executor中的线程池中共享该全局变量,极大的减少了网络传输,否则的话每个task都要传输一次该变量,并极大的节省了内存,当也隐式的提高了CPU的有效工作

图示广播:

![](/assert/img/bigdata/spark内核解密/广播.png)


代码:

```
val number = 10
val broadcastNumber = sc.broadcast(number)

val dataRdd = sc.parallelize(1 to 100)
val dataMap = dataRdd.map(_ * broadcastNumber.value)

dataMap.collect.foreach(println)

```


# Spark累加器

Accumulator:对于Executor只能修改,但不可读,只对Driver可读,在记录集群的状态,尤其是全局唯一的状态的时候很重要,即一个作业中的所有的Executor共享

```
val sum = sc.accumulator(0)
//sc.accumulator(0,"test_Acc") 指定Acc的名称

val dataRdd = sc.parallelize(1 to 100)
dataRdd.foreach(sum += _)
println(sum)


```





