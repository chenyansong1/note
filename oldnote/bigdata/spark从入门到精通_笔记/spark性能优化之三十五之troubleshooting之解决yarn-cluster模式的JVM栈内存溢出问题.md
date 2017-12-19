---
title: spark性能优化之三十五之troubleshooting之解决yarn-cluster模式的JVM栈内存溢出问题
categories: spark  
tags: [spark]
---



yarn-client模式提交的图示

<!--more-->

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/performance_yarn_cluster.png)





实践经验，碰到的yarn-cluster的问题：

有的时候，运行一些包含了spark sql的spark作业，可能会碰到yarn-client模式下，可以正常提交运行；yarn-cluster模式下，可能是无法提交运行的，会报出JVM的PermGen（永久代）的内存溢出，OOM。

yarn-client模式下，driver是运行在本地机器上的，spark使用的JVM的PermGen的配置，是本地的spark-class文件（spark客户端是默认有配置的），JVM的永久代的大小是128M，这个是没有问题的；但是呢，在yarn-cluster模式下，driver是运行在yarn集群的某个节点上的，使用的是没有经过配置的默认设置（PermGen永久代大小），82M。

spark-sql，它的内部是要进行很复杂的SQL的语义解析、语法树的转换等等，特别复杂，在这种复杂的情况下，如果说你的sql本身特别复杂的话，很可能会比较导致性能的消耗，内存的消耗。可能对PermGen永久代的占用会比较大。

所以，此时，如果对永久代的占用需求，超过了82M的话，但是呢又在128M以内；就会出现如上所述的问题，yarn-client模式下，默认是128M，这个还能运行；如果在yarn-cluster模式下，默认是82M，就有问题了。会报出PermGen Out of Memory error log。


如何解决这种问题？

既然是JVM的PermGen永久代内存溢出，那么就是内存不够用。咱们呢，就给yarn-cluster模式下的，driver的PermGen多设置一些。

spark-submit脚本中，加入以下配置即可：

```
--conf spark.driver.extraJavaOptions="-XX:PermSize=128M -XX:MaxPermSize=256M"
```

这个就设置了driver永久代的大小，默认是128M，最大是256M。那么，这样的话，就可以基本保证你的spark作业不会出现上述的yarn-cluster模式导致的永久代内存溢出的问题。



spark sql，sql，要注意，一个问题

sql，有大量的or语句。比如where keywords1='' or keywords2='' or keywords3=''
当达到or语句，有成百上千的时候，此时可能就会出现一个driver端的jvm stack overflow (JVM栈内存溢出的问题)

JVM栈内存溢出，基本上就是**由于调用的方法层级过多，因为产生了大量的，非常深的，超出了JVM栈深度限制的，递归**。递归方法。我们的猜测，spark sql，有大量or语句的时候，spark sql内部源码中，在解析sql，比如转换成语法树，或者进行执行计划的生成的时候，对or的处理是递归。or特别多的话，就会发生大量的递归。

JVM Stack Memory Overflow，栈内存溢出。

这种时候，建议不要搞那么复杂的spark sql语句。采用替代方案：将一条sql语句，拆解成多条sql语句来执行。每条sql语句，就只有100个or子句以内；一条一条SQL语句来执行。根据生产环境经验的测试，一条sql语句，100个or子句以内，是还可以的。通常情况下，不会报那个栈内存溢出。




