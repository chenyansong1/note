---
title: Storm常用shell操作命令
categories: storm   
toc: true  
tag: [storm]
---


有许多简单且有用的命令可以用来管理拓扑，它们可以提交、杀死、禁用、再平衡拓扑
```
#提交任务命令格式：
storm jar 【jar路径】 【拓扑包名.拓扑类名】 【拓扑名称】
bin/storm jar examples/storm-starter/storm-starter-topologies-0.9.6.jar storm.starter.WordCountTopology wordcount


#杀死任务命令格式：
storm kill 【拓扑名称】 -w 10（执行kill命令时可以通过-w [等待秒数]指定拓扑停用以后的等待时间）
storm kill topology-name -w 10


#停用任务命令格式：storm deactivte  【拓扑名称】
storm deactivte topology-name
#我们能够挂起或停用运行中的拓扑。当停用拓扑时，所有已分发的元组都会得到处理，但是spouts的nextTuple方法不会被调用。销毁一个拓扑，可以使用kill命令。它会以一种安全的方式销毁一个拓扑，首先停用拓扑，在等待拓扑消息的时间段内允许拓扑完成当前的数据流。


#启用任务命令格式：storm activate【拓扑名称】
storm activate topology-name


#重新部署任务命令格式：storm rebalance  【拓扑名称】
storm rebalance topology-name
#再平衡使你重分配集群任务。这是个很强大的命令。比如，你向一个运行中的集群增加了节点。再平衡命令将会停用拓扑，然后在相应超时时间之后重分配工人，并重启拓扑。

```