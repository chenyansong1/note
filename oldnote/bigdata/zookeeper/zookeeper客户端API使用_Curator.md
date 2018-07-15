---
title: zookeeper开源客户端
categories: hadoop   
tags: [zookeeper]
---



[TOC]



# 1.原生api的不足
* 连接的创建是异步的，需要开发人员自行编码实现等待
* 连接没有自动的超时重连机制
* zk本省不提供序列化机制,需要开发人员自行制定,从而实现数据的序列化和反序列化
* watcher注册一次只会生效一次,需要不断的重新注册
* watcher的使用方式不合符java本身的术语,如果采用监听方式,将更容易理解
* 不支持递归创建树形节点




# 2.开源客户端--ZkClient介绍
Github上一个开源的zk客户端，由datameer的工程师Stefan Groschupf和Peter Voss一起开发

* 解决session会话超时重连
* watcher反复注册
* 简化开发API
* 其他
* github地址:https://github.com/sgroschupf/zkclient

特点:
* 简单
* 社区不活跃,连API文档都不完善

# 3.开源客户端---Curator介绍
Apache基金会的顶级项目之一
* 解决session会话超时重连
* Watcher反复注册
* 简化开发api
* 遵循Fluent风格Api规范
* NodeExistsException异常处理
* 大招:共享锁服务 master选举 分布式计数器等
* 其他
* http://curator.apache.org


# Curator---API
## 创建会话

```Java
CuratorFrameworkFactory.newClient(connectString, retryPolicy)
CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy)
	
#启动:start()方法
CuratorFrameworkFactory.newClient("localhost:2181,localhost:2182", retryPolicy).start();
			
```

参数|说明
-----|--------
connectString|逗՚分开的ip:port对
retryPolicy|重试策略，默认四种:<br/>Exponential BackoffRetry,RetryNTimes,RetryOneTime,RetryUntilElapsed
sessionTimeoutMs|会话超时时间，单位为毫秒，默认60000ms
connectionTimeoutMs|连接创建超时时间，单位为毫秒，默认是15000ms


> 重试策略

* 实现接口RetryPolicy可以自定义重试策略
```java
public abstract interface org.apache.curator.RetryPolicy {  
  public abstract boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper);
}
```

参数名|说明
-----|------
retryCount|已经重试的次数，如果第一次重试，此值为0
elapsedTimeMs|重试花费的时间，单位为毫秒
sleeper|类似于Thread.sleep,用于sleep指定时间
返回值|如果还会继续重试,则返回Ture

> 四种默认重试策略

* ExponentialBackoffRetry
	* ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries)
	* ExponentialBackoffRetry(int baseSleepTimeMs, int maxRetries, int maxSleepMs)
	* 当前应该sleep的时间: baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))


参数名|说明
-----|------
baseSleepTimeMs|初始化sleep时间
maxRetries|最大重试次数
maxSleepMs|最大重试时间
返回值|如果还会继续重试,则返回Ture

* RetryNTimes
	* RetryNTimes(int n, int sleepMsBetweenRetries)

参数名|说明
-----|------
n|最大重试次数
sleepMsBetweenRetries|每次重试的时间间隔

* RetryOneTime
	* 只重试一次
		 RetryOneTime(int sleepMsBetweenRetry)		#sleepMsBetweenRetry为重试间隔的时间


* RetryUntilElapsed
	* RetryUntilElapsed(int maxElapsedTimeMs, int sleepMsBetweenRetries)
	* 重试的时间超过最大时间后，就不再重试

参数名|说明
-----|------
maxElapsedTimeMs|最大重试时间
sleepMsBetweenRetries|每次间隔重试时间


## Fluent风格的API
* 定义:一种面向对象的开发方式，目的是提高代码的可读性
* 实现方式:通过方法的级联或者方法链的方式实现
* 举例

```java
private CuratorFramework client = null;
RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

//以方法链的方式
client = CuratorFrameworkFactory.builder()
		.connectString("localhost:2181,localhost:2182")
		.sessionTimeoutMs(10000).retryPolicy(retryPolicy)
		.namespace("base")// 这里指定初始的路径，如果我们去创建节点的时候，就会再此namespace的基础上进行创建
    	.build();

client.start();
```

## 创建节点

```java
 client.create()	//返回CreateBuilder
	   .creatingParentsIfNeeded()	//递归创建父目录
	   .withMode(CreateMode.PERSISTENT)//设置节点属性，比如:CreateMode.PERSISTENT，如果是递归创建模式为临时节点，则只有ՙ子节点是临时节点，非ՙ子节点都为持久节点
	   .withACL(Ids.OPEN_ACL_UNSAFE)//设置acl
       .forPath(path, data);//指定路径
```

## 删除节点

```
client.delete()	//返回DeleteBuilder
		.guaranteed()	//确保节点被删除
		.deletingChildrenIfNeeded()	//递归删除所有子节点
		.withVersion(version)	//特定版本՚号
		.inBackground(new DeleteCallBack())
		.forPath(path);	//指定路径
```

