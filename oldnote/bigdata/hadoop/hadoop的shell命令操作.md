---
title: hadoop的shell命令操作
categories: hadoop
toc: true
tag: [hadoop]
---



---
**-ls**  
功能:显示目录信息 
示例: hadoop fs -ls hdfs://hadoop-server01:9000/  
备注：这些参数中，所有的hdfs路径都可以简写 
-->hadoop fs -ls /   等同于上一条命令的效果

---

**-mkdir**              
功能：在hdfs上创建目录，-p 递归创建 
示例：hadoop fs  -mkdir  -p  /aaa/bbb/cc/dd

<!--more-->
---
**-moveFromLocal**            
功能：从本地剪切粘贴到hdfs
示例：hadoop  fs  - moveFromLocal  /home/hadoop/a.txt  /aaa/bbb/cc/dd 


**-moveToLocal**              
功能：从hdfs剪切粘贴到本地 
示例：hadoop  fs  - moveToLocal   /aaa/bbb/cc/dd  /home/hadoop/a.txt

---

**-appendToFile**  
功能：追加一个文件到已经存在的文件末尾 
示例：hadoop  fs  -appendToFile  ./hello.txt  hdfs://hadoop-server01:9000/hello.txt 
可以简写为：Hadoop  fs  -appendToFile  ./hello.txt  /hello.txt 

---

**-cat**  
功能：显示文件内容  
示例：hadoop fs -cat  /hello.txt

** -tail**                  
功能：显示一个文件的末尾
示例：hadoop  fs  -tail  /weblog/access_log.1


** -text**                   
功能：以字符形式打印一个文件的内容
示例：hadoop  fs  -text  /weblog/access_log.1

---

** -chgrp** 
**-chmod**
**-chown**
功能：linux文件系统中的用法一样，对文件所属权限
示例：hadoop  fs  -chmod  666  /hello.txt
hadoop  fs  -chown  someuser:somegrp   /hello.txt


---

**-copyFromLocal**    
功能：从本地文件系统中拷贝文件到hdfs路径去
示例：hadoop  fs  -copyFromLocal  ./jdk.tar.gz  /aaa/


**-copyToLocal**      
功能：从hdfs拷贝到本地
示例：hadoop fs -copyToLocal /aaa/jdk.tar.gz



---

**-cp**              
功能：从hdfs的一个路径拷贝hdfs的另一个路径
示例： hadoop  fs  -cp  /aaa/jdk.tar.gz  /bbb/jdk.tar.gz.2 


**-mv**                     
功能：在hdfs目录中移动文件
示例： hadoop  fs  -mv  /aaa/jdk.tar.gz  /


---

**-get**              
功能：等同于copyToLocal，就是从hdfs下载文件到本地
示例：hadoop fs -get  /aaa/jdk.tar.gz

**-getmerge**             
功能：合并下载多个文件
示例：比如hdfs的目录 /aaa/下有多个文件:log.1, log.2,log.3,...
hadoop fs -getmerge /aaa/log.* ./log.sum


---

**-put**                
功能：等同于copyFromLocal
示例：hadoop  fs  -put  /aaa/jdk.tar.gz  /bbb/jdk.tar.gz.2


---

**-rm**                
功能：删除文件或文件夹
示例：hadoop fs -rm -r /aaa/bbb/ 
-r表示目录

**-rmdir**                 
功能：删除空目录
示例：hadoop  fs  -rmdir   /aaa/bbb/ccc


---

**-df**               
功能：统计文件系统的可用空间信息
示例：hadoop  fs  -df  -h  / 

**-du** 
功能：统计文件夹的大小信息
示例：
hadoop  fs  -du  -s  -h hadoop://mini:9000/*
hadoop  fs  -du  -s  -h /aaa/*


---

**-count**         
功能：统计一个指定目录下的文件节点数量
示例：hadoop fs -count /aaa/

---

**-setrep**                
功能：设置hdfs中文件的副本数量
示例：hadoop fs -setrep 3 /aaa/jdk.tar.gz
<这里设置的副本数只是记录在namenode的元数据中，是否真的会有这么多副本，还得看datanode的数量,如果datanode的数量为3，那么真实的副本数量只有3份，但是如果添加一个机器，那么机器数量就变成了4，此时副本数变成了4>

----


查看节点的详细信息（相当于web页面展示的信息）
**hdfs    dfsadmin    -report**

[root@hdp-node-01 hadoop-2.6.4]# hdfs dfsadmin -report 
Configured Capacity: 7718977536 (7.19 GB)
Present Capacity: 4682670080 (4.36 GB)
DFS Remaining: 4682625024 (4.36 GB)
DFS Used: 45056 (44 KB)
DFS Used%: 0.00%
Under replicated blocks: 1
Blocks with corrupt replicas: 0
Missing blocks: 0

Live datanodes (1): 

Name: 192.168.0.11:50010 (hdp-node-01)
Hostname: hdp-node-01
Decommission Status : Normal
Configured Capacity: 7718977536 (7.19 GB)
DFS Used: 45056 (44 KB)
Non DFS Used: 3036356608 (2.83 GB)
DFS Remaining: 4682575872 (4.36 GB)
DFS Used%: 0.00%
DFS Remaining%: 60.66%
Configured Cache Capacity: 0 (0 B)
Cache Used: 0 (0 B)
Cache Remaining: 0 (0 B)
Cache Used%: 100.00%
Cache Remaining%: 0.00%
Xceivers: 1
Last contact: Fri Nov 18 19:27:56 CST 2016