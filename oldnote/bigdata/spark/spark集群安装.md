---
title: spark集群安装
categories: spark  
tags: [spark]
---



# 1.安装
```
cd /export/servers/

#上传安装文件,解压
tar -zxvf spark-1.6.1-bin-hadoop2.6.tgz 
ln -s spark-1.6.1-bin-hadoop2.6/ spark        //创建软链接
//删除源文件,节省空间
rm -rf spark-1.6.1-bin-hadoop2.6.tgz 

#修改配置文件
cd spark/conf/
mv spark-env.sh.template spark-env.sh
vim spark-env.sh  //添加

export JAVA_HOME=/home/hadoop/app/jdk1.7.0_80
export SPARK_MASTER_IP=hdp-node-01            //配置master的机器
export SPARK_MASTER_PORT=7077

#######################################################
 
mv slaves.template slaves  
vim slaves      //添加worker的节点
 
hdp-node-01
hdp-node-02

 
#######################################################

#拷贝到其他节点
scp -r /export/servers/spark/ hdp-node-02:/export/servers/


#hdp-node-01 在启动
/export/servers/spark/sbin/start-all.sh 

```

 注意:

* 因为在mater所在的机器上要去启动其他的机器,所以在master所在的机器上要配置到其他机器的ssh , 这里省略了
* 需要关闭防火墙


#2.测试
```
#在mater上
[root@hdp-node-01 conf]# jps
1270 Jps
1159 Worker
1093 Master


#在worker上
[root@hdp-node-02 servers]# jps
1073 Worker
1107 Jps


```

# 3.浏览器访问
  

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark/install/1.png)

 
# 4.添加zk来解决master的单点问题

到此为止，Spark集群安装完毕，但是有一个很大的问题，那就是Master节点存在单点故障，要解决此问题，就要借助zookeeper，并且启动至少两个Master节点来实现高可靠，配置方式比较简单：
Spark集群规划：node1，node2是Master；node3，node4，node5是Worker  , 安装配置zk集群，并启动zk集群, 停止spark所有服务，修改配置文件spark-env.sh，在该配置文件中删掉SPARK_MASTER_IP并添加如下配置

```
export SPARK_DAEMON_JAVA_OPTS="-Dspark.deploy.recoveryMode=ZOOKEEPER -Dspark.deploy.zookeeper.url=zk1,zk2,zk3 -Dspark.deploy.zookeeper.dir=/spark"
```
1. 在node1节点上修改slaves配置文件内容指定worker节点
2. 在node1上执行sbin/start-all.sh脚本，然后在node2上执行sbin/start-master.sh启动第二个Master