> 关于guaranteed：
Solves edge cases where an operation may succeed on the server but connection failure occurs before a response can be successfully returned to the client
意思是:解决当某个删除操作在服务器端可能成功，但是此时客户端与服务器端的连接中断，而删除的响应没有成功返回到客户端
底层的本质:重试

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/curator/1.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/curator/2.png)

* 关于异步操作

```
inBackground()
inBackground(Object context)
inBackground(BackgroundCallback callback)
inBackground(BackgroundCallback callback, Object context)
inBackground(BackgroundCallback callback, Executor executor)
inBackground(BackgroundCallback callback, Object context, Executor executor)

//从参数看跟zk的原生异࠵api相同，多了一个线程池，用于执行回调

```

* 异步操作回调

```
client.delete().guaranteed().deletingChildrenIfNeeded().
	withVersion(version).inBackground(new DeleteCallBack()).forPath(path);

----------------DeleteCallBack--------------------

public class DeleteCallBack implements BackgroundCallback {
	public void processResult(CuratorFramework client, CuratorEvent event)
			throws Exception {
		System.out.println(event.getPath()+",data="+event.getData());
		System.out.println("event type="+event.getType());
		System.out.println("event code="+event.getResultCode());
	}
}

```

异步操作事件状态:event.getType()

```
public final enum CuratorEventType {
  
  CREATE;
  DELETE;
  EXISTS;
  GET_DATA;
  SET_DATA;
  CHILDREN;
  SYNC;
  GET_ACL;
  SET_ACL;
  WATCHED;
  CLOSING;

}
```

异步操作事件状态码:event.getResultCode()

```Java
/*--------------org.apache.zookeeper.KeeperException.Code ---------*/

public static enum Code implements CodeDeprecated {
    /** Everything is OK */
    OK (Ok),

    /** System and server-side errors.
     * This is never thrown by the server, it shouldn't be used other than
     * to indicate a range. Specifically error codes greater than this
     * value, but lesser than {@link #APIERROR}, are system errors.
     */
    SYSTEMERROR (SystemError),

    /** A runtime inconsistency was found */
    RUNTIMEINCONSISTENCY (RuntimeInconsistency),
    /** A data inconsistency was found */
    DATAINCONSISTENCY (DataInconsistency),
    /** Connection to the server has been lost */
    CONNECTIONLOSS (ConnectionLoss),
    /** Error while marshalling or unmarshalling data */
    MARSHALLINGERROR (MarshallingError),
    /** Operation is unimplemented */
    UNIMPLEMENTED (Unimplemented),
    /** Operation timeout */
    OPERATIONTIMEOUT (OperationTimeout),
    /** Invalid arguments */
    BADARGUMENTS (BadArguments),
    
    /** API errors.
     * This is never thrown by the server, it shouldn't be used other than
     * to indicate a range. Specifically error codes greater than this
     * value are API errors (while values less than this indicate a
     * {@link #SYSTEMERROR}).
     */
    APIERROR (APIError),

    /** Node does not exist */
    NONODE (NoNode),
    /** Not authenticated */
    NOAUTH (NoAuth),
    /** Version conflict */
    BADVERSION (BadVersion),
    /** Ephemeral nodes may not have children */
    NOCHILDRENFOREPHEMERALS (NoChildrenForEphemerals),
    /** The node already exists */
    NODEEXISTS (NodeExists),
    /** The node has children */
    NOTEMPTY (NotEmpty),
    /** The session has been expired by the server */
    SESSIONEXPIRED (SessionExpired),
    /** Invalid callback specified */
    INVALIDCALLBACK (InvalidCallback),
    /** Invalid ACL specified */
    INVALIDACL (InvalidACL),
    /** Client authentication failed */
    AUTHFAILED (AuthFailed),
    /** Session moved to another server, so operation is ignored */
    SESSIONMOVED (-118),
    /** State-changing request is passed to read-only server */
    NOTREADONLY (-119);

}
```

在回调函数中可以打印错误状态码
```
public class DeleteCallBack implements BackgroundCallback {

	public void processResult(CuratorFramework client, CuratorEvent event)
			throws Exception {
		System.out.println(event.getPath()+",data="+event.getData());
		System.out.println("event type="+event.getType());
		System.out.println("event code="+event.getResultCode());
	}
}

//打印结果:
/,data=null
event type=DELETE
event code=-111		//-111表示有子节点，所以删除失败，只有为0(ok)表示删除成功
```


## 读取数据

```
public void readNode(String path) throws Exception {
	Stat stat = new Stat();
	byte[] data = client.getData().storingStatIn(stat).forPath(path);
	System.out.println("读取节点" + path + "的数据:" + new String(data));
	System.out.println(stat.toString());
}

/*
client.getData() 返回GetDataBuilder
storingStatIn(org.apache.zookeeper.data.Stat stat) //把服务器端获取的状态数据存储到stat对象
Byte[] forPathͧString pathͨ//节点路径
*/

```


