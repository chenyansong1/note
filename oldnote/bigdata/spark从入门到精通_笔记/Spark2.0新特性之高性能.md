---
title: Spark2.0新特性之高性能
categories: spark  
tags: [spark]
---


在一个2015年的spark调查中显示，91%的spark用户是因为spark的高性能才选择使用它的。所以spark的性能优化也就是社区的一个重要的努力方向了。spark 1.x相较于hadoop mapreduce来说，速度已经快了数倍了，但是spark 2.x中，还能不能相较于spark 1.x来说，速度再提升10倍呢？

<!--more-->


带着这个疑问，我们可以重新思考一下spark的物理执行机制。对于一个现代的大数据处理引擎来说，CPU的大部分时间都浪费在了一些无用的工作上，比如说virtual function call，或者从CPU缓冲区中读写数据。现代的编译器为了减少cpu浪费在上述工作的时间，付出了大量的努力。



Spark 2.0的一个重大的特点就是搭载了最新的第二代tungsten引擎。第二代tungsten引擎吸取了现代编译器以及并行数据库的一些重要的思想，并且应用在了spark的运行机制中。其中一个核心的思想，就是在运行时动态地生成代码，在这些自动动态生成的代码中，可以将所有的操作都打包到一个函数中，这样就可以避免多次virtual function call，而且还可以通过cpu register来读写中间数据，而不是通过cpu cache来读写数据。上述技术整体被称作“whole-stage code generation”，中文也可以叫“全流程代码生成”。



之前有人做过测试，用单个cpu core来处理一行数据，对比了spark 1.6和spark 2.0的性能。spark 2.0搭载的是whole-stage code generation技术，spark 1.6搭载的是第一代tungsten引擎的expression code generation技术。测试结果显示，spark 2.0的性能相较于spark 1.6得到了一个数量级的提升。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark2_func.png)




除了刚才那个简单的测试以外，还有人使用完整的99个SQL基准测试来测试过spark 1.6和spark 2.0的性能。测试结果同样显示，spark 2.0的性能比spark 1.6来说，提升了一个数量级。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/spark2_func2.png)




spark 2.0中，除了whole-stage code generation技术以外，还使用了其他一些新技术来提升性能。比如说对Spark SQL的catalyst查询优化器做了一些性能优化，来提升对一些常见查询的优化效率，比如null值处理等。再比如说，通过vectarization技术将parquet文件扫描的吞吐量提升了3倍以上。






















