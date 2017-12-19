---
title: RDD-API之aggregateByKey.md
categories: spark  
tags: [spark]
---



# 1. aggregateByKey的原理
```
  /**
   * Aggregate the values of each key, using given combine functions and a neutral "zero value".
   * This function can return a different result type, U, than the type of the values in this RDD,
   * V. Thus, we need one operation for merging a V into a U and one operation for merging two U's,
   * as in scala.TraversableOnce. The former operation is used for merging values within a
   * partition, and the latter is used for merging values between partitions. To avoid memory
   * allocation, both of these functions are allowed to modify and return their first argument
   * instead of creating a new U.
   */
  def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) => U,
      combOp: (U, U) => U): RDD[(K, U)] = {
    // Serialize the zero value to a byte array so that we can get a new clone of it on each key
    val zeroBuffer = SparkEnv.get.serializer.newInstance().serialize(zeroValue)
    val zeroArray = new Array[Byte](zeroBuffer.limit)
    zeroBuffer.get(zeroArray)
 
    lazy val cachedSerializer = SparkEnv.get.serializer.newInstance()
    val createZero = () => cachedSerializer.deserialize[U](ByteBuffer.wrap(zeroArray))
 
    combineByKey[U]((v: V) => seqOp(createZero(), v), seqOp, combOp, partitioner)
  }


/*
从aggregateByKey的源代码中，可以看出
a.aggregateByKey把类型为(K,V)的RDD转换为类型为(K,U)的RDD，V和U的类型可以不一样，这一点跟combineByKey是一样的，即返回的二元组的值类型可以不一样

b.aggregateByKey内部是通过调用combineByKey实现的，combineByKey的createCombiner函数逻辑由zeroValue这个变量实现，zeroValue作为聚合的初始值，通常对于加法聚合则为0，乘法聚合则为1，集合操作则为空集合
c.seqOp在combineByKey中的功能是mergeValues，(U,V)=>U
d.combOp在combineByKey中的功能是mergeCombiners

*/

```


# 2.aggregateByKey举例
```
#求均值

val rdd = sc.textFile("气象数据")  
val rdd2 = rdd.map(x=>x.split(" ")).map(x => (x(0).substring("从年月日中提取年月"),x(1).toInt))  
val zeroValue = (0,0) 
val seqOp= (u:(Int, Int), v:Int) => {  
 (u._1 + v, u._2 + 1)  
}  
  
val compOp= (c1:(Int,Int),c2:(Int,Int))=>{  
  (u1._1 + u2._1, u1._2 + u2._2)  
}  
  
  
val vdd3 = vdd2.aggregateByKey(  
zeroValue ,  
seqOp,  
compOp
)  
  
rdd3.foreach(x=>println(x._1 + ": average tempreture is " + x._2._1/x._2._2) 

/*
从求均值的实现来看，aggregate通过提供零值的方式，避免了combineByKey中的createCombiner步骤(createCombiner本质工作就是遇到第一个key时进行初始化操作，这个初始化不是提供零值，而是对第一个(k,v)进行转换得到c的初始值））
*/

```



