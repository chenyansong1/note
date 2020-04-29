---
title: checkpoint
categories: spark  
tags: [spark]
---



# 1.为什么要有checkpoint?
因为在spark进行计算的时候,会有很多的中间结果,但是一旦中间某一步失败,那么又要重新从头开始计算,但是如果我们将中间的某一个计算的结果checkpoint下来,那么下次计算的时候,直接从checkpoint的点拿数据,那么将会大大提高计算的速度

<!--more-->

# 2.checkpoint的源码说明
```

/**
 * Mark this RDD for checkpointing. It will be saved to a file inside the checkpoint
 * directory set with `SparkContext#setCheckpointDir` and all references to its parent
 * RDDs will be removed. This function must be called before any job has been
 * executed on this RDD. It is strongly recommended that this RDD is persisted in
 * memory, otherwise saving it on a file will require recomputation.
直到一个action被调用,那么checkpoint才会被执行
如果做了checkpoint,那么checkpoint之前的所有的lineage(rdd之间的依赖关系)将被移除
强烈建议将checkpoint的rdd保存到内存中(cache),不然在进行checkpoint的时候,又要重新进行计算
 */
def checkpoint(): Unit = RDDCheckpointData.synchronized {
  // NOTE: we use a global lock here due to complexities downstream with ensuring
  // children RDD partitions point to the correct parent partitions. In the future
  // we should revisit this consideration.
  if (context.checkpointDir.isEmpty) {
    throw new SparkException("Checkpoint directory has not been set in the SparkContext")
  } else if (checkpointData.isEmpty) {
    checkpointData = Some(new ReliableRDDCheckpointData(this))
  }
}

```

# 实例代码
&emsp;在进行checkpoint的时候,会进行重新的计算,然后将checkpoint的结果放到磁盘,但是如果我们在checkpoint之前就进行一次cache,那么checkpoint的时候需要的计算结果就直接从内存中拿到,然后在将数据保存到磁盘

```
package spark.examples.rddapi  
  
import org.apache.spark.{SparkContext, SparkConf}  
  
object CheckpointTest {  
  def main(args: Array[String]) {  
    val conf = new SparkConf().setMaster("local").setAppName("AggregateTest_00")  
    val sc = new SparkContext(conf);  
    val z = sc.parallelize(List(3, 6, 7, 9, 11)).cache()        //将数据cache,然后在checkpoint的时候,直接从cache中的拿数据,不用从头开始计算   
    sc.setCheckpointDir("file:///d:/checkpoint")  //这里指定的是本地文件系统,不建议使用,因为如果本地机器宕机了,其他机器将无法拿到数据进行恢复,实际的生产过程中使用的是hdfs
    z.checkpoint()  
    println("length: " + z.collect().length) //rdd存入目录  
    println("count: " + z.count()) //5  
  }  
}  
```
# checkpoint的文件保存路径
```
d:\checkpoint>tree /f  
文件夹 PATH 列表  
卷序列号为 EA23-0890  
D:.  
└─9b0ca0d9-f7fb-46bb-84dc-097d95b9e7b8  
    └─rdd-0  
            .part-00000.crc  
            part-00000 
 
 
 
/*
1. 运行过程中发现，checkpoint目录会自动创建，无需预创建
2.程序运行结束后，checkpoint目录并没有删除，上面这些属于checkpoint目录下的目录和文件也没有删除，再次运行会产生新的目录
*/
```



