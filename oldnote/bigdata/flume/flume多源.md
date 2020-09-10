[toc]



以下代码主要演示：多个sources的情况

```shell
# Name the components on this agent
a1.sources = r1 r2
a1.sinks = k1
a1.channels = c1


a1.sources.r2.type = exec
a1.sources.r2.command = tail -f /PATH/bper-peg-pt-rest.log

a1.sources.r1.type = exec
a1.sources.r1.command = tail -f /PATH/bper-peg-ejb.log


# Describe the sink
a1.sinks.k1.type = file_roll
a1.sinks.k1.sink.directory = /home/vbsc/Desktop/flume_project_logging/logs_aggregated
a1.sinks.k1.sink.rollInterval = 0

# Use file channel
a1.channels.c1.type = file

# Bind the source and sink to the channel
a1.sinks.k1.channel = c1
a1.sources.r2.channels = c1
a1.sources.r1.channels = c1

```





