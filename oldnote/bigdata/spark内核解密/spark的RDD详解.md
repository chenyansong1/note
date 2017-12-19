---
title: spark的RDD详解
categories: spark   
toc: true  
tag: [spark]
---




无论是基于工作集还是数据集都有:位置感知,容错,负载均衡的特点
基于数据集的处理:工作方式:从物理存储上加载数据,然后操作数据,然后写入物理存储设备(如:hadoop的mapreduce),	基于数据集的操作不适应的场景:   
1.不适应大量的迭代
2.不适合于交互式查询
3.基于数据流的方式不能够复用中间的计算结果(1.对于相同的查询,需要进行重复的计算,2.对于有相同的查询步骤的计算,其相同步骤的结果不能复用)
4.spark的RDD是基于工作集


<!--more-->


RDD:Resilient Distributed Dataset (弹性分布式数据集)    
弹性之一:自动的进行内存和磁盘数据存储的切换
弹性之二:基于Lineage的高效容错(依赖关系实现)
弹性之三:Task如果失败会自动进行特定次数的重试
弹性之四:Stage如果失败会自动进行特定次数的重试,而且只会计算失败的分片
弹性之五:checkpoint和persist
弹性之六:数据调度弹性,DAG Task和资源管理无关
弹性之七:数据分片的高度弹性,(当我们的Partition比较小的时候,如果每个Partition都占用一个线程去处理,那么会占用很多的资源,此时将多个Partition合并成一个相对较大的Partition,这样来提高效率;另外一个方面,如果内存不是那么多,但是每个Partition比较的大,即数据的block比较的大,这个时候可能考虑把他变成更小的分片,这样让spark有更多的处理批次,但是不会出现OOM),所以降低并行度和提高并行度是高度弹性的一个表现(重新分片的时候,例如将100万的PartitionsShuffle成10万个Partitions,此时不要使用reparation,因为reparation使用了Shuffle=true,可以使用coalesce,coalesce内部默认Shuffle=false)


因为对RDD的操作是只读的,那么会在stage中产生很多的中间结果,那么怎么办,解决的办法是不让其产生中间结果,即使用lazy的模式,只不过产生了一个操作的标记(因为在源码中可以看到一个RDD产生另一个RDD的操作的过程实际上是将父RDD传给了子RDD,如下)
```
val rdd = sc.textFile("xx.txt")
val rdd2 = rdd.flatMap(_.split(" "))

/*
从rdd到rdd2的过程中,是flatMap返回的一个新的rdd,而flatMap源码实现是:
*/
def flatMap[](f:xx){
	//...
	new MapPartitionRDD[T](this,(context,pid,iter)=>iter.flatMap(cleanF))
}
//可以知道只是将this传给了子rdd,也就是将rdd作为this传给了子rdd2,从而确定了两个rdd的关系链,而在一个stage中是lazy的模式,所以spark并不是立即进行计算,即不会在一个stage中产生中间的结果


```

综上:spark在每次产生新的RDD的时候,都是将父RDD的作为this传到子RDD中,所以就构成了一个链条,在最后又action的时候才会触发,如下的例子:
```
z=2;
y=z+4;
x=y+3;
f(x)=x+2;

//现在要计算f(x),那么就会从后向前追溯x的值,在追溯y的值,最后追溯z的值

```




常规的容错方式:
1.数据检查点
2.记录数据的更新(每次数据变化的时候就记录一下,存在的问题:复杂;耗性能)      

spark采用的是数据更新的方式,那么RDD通过记录数据更新的方式为何是高效的呢?
1.RDD是不可变的,并且是lazy
2.RDD的写操作是粗粒度的,但是RDD的读操作既可以是粗粒度的,也可以是细粒度的



# RDD的创建过程
创建RDD的几种方式
1.使用程序中的集合创建RDD:本地测试
2.使用本地文件系统创建RDD:测试大量数据的文件
3.使用HDFS创建RDD:生产环境最常用的创建方式	
4.基于DB创建RDD
5.基于NoSql创建RDD,例如HBase
6.基于S3创建RDD
7.基于数据流创建RDD


