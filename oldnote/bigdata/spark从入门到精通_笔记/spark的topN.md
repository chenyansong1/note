---
title: spark的topN
categories: spark   
toc: true  
tag: [spark]
---



取最大的前3个数字:其实就是在sortByKey之后取take(3)
```
val lineRdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
val reducedRdd = lineRdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
val countWords = reducedRdd.map(count=>(count._2,count._1))
val sortedRdd = countWords.sortByKey(false)
val result = sortedRdd.map(sort=>(sort._2,sort._1))
val top3Number = result.take(3)

top3Number.foreach(println)

/*执行结果:
(spark,19)
(hadoop,13)
(88,6)
 */

```


获取分组之后的组内的topN,实现步骤如下:
1.对rdd进行groupByKey,返回的是(K, Iterable[V])
2.在遍历1的结果,然后在每组中使用sortWith(排序的规则),对组内数据进行排序,取组内的topN,并返回组内的数据
3.对组与组之间进行排序,sortBy可以指定某列进行排序
```
 val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
 val lines=rdd.map{ line => (line.split(" ")(0),line.split(" ")(1).toInt) }

 //分组
 val groups=lines.groupByKey() //返回:RDD[(K, Iterable[V])]
 //组内进行排序
 val groupsSort=groups.map(tu=>{
   val key=tu._1
   val values=tu._2
   val sortValues=values.toList.sortWith(_>_).take(4)//取top 4
   (key,sortValues)
 })

 //组与组之间进行排序
 groupsSort.sortBy(tu=>tu._1, false, 1).collect.foreach(value=>{
   print(value._1)
   value._2.foreach(v=>print("\t"+v))
   println()
 })

 /*
 打印结果:
 spark	100	99	94	88
 hadoop	88	56	35	33
  */

```
