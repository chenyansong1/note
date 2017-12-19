$ bin/flume-ng agent --conf conf --conf-file example.conf  --name a1 -Dflume.root.logger=INFO,console

# -Dflume.root.logger=INFO,console 该参数将会把flume的日志输出到console,为了将其输出到日志文件(默认
在$FLUME_HOME/logs),可以将console改为LOGFILE形式,具体的配置可以修改$FLUME_HOME/conf/log4j.properties


实验没用，不知道为什么