集合创建RDD的源码实现过程:
```
val sc = new SparkContext(conf)
val numbers = 1 to 100
val rdd = sc.parallelize(numbers)

val sum = rdd.reduce(_+_)

//parallelize方法
def parallelize[T: ClassTag](seq: Seq[T], numSlices: Int = defaultParallelism): RDD[T] = withScope {
	//..
	new ParallelCollectionRDD[T](this, seq, numSlices, Map[Int, Seq[String]]())
}


//ParallelCollectionRDD 类中有如下的方法:
override def getPartitions: Array[Partition] = {
	val slices = ParallelCollectionRDD.slice(data, numSlices).toArray
	slices.indices.map(i => new ParallelCollectionPartition(id, i, slices(i))).toArray	//因为slice返回的是Seq[Seq[T]],所以toArray之后返回的是Array[Seq[T]],即对数据集进行了分片
}

def slice[T: ClassTag](seq: Seq[T], numSlices: Int): Seq[Seq[T]]

//计算每一个分片
override def compute(s: Partition, context: TaskContext): Iterator[T] = {
	new InterruptibleIterator(context, s.asInstanceOf[ParallelCollectionPartition[T]].iterator)
}



```


```


rdd.collect.foreach(pair=>print(pair))
collect是将结果收集到Driver,然后变成Array,所以可以使用foreach去遍历

def collect(): Array[T] = withScope {
	val results = sc.runJob(this, (iter: Iterator[T]) => iter.toArray)
	Array.concat(results: _*)	//返回的是一个Array
}

在实际生产中,不要使用collect,因为数据量将非常的大
```







RDD执行手动绘图

![](/assert/img/bigdata/spark内核解密/Transformation流程图.png)




# 常见的RDD使用

