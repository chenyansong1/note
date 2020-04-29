---
title: spark core之spark算子的闭包原理详解
categories: spark   
toc: true  
tag: [spark]
---


通常来说，这个问题跟在RDD的算子中操作作用域外部的变量有关,所谓RDD算子中，操作作用域外部的变量，指的是，类似下面的语句: var a = 0; rdd.foreach(i -> a += i),此时，对rdd执行的foreach算子的作用域，其实仅仅是它的内部代码，但是这里却操作了作用域外部的a变量,根据不同的编程语言的语法，这种功能是可以做到的，而这种现象就叫做闭包

<!--more-->

闭包简单来说，就是操作的不属于一个作用域范围的变量


如果使用local模式运行spark作业，那么实际只有一个jvm进程在执行这个作业,此时，你所有的RDD算子的代码执行以及它们操作的外部变量，都是在一个进程的内存中，这个进程就是driver进程,此时是没有任何问题的


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark core之spark算子的闭包原理详解1.png)



但是在作业提交到集群执行的模式下（无论是client或cluster模式，作业都是在集群中运行的）,为了分布式并行执行你的作业，spark会将你的RDD算子操作，分散成多个task，放到集群中的多个节点上的executor进程中去执行,每个task执行的是相同的代码，但是却是处理不同的数据

在提交作业的task到集群去执行之前，spark会先在driver端处理闭包,spark中的闭包，特指那些，不在算子的作用域内部，但是在作用域外部却被算子处理和操作了的变量
而算子代码的执行也需要这些变量才能顺利执行,此时，这些闭包变量会被序列化成多个副本，然后每个副本都发送到各个executor进程中，供那个executor进程运行的task执行代码时使用


注意:从spark的官网描述在local模式下,是可以看到的,但是我们真正试验的时候,在local模式下,并不是如官网描述的那样,如下代码:
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)

 val numberRdd = sc.parallelize(Seq(1,2,3,4,5))
 var sum = 0
 numberRdd.foreach{
   i=>sum+=i
   println("--------foreach内部:------------"+sum+".................")
 }

 println("---------driver:-----------"+sum+".................")


/*执行结果:
--------foreach内部:------------1.................
--------foreach内部:------------3.................
--------foreach内部:------------6.................
--------foreach内部:------------10.................
--------foreach内部:------------15.................

---------driver:-----------0.................

*/

```	



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark core之spark算子的闭包原理详解2.png)


对于上面说的闭包变量处理机制

对于local模式，没有任何特别的影响，毕竟都在一个jvm进程中，变量发送到executor，也不过就是进程中的一个线程而已,但是对于集群运行模式来说，每个executor进程，都会得到一个闭包变量的副本，这个时候，就会出问题

因此闭包变量发送到executor进程中之后，就变成了一个一个独立的变量副本了，这就是最关键的一点,此时在executor进程中，执行task和算子代码时，访问的闭包变量，也仅仅只是当前executor进程中的一个变量副本而已了,此时虽然在driver进程中，也有一个变量副本，但是却完全跟各个executor进程中的变量副本不是一个东西,此时，各个executor进程对于自己内存中的变量副本进行操作，即使改变了变量副本的值，但是对于driver端的程序，是完全感知不到的driver端的变量没有被进行任何操作

因此综上所述，在你使用集群模式运行作业的时候，切忌不要在算子内部，对作用域外面的闭包变量进行改变其值的操作,因为那没有任何意义，算子仅仅会在executor进程中，改变变量副本的值
对于driver端的变量没有任何影响，我们也获取不到executor端的变量副本的值,如果希望在集群模式下，对某个driver端的变量，进行分布式并行地全局性的修改,可以使用Spark提供的Accumulator，全局累加器


















