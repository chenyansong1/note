---
title: storm集群安装部署
categories: storm   
toc: true  
tag: [storm]
---



# 1.环境准备
```
'修改hosts文件'
[root@hdp-node-01 bin]# vim    /etc/hosts

192.168.0.11    hdp-node-01 zk01 storm01
192.168.0.22    hdp-node-02 zk02 storm02
192.168.0.33    hdp-node-03 zk03 storm03
[root@hdp-node-01 bin]# 
 

'关闭防火墙和selinux'
chkconfig iptables off  && setenforce 0        

'创建用户'
groupadd realtime &&　useradd realtime　&& usermod -a -G realtime realtime

'创建工作目录并赋权'
mkdir /export/servers -p
chmod 755 -R /export

'切换到realtime用户下'
su realtime


'配置zookeeper的环境，并启动'
#这里就不介绍了，参见zookeeper文档

```

<!--more-->

# 2.规划表

hdp-node-01| (zk01/storm01)	|zookeeper|	nimbus|-
:---------:|:---------------:|:--------:|:----:|:------:
hdp-node-02| (zk01/storm02)	|zookeeper|	-	|supervisor
hdp-node-03| (zk01/storm03)	|zookeeper|	-	|supervisor


# 3.安装部署
```
'解压安装包，并创建软链接'
cd /export/servers/
tar -zxvf apache-storm-0.9.6.tar.gz 

ln -s apache-storm-0.9.6 storm
rm -f apache-storm-0.9.6.tar.gz 

'修改配置文件'
vim storm.yaml
cd storm/conf/
cp storm.yaml storm.yaml.bak		#修改前先备份


####################################################################################
#指定storm使用的zk集群
storm.zookeeper.servers:
     - "zk01"
     - "zk02"
     - "zk03"
#指定storm本地状态保存地址
storm.local.dir: "/export/data/storm/workdir"
#指定storm集群中的nimbus节点所在的服务器
nimbus.host: "storm01"
#指定nimbus启动JVM最大可用内存大小
nimbus.childopts: "-Xmx1024m"
#指定supervisor启动JVM最大可用内存大小
supervisor.childopts: "-Xmx1024m"
#指定supervisor节点上，每个worker启动JVM最大可用内存大小
worker.childopts: "-Xmx768m"
#指定ui启动JVM最大可用内存大小，ui服务一般与nimbus同在一个节点上。
ui.childopts: "-Xmx768m"
#指定supervisor节点上，启动worker时对应的端口号，每个端口对应槽，每个槽位对应一个worker
supervisor.slots.ports:
    - 6700
    - 6701
    - 6702
    - 6703
	
#####################################################################################	

'分发安装包'
#在其他机器上创建目录，并将storm拷贝到其他机器
mkdir /export/servers/ -p
scp -r /export/servers/storm/ hdp-node-02:/export/servers/
scp -r /export/servers/storm/ hdp-node-03:/export/servers/


```

# 4.启动
```

'启动zk'
zkServer.sh start		#启动zk，并查看状态
zkServer.sh status


'启动集群'
#在nimbus.host所属的机器上启动 nimbus服务
cd /export/servers/storm/bin/
nohup ./storm nimbus &
#在nimbus.host所属的机器上启动ui服务
cd /export/servers/storm/bin/
nohup ./storm ui &
#在其它个点击上启动supervisor服务
cd /export/servers/storm/bin/
nohup ./storm supervisor &

'查看所有的机器上的jps'
[root@hdp-node-01 bin]# jps
1155 nimbus            #nimbus
1097 QuorumPeerMain            #zk
1227 core                #ui
1470 Jps
[root@hdp-node-01 bin]# 

[root@hdp-node-02 bin]# jps
2567 QuorumPeerMain           #zk
2642 supervisor                #supervisor
3078 Jps
[root@hdp-node-02 bin]# 

[root@hdp-node-03 storm]# jps
3342 Jps
2631 supervisor                      #supervisor
1106 QuorumPeerMain             #zk
[root@hdp-node-03 storm]# 

```

# 5.测试

访问nimbus.host:8080，即可看到storm的ui界面
nimbus.host所在的主机的IP


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/install_test.png)
 


