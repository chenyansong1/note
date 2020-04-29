---
title: spark性能优化之四十之数据倾斜解决方案之sample采样倾斜key单独进行join
categories: spark  
tags: [spark]
---


方案的思路图解

<!--more-->

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_data_skew_sample_key.png)



这个方案的实现思路，跟大家解析一下：其实关键之处在于，将发生数据倾斜的key，单独拉出来，放到一个RDD中去；就用这个原本会倾斜的key RDD跟其他RDD，单独去join一下，这个时候，key对应的数据，可能就会分散到多个task中去进行join操作。

就不至于说是，这个key跟之前其他的key混合在一个RDD中时，肯定是会导致一个key对应的所有数据，都到一个task中去，就会导致数据倾斜。如下图:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_data_skew_sample_key2.png)


这种方案什么时候适合使用？

优先对于join，肯定是希望能够采用上一讲讲的，reduce join转换map join。两个RDD数据都比较大，那么就不要那么搞了。

针对你的RDD的数据，你可以自己把它转换成一个中间表，或者是直接用countByKey()的方式，你可以看一下这个RDD各个key对应的数据量；此时如果你发现整个RDD就一个，或者少数几个key，是对应的数据量特别多；尽量建议，比如就是一个key对应的数据量特别多。

此时可以采用咱们的这种方案，单拉出来那个最多的key；单独进行join，尽可能地将key分散到各个task上去进行join操作。

什么时候不适用呢？

如果一个RDD中，导致数据倾斜的key，特别多；那么此时，最好还是不要这样了；还是使用我们最后一个方案，终极的join数据倾斜的解决方案。



```
//假设rdd.join(rdd2)会产生数据倾斜

//假设rdd的数据格式为("hell",1)这样的格式
//采样10%的数据
val sampleRDD = rdd.sample(false,0.1,9)
//对采样数据进行reduceByKey
val reduceByKeyRdd = sampleRDD.map((_,1)).reduceByKey(_+_)

//反转Tuple,对key进行排序
val reversedSampleRdd = reduceByKeyRdd.map(tup=>(tup._2, tup._1))
val wkewedKeyList = reversedSampleRdd.sortByKey(false).take(1)

//take返回的是一个list,这里是去取list中Tuple,然后取Tuple的key,这样就拿到了导致倾斜的key
val keyStr = wkewedKeyList.toList.get(0)._2

//------------上面的过程是去拿到导致数据倾斜的key----------------

//拿到产生数据倾斜key对应的RDD
val skewedRdd = rdd.filter{
	tuple=>
		tuple._1.equals(keyStr)
}


//拿到非数据倾斜的key对应的RDD
val noSkewedRdd = rdd.filter{
	tuple=>
		!tuple._1.equals(keyStr)
}


//------上面的过程是去拿到产生数据倾斜key对应的RDD 和非数据倾斜的key对应的RDD-----


//对上面产生的RDD分别去进行join
val joinedRdd1 = skewedRdd.join(rdd2)
val joinedRdd2 = noSkewedRdd.join(rdd2)
val joinedRdd = joinedRdd1.union(joinedRdd2)

```


在上面的基础上我们可以进行更进一步的优化:


就是说，咱们单拉出来了，一个或者少数几个可能会产生数据倾斜的key，然后还可以进行更加优化的一个操作；

对于那个key，从另外一个要join的表中，也过滤出来一份数据，比如可能就只有一条数据。userid2infoRDD，一个userid key，就对应一条数据。

然后呢，采取对那个只有一条数据的RDD，进行flatMap操作，打上100个随机数，作为前缀，返回100条数据。

单独拉出来的可能产生数据倾斜的RDD，给每一条数据，都打上一个100以内的随机数，作为前缀。

再去进行join，是不是性能就更好了。肯定可以将数据进行打散，去进行join。join完以后，可以执行map操作，去将之前打上的随机数，给去掉，然后再和另外一个普通RDD join以后的结果，进行union操作。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_data_skew_sample_key3.png)



```
//假设rdd.join(rdd2)会产生数据倾斜

//假设rdd的数据格式为("hell",1)这样的格式
//采样10%的数据
val sampleRDD = rdd.sample(false,0.1,9)
//对采样数据进行reduceByKey
val reduceByKeyRdd = sampleRDD.map((_,1)).reduceByKey(_+_)

//反转Tuple,对key进行排序
val reversedSampleRdd = reduceByKeyRdd.map(tup=>(tup._2, tup._1))
val wkewedKeyList = reversedSampleRdd.sortByKey(false).take(1)

//take返回的是一个list,这里是去取list中Tuple,然后取Tuple的key,这样就拿到了导致倾斜的key
val keyStr = wkewedKeyList.toList.get(0)._2

//------------上面的过程是去拿到导致数据倾斜的key----------------



//拿到产生数据倾斜key对应的RDD
val skewedRdd = rdd.filter{
	tuple=>
		tuple._1.equals(keyStr)
}


//拿到非数据倾斜的key对应的RDD
val noSkewedRdd = rdd.filter{
	tuple=>
		!tuple._1.equals(keyStr)
}


//------上面的过程是去拿到产生数据倾斜key对应的RDD 和非数据倾斜的key对应的RDD-----


val skewDataInRdd2 = rdd2.filter(_.equals(keyStr)

val skewData100Rdd2 = skewDataInRdd2.flatMap{
	tuple=>
		list = Array()
		for(i<- 1 to 100){
			val prefix = Random.nextInt(100)
			list.add((prefix+"_"+tuple._1,tuple._2))
		}
		
		list
}

//------上面的过程对另外一个需要join的rdd进行过滤,拿到也过滤出来一份数据，比如可能就只有一条数据。然后呢，采取对那个只有一条数据的RDD，进行flatMap操作，打上100个随机数，作为前缀，返回100条数据

val joinedRdd1 = skewedRdd.map{
	tuple=>
	val prefix = Random.nextInt(100)
	(prefix+"_"+tuple._1,tuple._2)
}
//将两个都加上前缀的RDD进行join,因为有了前缀,这样他们就能打散
.join(skewData100Rdd2)
//去掉前缀
.map{
	tuple=>
		val arr = tuple._1.split("_")
		(arr(1),tuple._2)
}


val joinedRdd2 = noSkewedRdd.join(rdd2)
val joinedRdd = joinedRdd1.union(joinedRdd2)


```
