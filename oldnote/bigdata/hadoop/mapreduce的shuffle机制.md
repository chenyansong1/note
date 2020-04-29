---
title: mapreduce的shuffle机制
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---


# 概述

* mapreduce中，map阶段处理的数据如何传递给reduce阶段，是mapreduce框架中最关键的一个流程，这个流程就叫shuffle
* shuffle: 洗牌、发牌——（核心机制：数据分区，排序，缓存）
* 具体来说：就是将maptask输出的处理结果数据，分发给reducetask，并在分发的过程中，对数据按key进行了分区和排序


<!--more-->


# 整体过程图解

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/shuffle/mapreduce_shuffle.png "请在新标签页中打开")


简单说明一下shuffle的过程:  
* Map Task的整体流程：
1）Read：Map Task通过用户编写的RecordReader，从输入InputSplit中解析出一个个key/value。
2）Map：该阶段主要将解析出的key/value交给用户编写的map()函数处理，并产生一系列的key/value。
3）Collect：在用户编写的map()函数中，当数据处理完成后，一般会调用OutputCollector.collect()输入结果。在该函数内部，它会将生成的 key/value分片（通过Partitioner），并写入一个环形内存缓冲区中。
4）Spill：即“溢写”，当环形缓冲区满后，MapReduce会将数据写到本地磁盘上，生成一个临时文件。将数据写入本地磁盘之前，先要对数据进行一次本地排序，并在必要时对数据进行合并，压缩等操作。
5）Combine：当所有数据处理完成后，Map Task对所有临时文件进行一次合并，以确保最终只会生成一个数据文件。

* Reduce的整体流程：
1）Shuffle：也称Copy阶段。Reduce Task从各个Map Task上远程拷贝一片数据，并针对某一片数据，如果其大小超过一定阀值，则写到磁盘上，否则直接放到内存中。
2）Merge：在远程拷贝的同时，Reduce Task启动了两个后台线程对内存和磁盘上的文件进行合并，以防止内存使用过多或者磁盘上文件过多。
3）Sort：按照MapReduce语义，用户编写的reduce()函数输入数据是按key进行聚集的一组数据。为了将key相同的数据聚在一 起，Hadoop采用了基于排序的策略。由于各个Map Task已经实现了对自己的处理结果进行了局部排序，因此，Reduce Task只需对所有数据进行一次归并排序即可。
4）Reduce：在该阶段中，Reduce Task将每组数据依次交给用户编写的reduce()函数处理。
5）Write：reduce()函数将计算结果写到HDFS。



这里涉及到"环形缓冲区"的知识,可以参见[mapreduce环形缓冲区的结构说明(转)](http://chenyansong.site/2017/02/26/bigdata/hadoop/mapreduce%E7%8E%AF%E5%BD%A2%E7%BC%93%E5%86%B2%E5%8C%BA%E7%9A%84%E7%BB%93%E6%9E%84%E8%AF%B4%E6%98%8E/#more)说明

当环形缓冲区写到80%(默认)时,将会有溢出(spill),首先将80%的要溢出的buffer锁定(另外的20%空间还是可以继续写入的),然后对环形缓冲区中的元数据进行排序(按照分区进行,因为元数据中存放了partition数据),之后进行溢出操作,溢出是将内存(环形缓冲区80%)的数据写入到一个临时文件中(所以可以想象一下,这样的临时文件将会在每次环形缓冲区发生溢出的时候都会生成一个,所以临时文件有可能有多个),这样溢出到临时文件中的数据是分区,且在分区中是有排序的,如果在溢出的过程中我们指定了combiner,则溢出的文件将是合并之后的结果,如上图所示

溢出的多个临时文件会经过合并(merge),将多个文件中相同分区的放在一起,分区内进行key排序,同样如果我们指定了combiner,则会在分区内进行相同的key的合并,这样在每个map task上就形成了一个最终的文件,如上图

Shuffle：也称Copy阶段。Reduce Task从各个Map Task上远程拷贝一片数据，并针对某一片数据，如果其大小超过一定阀值，则写到磁盘上，否则直接放到内存中