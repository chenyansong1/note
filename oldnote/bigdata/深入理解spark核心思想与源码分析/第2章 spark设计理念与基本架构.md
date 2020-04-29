---
title: 第2章 spark设计理念与基本架构
categories: spark   
toc: true  
tag: [spark]
---



spark也是基于map reduce算法模式实现的分布式框架,拥有hadoop MapReduce所具有的优点,并且解决了Hadoop MapReduce中的诸多缺陷.


<!--more-->

# 1.初试spark

## 1.1.hadoop MRv1的局限

hadoop1.0版本的采用的是MRv1版本的MapReduce编程模型,MRv1版本的实现都封装在org.apache.hadoop.mapred包中,MRv1的Map和Reduce是通过接口实现的,MRv1包括三个部分:    
* 运行时环境(JobTracker和TaskTracker)
* 编程模型(MapReduce)
* 数据处理引擎(Map任务和Reduce任务)

MRv1存在的不足:  
1.可扩展性差:在运行时,JobTracker既负责资源管理又负责任务调度,当集群繁忙时,JobTracker很容易称为瓶颈,最终导致他的可扩展性问题  
2.可用性差:采用了单节点的Master,没有备用Master及选举操作,这导致一旦Master出现故障,整个集群将不可用
3.资源利用率低:TaskTracker使用slot等量划分本节点上的资源,slot代表计算资源(CPU,内存等),一个Task获取到一个slot才有机会运行,hadoop调度器负责各个TaskTracker山的空闲slot分配给Task使用,一些Task并不能充分利用slot,而其他Task也无法使用这些空闲的资源,slot分为Map slot和Reduce slot,分别供MapTask和ReduceTask使用,有时因为作业刚刚启动等原因导致MapTask很多,而ReduceTask任务还没有调度的情况,这时Reduce slot也会被闲置  
4.不能支持多种MapReduce框架:无法通过可插拔方式将自身的MapReduce框架替换为其他实现,如spark,storm等.   

MRv1.0的示意图

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/MRv1.png)


apache为了解决以上问题,对hadoop进行升级改造,MRv2最终诞生了,MRv2重用了MRv1中的编程模型和数据处理引擎,但是运行时环境被重构了,JobTracker被拆分成了通用的资源调度平台(ResourceManager,RM)和负责各个计算框架的任务调度模型(ApplicationMaster AM),MRv2中MapReduce的核心不再是MapReduce框架,而是YARN,在以Yarn为核心的MRv2中,MapReduce框架是可插拔的,完全可以替换为其他MapReduce实现,比如:spark,storm等,MRv2的示意图如下:

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/MRv2.png)


Hadoop MRv2虽然解决了MRv1中的一些问题,但是由于对HDFS的频繁操作(包括计算结果持久化,数据备份及Shuffle等)导致磁盘I/O成为系统性能的瓶颈,因此只适用于离线数据处理,而不能提供实时数据处理能力


## 1.2.spark使用场景

hadoop常用于解决高吞吐,批量处理的业务场景,例如离线计算结果用于浏览量统计,如果需要实时查看浏览量统计信息,hadoop显然不符合这样的要求,spark通过内存计算能力极大的提高了大数据处理速度,满足了以上场景的需求,此外,spark还支持sql查询,流式计算,图计算,机器学习等,通过对java,Python,scala,R等语言的支持,极大的方便了用户的使用.  

## 1.3.spark的特点
spark看到MRv1的问题,对MapReduce做了大量的优化,总结如下:    

1.快速处理能力:随着实时大数据应用越来越多,hadoop作为离线的高吞吐,低响应框架已不能满足这类需求,hadoop MapReduce的Job将中间输出和结果存储在HDFS中,读写HDFS造成磁盘I/O成为瓶颈,spark允许将中间输出和结果存储在内存中,避免了大量的磁盘I/O,同时spark自身的DAG执行引擎也支持数据在内存中计算,spark官网声称性能比hadoop快100倍,即便是内存不足,需要磁盘I/O,其速度也是hadoop的10倍以上.  
2.易于使用:spark现在支持java,scala,Python和R语言编写的应用程序,大大降低了使用者的门槛,自带80多个高等级操作符,允许在scala,Python,R的shell中进行交互式查询.  
3.支持查询:spark支持sql及hive sql对输数据查询
4.支持流式计算:与MapReduce智能处理离线数据相比,spark还支持实时的流计算,spark依赖spark streaming对数据进行实时的处理,其流式处理能力还要强于storm  
5.可用性高:spark自身实现了standalone模式部署,次模式下的Master可以有多个,解决了单点故障问题,此模式完全可以使用其他集群管理器替换,如yarn,Mesos,EC2等.  
6.丰富的数据源支持:spark除了可以访问操作系统自身的文件系统和HDFS,还可以访问Cassandra,HBase,Hive,Tachyon以及任何hadoop的数据源,这极大的方便了已经使用HDFS,HBase的用户顺利迁移到spark上.  



