---
title: spark性能优化十六之使用Kryo序列化
categories: spark  
tags: [spark]
---

# Kyro的优点

默认情况下，Spark内部是使用Java的序列化机制，ObjectOutputStream / ObjectInputStream，对象输入输出流机制，来进行序列化

<!--more-->

这种默认序列化机制的好处在于，处理起来比较方便；也不需要我们手动去做什么事情，只是，你在算子里面使用的变量，必须是实现Serializable接口的，可序列化即可。

但是缺点在于，默认的序列化机制的效率不高，序列化的速度比较慢；序列化以后的数据，占用的内存空间相对还是比较大。

可以手动进行序列化格式的优化

Spark支持使用Kryo序列化机制。Kryo序列化机制，比默认的Java序列化机制，速度要快，序列化后的数据要更小，大概是Java序列化机制的1/10。

所以Kryo序列化优化以后，可以让网络传输的数据变少；在集群中耗费的内存资源大大减少。


# 使用Kyro的几个地方

Kryo序列化机制，一旦启用以后，会生效的几个地方：

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_kryo.png)



1、算子函数中使用到的外部变量
2、持久化RDD时进行序列化，StorageLevel.MEMORY_ONLY_SER
3、shuffle

1、算子函数中使用到的外部变量，使用Kryo以后：优化网络传输的性能，可以优化集群中内存的占用和消耗
2、持久化RDD，优化内存的占用和消耗；持久化RDD占用的内存越少，task执行的时候，创建的对象，就不至于频繁的占满内存，频繁发生GC。
3、shuffle：可以优化网络传输的性能


# Kryo的使用

首先第一步，在SparkConf中设置一个属性，spark.serializer，org.apache.spark.serializer.KryoSerializer类；


```
SparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
```


Kryo之所以没有被作为默认的序列化类库的原因，就要出现了：主要是因为Kryo要求，如果要达到它的最佳性能的话，那么就一定要注册你自定义的类（比如，你的算子函数中使用到了外部自定义类型的对象变量，这时，就要求必须注册你的类，否则Kryo达不到最佳性能）。

第二步，注册你使用到的，需要通过Kryo序列化的，一些自定义类，SparkConf.registerKryoClasses()

项目中的使用：
```
#下面是java方法

.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
.registerKryoClasses(new Class[]{CategorySortKey.class})
```

