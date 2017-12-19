---
title: spark core之Standalone模式
categories: spark   
toc: true  
tag: [spark]
---


# Standalone client模式提交作业

通常情况来说,部署在测试机器上去,进行测试运行spark作业的时候,都是使用的是client模式,client模式下,提交作业以后,driver在本地启动,可以实时看到详细的日志信息,方便你追踪和排查错误

client的三种模式:
1.硬编码:SparkConf.setMaster("spark://IP:PORT")
2.spark-submit提交的时候设置一下:--master spark://IP:PORT
3.spark-shell去启动的时候可以指定:--master spark://IP:PORT

上面三种写法,使用第二种,是最合适的

在Standalone模式下中,在spark-submit提交脚本中,用--master指定Master的URL的,使用Standalone client模式或者是cluster模式,是要在spark-submit中使用--deploy-mode client/cluster来设置,但是如果不设置,默认的--deploy-mode为client模式


使用spark-submit脚本来提交application时，application jar是会自动被分发到所有worker节点上去的,对于你的application依赖的额外jar包，可以通过spark-submit脚本中的--jars标识，来指定，可以使用逗号分隔多个jar,比如说，你写spark-sql的时候，有的时候，在作业中，要往mysql中写数据，此时可能会出现找不到mysql驱动jar包的情况,此时，就需要你手动在spark-submit脚本中，使用--jars，加入一些依赖的jar包

提交的脚本

```
/export/servers/spark/bin/spark-submit \
--master spark://hdp-node-01:7077 \
--deploy-mode client \
--class cn.spark.study.core.WordCount \
--num-executors 1 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 1 \
/usr/xx/spark-study-java-0.0.1-SNAPSHOT-jar-with-dependencies.jar \

```



提交Standalone client模式的作业
1.--master 和 --deploy-mode 来提交作业
2.在web UI查看,可以看到completed applications一栏中,有刚刚提交的作业,对比一下UI上的ApplicationID和driver机器上打印的日志的ApplicationID
3.使用jps查看进程,看Standalone client模式提交作业的时候,当前机器上会有哪些进程(会看到一个sparksubmit相当于driver进程,还是一个进程是CoarseGrainedExecutorBackend进程,这个进程就是在worker机器上的executor进程)



# Standalone cluster模式提交作业

Standalone cluster模式,通常用于,spark作业部署到生产环境中去使用,Standalone client模式下,在spark-submit脚本执行的机器上,会启动driver进程,然后去进行整个作业的调度,通常来说,你的spark-submit脚本能够执行的机器,也就是,作为一个开发人员能够登录的机器,通常不会直接是spark集群部署的机器,因为不是随便谁都能够登录到spark集群中某个机器上去执行一个脚本,这是没有安全性可言的,用client模式,你的机器可能与spark集群部署的机器,都不在一个机房,或者是举例很远,那么此时远距离的频繁的网络通信会影响整个作业的执行性能,所以在生产环境中是使用的Standalone cluster模式,因为在这种模式下,会由master在集群中的一个节点上来启动driver,然后driver会进行频繁的作业调度,此时driver和集群是在一起的,这样性能是比较高的

此外,在Standalone cluster模式下,还支持监控你的driver进程,并且在driver进程挂掉的时候,自动重启该进程,要使用这个功能,在spark-submit脚本中,使用--supervise参数即可,这个参数其实在spark streaming中作为HA高可用来的,配置driver的高可用


如果想要杀掉反复挂掉的driver进程,使用以下即可:
```
bin/spark-class org.apache.spark.deploy.Client kill <master url> <driver ID>

#如果要查看driver id，通过http://<maser url>:8080即可查看到
```

提交的脚本

```
/export/servers/spark/bin/spark-submit \
--master spark://hdp-node-01:7077 \
--deploy-mode cluster \
--class cn.spark.study.core.WordCount \
--num-executors 1 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 1 \
/usr/xx/spark-study-java-0.0.1-SNAPSHOT-jar-with-dependencies.jar \

```



# Standalone模式下的多作业资源调度

Standalone集群对于同时提交上来的多个作业(Application),仅仅支持FIFO调度策略,也就是先入先出

默认情况下,集群对多作业同时执行的支持是不好的,没有办法同时执行多个作业,因为先提交上来的每一个作业都会尝试使用集群中的所有可用的cpu资源(spreadOut),此时相当于只能支持Application串行一个一个运行了,因此如果我们希望能够支持多作业同时运行,那么就需要调整一些资源参数了


我们需要调整的资源参数是:spark.cores.max,来限制每个作业能够使用的最大的cpu core数量,这样先提交上来的作业不会使用所有的cpu资源,后面提交上来的作业就可以获取到资源了,这样就可以同时运行多个Application

比如说,如果集群一共有20个节点，每个节点是8核，160 cpu core,那么，如果你不限制每个作业获取的最大cpu资源大小，而且在你spark-submit的时候，或者说，你就设置了num-executors，total-cores，160 此时，你的作业是会使用所有的cpu core资源的,所以，如果我们可以通过设置全局的一个参数，让每个作业最多只能获取到一部分cpu core资源,那么，后面提交上来的作业，就也可以获取到一部分资源,standalone集群，才可以支持同时执行多个作业


使用SparkConf或spark-submit中的--conf标识，设置参数即可

```

SparkConf conf = new SparkConf()
		.set("spark.cores.max", "10")
```

通常不建议使用SparkConf，硬编码，来设置一些属性，不够灵活,建议使用spark-submit来设置属性
```
--conf spark.cores.max=10
```

此外，还可以直接通过spark-env.sh配置每个application默认能使用的最大cpu数量来进行限制，默认是无限大，此时就不需要每个application都自己手动设置了
在spark-env.sh中配置spark.deploy.defaultCores即可
```
export SPARK_MASTER_OPTS="-Dspark.deploy.defaultCores=10"

```


# Standalone模式下的作业监控与日志记录

spark standalone模式，提供了一个web界面来让我们监控集群，并监控所有的作业的运行,web界面上,提供了master和worker的相关信息,默认的话,我们的web界面运行在master机器上的8080端口

spark web ui
1.哪些作业在跑
2.哪些作业跑完了,花了多长时间,使用了多少资源
3.哪些作业跑失败了

Application web ui
1.可以看到job,stage,task的详细运行信息
2.shuffle read,shuffle write,gc,运行时间,每个task分配的数据量
3.定位很多性能问题、troubleshooting等等，如task数据分布不允许，那么就是数据倾斜
4.哪个stage运行的时间最慢，通过之前讲解的stage划分算法，去你的代码里定位到，那个stage对应的是哪一块儿代码，你的那段代码为什么会运行太慢,使用优化策略去优化性能

但是有个问题，作业运行完了以后，我们就看不到了,此时跟history server有关，需要我们开启


日志记录
1、系统级别的，spark自己的日志记录
2、我们在程序里面，用log4j，或者System.out.println打印出来的日志

spark web ui中可以看到
1、看每个application在每个executor上的日志
2、stdout，可以显示我们用System.out.println打印出来的日志，stderr，可以显示我们用System.err.println打印出来的日志



此外，我们自己在spark作业代码中，打出来的日志，比如用System.out.println()等，是打到每个作业在每个节点的工作目录中去的,默认是SPARK_HOME/work目录下,这个目录下，每个作业都有两个文件，一个是stdout，一个是stderr，分别代表了标准输出流和异常输出流









