[TOC]





系统资源指标：metrics-server

自定义指标：prometheus， k8s-prometheus-adapter

新一代监控：

1. 核心指标流水线

   由kubectl, metrics-server,api-server提供的API组成：CPU(累计使用率)，内存实时使用率，Pod的资源占用率，容器的磁盘占用率

2. 监控流水线

   用于从系统收集各种指标数据并提供终端用户，存储系统以及HPA，他们包含核心指标及许多非核心指标，非核心指标本身不能被k8s所解析