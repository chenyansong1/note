---
title: SparkStreaming之输入DStream的Transformation操作
categories: spark  
tags: [spark]
---



map		对传入的每个元素,返回一个新的元素
flatMap	对传入的每个元素,返回一个或多个元素
filter	对传入的元素返回true或false,返回的false的元素被过滤掉
union	将两个DStream进行合并
count	返回元素的个数
reduce	对所有的values进行聚合
countByValue	对元素按照值进行分组,对每个组进行计数,最后返回<K,V>格式
reduceByKey		对key对应的values进行聚合
cogroup			对两个DStream进行连接操作,一个key连接起来的两个RDD的数据,都会以Iterable<V>的形式,<key,tuple<Iterable1,Iterable2>>
join	对两个DStream进行join操作,每个连接起来的pair,作为新DStream的RDD的一个元素
transform	对数据进行转换操作
updateStateByKey	为每个key维护一份state,并进行更新
window		对滑动窗口数据执行操作

RDD1=(1,1) (1,2) (1,3)
RDD2=(1,4) (2,1) (2,2)

RDD1 cogroup RDD2
cogroup--->(1,((1,2,3),(4))

RDD1 join RDD2
join------>(1,(1,4))  (1,(2,4))	  (1,(3,4)

