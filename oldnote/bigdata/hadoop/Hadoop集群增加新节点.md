---
title: Hadoop集群增加新节点
categories: hadoop
toc: true
tag: [hadoop]
---


首先需要在新机器上配置slave,参见hadoop的集群安装中的slave部分

在master节点的slaves文件中增加新节点的主机名

在新节点手动启动
hadoop-daemon.sh start datanode
yarn-daemon.sh start nodemanager

在主节点
hdfs dfsadmin -refreshNodes
start-balancer.sh

通过web ui查看新节点是否增加
查看nameNode的web UI:namenode_host:50070
查看yarn的webUI:resouceManager_host:8088

