$ bin/flume-ng agent --conf conf --conf-file example.conf  --name a1 -Dflume.root.logger=INFO,console

# -Dflume.root.logger=INFO,console 该参数将会把flume的日志输出到console,为了将其输出到日志文件(默认
在$FLUME_HOME/logs),可以将console改为LOGFILE形式,具体的配置可以修改$FLUME_HOME/conf/log4j.properties

实验没用，不知道为什么



参考：https://blog.csdn.net/tiantang_1986/article/details/49996711

```shell
#让日志回滚，只需要配置它的类型及文件大小（MaxFileSize--默认是10M）、文件数（MaxBackupIndex--默认是1）,如：
log4j.appender.LOGFILE=org.apache.log4j.RollingFileAppender
log4j.appender.LOGFILE.MaxFileSize=2MB
log4j.appender.LOGFILE.MaxBackupIndex=5
log4j.appender.LOGFILE.File=${flume.log.dir}/${flume.log.file}
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{yyyy-MM-dd HH\:mm\:ss.SSS} %-5p [%t] (%C.%M:%L) %x - %m%n
```

![1569493292636](E:\git-workspace\note\images\bigdata\flume\1569493292636.png)

flume如果有多个slf4j的jar可能会jar包冲突，需要删除对应的jar，例如删除hbase中的一个slf4j就可以了

![1569549577348](E:\git-workspace\note\images\bigdata\flume\1569549577348.png)





下面是flume中一份完整的配置

```shell
[root@master apache-flume-1.8.0-bin]# cat  conf/log4j.properties    
#
# For testing, it may also be convenient to specify
# -Dflume.root.logger=DEBUG,console when launching flume.

#flume.root.logger=DEBUG,console
#flume.root.logger=INFO,LOGFILE,ERROR
flume.root.logger=LOGFILE,ERROR
flume.log.dir=./apache-flume-1.8.0-bin/logs
flume.log.file=flume.log

log4j.logger.org.apache.flume.lifecycle = INFO
log4j.logger.org.jboss = WARN
log4j.logger.org.mortbay = INFO
log4j.logger.org.apache.avro.ipc.NettyTransceiver = WARN
log4j.logger.org.apache.hadoop = INFO
log4j.logger.org.apache.hadoop.hive = ERROR

# Define the root logger to the system property "flume.root.logger".
log4j.rootLogger=${flume.root.logger}

#ERROR
log4j.appender.ERROR=org.apache.log4j.RollingFileAppender
log4j.appender.ERROR.MaxFileSize=50MB
log4j.appender.ERROR.MaxBackupIndex=5
log4j.appender.ERROR.File=${flume.log.dir}/run_ERROR.log   
log4j.appender.ERROR.Append=true   
log4j.appender.ERROR.Threshold =ERROR
log4j.appender.ERROR.layout=org.apache.log4j.PatternLayout   
log4j.appender.ERROR.layout.ConversionPattern=[ %p ]  %-d{yyyy-MM-dd HH:mm:ss.SSS} [%F:%L]  %m%n


# Stock log4j rolling file appender
# Default log rotation configuration
log4j.appender.LOGFILE=org.apache.log4j.RollingFileAppender
log4j.appender.LOGFILE.MaxFileSize=100MB
log4j.appender.LOGFILE.MaxBackupIndex=10
log4j.appender.LOGFILE.File=${flume.log.dir}/${flume.log.file}
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{dd MMM yyyy HH:mm:ss,SSS} %-5p [%t] (%C.%M:%L) %x - %m%n


# Warning: If you enable the following appender it will fill up your disk if you don't have a cleanup job!
# This uses the updated rolling file appender from log4j-extras that supports a reliable time-based rolling policy.
# See http://logging.apache.org/log4j/companions/extras/apidocs/org/apache/log4j/rolling/TimeBasedRollingPolicy.html
# Add "DAILY" to flume.root.logger above if you want to use this
log4j.appender.DAILY=org.apache.log4j.rolling.RollingFileAppender
log4j.appender.DAILY.rollingPolicy=org.apache.log4j.rolling.TimeBasedRollingPolicy
log4j.appender.DAILY.rollingPolicy.ActiveFileName=${flume.log.dir}/${flume.log.file}
log4j.appender.DAILY.rollingPolicy.FileNamePattern=${flume.log.dir}/${flume.log.file}.%d{yyyy-MM-dd}
log4j.appender.DAILY.layout=org.apache.log4j.PatternLayout
log4j.appender.DAILY.layout.ConversionPattern=%d{dd MMM yyyy HH:mm:ss,SSS} %-5p [%t] (%C.%M:%L) %x - %m%n


# console
# Add "console" to flume.root.logger above if you want to use this
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d (%t) [%p - %l] %m%n
[root@master apache-flume-1.8.0-bin]#
```

