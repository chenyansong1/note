---
title: Kafka日志配置和清理
categories: kafka   
toc: true  
tag: [kafka]
---


# 1.日志设置

## 1.1、修改日志级别


config/log4j.properties中日志的级别设置的是TRACE，在长时间运行过程中产生的日志大小吓人，所以如果没有特殊需求，强烈建议将其更改成INFO级别。具体修改方法如下所示，将config/log4j.properties文件中最后的几行中的TRACE改成INFO，修改前如下所示：


```
log4j.logger.kafka.network.RequestChannel$=TRACE, requestAppender
log4j.additivity.kafka.network.RequestChannel$=false
#log4j.logger.kafka.network.Processor=TRACE, requestAppender
#log4j.logger.kafka.server.KafkaApis=TRACE, requestAppender
#log4j.additivity.kafka.server.KafkaApis=false
log4j.logger.kafka.request.logger=TRACE, requestAppender
log4j.additivity.kafka.request.logger=false
log4j.logger.kafka.controller=TRACE, controllerAppender
log4j.additivity.kafka.controller=false
log4j.logger.state.change.logger=TRACE, stateChangeAppender
log4j.additivity.state.change.logger=false
```

修改如下：

```
log4j.logger.kafka.network.RequestChannel$=INFO, requestAppender
log4j.additivity.kafka.network.RequestChannel$=false
#log4j.logger.kafka.network.Processor=INFO, requestAppender
#log4j.logger.kafka.server.KafkaApis=INFO, requestAppender
#log4j.additivity.kafka.server.KafkaApis=false
log4j.logger.kafka.request.logger=INFO, requestAppender
log4j.additivity.kafka.request.logger=false
log4j.logger.kafka.controller=INFO, controllerAppender
log4j.additivity.kafka.controller=false
log4j.logger.state.change.logger=INFO, stateChangeAppender
log4j.additivity.state.change.logger=false

```

## 1.2.修改log日志的位置

还有就是默认Kafka运行的时候都会通过log4j打印很多日志文件，比如server.log, controller.log, state-change.log等，而都会将其输出到$KAFKA_HOME/logs目录下，这样很不利于线上运维，因为经常容易出现打爆文件系统，一般安装的盘都比较小，而数据和日志会指定打到另一个或多个更大空间的分区盘

具体方法是，打开$KAFKA_HOME/bin/kafka-run-class.sh，找到下面标示的位置，并定义一个变量，指定的值为系统日志输出路径，重启broker即可生效。

```
# Log directory to use
if [ "x$LOG_DIR" = "x" ]; then
  LOG_DIR="$base_dir/logs"
fi

# Log4j settings
if [ -z "$KAFKA_LOG4J_OPTS" ]; then
  # Log to console. This is a tool.
  LOG4J_DIR="$base_dir/config/tools-log4j.properties"
  # If Cygwin is detected, LOG4J_DIR is converted to Windows format.
  (( CYGWIN )) && LOG4J_DIR=$(cygpath --path --mixed "${LOG4J_DIR}")
  KAFKA_LOG4J_OPTS="-Dlog4j.configuration=file:${LOG4J_DIR}"
else
  # create logs directory
  if [ ! -d "$LOG_DIR" ]; then
    mkdir -p "$LOG_DIR"
  fi
fi

# If Cygwin is detected, LOG_DIR is converted to Windows format.
(( CYGWIN )) && LOG_DIR=$(cygpath --path --mixed "${LOG_DIR}")
KAFKA_LOG4J_OPTS="-Dkafka.logs.dir=$LOG_DIR $KAFKA_LOG4J_OPTS"

```





参见：

http://openskill.cn/article/550

