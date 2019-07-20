[TOC]

资源：对象

* workload：Pod，controller（replicaSet, Deployment, StatefulSet, DaemonSet, Job, Cronjob)
* 服务发现及服务均衡：service， ingress
* 配置与存储相关的资源：Volume（云存储，本地分布式存储...）， CSI（容器存储接口）
  * ConfigMap
  * Secret:保存敏感数据
  * DownwardAPI
* 集群级别的资源
  * Namespace
  * Node
  * Role
  * ClusterRole
  * RoleBinding
  * ClusterRoleBinding
* 元数据型资源
  * HPA