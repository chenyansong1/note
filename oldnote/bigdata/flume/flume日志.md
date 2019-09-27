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

flume如果有多个slf4j的jar可能会jar包冲突，需要删除对应的jar

![1569549577348](E:\git-workspace\note\images\bigdata\flume\1569549577348.png)