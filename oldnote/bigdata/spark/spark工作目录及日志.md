[TOC]



# 工作目录说明



```shell
#executor 的  shuffle and RDD data ，这是一个临时的目录，默认是在tmp下

#vim $SPARK_HOME/conf/spark-env.sh 
export SPARK_LOCAL_DIRS=/home/hadoop/spark-2.0.2-bin-hadoop2.6/sparktmp

```



# 日志文件说明

```shell
https://blog.csdn.net/ZMC921/article/details/80238392

#excutor or driver的日志问题
https://blog.csdn.net/ZMC921/article/details/80238392
https://juejin.im/post/5b02934a51882542af043286


#以下是个提交的模板
[hadoop@spark ~]$ cat /home/workspace/spark.sh
#!/bin/sh

# 格式：应用名 —— 启动脚本

#======================日志===================

#SyslogAnalyzeServer
/home/hadoop/spark-2.0.2-bin-hadoop2.6/bin/spark-submit --supervise --class com.bluedon.kafka.SyslogAnalyzeServer  --master spark://spark:7077  --executor-memory 2G   --driver-memory 1G  --total-executor-cores 2 --executor-cores 2  --deploy-mode cluster --driver-java-options "-Dlog4j.configuration=file:/home/hadoop/spark-2.0.2-bin-hadoop2.6/conf/log4j-driver.properties" --conf spark.executor.extraJavaOptions="-Dlog4j.configuration=file:/home/hadoop/spark-2.0.2-bin-hadoop2.6/conf/log4j-executor.properties" /home/workspace/AnalyzeServer-Bigdata.jar

```



# master和work的pid目录

```shell
vim $saprk_home/conf/spark-env.sh
export SPARK_PID_DIR=/home/hadoop/spark-2.0.2-bin-hadoop2.6/sparktmp
```

