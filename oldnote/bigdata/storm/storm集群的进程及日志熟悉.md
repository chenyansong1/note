---
title: storm集群的进程及日志熟悉
categories: storm   
toc: true  
tag: [storm]
---




依次启动集群的各种角色:nimbus,supervisor,ui

查看nimbus的日志信息
在nimbus的服务器上
 
cd /export/servers/storm/logs
tail -100f /export/servers/storm/logs/nimbus.log
 


---


查看ui运行日志信息
在ui的服务器上，一般和nimbus一个服务器
 
cd /export/servers/storm/logs
tail -100f /export/servers/storm/logs/ui.log
 


---

查看supervisor运行日志信息
在supervisor服务上
 
cd /export/servers/storm/logs
tail -100f /export/servers/storm/logs/supervisor.log
 

---


查看supervisor上worker运行日志信息
在supervisor服务上
 
cd /export/servers/storm/logs
tail -100f /export/servers/storm/logs/worker-6702.log



---