---
title: storm执行Wordcount案例分析
categories: storm   
toc: true  
tag: [storm]
---


下面是从UI界面上对storm的各个组件的分布情况进行分析

<!--more-->
# 1.提交wordcount任务
我们提交了一个storm自带的wordcount程序
```
bin/storm jar examples/storm-starter/storm-starter-topologies-0.9.6.jar storm.starter.WordCountTopology wordcount
#wordcount 是拓扑的名称
```

# 2.浏览器观察
## 2.1.topology页面

我们可以看到topology,supervisor和nimbus的情况:

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/1.png)


## 2.2.topology详情页


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/2.png)



## 2.3.spout详情页

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/3.png)


## 2.4.bolt中split任务的详情页

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/4.png)


## 2.5.bolt中count任务的详情页

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/5.png)

## 2.6.总结

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/6.png)



# 3.代码分析
## 3.1.wordcount程序的不同版本实现

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/7.png)


## 3.2.wordcount程序中storm对spout、workers、split的设定

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount_ui/8.png)


其中spout、bolt、worker的数量的设定规则：
1. 根据上游的数据量来设置Spout的并发度
2. 根据业务复杂度和execute方法执行时间来设置Bolt并发度
3. 根据集群的可用资源配置，一般情况下70%的资源使用率
