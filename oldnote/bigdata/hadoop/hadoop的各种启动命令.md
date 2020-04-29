---
title: hadoop的各种启动命令
categories: hadoop
toc: true
tag: [hadoop]
---



# 1.格式化
## 1.1.启动的命令
```
hadoop namenode -format
```

<!--more-->

## 1.2.生成的目录
 
```
[root@hdp-node-01 hadoop-2.6.4]#  tree /home/hadoop/data/name/
/home/hadoop/data/name/
|-- current
|   |-- VERSION
|   |-- edits_0000000000000000001-0000000000000000002
|   |-- edits_0000000000000000003-0000000000000000010
|   |-- edits_0000000000000000011-0000000000000000012
|   |-- edits_0000000000000000013-0000000000000000014
|   |-- edits_0000000000000000015-0000000000000000016
|   |-- edits_0000000000000000017-0000000000000000018
|   |-- edits_0000000000000000019-0000000000000000020
|   |-- edits_inprogress_0000000000000000021
|   |-- fsimage_0000000000000000018
|   |-- fsimage_0000000000000000018.md5
|   |-- fsimage_0000000000000000020
|   |-- fsimage_0000000000000000020.md5
|   `-- seen_txid
`-- in_use.lock
 
1 directory, 15 files
 
```


# 2.手动启动namenode
## 2.1.启动的命令
```
hadoop-daemons.sh start namenode
```

## 2.2.生成的目录

namenode的文件目录是格式化的时候生成的，在：./etc/hadoop/hdfs-site.xml配置
```
<configuration>
        <property>
                <name>dfs.namenode.name.dir</name>
                <value>/home/hadoop/data/name</value>
        </property>
</configuration>
```

# 3.手动启动datanode
## 3.1.启动的命令
```
hadoop-daemons.sh start    datanode
```
 
## 3.2.生成的目录
在：./etc/hadoop/hdfs-site.xml配置
```
<configuration>
        <property>
                <name>dfs.datanode.data.dir</name>
                <value>/home/hadoop/data/data</value>
        </property>
</configuration>
```

启动datanode的时候会生成datanode文件目录
```
[root@hdp-node-01 hadoop-2.6.4]#  tree /home/hadoop/data/data/
/home/hadoop/data/data/
|-- current
|   |-- BP-1263609123-192.168.0.11-1479467214259
|   |   |-- current
|   |   |   |-- VERSION
|   |   |   |-- finalized
|   |   |   |   `-- subdir0
|   |   |   |       `-- subdir0
|   |   |   |           |-- blk_1073741825
|   |   |   |           `-- blk_1073741825_1001.meta
|   |   |   `-- rbw
|   |   |-- dncp_block_verification.log.curr
|   |   |-- dncp_block_verification.log.prev
|   |   `-- tmp
|   `-- VERSION
`-- in_use.lock
 
8 directories, 7 files
```




# 4.启动yarn
## 4.1.启动的命令
```
start-yarn.sh 
```

# 5.集群启动
```
start-dfs.sh            
#启动namenode和datanode

start-yarn.sh        
#启动yarn


#集群关闭
stop-yarn.sh
stop-dfs.sh
```
