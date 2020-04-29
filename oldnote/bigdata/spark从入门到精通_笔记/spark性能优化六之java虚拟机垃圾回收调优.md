---
title: spark性能优化六之java虚拟机垃圾回收调优
categories: spark  
tags: [spark]
---


java虚拟机垃圾回收调优的背景

如果在持久化RDD的时候,持久化了大量的数据,那么java虚拟机的垃圾回收就可能成为一个性能瓶颈,因为java虚拟机会定期进行垃圾回收,此时就会追踪所有的java对象,并且在垃圾回收时,找到那些已经不再使用的对象,然后清理旧的对象,来给新的对象腾出内存空间

垃圾回收的性能开销,是跟内存中的对象的数量,成正比的,所以,对于垃圾回收的性能问题,首先要做的就是,使用高性能的数据结构,比如array和String;其次就是在持久化rdd的时候,使用序列化级别,而且用Kyo序列化类库,这样每个partition就只是一个对象----- 一个字节数组


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/GC对spark性能影响的原理.png)


监测垃圾回收

我们可以对垃圾回收进行监测,包括多久进行一次垃圾回收,以及每次垃圾回收耗费的时间,只要在spark-submit脚本中,增加一个配置即可:

```
--conf "spark.executor.extraJavaOptions=-verbose:gc-X;+PrintGCDetails-XX;+PrintGCTimeStamps"
```

但是要记住,这里虽然会打印出java虚拟机的垃圾回收的相关信息,但是是输出到了worker上的日志中,而不是driver的日志中

另一种方式:其实也完全可以通过SparKUI(4040端口)来观察每个stage的垃圾回收情况


优化Executor内存比例

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/GC对spark性能影响的原理2.png)


对于垃圾回收来说,最重要的就是调节RDD缓存占用的内存空间,与算子执行时创建的对象占用的内存空间的比例,默认情况下,spark使用每个Executor60%的内存空间来缓存RDD,那么在task执行期间创建的对象,只有40%的内存空间来存放

在这种情况下,很有可能因为你的内存空间的不足,task创建的对象过大,那么一旦发现40%内存空间不够用了,就会触发java虚拟机的垃圾回收操作,因为在极端情况下,垃圾回收操作可能会被频繁触发

在上述情况下,如果发现垃圾回收频繁发生,那么就需要对那个比例进行调优,使用:

```
SparkConf().set("spark.storage.memoryFaction", "0.5")

```
可以将RDD缓存占用空间的比例降低,从而给更多的空间让task创建的对象进行使用

因此,对于RDD持久化,完全可以使用Kryo序列化,加上降低其Executor内存占比的方式来减少其内存消耗,给task提供更多的内存,从而避免task的执行频繁触发GC




高级垃圾回收调优

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/full_gc.png)




java堆空间被划分成了两块空间,一个是年轻代,一个是老年代,年轻代存放的是段时间存活的对象,老年代存放的是长时间存活的对象,年轻代又被划分为三块空间:Eden,Survivor1,Survivor2

首先,Eden区域和Survivor1区域用于存放对象,Survivor2区域备用,创建的对象,首先放入Eden区域和Survivor1区域,如果Eden区域满了,那么就会触发一次minor gc,进行年轻代的垃圾回收,Eden和Survivor1区域中存活的对象,会被移动到Survivor2区域中,然后Survivor1和Survivor2的角色调换,Survivor1变成了备用

如果一个对象,在年轻代中,撑过了多次垃圾回收,都没有被回收掉,那么会被认为是长时间存活的,因此会被移入老年代中,此外,如果将Eden和Survivor1中的存活对象,尝试放入Survivor2中时,发现Survivor2放满了,那么会直接放入老年代中,此时就出现了,短时间存活的对象进入老年代的问题

如果老年代的空间满了,那么就会触发full gc ,进行老年代的垃圾回收操作



spark中,垃圾回收调优的目标就是:只有真正长时间存活的对象,才能进入老年代,短时间存活的对象,只能待在年轻代中,不能因为某个Survivor区域空间不够,在minor gc时,就进入老年代,从而造成短时间存活的对象长期待在老年代中占据了空间,而且full gc时要回收大量的短时间存活的对象,导致full gc速度缓慢


如果发现,在task执行期间,大量full gc发生了,那么说明,年轻代的Survivor区域,给的空间不够大,此时可以执行一些操作来优化垃圾回收行为:
1.包括降低spark.storage.memoryFaction的比例,给年轻代更多的空间,来存放短时间存活的对象
2.给Eden区域分配更大的空间,使用-xmn即可(在spark.executor.extraJavaOptions中配置,见上面),通常建议给Eden区域,预计大小的4/3
3.如果使用的是HDFS文件,那么很好估计Eden区域大小,如果每个Executor有4个task,然后每个HDFS压缩块解压后大小是3倍,此外每个HDFS块的大小为128M,那么Eden区域的预计大小就是:4*3*128M,然后通过-Xmn参数,将Eden区域大小设置为4*3*128*4/3



其实,根据经验来看,对于垃圾回收的调优,尽量就是调节Executor内存的比例就可以了,因为jvm的调优是非常负责和敏感的,除非是真的到了万不得已的地步,自己本身对jvm相关的技术很了解,那么此时进行Eden区域的调优是可以的

一些高级的参数:
-XX:SurvivorRatio=4 如果值为4,那么就是一个Survivor跟Eden的比例是1:4,也就是说每个Survivor占据的年轻代的比例是1/6,所以你其实也可以尝试调大Survivor区域的大小

-XX:NewRatio=4 调节新生代和老年代的比例
































