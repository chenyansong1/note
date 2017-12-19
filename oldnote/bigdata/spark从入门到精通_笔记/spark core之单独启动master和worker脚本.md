---
title: spark core之单独启动master和worker脚本
categories: spark   
toc: true  
tag: [spark]
---



# 单独启动master和worker脚本

sbin/start-all.sh脚本可以直接启动集群中的master进程和worker进程


这里讲的是单独启动master和worker进程

因为worker进程启动之后,会向master进程去注册,所以需要先启动master进程


为什么有的时候要单独启动master和worker进程?
因为可以通过命令行参数为进程配置一些独特的参数,如监听的端口号,web ui 的端口号,使用的cpu和内存等,比如:你可能向单独给某个节点配置不同的cpu和内存资源的使用限制,那么就可以使用脚本单独启动worker进程的时候,通过命令行参数来设置

手动启动master进程
需要在某个部署了spark安装包的节点上,使用sbin/start-master.sh启动,master启动之后,启动日志就会打印一行spark://HOST:PORT出来,这就是master的url地址,worker进程就会通过这个地址来连接到master进程,并且进行注册

另外，除了worker进程要使用这个URL以外，我们自己在编写spark代码时，也可以给SparkContext的setMaster()方法，传入这个URL地址
然后我们的spark作业，就会使用standalone模式连接master，并提交作业

此外，还可以通过http://MASTER_HOST:8080 URL来访问master集群的监控web ui，那个web ui上，也会显示master的URL地址



手动启动worker进程
在部署了spark安装包的前提下,在你希望作为worker node的节点上,使用sbin/start-slave.sh <master-spark-url>在当前节点上启动,启动worker的时候需要指定master的url

启动worker进程之后,再访问:http:MASTER_HOST:8080,在集群web ui上,就可以看到新启动的节点,包括该节点的cpu和内存资源

此外,以下参数是可以在手动启动master和worker的时候指定的:
-h host, --host host	 在哪台机器上启动,默认都是本机
-p port, --port port 在机器上启动后,使用哪个端口对外提供服务,master默认是7077,worker默认是随机的
--webui-port port  web ui端口,master默认的是8080,worker默认的是8081
-c cores, --cores cores 仅限于worker,总共能让spark Application使用多少个cpu core,默认是当前机器上的所有的cpu core
-m mem, --memory mem  仅限于worker,总共能让spark Application使用多少内存,是100M或者1G这样的格式
-d dir, --worker-dir dir	仅限于worker,工作目录,默认是spark home/work目录
--properties-file file master和worker加载配置文件的地址,默认是spark_home/conf/spark-defaults.conf

咱们举个例子，比如说小公司里面，物理集群可能就一套，同一台机器上面，可能要部署Storm的supervisor进程，可能还要同时部署Spark的worker进程机器，cpu和内存，既要供storm使用，还要供spark使用
这个时候，可能你就需要限制一下worker节点能够使用的cpu和内存的数量

小公司里面，搭建spark集群的机器可能还不太一样，有的机器比如说是有5个g内存，有的机器才1个g内存那你对于1个g内存的机器，是不是得限制一下内存使用量，比如说500m

实例:
1、启动master: 日志和web ui，观察master url

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/单独启动master.png)


2、启动worker: 观察web ui，是否有新加入的worker节点，以及对应的信息

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/单独启动worker.png)


3、单独关闭master和worker,此时的顺序得反过来,先关闭worker,再去关闭master

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/stop_worker_master.png)






4、再次单独启动master和worker，给worker限定，就使用500m内存，跟之前看到的worker信息比对一下内存最大使用量

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/worker_allocate_memory.png)


