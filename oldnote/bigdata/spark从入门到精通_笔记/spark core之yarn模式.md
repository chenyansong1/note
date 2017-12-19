---
title: spark core之yarn模式
categories: spark   
toc: true  
tag: [spark]
---



# yarn-client模式原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/yarn-client模式原理.png)



# yarn-cluster模式原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/yarn-cluster模式原理.png)


# yarn-client模式提交spark作业

## yarn运行spark作业的前提

如果想要让spark作业可以运行在yarn上面,那么首先就必须在spark-env.sh文件中,配置HADOOP_CONF_DIR或者YARN_CONF_DIR属性,值为hadoop的配置文件的目录,即:HADOOP_HOME/etc/hadoop,其中包含了hadoop和yarn所有的配置文件,比如:hdfs-site.xml,yarn-site.xml等,spark需要这些配置来读写HDFS,以及连接到yarn ResourceManager上,这个目录中包含的配置文件都会被分发到yarn集群中去的

vim spark/conf/spark-env.sh
```
export HADOOP_CONF_DIR=/usr/local/hadoop/etc/hadoop

/*在/usr/local/hadoop/etc/hadoop目录下有:
yarn-site.xml(其中可以找到ResourceManager所在的机器)
还有一些其他的配置文件
*/
```


跟spark standalone模式不同,通常不需要使用--master指定master URL
因为spark会从hadoop的配置文件中去读ResourceManager的配置,这样就知道了ResourceManager所在的机器(master),所以不需要我们指定,但是我们需要指定deploy mode,如下示例:
```
/export/servers/spark/bin/spark-submit \
--class cn.spark.study.core.WordCount \
--master yarn-cluster
#--master yarn-client
--num-executors 1 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 1 \
--queue hadoop队列
/usr/xx/spark-study-java-0.0.1-SNAPSHOT-jar-with-dependencies.jar \

```

--queue,在不同的部门,或者是不同的大数据项目,共用一个yarn集群,运行spark作业,推荐一定要用--queue,指定不同的hadoop队列,做项目或者部门之间的队列隔离



与Standalone模式类似,yarn-client模式通常建议在测试的时候使用,方便你直接在提交作业的机器上查看日志,但是作业实际部署到生产环境中进行运行的时候,还是使用yarn-cluster模式


yarn模式下需要观察的点:
1.日志
命令行日志
web ui日志

2.web ui的地址不再是spark://192.168.0.108:8080这种URL了,因为那是Standalone模式下的监控web ui,在yarn模式下,要看yarn的web ui: http://192.168.0.108:8088/ 这是yarn的URL地址

3.进程
driver是什么进程

AppLicationMaster是什么进程

executor进程


# yarn模式下的日志查看
在yarn模式下,spark作业运行相关的executor和ApplicationMaster都是运行在yarn的container中的,一个作业运行完了以后,yarn有两种方式来处理spark作业打印出的日志

1.聚合日志方式(推荐,比较常用)
这种格式将散落在集群中各个机器上的日志,最后都给聚合起来,让我们可以统一查看,如果yarn的日志聚合的选项打开了,即:yarn.log-aggregation-enable(yarn-site.xml文件中配置), container的日志会拷贝到HDFS上去,并从机器中删除

然后我们使用yarn logs -applicationId <app Id> 命令来查看日志(app Id在yarn的web ui上看:resourceManager_host:8088)

yarn logs命令,会打印出application对应的所有container的日志出来,当然,因为日志是在HDFS上的,我们自然可以通过HDFS的命令行来直接从HDFS中查看日志,日志在HDFS中的目录,可以通过查看yarn.nodemanager.remote-app-log-dir和yarn.nodemanager.remote-app-log-dir-suffix属性来获知



2.web ui
日志也可以通过spark web ui来查看executor的输出日志
但是此时需要启动History Server,需要让spark history server和mapreduce history server运行着;并且在yarn-site.xml文件中,配置yarn.log.server.url属性
spark history server web ui中的log url,会将你重新定向到mapreduce history server上去查看日志



3.分散查看(通常不推荐)
如果没有打开聚合日志选项,那么日志默认就是散落在各个机器上的本次磁盘目录中的,在YARN_APP_LOGS_DIR目录下,根据hadoop版本的不同,通常在/tmp/logs目录下,或者在$HADOOP_HOME/logs/userlogs目录下,如果你要查看某个container的日志,那么就得登录到那台机器上去,然后到指定的目录下如,找到那个日志文件,然后才能查看



# yarn模式相关的参数

```
yarn模式运行spark作业所有属性详解

属性名称											默认值							含义
spark.yarn.am.memory								512m							client模式下，YARN Application Master使用的内存总量
spark.yarn.am.cores									1								client模式下，Application Master使用的cpu数量
spark.driver.cores									1								cluster模式下，driver使用的cpu core数量，driver与Application Master运行在一个进程中，所以也控制了Application Master的cpu数量
spark.yarn.am.waitTime								100s							cluster模式下，Application Master要等待SparkContext初始化的时长; client模式下，application master等待driver来连接它的时长
spark.yarn.submit.file.replication					hdfs副本数						作业写到hdfs上的文件的副本数量，比如工程jar，依赖jar，配置文件等，最小一定是1
spark.yarn.preserve.staging.files					false							如果设置为true，那么在作业运行完之后，会避免工程jar等文件被删除掉
spark.yarn.scheduler.heartbeat.interval-ms			3000							application master向resourcemanager发送心跳的间隔，单位ms
spark.yarn.scheduler.initial-allocation.interval	200ms							application master在有pending住的container分配需求时，立即向resourcemanager发送心跳的间隔
spark.yarn.max.executor.failures					executor数量*2，最小3			整个作业判定为失败之前，executor最大的失败次数
spark.yarn.historyServer.address					无								spark history server的地址
spark.yarn.dist.archives							无								每个executor都要获取并放入工作目录的archive
spark.yarn.dist.files								无								每个executor都要放入的工作目录的文件
spark.executor.instances							2								默认的executor数量
spark.yarn.executor.memoryOverhead					executor内存10%					每个executor的堆外内存大小，用来存放诸如常量字符串等东西
spark.yarn.driver.memoryOverhead					driver内存7%					同上
spark.yarn.am.memoryOverhead						AM内存7%						同上
spark.yarn.am.port									随机							application master端口
spark.yarn.jar										无								spark jar文件的位置
spark.yarn.access.namenodes							无								spark作业能访问的hdfs namenode地址
spark.yarn.containerLauncherMaxThreads				25								application master能用来启动executor container的最大线程数量
spark.yarn.am.extraJavaOptions						无								application master的jvm参数
spark.yarn.am.extraLibraryPath						无								application master的额外库路径
spark.yarn.maxAppAttempts															提交spark作业最大的尝试次数
spark.yarn.submit.waitAppCompletion					true							cluster模式下，client是否等到作业运行完再退出


```

以上这些参数可以在spark-submit中配置,使用--conf配置




# spark-submit详解

spark-submit可以通过一个统一的接口,将spark应用程序提交到所有spark支持的集群管理器上(Standalone(mater),Yarn(ResourceManager)等),所以我们并不需要为每种集群管理器都做特殊的配置


--master
1.如果不设置,那么就是local模式
2.如果设置spark://开头的URL,那么就是Standalone模式,会提交到指定的URL的Mater进程上去
3.如果设置yarn-client/yarn-cluster,那么就是yarn模式,会读取hadoop配置文件,然后连接ResourceManager






