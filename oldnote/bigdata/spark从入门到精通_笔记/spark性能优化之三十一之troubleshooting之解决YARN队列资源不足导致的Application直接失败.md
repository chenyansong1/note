---
title: spark性能优化之三十一之troubleshooting之解决YARN队列资源不足导致的Application直接失败
categories: spark  
tags: [spark]
---


# yarn资源不足导致的Application提交失败的现象说明

如果说，你是基于yarn来提交spark。比如yarn-cluster或者yarn-client。你可以指定提交到某个hadoop队列上的。每个队列都是可以有自己的资源的。

<!--more-->


跟大家说一个生产环境中的，给spark用的yarn资源队列的情况：500G内存，200个cpu core。

比如说，某个spark application，在spark-submit里面你自己配了，executor，80个；每个executor，4G内存；每个executor，2个cpu core。你的spark作业每次运行，大概要消耗掉320G内存，以及160个cpu core。

乍看起来，咱们的队列资源，是足够的，500G内存，280个cpu core。

首先，第一点，你的spark作业实际运行起来以后，耗费掉的资源量，可能是比你在spark-submit里面配置的，以及你预期的，是要大一些的。400G内存，190个cpu core。

那么这个时候，的确，咱们的队列资源还是有一些剩余的。但是问题是，如果你同时又提交了一个spark作业上去，一模一样的。那就可能会出问题。

第二个spark作业，又要申请320G内存+160个cpu core。结果，发现队列资源不足。。。。

此时，可能会出现两种情况：（备注，具体出现哪种情况，跟你的YARN、Hadoop的版本，你们公司的一些运维参数，以及配置、硬件、资源肯能都有关系）

1、YARN，发现资源不足时，你的spark作业，并没有hang在那里，等待资源的分配，而是直接打印一行fail的log，直接就fail掉了。
2、YARN，发现资源不足，你的spark作业，就hang在那里。一直等待之前的spark作业执行完，等待有资源分配给自己来执行。


# 采用如下方案

1、在你的J2EE（我们这个项目里面，spark作业的运行，之前说过了，J2EE平台触发的，执行spark-submit脚本），**限制，同时只能提交一个spark作业到yarn上去执行**，确保一个spark作业的资源肯定是有的。

2、你应该采用一些简单的调度区分的方式，比如说，你有的spark作业可能是要长时间运行的，比如运行30分钟；有的spark作业，可能是短时间运行的，可能就运行2分钟。此时，都提交到一个队列上去，肯定不合适。很可能出现30分钟的作业卡住后面一大堆2分钟的作业。**分队列**，可以申请（如果你的集群不是你管理的,跟你们的YARN、Hadoop运维的同学申请）。(如果集群是自己管理的)你自己给自己搞两个调度队列。每个队列的根据你要执行的作业的情况来设置。在你的J2EE程序里面，要判断，如果是长时间运行的作业，就干脆都提交到某一个固定的队列里面去把；如果是短时间运行的作业，就统一提交到另外一个队列里面去。这样，避免了长时间运行的作业，阻塞了短时间运行的作业。

3、你的队列里面，无论何时，只会有一个作业在里面运行。那么此时，就应该用我们之前讲过的性能调优的手段，去将每个队列能承载的最大的资源，分配给你的每一个spark作业，比如80个executor；6G的内存；3个cpu core。尽量让你的spark作业每一次运行，都达到最满的资源使用率，最快的速度，最好的性能；并行度，240个cpu core，720个task。

4、在J2EE中，通过线程池的方式（一个线程池对应一个资源队列），来实现上述我们说的方案。
```
ExecutorService threadPool = Executors.newFixedThreadPool(1);

threadPool.submit(new Runnable() {
			
@Override
public void run() {

}
			
});

```









