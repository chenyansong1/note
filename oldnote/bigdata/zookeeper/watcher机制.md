---
title: watcher简介及简单示例
categories: hadoop   
tags: [zookeeper]
---

[TOC]



# watcher

* 问题

 1. 集群中有多个机器,当某个通用的配置发生变化后,怎么让所有服务器的配置都统一生效?
 2. 当某个集群节点宕机,其他节点怎么知道?
> zk中引入了watcher机制来实现发布/订阅功能,能够让多个订阅者同时监听某一个主题对象,当这个主题对象自身状态变化时,会通知所有的订阅者


* watcher组成
  ![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/watcher/1.png)
  * 客户端
  * 客户端watcherManager
  * zk服务器
* watcher机制
  1. 客户端向zk服务器注册watcher的同时,会将watcher对象存储在客户端的watcherManager
  2. zk服务器触发watcher事件后,回向客户端发送通知,客户端线程从watcherManager中调取watcher执行 






Watcher 监听机制是 Zookeeper 中非常重要的特性，我们 基于 zookeeper 上创建的节点，可以对这些节点绑定监听 事件，比如可以监听节点数据变更、节点删除、子节点状 态变更等事件，通过这个事件机制，可以基于 zookeeper 实现分布式锁、集群管理等功能 

watcher 特性:当数据发生变化的时候， zookeeper 会产 生一个 watcher 事件，并且会发送到客户端。但是**客户端只会收到一次通知**。如果后续这个节点再次发生变化，那 么之前设置 watcher 的客户端不会再次收到消息。 (watcher 是一次性的操作)。 可以通过循环监听去达到 永久监听效果 






* watcher接口

```
public class ZLock implements Watcher
public void process(WatcherEvent event)
```

* 事件类型
	* 通知状态:org.apache.zookeeper.Watcher.Event.KeeperState
	* 事件类型:org.apache.zookeeper.Watcher.Event.EventType



|keeperState|EventType|触发条件|说明|
|-|-|-|-|
|SyncConnected|None(-1)|客户端与服务器成功建立会话|此时客户端处于连接状态|
|SyncConnected|NodeCreated(1)|Watcher监听的对应数据节点被创建|此时客户端处于连接状态|
|SyncConnected|NodeDataChanged(3)|数据节点的数据内容发生变更|此时客户端处于连接状态|
|SyncConnected|NodeChildrenChanged(4)|被监控的数据节点的子节点列表发生变更|此时客户端处于连接状态|
|Disconnected(0)|None(-1)|客户端与zk服务器端口连接|此时客户端与服务器处于断开连接状态|
|Expired(-12)|None(-1)|会话超时|此时客户端会话失效,通常会收到SessionExpiredException异常|
|AuthFailed(4)|None(-1)|1.使用错误的scheme进行授权检查;2.SALA权限检查失败|通常会受到AuthFailedException异常|
|Unknown(-1)|||从3.0版本开始已经废弃|
|NoSyncConnected|



* NodeDataChanged事件
	* 无论节点数据发生变化还是数据版本发生变化都会触发
	* 即使被更新数据与新数据一样,数据版本都会发生变化
```
[zk: localhost:2181(CONNECTED) 6] get /student
"zhangsan"
cZxid = 0x3
ctime = Thu Feb 23 17:29:37 CST 2017
mZxid = 0x3
mtime = Thu Feb 23 17:29:37 CST 2017
pZxid = 0x3
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 10
numChildren = 0

#重新以相同的内容设置节点
[zk: localhost:2181(CONNECTED) 7] set /student "zhangsan"
cZxid = 0x3
ctime = Thu Feb 23 17:29:37 CST 2017
mZxid = 0x4
mtime = Thu Feb 23 17:31:12 CST 2017
pZxid = 0x3
cversion = 0
dataVersion = 1		#数据版本发生了变化
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 10
numChildren = 0

```

* NodeChildrenChanged
	* 新增节点或者删除节点
* AuthFailed
	* 重点不是客户端会话没有权限而是授权失败
> 客户端只能收到相关事件的通知,但是并不能获取对应数据节点的原始数据内容以及变更之后新数据内容,因此,如果业务需要知道变更前的数据或者是变更收的数据,则需要业务保存变更前的数据和调用接口获取新的数据

* watcher注册
	* 创建zk客户端对象实例时注册
```java
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)

/*
通过上述方式注册的watcher将会作为整个zk会话期间的默认watcher,会被一直保存在客户端ZKWatchManager的defaultWatcher中,如果有其他的设置,则这个watcher会被覆盖
*/
```
	* 其他注册API
