---
title: zookeeper命令行简单的CRUD命令
categories: hadoop   
tags: [zookeeper]
---


# 1.登录客户端
在启动Zookeeper服务之后，输入以下命令，连接到Zookeeper服务：
```
zkCli.sh -server localhost:2181   #连接的是localhost：2181服务
```

# 2.查看客户端帮助信息
```
[zk: localhost:2181(CONNECTED) 0] help
ZooKeeper -server host:port cmd args
        stat path [watch]
        set path data [version]
        ls path [watch]
        delquota [-n|-b] path
        ls2 path [watch]
        setAcl path acl
        setquota -n|-b val path
        history
        redo cmdno
        printwatches on|off
        delete path [version]
        sync path
        listquota path
        rmr path
        get path [watch]
        create [-s] [-e] path data acl
        addauth scheme auth
        quit
        getAcl path
        close
        connect host:port
[zk: localhost:2181(CONNECTED) 1] 

```
# 3.简单的CRUD客户端操作命令
在敲客户端命令的时候，可以使用tab键来补全
## 3.1.查看节点
```
[zk: localhost:2181(CONNECTED) 1] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 2]
```



## 3.2.创建节点
create [-s] [-e] path data acl
1. 创建zookeeper节点
2. -s或者-e表示创建的是顺序或临时节点，不加默认创建的是持久节点
3. Path为节点的全路径，没有相对节点的表示方式
4. data位当前创建节点内存储的数据
5. acl 用来进行权限控制，缺省情况下不做任何权限控制

```
[zk: localhost:2181(CONNECTED) 14] create /test "te_data"
Created /test

//查看
[zk: localhost:2181(CONNECTED) 8] ls /
[zookeeper, test]
[zk: localhost:2181(CONNECTED) 9] 

#创建顺序节点：会在节点的名字后面添加一系列数字
[zk: localhost:2181(CONNECTED) 21] create -s /name "zhangsan"
Created /name0000000003
[zk: localhost:2181(CONNECTED) 21] create -s /name "zhangsan2"            #此时可以重复指定节点名字，因为最终要在节点名后添加序号的，所以名字最终还是唯一的
Created /name0000000004
[zk: localhost:2181(CONNECTED) 22] ls /
[zookeeper, name0000000003, name0000000004]
[zk: localhost:2181(CONNECTED) 23] 


#创建临时节点，在会话退出后，节点将会被删除
[zk: localhost:2181(CONNECTED) 1] create -e /age 21
Created /age
[zk: localhost:2181(CONNECTED) 2] ls /
[zookeeper, age, name0000000003]
#可以退出之后在登录客户端，ls    /    查看



```

## 3.3.查看节点数据
```
 [zk: localhost:2181(CONNECTED) 15] get /test
"te_data"
cZxid = 0x10000000b
ctime = Sun Nov 06 22:53:59 CST 2016
mZxid = 0x10000000b
mtime = Sun Nov 06 22:53:59 CST 2016
pZxid = 0x10000000b
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 9
numChildren = 0
 
```

## 3.4.修改节点数据
```
[zk: localhost:2181(CONNECTED) 16] set /test "update_test"
cZxid = 0x10000000b
ctime = Sun Nov 06 22:53:59 CST 2016
mZxid = 0x10000000c
mtime = Sun Nov 06 22:54:47 CST 2016
pZxid = 0x10000000b
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 13
numChildren = 0

//查看
[zk: localhost:2181(CONNECTED) 17] get /test
"update_test"                            #修改之后的数据
cZxid = 0x10000000b
ctime = Sun Nov 06 22:53:59 CST 2016
mZxid = 0x10000000c
mtime = Sun Nov 06 22:54:47 CST 2016
pZxid = 0x10000000b
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 13
numChildren = 0
[zk: localhost:2181(CONNECTED) 18] 
```
## 3.5.删除节点
```
//删除前查看
[zk: localhost:2181(CONNECTED) 18] ls /
[zookeeper, test]

#删除
[zk: localhost:2181(CONNECTED) 19] delete /test

#删除后查看
[zk: localhost:2181(CONNECTED) 20] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 21] 
```


