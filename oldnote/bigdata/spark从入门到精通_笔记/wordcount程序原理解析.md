---
title: wordcount程序原理解析
categories: spark   
toc: true  
tag: [spark]
---


程序如下:
```
val conf = new SparkConf().setAppName("WordCount")
val sc = new SparkContext(conf)
val linesRdd = sc.textFile("/etc/hosts")
val wordsRdd = linesRdd.flatMap(_.split(" "))
val pairsRdd = wordRdd.map((_,1))
val wordCountRdd = pairsRdd.reduceByKey(_+_)



//打印
wordCountRdd.foreach(println)

```

下面的图是通过RDD的产生流程进行解析的

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/wordcount程序原理解析.png)




下面的图是通过源码的角度进行解析的

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/wordcount的运行原理及源码解读.png)
