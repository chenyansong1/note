---
title: 基于Yarn的两种提交模式
categories: spark  
tags: [spark]
---



spark的三种提交模式
1.Standalone模式,基于spark自己的Master-Worker集群
2.基于Yarn的yarn-cluster模式
3.基于Yarn的yarn-client模式

在我们之前提交的spark应用程序的spark-submit脚本,加上--master参数,设置yarn-cluster,或yarn-client,即可,如果你没有设置,那么就是Standalone模式,同时需要在spark-env.sh中补充配置
```
export HADOOP_HOME=XXXXX

```



yarn-cluster和yarn-client的提交模式

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/基于yarn的两种提交模式.png)















