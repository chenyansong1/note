ganglia的原理图如下：


ganglia监控的指标：

* ganglia可以测量机器的一些基础指标：CPU，mem，disk，network，load，io，jvm
* ganglia可以对hadoop和hbase，包括spark做监控，他们对ganglia提供了支持
	* 对hadoop的监控可以到NameNode，DataNode，resourceManager级别
	* 对hbase的监控可以到regionServer,region级别（细化到表的region）
	* Spark在默认情况下不会将GangliSink编译进去（涉及到版权问题），如果需要使用ganglia监控spark，需要重新编译spark
* nagios可以和ganglia整合，而且只需要一个nagios监控节点即可，nagios从gmetad节点获取监控数据，然后报警

* 性能开销预算：对于单纯的Gmond节点来说，性能开销很低。主要的瓶颈在中央节点
* 

存在的问题：
* 指标太多，对具体的监控对象要采用哪些指标，比如对hbase监控，需要监控哪些指标不能确定（**很重要**)
* 告警流程框架：Ganglia本身并不具备，可以选用Nagios补充
* 日志管理框架：Ganglia本身并不具备，可以选用Splunk补充
* 