```java
getChildren(String path, Watcher watcher)

getChildren(String path, boolean watch)	
	#Boolean watch 表示是否使用上下文中默认的watcher,即创建zk实例时设置的watcher
	#The watch willbe triggered by a successful operation that deletes the node of the given path or creates/delete a child under the node. 

getData(String path, bolean watch, Stat stat)
	#Boolean watch 表示是否使用上下文中默认的watcher,即创建zk实例时设置的watcher
	#The watch will be triggered by a successful operation that sets data on the node, or deletes the node. 

getData(String path, Watcher watcher, AsyncCallback.DataCallback cb, Objcet ctx)

exists(String path, boolean watch)
	#Boolean watch 表示是否使用上下文中默认的watcher,即创建zk实例时设置的watcher
	#The watch will be triggered by a successful operation that creates/delete the node or sets the data on the node.

exists(String path, Watcher watcher)

```


# 事件的实现原理 



![image-20180711213506467](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/watcher1.png)



![image-20180711213543007](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/watcher2.png)



watcher设置后,一旦触发一次即会失效,如果需要一直监听,就需要再注册(很重要)

下面用代码来说明:
```java
#Watcher

public class WatcherExample1 implements Watcher {
	private ZooKeeper  zk = null;
	@Override
	public void process(WatchedEvent event) {#watcher事件触发后的处理动作
		System.out.println("watcher="+this.getClass().getName());
		System.out.println("path="+event.getPath());
		System.out.println("eventType="+event.getType().name());
	}
	public ZooKeeper getZk() {
		return zk;
	}
	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}
}
-----------------------------------

#注册watcher
public class WatcherRegister {
	private ZooKeeper  zk = null;
	public WatcherRegister(String connectString,Watcher watcher) {
		try {
			zk = new ZooKeeper(connectString,10000,watcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void testWatcherdisabled(String path) throws KeeperException, InterruptedException{
		WatcherExample1 we1 = new WatcherExample1();
		we1.setZk(zk);
		//向getData上注册一个watcher
		zk.getData(path, we1, null);
	}
	public static void main(String[] args) {
		WatcherExample we = new WatcherExample();
		//注册watcher
		WatcherRegister wr = new WatcherRegister("localhost:2181",we);
		try {
			wr.testWatcherdisabled("/student");
			Thread.sleep(300000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


```
启动执行
```
watcher=com.zk.example.watcher.WatcherExample
path=null
eventType=None
```
执行: set /student "zhangsan3" 打印下面的结果
```
watcher=com.zk.example.watcher.WatcherExample1
path=/student
eventType=NodeDataChanged


```
再次执行: set /student "zhangsan3" 没有打印
说明:watcher设置后,一旦触发一次即会失效,如果需要一直监听,就需要再注册


下面是触发一次watcher之后,重新注册的例子
```java
#watcher
public class WatcherExample1 implements Watcher {
	private ZooKeeper  zk = null;
	@Override
	public void process(WatchedEvent event) {
		System.out.println("watcher="+this.getClass().getName());
		System.out.println("path="+event.getPath());
		System.out.println("eventType="+event.getType().name());
		try {
			//在一次触发watcher之后,重新设置watcher
			WatcherExample1 we1 = new WatcherExample1();
			we1.setZk(zk);
			zk.getData(event.getPath(), we1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ZooKeeper getZk() {
		return zk;
	}
	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}
}

---------------------------------------------


# 注册watcher

public class WatcherRegister {
	private ZooKeeper  zk = null;
	public WatcherRegister(String connectString,Watcher watcher) {
		try {
			zk = new ZooKeeper(connectString,10000,watcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void testWatcherdisabled(String path) throws KeeperException, InterruptedException{
		WatcherExample1 we1 = new WatcherExample1();
		we1.setZk(zk);
		zk.getData(path, we1, null);
	}
	public static void main(String[] args) {
		WatcherExample we = new WatcherExample();
		WatcherRegister wr = new WatcherRegister("localhost:2181",we);
		try {
			wr.testWatcherdisabled("/student");
			Thread.sleep(300000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```

启动
```
watcher=com.zk.example.watcher.WatcherExample
path=null
eventType=None
```

修改:  set /student "zhangsan3"
```
watcher=com.zk.example.watcher.WatcherExample1
path=/student
eventType=NodeDataChanged
```

再次修改:  set /student "zhangsan3" (** 这里需要说明的是修改此次修改数据的内容还是"zhangsan3",但是数据的版本是被改变了,所以数据节点还是被改变了 **)

```
watcher=com.zk.example.watcher.WatcherExample1
path=/student
eventType=NodeDataChanged
```