# 2.spark基础知识

## 2.1.基本概念

* RDD: resilient distributed dataset 弹性分布式数据集
* Task:具体执行任务,Task分为ShuffleMapTask和ResultTask两种,ShuffleMapTask和ResultTask分别类似于Hadoop只能的Map和Reduce
* Job:用户提交的作业,一个job可能由一到多个Task组成
* Stage:Job分成的阶段,以job可能被划分为一到多个Stage
* Partition:数据分区,即一个RDD的数据可以划分为多少个分区
* NarrowDependency:窄依赖,即子RDD依赖于父RDD中固定的Partition,NarrowDependency分为OneToOneDependency和RangeDependency两种
* ShuffleDependency:Shuffle依赖,也称为宽依赖,即子RDD对父RDD中的所有Partition都有依赖
* DAG: directed acycle graph 有向无环图,用于反映各RDD之间的依赖关系


# 3.spark的基本设计思想

## 3.1.spark模块设计
整个spark主要由以下模块组成:
* spark core:spark的核心模块功能实现,包括:SparkContext的初始化(Driver Application通过SparkContext提交),部署模式,存储系统,任务提交与执行,计算引擎等
* Spark SQL:提供SQL处理能力,便于熟悉关系型数据库操作的工程师进行交互式查询,此外,还为熟悉Hadoop的用户提供了hive sql处理能力
* Spark Streaming:提供流式计算处理能力,目前支持Kafka,flume,Twitter,MQTT,ZeroMQ,Kinesis和简单 的TCP套接字等数据源,此外,还提供了窗口操作.  
* GraphX:提供了图计算处理能力,支持分布式,Pregel提供的API可以解决图计算中的常见问题. 
* MLib:提供机器学习相关的统计,分类,回归等领域的多种算法实现,其一致的API接口大大降低了用户的学习成本.   

Spark SQL, Spark Streaming, GraphX, Mlib的能力都是建立在核心引擎之上,如下:

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/sparkCore.png)


Spark的核心功能

spark Core提供Spark最基础与最核心的功能,主要包括以下功能:
* SparkContext:通常而言,Driver Application的执行与输出都是通过SparkContext来完成的,在正式提交Application之前,首先需要初始化SparkContext, SparkContext隐藏了网络通信,分布式部署,消息通信,存储能力,计算能力,缓存,测量系统,文件服务,Web服务等内容,应用程序开发者只需要使用SparkContext提供的API完成功能开发,SparkContext内置的DAGScheduler负责创建Job,将DAG中的RDD划分到不同的Stage,提交stage等功能,内置的TaskScheduler负责资源的申请,任务的提交及请求集群对任务的调度等工作
* 存储系统:spark优先考虑使用各节点的内存作为存储,当内存不足时才会考虑使用磁盘,这极大的减少了磁盘I/O,提升了任务执行的效率,是的Spark适用于实时计算,流式计算等场景,此外,spark还提供了以内存为中心的高容错的分布式文件系统Tachyon供用户进行选择,Tachyon能够为spark提供可靠的内存级的文件共享服务
* 计算引擎:计算引擎由SparkContext中的DAGScheduler,RDD以及具体节点上的Executor负责执行的Map和Reduce任务组成,DAGScheduler和RDD虽然位于SparkContext内部,但是在任务提交与执行之前会将Job中的RDD组织成有向无环图(DAG),并对stage进行划分,决定了任务执行阶段任务的数量,迭代计算,Shuffle等过程
* 部署模式:由于单节点不足以提供足够的存储及计算能力,所以作为大数据处理的spark在SparkContext的TaskScheduler组件中提供了对Standalone部署模式的实现和Yarn,Mesos等分布式资源管理系统的支持,通过使用Standalone,Yarn,Mesos等部署模式为Task分配计算资源,提高任务的并发执行效率,除了可用于实际生产环境的Standalone,Yarn,Mesos等部署模式外,spark还提供了Local模式和local-cluster模式便于开发和调试.


