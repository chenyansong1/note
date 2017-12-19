---
title: mapreduce程序运行模式
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---




# 1.本地运行模式（建议）
如果实在Windows下跑，需要修改Hadoop的bin目录和lib目录，因为Windows和linux中目录分隔符的表示不一样
在    F:\weizhi_data\Data\1327401579@qq.com\My Notes\大数据\Hadoop 中 <无jar版windows平台hadoop-2.6.1> 有Windows的Hadoop

<!--more-->


本地运行模式

（1）mapreduce程序是被提交给LocalJobRunner在本地以单进程的形式运行,其实是启动了多个线程
（2）而处理的数据及输出结果可以在本地文件系统，也可以在hdfs上
（3）怎样实现本地运行？写一个程序，不要带集群的配置文件（本质是你的mr程序的conf中是否有mapreduce.framework.name=local以及yarn.resourcemanager.hostname参数）
（4）本地模式非常便于进行业务逻辑的debug，只要在eclipse中打断点即可
 

```
        //是否运行为本地模式，就是看这个参数值是否为local，默认就是local
        /*conf.set("mapreduce.framework.name", "local");*/
        
        //本地模式运行mr程序时，输入输出的数据可以在本地，也可以在hdfs上
        //到底在哪里，就看以下两行配置你用哪行，默认就是file:///
        /*conf.set("fs.defaultFS", "hdfs://mini1:9000/");*/
        /*conf.set("fs.defaultFS", "file:///");*/  
```

如果在windows下想运行本地模式来测试程序逻辑，需要在windows中配置环境变量：
```
％HADOOP_HOME％  =  d:/hadoop-2.6.1
%PATH% =  ％HADOOP_HOME％\bin
```
并且要将d:/hadoop-2.6.1的lib和bin目录替换成windows平台编译的版本
 

# 2.集群运行模式


```
//运行集群模式，就是把程序提交到yarn中去运行
        //要想运行为集群模式，以下3个参数要指定为集群上的值
        /*conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "mini1");
        conf.set("fs.defaultFS", "hdfs://mini1:9000/");*/  
```











