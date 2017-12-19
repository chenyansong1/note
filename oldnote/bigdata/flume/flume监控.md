# http监控

flume提供了一个度量框架，可以通过http的方式进行展现，当启动agent的时候通过传递参数 -Dflume.monitoring.type=http参数给flume agent:

```
$ bin/flume-ng agent --conf conf --conf-file example.conf --name a1 
		-Dflume.monitoring.type=http 
		-Dflume.monitoring.port=5653 
		-Dflume.root.logger=INFO,console
```
这样flume会在5653端口上启动一个HTTP服务器，访问如下地址，
在浏览器访问：http://ip:5653/metrics

将返回JSON格式的flume相关指标参数:

```
# http://flume-agent-host:5653/metrics

结果: 其中src-1是子自定义的source名称
	{
	"SOURCE.src-1":{
		"OpenConnectionCount":"0",		//目前与客户端或sink保持连接的总数量(目前只有avro source展现该度量)
		"Type":"SOURCE",					
		"AppendBatchAcceptedCount":"1355",	//成功提交到channel的批次的总数量
		"AppendBatchReceivedCount":"1355",	//接收到事件批次的总数量
		"EventAcceptedCount":"28286",	//成功写出到channel的事件总数量，且source返回success给创建事件的sink或RPC客户端系统
		"AppendReceivedCount":"0",		//每批只有一个事件的事件总数量(与RPC调用中的一个append调用相等)
		"StopTime":"0",			//source停止时自Epoch以来的毫秒值时间
		"StartTime":"1442566410435",	//source启动时自Epoch以来的毫秒值时间
		"EventReceivedCount":"28286",	//目前为止source已经接收到的事件总数量
		"AppendAcceptedCount":"0"		//单独传入的事件到Channel且成功返回的事件总数量
	},
	"CHANNEL.ch-1":{
		"EventPutSuccessCount":"28286",	//成功写入channel且提交的事件总数量
		"ChannelFillPercentage":"0.0",	//channel满时的百分比
		"Type":"CHANNEL",
		"StopTime":"0",			//channel停止时自Epoch以来的毫秒值时间
		"EventPutAttemptCount":"28286",	//Source尝试写入Channe的事件总数量
		"ChannelSize":"0",			//目前channel中事件的总数量
		"StartTime":"1442566410326",	//channel启动时自Epoch以来的毫秒值时间
		"EventTakeSuccessCount":"28286",	//sink成功读取的事件的总数量
		"ChannelCapacity":"1000000",       //channel的容量
		"EventTakeAttemptCount":"313734329512" //sink尝试从channel拉取事件的总数量。这不意味着每次事件都被返回，因为sink拉取的时候channel可能没有任何数据
	},
	"SINK.sink-1":{
		"Type":"SINK",
		"ConnectionClosedCount":"0",	//下一阶段或存储系统关闭的连接数量(如在HDFS中关闭一个文件)
		"EventDrainSuccessCount":"28286",	//sink成功写出到存储的事件总数量
		"KafkaEventSendTimer":"482493",    
		"BatchCompleteCount":"0",		//与最大批量尺寸相等的批量的数量
		"ConnectionFailedCount":"0",	//下一阶段或存储系统由于错误关闭的连接数量（如HDFS上一个新创建的文件因为超时而关闭）
		"EventDrainAttemptCount":"0",	//sink尝试写出到存储的事件总数量
		"ConnectionCreatedCount":"0",	//下一个阶段或存储系统创建的连接数量（如HDFS创建一个新文件）
		"BatchEmptyCount":"0",		//空的批量的数量，如果数量很大表示souce写数据比sink清理数据慢速度慢很多
		"StopTime":"0",			
		"RollbackCount":"9",			//
		"StartTime":"1442566411897",
		"BatchUnderflowCount":"0"		//比sink配置使用的最大批量尺寸更小的批量的数量，如果该值很高也表示sink比souce更快
	}
	}

```


# ganglia监控

Flume也可发送度量信息给Ganglia，用来监控Flume。在任何时候只能启用一个Ganglia或HTTP监控。Flume默认一分钟一次周期性的向Ganglia报告度量:

```
$ bin/flume-ng agent --conf conf --conf-file example.conf --name a1 
		-Dflume.monitoring.type=ganglia  # 默认情况下flume以Ganglia3.1格式报告指标
		-Dflume.monitoring.pollFrequency=45 # 报告间隔时间(秒)
		-Dflume.monitoring.isGanglia3=true # 启用ganglia3个格式报告 
		-Dflume.root.logger=INFO,console

```
