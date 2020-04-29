---
title: zookeeper的ACL
categories: hadoop   
tags: [zookeeper]
---


# ACL组成
*	Scheme:id:permission 比如:world:anyone:crdwa
*	Scheme:验证过程中使用的检验策略
*	id:权限被赋予的对象,比如ip或者某个用户
*	permission为权限,上面的crdwa,表示5个权限组合
*	通过setAcl命令设置节点的权限
*	节点的acl不具有继承关系
*	getAcl可以查看节点的acl权限信息

```

[zk: localhost:2181(CONNECTED) 0] getAcl /student
'world,'anyone
: cdrwa

```

## Scheme类型--world
*	scheme:id:permission
*	id为固定值,anyone,表示任何用户
*	world:anyone:crdwa	表示任何用户都具有crdwa权限

```
[zk: localhost:2181(CONNECTED) 1] setAcl /student world:anyone:ca

[zk: localhost:2181(CONNECTED) 2] getAcl /student
'world,'anyone
: ca		#没有读权限
[zk: localhost:2181(CONNECTED) 3] get /student
Authentication is not valid : /student
```

## Scheme类型--auth
* Scheme:id:permisstion,比如:auth:username:password:crdwa
* 表示给认证通过的所有用户设置acl权限
* 同时可以添加多个用户
* 通过addauth命ј进行认证用户的添加:addauth digest <username>:<password>
* auth策略的本质就是digest
* 如果通过addauth创建多组用户和密码，当使用setAcl修改权限时，所有的用户和密码的权限都会跟着修改
* 通过addauth新创建的用户和密码组需要重新调用setAcl才会入到权限组中去

```
#创建节点
[zk: localhost:2181(CONNECTED) 4] create /node2 "node2data"
Created /node2

#获取默认的acl
[zk: localhost:2181(CONNECTED) 5] getAcl /node2
'world,'anyone
: cdrwa

#设置acl(不能设置,因为node2u:1111这样的用户是不存在的,所以验证不通过,即不能设置)
[zk: localhost:2181(CONNECTED) 6] setAcl /node2 auth:node2u:1111:crdwa
Acl is not valid : /node2

#添加用户
[zk: localhost:2181(CONNECTED) 7] addauth digest node2u:1111

#再次设置acl
[zk: localhost:2181(CONNECTED) 8] setAcl /node2 auth:node2u:1111:crdwa
cZxid = 0x2d
ctime = Thu Feb 23 21:52:48 CST 2017
mZxid = 0x2d
mtime = Thu Feb 23 21:52:48 CST 2017
pZxid = 0x2d
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 11
numChildren = 0
[zk: localhost:2181(CONNECTED) 9]



#在另外一个客户端上(因为本地没有验证用户)
[zk: localhost:2181(CONNECTED) 0] get /node2
Authentication is not valid : /node2

#添加验证用户
[zk: localhost:2181(CONNECTED) 3] addauth digest node2u:1111
[zk: localhost:2181(CONNECTED) 4] get /node2
"node2data"
cZxid = 0x2d
ctime = Thu Feb 23 21:52:48 CST 2017
mZxid = 0x2d
mtime = Thu Feb 23 21:52:48 CST 2017
pZxid = 0x2d
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 11
numChildren = 0
[zk: localhost:2181(CONNECTED) 5]

```



## Scheme类型--digest
* scheme:id:permission, 比如: digest:username:password:crdwa
* 指定某个用户及他的密码可以访问
* 此处的username:password必须进过SHA-1和BASE64编码
	* BASE64(SHA1(username:password))
* 通过addauth命令进行认证用户的添加
	* addauth digest <username>:<password>


## Scheme类型---IP
* Scheme:id:permission ，比如: ip:127.0.0.1:crdwa
* 指定某个IP地址可以访问

```
[zk: localhost:2181(CONNECTED) 8] create /node5 "node5data"
Created /node5

[zk: localhost:2181(CONNECTED) 9] setAcl /node5 ip:127.0.0.1:crdwa
cZxid = 0x34
ctime = Thu Feb 23 22:06:30 CST 2017
mZxid = 0x34
mtime = Thu Feb 23 22:06:30 CST 2017
pZxid = 0x34
cversion = 0
dataVersion = 0
aclVersion = 1
ephemeralOwner = 0x0
dataLength = 11
numChildren = 0
[zk: localhost:2181(CONNECTED) 10] get /node5	##这是为什么?????
Authentication is not valid : /node5

[zk: localhost:2181(CONNECTED) 11] setAcl ip:192.168.0.33:crdwa
[zk: localhost:2181(CONNECTED) 12] get /node5
Authentication is not valid : /node5
[zk: localhost:2181(CONNECTED) 13]
```




## Scheme类型---super
* 供运维人员维护节点使用
* 有权限操作任何节点
* 启动时,在命令参数中配置
	* -Dzookeeper.DigestAuthenticationProvider.superDigest=admin:015uTByzA4zSglcmseJsxTo7n3c=
	* 打开zkCli.cmd，在java命令后面增加以上配置
* 用户名和密码也需要通过sha1和base64编码