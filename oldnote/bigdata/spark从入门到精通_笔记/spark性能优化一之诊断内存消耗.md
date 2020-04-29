---
title: spark性能优化一之诊断内存消耗
categories: spark  
tags: [spark]
---



# 内存都花费到哪里去了?

1.每个java对象,都有一个对象头,会占用16个字节,主要包括了一些对象的元信息,比如指向他的类的指针,如果一个对象本身很小,比如就包含了一个int类型的Field,那么他的对象头实际上比对象自己还要大

2.java的string对象,会比他内部的原始数据,要多出40个字节,因为他内部使用char数组来保存内部的字符序列的,并且还得保存诸如数组长度之类的信息,而且因为string使用的是UTF-16编码,所以每个字符会占用2个字节,比如,包含10个字符的string,会占用60个字节

3.java中的集合类型,比如HashMap和LinkedList,内部使用的是链表数据结构,所以对链表中的每一个数据,都使用了Entry对象来包装,Entry对象不光有对象头,还有指向下一个Entry的指针,通常占用8个字节

4.元素类型为袁术数据类型(比如int)的集合,内部通常会使用原始数据类型的包装类型,比如Integer来存储元素



# 如何判断你的程序消耗了多少内存?
1.首先,自己设置RDD的并行度,有两种方式:要不然在parallelize(),textFile()等方法中,传入第二个参数,设置RDD的task/partition的数量;要不然,用sparkConf.set()方法,设置一个参数,spark.default.parallelism,可以统一设置这个Application所有RDD的partition数量

2.其次,在程序汇总将RDD cache到内存中,调用RDD.cache()方法即可
3.最后,观察Driver的log,你会发现类似于:"INFO BlockManagerMasterActor:Added rdd_0 in memory on mbk.local:50311(size:717.5KB,free:332.3MB)"的日志信息,这就显示了每个partition占用了多少内存

4.将这个内存信息乘以partition数量,即可得出RDD的内存占用量