## 更新数据

```Java
public void updateNode(String path, byte[] data, int version) throws Exception {
	client.setData().withVersion(version).forPath(path, data);
}

/*
client.setData() //返回SetDataBuilder
withVersion(version)	//特定版本号
forPath(path, data)	//节点路径和data
forPath(path)	//节点路径

*/

```


## 读取子节点

```Java
public void getChildren(String path) throws Exception {
	List<String> children = client.getChildren().usingWatcher(new WatcherTest()).forPath("/curator");
	for (String pth : children) {
		System.out.println("child=" + pth);
	}
}

/*
client.getChildren()	//返回GetChildrenBuilder
storingStatIn(org.apache.zookeeper.data.Stat stat) //把服务器端获取的状态数据存储到stat对象
Byte[] forPathͧString pathͨ//节点路径
usingWatcher(org.apache.zookeeper.Watcher watcher) //设置watcher，类似于zk本身的api，也只能使用一次
usingWatcher(CuratorWatcher watcher) //设置watcher ，类似于zk本身的api，也只能使用一次
*/
```

## 设置watcher
* watcher之NodeCache
	* 监听数据节点的内容变更
	* 监听节点的创建,即如果指定的节点不存在，则节点创建后，会触发这个监听

构造函数

```
NodeCache(CuratorFramework client, String path)
NodeCache(CuratorFramework client, String path, boolean dataIsCompressed)

/*
client 		客户端实例
path 		数据节点路径
dataIsCompressed  是否进行数据压缩
*/

```

回调接口
```
public interface NodeCacheListener
void nodeChanged() //没有参数，怎么获取事件信息以及节点数据？

```

实例代码
```Java
public void addNodeDataWatcher(String path) throws Exception {
	final NodeCache nodeC = new NodeCache(client, path);
	nodeC.start(true);

	nodeC.getListenable().addListener(new NodeCacheListener() {//回调接口
		public void nodeChanged() throws Exception {
			String data = new String(nodeC.getCurrentData().getData());//直接通过NodeCache获取数据
			System.out.println("path=" + nodeC.getCurrentData().getPath()
					+ ":data=" + data);
		}
	});
}
```

* watcher之PathChildrenCache
	* 监听指定节点的子节点变化情况
	* 包括:新增子节点,子节点数据变更和子节点删除

构造函数

```Java
PathChildrenCache(CuratorFramework client, String path, boolean cacheData)

PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, CloseableExecutorService executorService)

PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, ExecutorService executorService)

PathChildrenCache(CuratorFramework client, String path, boolean cacheData, boolean dataIsCompressed, ThreadFactory threadFactory)

PathChildrenCache(CuratorFramework client, String path, boolean cacheData, ThreadFactory threadFactory)

/*

*/

```

回调接口
```
interface PathChildrenCacheListener{
	void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
}
```

构造函数参数说明

|参数名|说明|
|------|----------|
|client|客户端实例|
|path|数据节点路径|
|dataIsCompressed|是否进行数据压缩|
|cacheData|用于配置是否把节点内容缓存起来，如果配置为true，那么客户端在接收到节点列表变更的同时，也能够获取到节点的数据内容ͺ如果为false,则无法取到数据内容|
|threadFactory|通过଑两个参数构造专门的线程池来处理事件通知|
|executorService||




实例代码
```java
public void addChildWatcher(String path) throws Exception {
	final PathChildrenCache cache = new PathChildrenCache(this.client, path, true);
	cache.start(StartMode.POST_INITIALIZED_EVENT);//ppt中需要讲StartMode
	System.out.println(cache.getCurrentData().size());
	
	cache.getListenable().addListener(new PathChildrenCacheListener() {
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
			if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
				System.out.println("客户端子节点cache初始化数据完成");
				System.out.println("size="+cache.getCurrentData().size());
			}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
				System.out.println("添加子节点:"+event.getData().getPath());
				System.out.println("修改子节点数据:"+new String(event.getData().getData()));
			}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
				System.out.println("删除子节点:"+event.getData().getPath());
			}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
				System.out.println("修改子节点数据:"+event.getData().getPath());
				System.out.println("修改子节点数据:"+new String(event.getData().getData()));
			}
		}
	});
}

/*
PathChildrenCache.StartMode	有:
BUILD_INITIAL_CACHE //同࠵初始化客户端的cache，及创建cache后，就从服务器端拉入对应的数据
NORMAL //异࠵初始化cache
POST_INITIALIZED_EVENT //异࠵初始化，初始化完成触发事件PathChildrenCacheEvent.Type.INITIALIZED
*/
```


![image-20180714170341107](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/watcher-listener.png)

```
PathChilidCache: 监听一个节点下子节点的创建，删除，更新
NodeCache：监听一个节点的更新和创建事件
TreeCache：综合PathChilidCache和NodeCache

# 通过向Cache中添加Listener的方式来设置
	1.创建cache
	2.创建listener(根据cache的类型，创建对应的listener)
	3.向cache中添加listener
```



