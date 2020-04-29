---
title: SparkSQL的前世今生
categories: spark  
tags: [spark]
---


spark1.0版本开始,推出了spark sql,其实最早使用的都是Hadoop自己的hive查询引擎,但是后来spark提供了Shark,再后来Shark被淘汰,退出了Spark sql,Shark的性能比hive就要高出一个数量级,而spark sql的性能又比Shark高出一个数量级

最早来说,hive的诞生,主要是因为要让哪些不熟悉java,无法深入进行MapReduce编程的数据分析师,能够使用它们熟悉的关系型数据库的sql模型,来操作HDFS上的数据,因此推出了hive,hive底层基于MapReduce实现了sql功能,能够让数据分析人员,以及数据开发人员,方便的使用hive进行数据仓库的建模和建设,然后使用sql模型针对数据仓库中的数据进行统计和分析,但是hive有个致命的缺陷,就是他的底层是基于MapReduce的,而MapReduce的shuffle又是基于磁盘的,因此导致hive的性能异常低下,经常出现复杂的sql etl,要运行数个小时,甚至数十个小时的情况

后来,spark推出了Shark,shark与hive实际上还是紧密关联的,shark底层的很多东西还是依赖于hive,但是修改了内存管理,物理计划,执行三个模块,底层使用spark的基于内存的计算模型,从而让性能比hive提升了数倍到上百倍

然而,shark还是他的问题梭子,shark底层依赖了hive的语法解析器,查询优化器等组件,因此对于其性能的提升还是造成了制约,所以后来spark团队决定,完全抛弃shark,退出了全新的spark sql项目

spark sql就不只是针对hive中的数据了,而是可以支持其他很多数据源的查询

spark sql的特点:
1.支持多种数据源:hive,rdd,Parquet,Json, jdbc等
2.多种性能优化技术:in-memory columnar storage, byte-code generation, cost model动态评估等
3.组件扩展性:对于sql的语法解析器,分析器以及优化器,用户都可以自己重新开发,并且动态扩展

这样spark sql比Shark来说,性能又有了数倍的提升




spark sql的性能优化技术简介
1.内存列存储(in-memory columnar storage)
内存列存储意味着,spark sql的数据,不是使用java对象的方式来进行存储,而是使用面向列的内存存储的方式来进行存储,也就是说,每一个作为一个数据存储的单位,从而大大优化了内存使用的效率,采用了内存列存储之后,减少了对内存的消耗,也就避免了gc大量数据的性能开销

2.字节码生成技术(byte-code generation)
spark sql在其catalyst模块的expressions中增加了codegen模块,对于sql语句中的计算表达式,比如select num+num from t 这样的sql,就可以使用动态字节码技术来优化其性能

3.scala代码编写的优化
对于scala代码编写中,可能会造成较大的性能开销的地方,自己重写,使用更加复杂的方式,来获取更好的性能,比如Option样例类,for循环,map/filter/foreach等高阶函数,以及不可变对象,都改成了用null,while循环等来实现,并且重用了可变的对象
