---
title: spark shell
categories: spark   
toc: true  
tag: [spark]
---


启动
```
spark-shell



```



使用一个shell脚本去提交程序

```
[root@hdp-node-01 install]# cat wordcount.sh 
/install/spark-1.6.1-bin-hadoop2.6/bin/spark-submit \
--class cn.... \
--num-executors 3 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 3 \
xxxx.jar


####上面的代码是本地模式运行,但是如果要提交到集群上需要加上--master
/install/spark-1.6.1-bin-hadoop2.6/bin/spark-submit \
--class cn.... \
--master spark://node1:7077 \
--num-executors 3 \
--driver-memory 100m \
--executor-memory 100m \
--executor-cores 3 \
xxxx.jar



```
