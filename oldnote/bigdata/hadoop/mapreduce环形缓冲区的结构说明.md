
---
title: mapreduce环形缓冲区的结构说明(转)
categories: hadoop   
toc: true  
tag: [mapreduce,hadoop]
---



MapOutPutBuffer就是map任务暂存记录的内存缓冲区。不过这个缓冲区是有限的，当写入的数据超过缓冲区设定的阈值时，需要将缓冲区的数据写入到磁盘，这个过程叫spill。在溢出数据到磁盘的时候，会按照key进行排序，保证刷新到磁盘的记录时排好序的。该缓冲区的设计非常有意思，它做到了将数据的meta data(索引)和raw data(原始数据)一起存放到了该缓冲区中，并且，在spill的过程中，仍然能够往该缓冲区中写入数据，我们在下面会详细分析该缓冲区是怎么实现这些功能的。

<!--more-->


# 缓冲区分析
MapoutPutBuffer是一个环形缓冲区，每个输入的key->value键值对以及其索引信息都会写入到该缓冲区，当缓冲区块满的时候，有一个后台的守护线程会负责对数据排序，将其写入到磁盘。


# 核心成员变量

1、 kvbuffer :字节数组，数据和数据的索引都会存在该数组中
2、 kvmeta：只是kvbuffer中索引存储部分的一个视角，为什么这么说？因为索引往往是按整型存储（4个字节），所以使用kvmeta来重新组织该部分的字节（kvmeta中的一个单元相当于4个字节，但是kvmeta并没有重新开辟内存，其指向的还是kvbuffer）
3、 equator:缓冲区的分割线，用来分割数据和数据的索引信息。
4、 kvindex:下次要插入的索引的位置
5、 kvstart:溢出时索引的起始位置
6、 kvend:溢出时索引的结束位置
7、 bufindex:下次要写入的raw数据的位置
8、 bufstart:溢出时raw数据的起始位置
9、 bufend:溢出时raw数据的结束位置
10、spiller:当数据占用超过这个比例时，就溢出
11、sortmb:kvbuffer总的内存量，默认值是100m，可以配置
12、indexCacheMemoryLimit:存放溢出文件信息的缓存大小，默认1m，可以配置
13、bufferremaining:buffer剩余空间，字节为单位
14、softLimit:溢出阈值，超出后就溢出。Sortmb*spiller


# 初始状态


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapoutputbuffer/buffer_init.png)


初始时，equator=0，在写入数据时，raw data往数组下标增大的方向延伸，而meta data（索引信息）往从数组后面往下标减小的方向延伸。从上图来看，raw data就是按照顺时针来写入数据，而meta data按照逆时针写入数据。我们再看一下各个变量的初始化情况，raw data部分的变量，bufstart、bufend、bufindex都初始化为0。Meta data部分的变量，kvstart 、kvend、kvindex都是按逆时针偏移了16个字节（metasize=16个字节），因为一个meta data占用16个字节（4个整数，分别存储keystart,valuestart,partion,valuelen），所以需要逆时针偏移16个字节来标记第一个存储的metadata的起始位置。还有一个重要的变量，bufferremaining = softlimit（默认是sortmb*80%）。



# 写入第一个<key,value>的状态


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapoutputbuffer/buffer_put.png)

我们看一下写入第一个<key,value>的情况，首先放入key，在bufferindex的基础上累加key的字节数，然后放入value，继续累加bufferindex的字节数。接下来放入metadata，meta data一共包括4个整数，第一个int放valuestart，第二个int放keystart，第三个int放partion，第四个int放value的长度。为什么只有value的长度，没有key的长度？个人理解key的长度可以有valuestart – keystart得出，不需要额外的空间来存储key的长度。需要注意的是，bufindex和kvindex发生了变化，分别指向了下一个数据需要插入的地方。但是bufstart，endstart,kvstart,kvend都没有变化，bufferremaining相应地减少了meta data 和raw data占据的空间。


# 溢出文件

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapoutputbuffer/buffer_spill.png)



随着kvindex和bufindex的不断偏移，剩余的空间越来越小，当剩余空间不足时，就会触发spill操作。如上图，kvindex和bufindex之间的空间已经很小了。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapoutputbuffer/buffer_spill_2.png)




溢出文件开始前，需要先更新kvend、bufend,kvend 需要更新成kvindex + 4 int，因为kvindex始终指向下一个需要写入的meta data的位置，必须往后回退4 int 才是meta data真正结束的位置，如上图，kvend加了4int往顺时针方向偏移了。Kvstart指向最后一个meta data写入的位置。Bufstart标识着最后一个key 开始的位置，bufend 标识最第一个value的结束位置。Kvstart和kvend之间（黄色部分）是需要溢出的meta data。Bufstart和bufend之间（浅绿色）是需要溢出的raw data。溢出的时候，其他的缓存空间（深绿色）仍然可以写入数据，不会被溢出操作阻塞住。默认的Spiller是80%，也就是还有20%的空间可以在溢出的时候使用。 
溢出开始前，需要确定新的equator，新的equator一定在kvend和bufend之间。新的equator一定要做到合适的划分，保证能写入更多的metadata和raw data。确定了equator后，我们需要更新bufindex和kvindex的位置，更新bufferremaining的大小，bufferremaining要选择equator到bufindex、kvindex较小的那个。任何一个用完了，都代表不能写入数据，这也说明了equator划分均匀的重要性。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapoutputbuffer/buffer_spill_3.png)

在溢出完成后，空间都已经释放出来，溢出完成后的缓存状态就变成了上图：meta data从新的equator开始逆时针写入数据，raw data从新的equator开始顺时针写入数据。当剩余的空间又到了溢出的阈值时，再次划分equator，再次溢出文件。



转自:
[MapReduce之mapOutputBuffer解析](http://blog.csdn.net/wangqinghuan1993/article/details/53785403)