```

package org.dt.spark

import org.apache.spark.{SparkConf, SparkContext}

object MakeRDD {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    val sc = sparkContext("Transformation Operations")
    //main方法中的调用的每一个功能都必须是模块化的,而每一个调用的模块必须使用函数来封装

    //reduceByKeyTransformation(sc)
    joinTransformation(sc)

    sc.stop()//停止SparkContext,销毁相关的Driver对象,释放资源
  }


  //在实际的生成中,我们是封装函数来进行逻辑的组织
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")

    //创建SparkContext,这是第一个RDD创建的唯一入口,是通往集群的唯一通道
    val sc = new SparkContext(conf)
    sc
  }

  //map
  def mapTransformation(sc:SparkContext){
    val nums = sc.parallelize(1 to 10)

    //map遍历集合的每一个元素x,对每一个元素都作用指定的函数f(x),将f(x)作为集合新的元素返回
    val mapped = nums.map(_*2)
    mapped.collect.foreach(println) //收集计算结果并通过foreach循环打印
  }

  //filter
  def filterTransformation(sc:SparkContext){
    val nums = sc.parallelize(1 to 10)

    //遍历集合的每一个元素x,满足f(x)==true,就留下x,则集合中最终留下的都是满足f(x)==true的元素
    val filtered = nums.filter(_%2==0) // filter的源码实现:   def filter(f: T => Boolean): RDD[T]
    filtered.collect.foreach(println)
  }

  //flatMap
  def flatMapTransformation(sc:SparkContext): Unit ={
    val bigData = Array("scala cccc", "java jjjjj", "spark sss")
    val bigDataRDD = sc.parallelize(bigData) //返回ParallelCollectionRDD[String]

    //遍历集合的每一个元素x,首先返回Array[f(x1)],Array[f(x2)],..然后对所有的Array[f(x)]进行flat,最终返回Array[f(x1),f(x2)...]
    val flatMaped = bigDataRDD.flatMap(_.split(" "))//对每个元素,split切分的结果是一个Array,所以每一个元素都会产生一个Array,最后要对每个Array进行flat,产生一个大的集合
    flatMaped.collect.foreach(println)
  }

  //groupByKey
  def groupByKeyTransformation(sc:SparkContext): Unit ={
    val data = Array((11,"zhangsan"), (22,"zhangsan22"),(22,"zhangsan2222222"), (33,"zhangsan33"), (44,"zhangsan44"))
    val groupByKeyRdd = sc.parallelize(data)

    //将Tuple的第一个元素作为key,然后返回:RDD[(K, Iterable[V])  ----按照相同的key对value进行分组
    val groupByKeyed = groupByKeyRdd.groupByKey() //返回:def groupByKey(): RDD[(K, Iterable[V])]  注意返回的集合中的元素是:(K, Iterable[V])
    groupByKeyed.collect.foreach(println)
    /*
    打印结果:
    (22,CompactBuffer(zhangsan22, zhangsan2222222))
    (11,CompactBuffer(zhangsan))
    (33,CompactBuffer(zhangsan33))
    (44,CompactBuffer(zhangsan44))
     */
  }

  //reduceByKey
  def reduceByKeyTransformation(sc: SparkContext): Unit ={
    val data = Array((11,"zhangsan"), (22,"zhangsan22"),(22,"zhangsan2222222"), (33,"zhangsan33"), (44,"zhangsan44"))
    val reduceByKeyRdd = sc.parallelize(data)

    //例如集合中的元素是:(Int,String),则reduceByKey之后,集合中的元素还是(Int,String),只不过是对相同key的String进行处理,如下面是将相同key的所有字符串进行concat拼接,返回的还是String
    val reduceByKeyed = reduceByKeyRdd.reduceByKey(_.concat(":").concat(_))//返回:def reduceByKey(func: (V, V) => V): RDD[(K, V)]

    reduceByKeyed.collect.foreach(println)
    /*
    打印结果:
    (22,zhangsan22:zhangsan2222222)
    (11,zhangsan)
    (33,zhangsan33)
    (44,zhangsan44)
    */
  }

  //join:将两个集合中根据key进行内容的连接
  def joinTransformation(sc: SparkContext): Unit ={
    //大数据中最重要的算子
    val studentName = Array((1,"zhangsan"), (2,"wangwu"), (3,"lisi"), (3,"lisi333"), (4,"zhaoliu"))
    val studentScore = Array((1,11), (2,22), (2,222222),(3,33),(7,77))

    val nameRdd = sc.parallelize(studentName)
    val scoreRdd = sc.parallelize(studentScore)

    //def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
    val studentNameScore = nameRdd.join(scoreRdd)
    studentNameScore.collect.foreach(println)
    /*
    打印结果:
      (1,(zhangsan,11))
      (3,(lisi,33))
      (3,(lisi333,33))
      (2,(wangwu,22))
      (2,(wangwu,222222))
     */
  }

  //cogroup
  def cogroupTransformation(sc: SparkContext): Unit ={
    val studentName = Array((1,"zhangsan"), (1,"zhangsan1111"), (2,"wangwu"), (3,"lisi"), (3,"lisi333"), (4,"zhaoliu44"))
    val studentScore = Array((1,11), (2,22), (3,33), (2,2222), (3,333333),(5,5555))

    val nameRdd = sc.parallelize(studentName)
    val scoreRdd = sc.parallelize(studentScore)

    //def cogroup[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
    //先将两个RDD groupByKey之后再join
    val studentNameScore = nameRdd.cogroup(scoreRdd)  //返回:RDD[(K, (Iterable[V], Iterable[W]))
    studentNameScore.collect.foreach(println)
    /*
    打印结果:
      (4,(CompactBuffer(zhaoliu44),CompactBuffer())) <-----------这里有null的存在
      (1,(CompactBuffer(zhangsan, zhangsan1111),CompactBuffer(11)))
      (3,(CompactBuffer(lisi, lisi333),CompactBuffer(33, 333333)))
      (5,(CompactBuffer(),CompactBuffer(5555)))   <-----------这里有null的存在
      (2,(CompactBuffer(wangwu),CompactBuffer(22, 2222)))
     */
  }
}


```