spark扩展功能

为了扩大应用范围,spark陆续提供了一些扩展功能,主要包括:
* Spark SQL:sql具有普及率高,学习成本低等特点,为了扩大Spark的应用面,增加了对sql及hive的支持,sparksql的过程可以总结为:首先使用sql语句解析器(SqlParser)将SQL转换为语法树(Tree),并且使用规则执行器(RuleExecutor)将一系列规则(Rule)应用到语法树,最终生成物理执行计划并执行,其中,规则执行器包括语法分析器(Analyzer)和优化器(Optimizer),hive的执行过程与sql类似
* Spark Streaming:Sparking streaming与Apache Storm类似,也用于流式计算,Spark streaming支持Kafka,flume,Twitter,MQTT,ZeroMQ,Kinesis和简单的TCp套接字等多种数据源,输入流接收器负责接入数据,是接入数据流的接口规范,Dstream是SparkStreaming中所有数据流的抽象,Dstream可以被组织为Dstream Graph,Dstream本质上由一系列连续的RDD组成
* GraphX:Spark提供的分布式图计算框架,GraphX主要遵循整体同步并行计算模式下的Pregel模式实现,GraphX提供了对图的抽象Graph,Graph由顶点(Vertex),边(Edge)及继承了Edge的EdgeTriplet(添加了srcAttr和dstAttr用来保存源顶点和目的顶点的属性)三种结构组成,GraphX目前已经封装了最短路径,网页排名,连接组件,三角关系统计等算法的实现,用户可以选择使用
* MLib:Spark提供的机器学习框架,机器学习是一门设计概率论,统计学,逼近论,凸分析,算法复杂度理论等多领域的交叉学科,MLib目前已经提供了基础统计,分类,回归,决策树,随机森林,朴素贝叶斯,保序回归,协同过滤,聚类,维数缩减,特征提取与转型,频繁模式挖掘,语言模型标记语言,管道等多种数理统计,概率论,数据挖掘方面的数学算法.

## 3.2.spark模型设计
 
1.spark编程模型

Spark应用程序从编写到提交,执行,输出的整个过程如下图:

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/daimazhixingguocheng.png)

图中描述的步骤如下:
* 用户使用SparkContext提供的API(常用的有textFile,sequenceFile,runJob,stop等)编写的Driver Application程序,此外SQLContext,HiveContext及StreamingContext对SparkContext进行封装,并提供了SQL,Hive及流式计算相关的API   
* 使用SparkContext提交的用户应用程序,首先会使用BlockManager和BroadcastManager将任务的Hadoop配置进行广播,然后由DAGScheduler将任务转换为RDD并组织DAG,DAG还将被划分为不同的stage,最后由TaskScheduler借助ActorSystem将任务提交给集群管理器(Cluster Manager)
* 集群管理器(Cluster Manager)给任务分配资源,即将具体任务分配到Worker上,Worker创建Executor来处理任务的运行,Standalone,Yarn,Mesos,EC2等都可以作为spark的集群管理器


2.RDD计算模型
RDD可以看做是对各种数据模型的统一抽象,spark的计算过程主要是RDD的迭代计算的过程,如下图,

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/RDD_jisuanmoxing.png)

RDD的迭代过程非常类似于管道,分区数量取决于Partition数量的设定,每个分区的数据只会在一个Task中计算,所有分区可以在多个机器节点的Executor上并行执行



# 4.spark的基本架构
从集群部署的角度来看,spark集群由以下部分组成:
* Cluster Manager:Spark的集群管理器,主要负责资源的分配与管理,集群管理器分配的资源属于一级分配,他将各个Worker上的内存,CPU等资源分配个应用程序,但是并不负责对Executor的资源分配,目前,Standalone,Yarn,Mesos,EC2等都可以作为Spark的集群管理器
* Worker:Spark的工作节点,对Spark应用程序来说,由集群管理器分配得到资源的Worker节点主要负责以下工作,创建Executor,将资源和任务进一步分配给Executor,同步资源信息给Cluster Manager
* Executor:执行计算任务的一线进程,主要负责任务的执行以及与Worker,Driver APP的信息同步
* Driver APP :客户端驱动程序,也可以理解为客户端应用程序,用于将任务程序转换为RDD和DAG,并与Cluster Manager进行通信与调度

这些组成部分之间的整体关系如图:

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/spark_jibenjiagou.png)



















