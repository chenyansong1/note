---
title: 第3章 SparkContext的初始化
categories: spark   
toc: true  
tag: [spark]
---


SparkContext的初始化是Driver应用程序提交执行的前提,本章内容以local模式为主,并按照代码执行顺序讲解

<!--more-->


# 1.SparkContext概述

Spark Driver用于提交用户应用程序,实际可以看做Spark的客户端,Spark Driver的初始化始终围绕着SparkContext的初始化,SparkContext可以算得上是所有Spark应用程序的发动机引擎,轿车要想跑起来,发送机首先要启动,SparkContext初始化完毕,才能向Spark集群提交任务,在平坦的公路上,发送机只需要以较低的转速,较低的功率就可以游刃有余,而在山区中,可能需要一台能够提供大功率的发动机才能满足你的需求,这些参数都是通过驾驶员操作油门,档位等传送给发送机的,而SparkContext的配置参数则由SparkConf负责,SparkConf就是你的操作面板

SparkConf的构造很简单,主要是通过ConcurrentHashMap来维护各种Spark的配置属性,SparkConf代码结构如下,spark的配置属性都是以"spark."开头的字符串

```
class SparkConf(loadDefaults: Boolean) extends Cloneable with Logging {

  import SparkConf._

  /** Create a SparkConf that loads defaults from system properties and the classpath */
  def this() = this(true)

  private val settings = new ConcurrentHashMap[String, String]()

  if (loadDefaults) {
    //加载任何以spark.开头的系统属性
    for ((key, value) <- Utils.getSystemProperties if key.startsWith("spark.")) {
      set(key, value)
    }
  }
  //其余代码省略 
}


```

下面是SparkContext的初始化步骤:
1.创建Spark执行环境SparkEnv
2.创建RDD清理器metadataCleaner
3.创建并初始化SparkUI
4.Hadoop相关配置文件及Executor环境变量的设置
5.创建任务调度TaskScheduler
6.创建和启动DAGScheduler
7.TaskScheduler的启动
8.初始化块管理器BlockManager(BlockManager是存储系统的主要组件之一,后面介绍)
9.启动测量系统MetricsSystem
10.创建和启动Executor分配管理器ExecutorAllocationManager
11.ContextCleaner的创建与启动
12.Spark环境更新
13.创建DAGSchedulerSource和BlockManagerSource
14.将SparkContext标记为激活


```
class SparkContext(config: SparkConf) extends Logging with ExecutorAllocationClient {

  // The call site where this SparkContext was constructed.
  private val creationSite: CallSite = Utils.getCallSite()

  // If true, log warnings instead of throwing exceptions when multiple SparkContexts are active
  private val allowMultipleContexts: Boolean =
    config.getBoolean("spark.driver.allowMultipleContexts", false)

  // In order to prevent multiple SparkContexts from being active at the same time, mark this
  // context as having started construction.
  // NOTE: this must be placed at the beginning of the SparkContext constructor.
  SparkContext.markPartiallyConstructed(this, allowMultipleContexts)
  
  //省略代码...
 }

```

上面的代码中:CallSite存储了线程栈中最靠近栈顶的用户类及最靠近栈低的scala或者spark核心类信息   
SparkContext默认只有一个实例(有属性spark.driver.allowMultipleContexts来控制,用户需要多个SparkContext实例时,可以将其设置为true),方法markPartiallyConstructed用来确保实例的唯一性,并将当前SparkContext标记为正在构建中


接下来对SparkConf进行复制,然后对各种配置信息进行校验,代码如下:
```
_conf = config.clone()
_conf.validateSettings()

if (!_conf.contains("spark.master")) {
  throw new SparkException("A master URL must be set in your configuration")
}
if (!_conf.contains("spark.app.name")) {
  throw new SparkException("An application name must be set in your configuration")
}


```

从上面的代码看到必须制定属性spark.master和spark.app.name,否则会抛出异常,结束初始化过程,spark.master用于设置部署模式,spark.app.name用于指定应用程序名称




# 2.创建执行环境SparkEnv

SparkEnv是Spark的执行环境对象,其中包括众多与Executor执行相关的对象,由于在local模式下Driver会创建Executor,local-cluster部署模式或者Standalone部署模式下Worker另起的CoarseGrainedExecutorBackend进行中也会创建Executor,所以SparkEnv存在于Driver或者是CoarseGrainedExecutorBackend进程中,创建SparkEnv主要使用SparkEnv的createDriverEnv,SparkEnv.createDriverEnv方法有三个参数:conf,isLocal,listenerBus
```

_env = createSparkEnv(_conf, isLocal, listenerBus)
SparkEnv.set(_env)

---------------------------------
//下面是createSparkEnv方法的实现
private[spark] def createSparkEnv(
    conf: SparkConf,
    isLocal: Boolean,
    listenerBus: LiveListenerBus): SparkEnv = {
  SparkEnv.createDriverEnv(conf, isLocal, listenerBus, SparkContext.numDriverCores(master))
}

//createSparkEnv的参数如下:
_conf = config.clone()

def isLocal: Boolean = (master == "local" || master.startsWith("local["))

private[spark] val listenerBus = new LiveListenerBus
----------------------------------


```
上面的代码中的conf是对SparkConf的复制,isLocal标识是否是单机模式,listenerBus采用监听器模式维护各类事件的处理

SparkEnv的方法createDriverEnv最终调用create创建SparkEnv(可以一步一步的点进去看)

SparkEnv的构造步骤如下:
1.创建安全管理器SecurityManager
2.创建基于Akka的分布式消息系统ActorSystem
3.创建Map任务输出更踪器mapOutputTracker
4.实例化ShuffleManager
5.创建ShuffleMemoryManager
6.创建块传输服务BlockTransferService
7.创建BlockManagerMaster
8.创建块管理器BlockManager
9.创建广播管理器BroadcastManager
10.创建缓存管理器CacheManger
11.创建HTTP文件服务器HttpFileServer
12.创建测量系统MetricsSystem
13.创建SparkEnv

```
//在create方法中,有下面的代码:
val envInstance = new SparkEnv(
  executorId,
  rpcEnv,
  actorSystem,
  serializer,
  closureSerializer,
  cacheManager,
  mapOutputTracker,
  shuffleManager,
  broadcastManager,
  blockTransferService,
  blockManager,
  securityManager,
  sparkFilesDir,
  metricsSystem,
  memoryManager,
  outputCommitCoordinator,
  conf)


//返回实例
envInstance

```



## 2.1.安全管理器SecurityManager

SecurityManager主要对权限,账号进行设置,如果使用Hadoop Yarn作为集群管理器,则需要使用证书生成secret key登录,最后给当前系统设置默认的口令认证实例,此实例采用匿名内部类实现
```
private val secretKey = generateSecretKey()

//使用HTTP连接设置口令认证
if (authOn) {
  Authenticator.setDefault(
    new Authenticator() {
      override def getPasswordAuthentication(): PasswordAuthentication = {
        var passAuth: PasswordAuthentication = null
        val userInfo = getRequestingURL().getUserInfo()
        if (userInfo != null) {
          val  parts = userInfo.split(":", 2)
          passAuth = new PasswordAuthentication(parts(0), parts(1).toCharArray())
        }
        return passAuth
      }
    }
  )
}

```


## 2.2.基于Akka的分布式消息系统ActorSystem

ActorSystem是spark中最基础的设施,spark既使用它发送分布式消息,又用它实现并发编程,消息系统可以实现并发?要解释清楚这个问题,首先应该简单介绍下scala语言的Actor并发编程模型:Scala认为java线程通过共享数据以及通过锁来维护共享数据的一致性是糟糕的做法,容易引起锁的争用,降低并发程序的性能,甚至会引起死锁的问题,在scala中需要自定义类型继承Actor,并且提供act方法,就如同Java里实现Runnable接口,需要实现run方法一样,但是不能直接调用act方法,而是通过发送消息的方式(scala发送消息时异步的)传送数据,如:
```
Actor!message
```

Akka是Actor编程模型的高级类库,雷雨JDK1.5之后越来越丰富的并发工具包,简化了程序员并发编程的难度,ActorSystem便是Akka提供的用于创建分布式消息通信系统的基础类

正是因为Actor轻量级的并发编程,消息发送以及ActorSystem支持分布式消息发送等特点,Spark选择了ActorSystem


SparkEnv中创建ActorSystem时用到了AkkaUtils工具类,AkkaUtils.createActorSystem,如下:
```
AkkaUtils.createActorSystem(
  actorSystemName + "ActorSystem",
  hostname,
  actorSystemPort,
  conf,
  securityManager
)._1

```

createActorSystem方法如下,他会调用:startServiceOnPort
```
  def createActorSystem(
      name: String,
      host: String,
      port: Int,
      conf: SparkConf,
      securityManager: SecurityManager): (ActorSystem, Int) = {
    val startService: Int => (ActorSystem, Int) = { actualPort =>
      doCreateActorSystem(name, host, actualPort, conf, securityManager)
    }
    Utils.startServiceOnPort(port, startService, conf, name)
  }

```

startServiceOnPort中调用startService
```
def startServiceOnPort[T](
    startPort: Int,
    startService: Int => (T, Int),
    conf: SparkConf,
    serviceName: String = ""): (T, Int) = {
	
	//....
	val (service, port) = startService(tryPort)
	//...
}



```

//他会调用createActorSystem中的传过来的方法 startService,所以会调用doCreateActorSystem,所以正真启动ActorSystem是由doCreateActorSystem方法完成的

Spark的Driver中Akka的默认访问地址是akka://sparkDriver,Spark的Executor中Akka的默认访问地址是 akka://sparkExecutor,如果不指定ActorSystem的端口,那么所有节点的ActorSystem端口每次启动时随机产生



## 2.3.map任务输出跟踪器mapOutputTracker

mapOutputTracker用于跟踪map阶段任务的输出状态,此状态便于reduce阶段任务获取地址及中间输出结果,每个map任务或者reduce任务都会有唯一的标识,分别为mapId何reduceId,每个reduce任务的输入可能是多个map的输出,reduce回到各个map任务的所在节点上拉取Block,这一过程叫做Shuffle,每批Shuffle过程都有唯一的标识ShuffleId

```
val mapOutputTracker = if (isDriver) {
  new MapOutputTrackerMaster(conf)
} else {
  new MapOutputTrackerWorker(conf)
}

```

这里先介绍下MapOutputTrackerMaster,在其内部有下面的代码:
```
protected val mapStatuses = new TimeStampedHashMap[Int, Array[MapStatus]]()
private val cachedSerializedStatuses = new TimeStampedHashMap[Int, Array[Byte]]()

```
其中TimeStampedHashMap[Int, Array[MapStatus]]的int是对应ShuffleId,Array存储各个map任务对应的状态信息MapStatus,由于MapStatus维护了map输出Block的地址BlockManagerId,这样reduce任务知道从何处获取map任务的中间输出
```
private[spark] sealed trait MapStatus {
  /** Location where this task was run. */
  def location: BlockManagerId

  def getSizeForBlock(reduceId: Int): Long
}

```

同时MapOutputTrackerMaster还使用cachedSerializedStatuses:TimeStampedHashMap[Int, Array[Byte]]维护序列化后的各个map任务的输出状态,其中int对应的是ShuffleId,Array存储各个序列化MapStatus生成的字节数组


Driver和Executor处理MapOutputTrackerMaster的方式有所不同
如果当前应用程序是Driver,则创建MapOutputTrackerMaster,然后创建MapOutputTrackerMasterActor,并且注册到ActorSystem中
如果当前应用程序是Executor,则创建MapOutputTrackerWorker,并从ActorSystem中找到MapOutputTrackerMasterActor

无论是Driver还是Executor,最后都是由mapOutputTracker的属性trackerEndpoint持有MapOutputTrackerMasterActor的引用,如下:
```
val mapOutputTracker = if (isDriver) {
  new MapOutputTrackerMaster(conf)
} else {
  new MapOutputTrackerWorker(conf)
}

// Have to assign trackerActor after initialization as MapOutputTrackerActor
// requires the MapOutputTracker itself
mapOutputTracker.trackerEndpoint = registerOrLookupEndpoint(MapOutputTracker.ENDPOINT_NAME, new MapOutputTrackerMasterEndpoint(rpcEnv, mapOutputTracker.asInstanceOf[MapOutputTrackerMaster], conf))


//
def registerOrLookupEndpoint(
    name: String, endpointCreator: => RpcEndpoint):
  RpcEndpointRef = {
  if (isDriver) {
    logInfo("Registering " + name)
    rpcEnv.setupEndpoint(name, endpointCreator)
  } else {
    RpcUtils.makeDriverRef(name, conf, rpcEnv)
  }
}


```


后面的章节就会知道map任务的状态正是由Executor向持有的MapOutputTrackerMasterActor发送消息,将map任务状态同步到MapOutputTracker的MapStatuses和cachedSerializedStatuses的,Executor究竟是如何找到MapOutputTrackerMasterActor的?registerOrLookupEndpoint方法通过RpcUtils.makeDriverRef找到MapOutputTrackerMasterActor,实际正是利用ActorSystem提供的分布式消息机制实现的


## 2.4.实例化ShuffleManager

ShuffleManager负责管理本地及远程的block数据的shuffle操作,ShuffleManager默认为通过反射方法生成的SortShuffleManager的实例,例如可以修改属性spark.shuffle.manager为hash来显示控制使用HashShuffleManager
```
//下面是几种ShuffleManager的短名称和类名的映射
val shortShuffleMgrNames = Map(
  "hash" -> "org.apache.spark.shuffle.hash.HashShuffleManager",
  "sort" -> "org.apache.spark.shuffle.sort.SortShuffleManager",
  "tungsten-sort" -> "org.apache.spark.shuffle.sort.SortShuffleManager")

//从配置文件中获取,是否有配置 spark.shuffle.manager
val shuffleMgrName = conf.get("spark.shuffle.manager", "sort")

//得到ShuffleManager的类名
val shuffleMgrClass = shortShuffleMgrNames.getOrElse(shuffleMgrName.toLowerCase, shuffleMgrName)

//根据类名反射
val shuffleManager = instantiateClass[ShuffleManager](shuffleMgrClass)

//instantiateClass的方法内容
def instantiateClass[T](className: String): T = {
  val cls = Utils.classForName(className)
  try {
    cls.getConstructor(classOf[SparkConf], java.lang.Boolean.TYPE)
      .newInstance(conf, new java.lang.Boolean(isDriver))
      .asInstanceOf[T]
  }
  //....

}
```

SortShuffleManager通过持有的IndexShuffleBlockResolver间接BlockManager中的DiskBlockManager将map结果写入本地,并根据shuffleId,mapId写入索引文件,也能通过MapOutputTrackerMaster中维护的MapStatuses从本地或者其他远程节点读取文件,有人会问,为什么需要shuffle?spark作为并行计算框架,同一个作业会被划分为多个任务在多个节点上并行执行,reduce的输入可能存在于多个节点上,因此需要通过"洗牌"将所有的reduce的输入汇总起来,这个过程就是shuffle


## 2.5.shuffle线程内存管理器ShuffleMemoryManager
在我阅读spark1.6的时候,没有看到作者指定的ShuffleMemoryManager类,在spark1.6的源码中是这样的:
```
//使用传统内存管理器
val useLegacyMemoryManager = conf.getBoolean("spark.memory.useLegacyMode", false)
val memoryManager: MemoryManager =
  if (useLegacyMemoryManager) {
    new StaticMemoryManager(conf, numUsableCores) //静态内存管理器
  } else {
    UnifiedMemoryManager(conf, numUsableCores)	//统一内存管理器
  }

```

如果配置了传统的内存管理器,代码实现如下:
```
private[spark] class StaticMemoryManager(
    conf: SparkConf,
    maxOnHeapExecutionMemory: Long,
    override val maxStorageMemory: Long,
    numCores: Int)
  extends MemoryManager(
    conf,
    numCores,
    maxStorageMemory,
    maxOnHeapExecutionMemory) {

  def this(conf: SparkConf, numCores: Int) {
    this(
      conf,
      StaticMemoryManager.getMaxExecutionMemory(conf),
      StaticMemoryManager.getMaxStorageMemory(conf),
      numCores)
  }
//....

}



其中两个重要的方法实现如下:
 /**
 * Return the total amount of memory available for the storage region, in bytes.
 */
private def getMaxStorageMemory(conf: SparkConf): Long = {
  val systemMaxMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime.maxMemory)
  val memoryFraction = conf.getDouble("spark.storage.memoryFraction", 0.6)
  val safetyFraction = conf.getDouble("spark.storage.safetyFraction", 0.9)
  (systemMaxMemory * memoryFraction * safetyFraction).toLong
}

/**
 * Return the total amount of memory available for the execution region, in bytes.
 */
private def getMaxExecutionMemory(conf: SparkConf): Long = {
  val systemMaxMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime.maxMemory)
  val memoryFraction = conf.getDouble("spark.shuffle.memoryFraction", 0.2)
  val safetyFraction = conf.getDouble("spark.shuffle.safetyFraction", 0.8)
  (systemMaxMemory * memoryFraction * safetyFraction).toLong
}

```

没有配置传统内存管理器,使用的是统一内存管理器的代码如下:
```
def apply(conf: SparkConf, numCores: Int): UnifiedMemoryManager = {
  val maxMemory = getMaxMemory(conf)
  new UnifiedMemoryManager(
    conf,
    maxMemory = maxMemory,
    storageRegionSize =
      (maxMemory * conf.getDouble("spark.memory.storageFraction", 0.5)).toLong,
    numCores = numCores)
}


//而getMaxMemory的方法实现如下:
/**
 * Return the total amount of memory shared between execution and storage, in bytes.
 */
private def getMaxMemory(conf: SparkConf): Long = {
  val systemMemory = conf.getLong("spark.testing.memory", Runtime.getRuntime.maxMemory)
	//保留内存
  val reservedMemory = conf.getLong("spark.testing.reservedMemory", if (conf.contains("spark.testing")) 0 else RESERVED_SYSTEM_MEMORY_BYTES)
  val minSystemMemory = reservedMemory * 1.5

  if (systemMemory < minSystemMemory) {
    throw new IllegalArgumentException(s"System memory $systemMemory must " +
      s"be at least $minSystemMemory. Please use a larger heap size.")
  }
  val usableMemory = systemMemory - reservedMemory
	//内存百分比
  val memoryFraction = conf.getDouble("spark.memory.fraction", 0.75)
  (usableMemory * memoryFraction).toLong
}


```



## 2.6.块传输服务BlockTransferService
BlockTransferService使用的是NettyBlockTransferService,他使用Netty提供的异步事件驱动的网络应用框架,提供web服务及客户端,获取远程节点上Block的集合

```
val blockTransferService = new NettyBlockTransferService(conf, securityManager, numUsableCores)

```

NettyBlockTransferService的具体实现在第4章有详细介绍

## 2.7.BlockManagerMaster介绍
BlockManagerMaster负责对Block的管理和协调,具体操作依赖于BlockManagerMasterEndpoint,Driver和Executor处理BlockManagerMaster的方式不同
如果当前应用程序是Driver,则创建BlockManagerMasterEndpoint,并且注册到ActorSystem中,如果当前应用程序是Executor,则从ActorSystem中找到BlockManagerMasterEndpoint.

无论是Driver还是Executor,最后BlockManagerMaster的属性driverEndpoint将持有对BlockManagerMasterEndpoint的引用,BlockManagerMaster的创建代码如下:

```

val blockManagerMaster = new BlockManagerMaster(
							registerOrLookupEndpoint(BlockManagerMaster.DRIVER_ENDPOINT_NAME, new BlockManagerMasterEndpoint(rpcEnv, isLocal, conf, listenerBus)),   
							conf, 
							isDriver)
```
registerOrLookupEndpoint在2.3节有介绍,不再详述




## 2.8.创建块管理器BlockManager
BlockManager负责对Block的管理,只有在BlockManager的初始化initialize被调用后,他才是有效的,具体实现见第4章

```
 // NB: blockManager is not valid until initialize() is called later.
 val blockManager = new BlockManager(executorId, rpcEnv, blockManagerMaster,
   serializer, conf, memoryManager, mapOutputTracker, shuffleManager,  blockTransferService, securityManager, numUsableCores)

```



## 2.9.创建广播管理器BroadcastManager

BroadcastManager用于将配置信息和序列化后的RDD,Job以及ShuffleDependency等信息在本地存储,如果为了容灾,也会复制到其他节点上,实现代码如下:

```
val broadcastManager = new BroadcastManager(isDriver, conf, securityManager)
```

BroadcastManager必须在其初始化方法initialize被调用后才能生效,initialize方法实际利用反射生成广播工厂实例broadcastFactory(可以配置属性:spark.broadcast.factory

```
// Called by SparkContext or Executor before using Broadcast
private def initialize() {
  synchronized {
    if (!initialized) {
      val broadcastFactoryClass =
        conf.get("spark.broadcast.factory", "org.apache.spark.broadcast.TorrentBroadcastFactory")

      broadcastFactory =
        Utils.classForName(broadcastFactoryClass).newInstance.asInstanceOf[BroadcastFactory]

      // Initialize appropriate BroadcastFactory and BroadcastObject
      broadcastFactory.initialize(isDriver, conf, securityManager)

      initialized = true
    }
  }
}

```

BroadcastManager的newBroadcast实际代理了工厂的newBroadcast方法来生成广播对象,代理unbroadcast来生成非广播对象

```
def newBroadcast[T: ClassTag](value_ : T, isLocal: Boolean): Broadcast[T] = {
  broadcastFactory.newBroadcast[T](value_, isLocal, nextBroadcastId.getAndIncrement())
}

def unbroadcast(id: Long, removeFromDriver: Boolean, blocking: Boolean) {
  broadcastFactory.unbroadcast(id, removeFromDriver, blocking)
}

```



## 2.10.创建缓存管理器CacheManager
CacheManager用于缓存RDD某个分区计算后的中间结果,缓存计算结果发生在迭代计算的时候,在6.1节会讲到,而CacheManager将在4.10节详细描述,创建CacheManager的代码如下:
```
val cacheManager = new CacheManager(blockManager)
```


## 2.11.HTTP文件服务器httpFileServer
在spark1.6中是下面的代码实现

```
// Set the sparkFiles directory, used when downloading dependencies.  
// In local mode, this is a temporary directory; 
// in distributed mode, this is the executor's current working directory.
val sparkFilesDir: String = if (isDriver) {
  Utils.createTempDir(Utils.getLocalDir(conf), "userFiles").getAbsolutePath
} else {
  "."
}

```

HttpFileServer的初始化过程代码如下:
```
  def initialize() {
    baseDir = Utils.createTempDir(Utils.getLocalDir(conf), "httpd")
    fileDir = new File(baseDir, "files")
    jarDir = new File(baseDir, "jars")
    fileDir.mkdir()
    jarDir.mkdir()
    logInfo("HTTP File server directory is " + baseDir)
    httpServer = new HttpServer(conf, baseDir, securityManager, requestedPort, "HTTP file server")
    httpServer.start()
    serverUri = httpServer.uri
    logDebug("HTTP file server started at: " + serverUri)
  }

```

包括下面的步骤:
1.使用Utils工具类创建文件服务器的根目录及临时目录(临时目录在运行环境关闭时会删除)
2.创建存放jar包及其他文件的文件目录
3.创建并启动Http服务

httpServer的构造和start方法的实现如下:
```
  def start() {
    if (server != null) {
      throw new ServerStateException("Server is already started")
    } else {
      logInfo("Starting HTTP Server")
      val (actualServer, actualPort) =
        Utils.startServiceOnPort[Server](requestedPort, doStart, conf, serverName)
      server = actualServer
      port = actualPort
    }
  }

```
用到了 Utils.startServiceOnPort方法,因此会回调doStart方法,在doStart方法总内嵌了Jetty所提供的HTTP服务


## 2.12.创建测量系统MetricsSystem

MetricsSystem是spark的测量系统,创建代码如下:
```
val metricsSystem = if (isDriver) {
  // Don't start metrics system right now for Driver.
  // We need to wait for the task scheduler to give us an app ID.
  // Then we can start the metrics system.
  MetricsSystem.createMetricsSystem("driver", conf, securityManager)
} else {
  // We need to set the executor ID before the MetricsSystem is created because sources and
  // sinks specified in the metrics configuration file will want to incorporate this executor's
  // ID into the metrics they report.
  conf.set("spark.executor.id", executorId)
  val ms = MetricsSystem.createMetricsSystem("executor", conf, securityManager)
  ms.start()
  ms
}

```


上面调用的createMetricsSystem方法实际创建了MetricsSystem,如下:
```
  def createMetricsSystem(
      instance: String, conf: SparkConf, securityMgr: SecurityManager): MetricsSystem = {
    new MetricsSystem(instance, conf, securityMgr)
  }

```

构造MetricsSystem的过程最重要的是调用了MetricsConfig.initialize()方法
```
  def initialize() {
    // Add default properties in case there's no properties file
    setDefaultProperties(properties)

    loadPropertiesFromFile(conf.getOption("spark.metrics.conf"))

    // Also look for the properties in provided Spark configuration
    val prefix = "spark.metrics.conf."
    conf.getAll.foreach {
      case (k, v) if k.startsWith(prefix) =>
        properties.setProperty(k.substring(prefix.length()), v)
      case _ =>
    }

    propertyCategories = subProperties(properties, INSTANCE_REGEX)
    if (propertyCategories.contains(DEFAULT_PREFIX)) {
      val defaultProperty = propertyCategories(DEFAULT_PREFIX).asScala
      for((inst, prop) <- propertyCategories if (inst != DEFAULT_PREFIX);
          (k, v) <- defaultProperty if (prop.get(k) == null)) {
        prop.put(k, v)
      }
    }
  }

```

从以上实现可以看出,MetricsConfig的initialize方法主要负责加载metrics.properties文件中的属性配置,并对属性进行初始化转换,变成Map

## 2.13.创建SparkEnv
当所有的基础组件准备好后,最终使用下面的代码创建执行环境SparkEnv
```

val envInstance = new SparkEnv(
  executorId,
  rpcEnv,
  actorSystem,
  serializer,
  closureSerializer,
  cacheManager,
  mapOutputTracker,
  shuffleManager,
  broadcastManager,
  blockTransferService,
  blockManager,
  securityManager,
  sparkFilesDir,
  metricsSystem,
  memoryManager,
  outputCommitCoordinator,
  conf)

```


# 3.创建metadtaCleaner

我们回到SparkContext类中,如下代码:
```
// Create the Spark execution environment (cache, map output tracker, etc)
_env = createSparkEnv(_conf, isLocal, listenerBus)
SparkEnv.set(_env)

_metadataCleaner = new MetadataCleaner(MetadataCleanerType.SPARK_CONTEXT, this.cleanup, _conf)

```
我们发现在创建完SparkEnv之后,接下来是创建MetadataCleaner

sparkContext为了保持对所有持久化的RDD的跟踪,使用类型是TimeStampedWeakValueHashMap的persistentRDDs缓存,metadataCleaner的功能是清除过期的持久化RDD,创建metadataCleaner的代码如下:
```
_metadataCleaner = new MetadataCleaner(MetadataCleanerType.SPARK_CONTEXT, this.cleanup, _conf)

```
注意上面的代码中会传给MetadataCleaner构造器一个函数cleanup,cleanup的代码如下:
```
  /** Called by MetadataCleaner to clean up the persistentRdds map periodically */
  private[spark] def cleanup(cleanupTime: Long) {
    persistentRdds.clearOldValues(cleanupTime)
  }

```

我们来看下MetadataCleaner构造器的代码:
```

/**
 * Runs a timer task to periodically clean up metadata (e.g. old files or hashtable entries)
 */
private[spark] class MetadataCleaner(
    cleanerType: MetadataCleanerType.MetadataCleanerType,
    cleanupFunc: (Long) => Unit,//cleanup方法会传递过来
    conf: SparkConf)
  extends Logging
{
  val name = cleanerType.toString

  private val delaySeconds = MetadataCleaner.getDelaySeconds(conf, cleanerType)
  private val periodSeconds = math.max(10, delaySeconds / 10)
  private val timer = new Timer(name + " cleanup timer", true)

  //将cleanup方法封装成一个task,然后去定时执行
  private val task = new TimerTask {
    override def run() {
      try {
        cleanupFunc(System.currentTimeMillis() - (delaySeconds * 1000))
        logInfo("Ran metadata cleaner for " + name)
      } catch {
        case e: Exception => logError("Error running cleanup task for " + name, e)
      }
    }
  }

  if (delaySeconds > 0) {
    logDebug(
      "Starting metadata cleaner for " + name + " with delay of " + delaySeconds + " seconds " +
      "and period of " + periodSeconds + " secs")

	/启动定时任务
    timer.schedule(task, delaySeconds * 1000, periodSeconds * 1000)
  }

  def cancel() {
    timer.cancel()
  }
}

```

从上面的代码可以看出MetadataCleaner实质上是一个用TimerTask实现的定时器,不断的调用cleanupFunc这样的函数,在构造metadataCleaner时的函数参数是cleanup,用于清理persistentRdds的过期内容




# 4.SparkUI详解

任何系统都需要提供监控系统,用浏览器能访问具有样式及布局并提供丰富监控数据的页面无疑是一种简单,高效的方式.


在大型分布式系统中,采用事件监听机制是最常见的,为何要使用事件监听机制,加入SparkUI采用scala的函数调用方式,那么随着整个集群规模的增加,对函数的调用会越来越多,最终会受到Driver所在JVM的线程数量限制而影响监控数据的更新,甚至出现监控数据无法及时显示给用户的情况,由于函数调用多数情况下是同步调用,这就导致现场被阻塞,在分布式环境中,还可能因为网络问题,导致线程被长时间占用,将函数调用更换为发送事件,事件的处理是异步的,当前线程可以继续执行后续逻辑,线程池中的线程还可以被重用,这样整个系统的并发度会大大增加,发送的事件会存入缓存,由定时调度器取出后,分配给监听此事件的监听器对监控数据进行更新

SparkUI就是这样的服务,他的架构如下图:
![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/SparkUI.png)

我们首先简单介绍图中的各个组件:
* DAGScheduler:主要的产生各类SparkListenerEvent的源头,他将各种SparkListenEvent发送到listenBus的事件队列中
* listenBus通过定时器将SparkListenerEvent事件匹配到具体的SparkListener,改变SparkListener中的统计监控数据,最终由SparkUI的界面展示
* 图中还可以看到Spark里面定义了很多监听器SparkListener的时间,包括JobProgressListener,EnvironmentListener,StorageListener,ExecutorsListenter,他们的继承体系如下图:

![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/SparkListener.png)


## 4.1.listenerBus详解

listenerBus的类型是LiveListenerBus,LiveListenerBus实现了监听器模型,通过监听事件触发对各种监听器状态信息的修改,达到UI界面的数据刷新效果,LiveListenerBus由以下部分组成:
* 事件阻塞队列:类型为LinkedBlockingQueue[SparkListenerEvent],固定大小为10000
* 监听器数组:类型为CopyOnWriteArrayList,存放各类监听器SparkListener

```
private[spark] class LiveListenerBus
  extends AsynchronousListenerBus[SparkListener, SparkListenerEvent]("SparkListenerBus")
  with SparkListenerBus {

//...
}


private[spark] abstract class AsynchronousListenerBus[L <: AnyRef, E](name: String)
  extends ListenerBus[L, E] {

  self =>

  private var sparkContext: SparkContext = null

  private val EVENT_QUEUE_CAPACITY = 10000
  //事件阻塞队列
  private val eventQueue = new LinkedBlockingQueue[E](EVENT_QUEUE_CAPACITY)

//...

}


//
private[spark] trait ListenerBus[L <: AnyRef, E] extends Logging {

  //监听器数组
  private[spark] val listeners = new CopyOnWriteArrayList[L]

  /**
   * Add a listener to listen events. This method is thread-safe and can be called in any thread.
   */
  final def addListener(listener: L) {
    listeners.add(listener)
  }

//....
}
```

* 事件匹配监听器的线程:此Thread不断拉取LinkedBlockingQueue中的事件,遍历监听器,调用监听器的方法,任何事件都会在LinkedBlockingQueue中存在一段时间,然后Thread处理了此事件后,会将其清除,因此使用listenerBus这个名字再合适不过了,到站就下车,listenerBus的实现如下:
```
  private val eventLock = new Semaphore(0)

  private val listenerThread = new Thread(name) {
    setDaemon(true)
    override def run(): Unit = Utils.tryOrStopSparkContext(sparkContext) {
      AsynchronousListenerBus.withinListenerThread.withValue(true) {
        while (true) {
          eventLock.acquire()
          self.synchronized {
            processingEvent = true
          }
          try {
            val event = eventQueue.poll
            if (event == null) {
              // Get out of the while loop and shutdown the daemon thread
              if (!stopped.get) {
                throw new IllegalStateException("Polling `null` from eventQueue means" +
                  " the listener bus has been stopped. So `stopped` must be true")
              }
              return
            }
            postToAll(event)
          } finally {
            self.synchronized {
              processingEvent = false
            }
          }
        }
      }
    }
  }

  /**
   * Start sending events to attached listeners.
   *
   * This first sends out all buffered events posted before this listener bus has started, then
   * listens for any additional events asynchronously while the listener bus is still running.
   * This should only be called once.
   *
   * @param sc Used to stop the SparkContext in case the listener thread dies.
   */
  def start(sc: SparkContext) {
    if (started.compareAndSet(false, true)) {
      sparkContext = sc
      listenerThread.start()
    } else {
      throw new IllegalStateException(s"$name already started!")
    }
  }

  def post(event: E) {
    if (stopped.get) {
      // Drop further events to make `listenerThread` exit ASAP
      logError(s"$name has already stopped! Dropping event $event")
      return
    }
    val eventAdded = eventQueue.offer(event)
    if (eventAdded) {
      eventLock.release()
    } else {
      onDropEvent(event)
    }
  }

  def listenerThreadIsAlive: Boolean = listenerThread.isAlive

 
  def stop() {
    if (!started.get()) {
      throw new IllegalStateException(s"Attempted to stop $name that has not yet started!")
    }
    if (stopped.compareAndSet(false, true)) {
      // Call eventLock.release() so that listenerThread will poll `null` from `eventQueue` and know
      // `stop` is called.
      eventLock.release()
      listenerThread.join()
    } else {
      // Keep quiet
    }
  }

```

LiveListenerBus中调用的postToAll方法实际定义在父类SparkListenerBus中,如下:
```
//在类ListenerBus中:

  final def postToAll(event: E): Unit = {
	//这里是遍历所有的listeners
    val iter = listeners.iterator
    while (iter.hasNext) {
      val listener = iter.next()
      try {
        onPostEvent(listener, event)
      } catch {
        case NonFatal(e) =>
          logError(s"Listener ${Utils.getFormattedClassName(listener)} threw an exception", e)
      }
    }
  }


//而onPostEvent的实现是在SparkListenerBus实现的,如下

  override def onPostEvent(listener: SparkListener, event: SparkListenerEvent): Unit = {
    event match {
      case stageSubmitted: SparkListenerStageSubmitted =>
        listener.onStageSubmitted(stageSubmitted)
      case stageCompleted: SparkListenerStageCompleted =>
        listener.onStageCompleted(stageCompleted)
      case jobStart: SparkListenerJobStart =>
        listener.onJobStart(jobStart)
      case jobEnd: SparkListenerJobEnd =>
        listener.onJobEnd(jobEnd)
      case taskStart: SparkListenerTaskStart =>
        listener.onTaskStart(taskStart)
      case taskGettingResult: SparkListenerTaskGettingResult =>
        listener.onTaskGettingResult(taskGettingResult)
      case taskEnd: SparkListenerTaskEnd =>
        listener.onTaskEnd(taskEnd)
      case environmentUpdate: SparkListenerEnvironmentUpdate =>
        listener.onEnvironmentUpdate(environmentUpdate)
      case blockManagerAdded: SparkListenerBlockManagerAdded =>
        listener.onBlockManagerAdded(blockManagerAdded)
      case blockManagerRemoved: SparkListenerBlockManagerRemoved =>
        listener.onBlockManagerRemoved(blockManagerRemoved)
      case unpersistRDD: SparkListenerUnpersistRDD =>
        listener.onUnpersistRDD(unpersistRDD)
      case applicationStart: SparkListenerApplicationStart =>
        listener.onApplicationStart(applicationStart)
      case applicationEnd: SparkListenerApplicationEnd =>
        listener.onApplicationEnd(applicationEnd)
      case metricsUpdate: SparkListenerExecutorMetricsUpdate =>
        listener.onExecutorMetricsUpdate(metricsUpdate)
      case executorAdded: SparkListenerExecutorAdded =>
        listener.onExecutorAdded(executorAdded)
      case executorRemoved: SparkListenerExecutorRemoved =>
        listener.onExecutorRemoved(executorRemoved)
      case blockUpdated: SparkListenerBlockUpdated =>
        listener.onBlockUpdated(blockUpdated)
      case logStart: SparkListenerLogStart => // ignore event log metadata
    }
  }

```

其实上的过程就是将对应的事件发送到对应的Listener进行处理


## 4.2.构造JobProgressListener

我们以JobProgressListener为例来讲解SparkListener,**JobProgressListener是SparkContext中一个重要的组成部分(在SparkContext代码中可以看到)**,通过监听listenBus中的事件更新任务进度,SparkStatusTracker和SparkUI实际上也是通过JobProgressListener来实现任务状态跟踪的,在SparkContext中创建JobProgressListener的代码如下:
```
 // "_jobProgressListener" should be set up before creating SparkEnv because when creating
 // "SparkEnv", some messages will be posted to "listenerBus" and we should not miss them.
 _jobProgressListener = new JobProgressListener(_conf)
 listenerBus.addListener(jobProgressListener)

_statusTracker = new SparkStatusTracker(this)

```


JobProgressListener的作用是通过HashMap,ListBuffer等数据结构存储JobId及对应的JobUIData信息,并按照激活,完成,失败等job状态统计,对stageId,stageInfo等信息按照激活,完成,忽略,失败等Stage状态统计,并且存储StageId与jobId的一对多关系,这些统计信息最终会被JobPage和StagePage等页面访问和渲染,JobProgressListener的数据结构如下:
```
class JobProgressListener(conf: SparkConf) extends SparkListener with Logging {

  // Define a handful of type aliases so that data structures' types can serve as documentation.
  // These type aliases are public because they're used in the types of public fields:

  type JobId = Int
  type JobGroupId = String
  type StageId = Int
  type StageAttemptId = Int
  type PoolName = String
  type ExecutorId = String

  // Application:
  @volatile var startTime = -1L
  @volatile var endTime = -1L

  // Jobs:
  val activeJobs = new HashMap[JobId, JobUIData]
  val completedJobs = ListBuffer[JobUIData]()
  val failedJobs = ListBuffer[JobUIData]()
  val jobIdToData = new HashMap[JobId, JobUIData]
  val jobGroupToJobIds = new HashMap[JobGroupId, HashSet[JobId]]

  // Stages:
  val pendingStages = new HashMap[StageId, StageInfo]
  val activeStages = new HashMap[StageId, StageInfo]
  val completedStages = ListBuffer[StageInfo]()
  val skippedStages = ListBuffer[StageInfo]()
  val failedStages = ListBuffer[StageInfo]()
  val stageIdToData = new HashMap[(StageId, StageAttemptId), StageUIData]
  val stageIdToInfo = new HashMap[StageId, StageInfo]
  val stageIdToActiveJobIds = new HashMap[StageId, HashSet[JobId]]
  val poolToActiveStages = HashMap[PoolName, HashMap[StageId, StageInfo]]()
  // Total of completed and failed stages that have ever been run.  These may be greater than
  // `completedStages.size` and `failedStages.size` if we have run more stages or jobs than
  // JobProgressListener's retention limits.
  var numCompletedStages = 0
  var numFailedStages = 0
  var numCompletedJobs = 0
  var numFailedJobs = 0
//...

```

JobProgressListener实现了onJobStart,onJobEnd,onStageCompleted,onStageSubmitted,onTashStart,onTaskEnd等方法,这些方法正是在listenBus的驱动下,改变JobProgressListener中的各种Job,Stage相关的数据



## 4.3.SparkUI的创建与初始化

在SparkContext中接下来的是SparkUI的创建

```
 _ui =
   if (conf.getBoolean("spark.ui.enabled", true)) {
     Some(SparkUI.createLiveUI(this, _conf, listenerBus, _jobProgressListener,
       _env.securityManager, appName, startTime = startTime))
   } else {
     // For tests, do not enable the UI
     None
   }

 // Bind the UI before starting the task scheduler to communicate
 // the bound port to the cluster manager properly
 _ui.foreach(_.bind())

```

可以看到如果不需要提供SparkUI服务,可以将属性spark.ui.enabled修改为false,其中createLiveUI实际是调用create方法,如下:
```
  def createLiveUI(
      sc: SparkContext,
      conf: SparkConf,
      listenerBus: SparkListenerBus,
      jobProgressListener: JobProgressListener,
      securityManager: SecurityManager,
      appName: String,
      startTime: Long): SparkUI = {
    create(Some(sc), conf, listenerBus, securityManager, appName,
      jobProgressListener = Some(jobProgressListener), startTime = startTime)
  }

```


而create方法的实现如下:
```

  private def create(
      sc: Option[SparkContext],
      conf: SparkConf,
      listenerBus: SparkListenerBus,
      securityManager: SecurityManager,
      appName: String,
      basePath: String = "",
      jobProgressListener: Option[JobProgressListener] = None,
      startTime: Long): SparkUI = {

    val _jobProgressListener: JobProgressListener = jobProgressListener.getOrElse {
      val listener = new JobProgressListener(conf)
      listenerBus.addListener(listener)
      listener
    }

    val environmentListener = new EnvironmentListener
    val storageStatusListener = new StorageStatusListener
    val executorsListener = new ExecutorsListener(storageStatusListener)
    val storageListener = new StorageListener(storageStatusListener)
    val operationGraphListener = new RDDOperationGraphListener(conf)

    listenerBus.addListener(environmentListener)
    listenerBus.addListener(storageStatusListener)
    listenerBus.addListener(executorsListener)
    listenerBus.addListener(storageListener)
    listenerBus.addListener(operationGraphListener)
	
	创建SparkUI
    new SparkUI(sc, conf, securityManager, environmentListener, storageStatusListener,
      executorsListener, _jobProgressListener, storageListener, operationGraphListener,
      appName, basePath, startTime)
  }

```

在上述的代码中可以看到,在create方法里除了JobProgressListener是外部传入的之外,又增加了一些SparkListener,例如,用于对JVM参数,Spark属性,java系统属性,classpath等进行监控的EnvironmentListener;用于维护Executor的存储状态的StorageStatusListener;用于准备将Executor的信息展示在ExecutorsTab的ExecutorsListener;用于准备将Executor相关存储信息展示在BlockManagerUI的StorageListener等,


最后创建SparkUI,SparkUI服务默认是可以被杀掉的,通过修改属性spark.ui.killEnabled为false,可以保证不被杀死,initialize方法会组织前端页面各个Tab和Page的展示及布局:

```
private[spark] class SparkUI private (
    val sc: Option[SparkContext],
    val conf: SparkConf,
    securityManager: SecurityManager,
    val environmentListener: EnvironmentListener,
    val storageStatusListener: StorageStatusListener,
    val executorsListener: ExecutorsListener,
    val jobProgressListener: JobProgressListener,
    val storageListener: StorageListener,
    val operationGraphListener: RDDOperationGraphListener,
    var appName: String,
    val basePath: String,
    val startTime: Long)
  extends WebUI(securityManager, SparkUI.getUIPort(conf), conf, basePath, "SparkUI")
  with Logging
  with UIRoot {

  val killEnabled = sc.map(_.conf.getBoolean("spark.ui.killEnabled", true)).getOrElse(false)


  val stagesTab = new StagesTab(this)

  var appId: String = _

  /** Initialize all components of the server. */
  def initialize() {
    attachTab(new JobsTab(this))
    attachTab(stagesTab)
    attachTab(new StorageTab(this))
    attachTab(new EnvironmentTab(this))
    attachTab(new ExecutorsTab(this))
    attachHandler(createStaticHandler(SparkUI.STATIC_RESOURCE_DIR, "/static"))
    attachHandler(createRedirectHandler("/", "/jobs/", basePath = basePath))
    attachHandler(ApiRootResource.getServletHandler(this))
    // This should be POST only, but, the YARN AM proxy won't proxy POSTs
    attachHandler(createRedirectHandler(
      "/stages/stage/kill", "/stages/", stagesTab.handleKillRequest,
      httpMethods = Set("GET", "POST")))
  }
  
  //初始化
  initialize()

///....

}

```


## 4.4.Spark UI的页面布局与展示
SparkUI究竟是如何实现页面布局及展示的?JobsTab展示所有的Job的进度,状态信息,这里我们以他为例来说明,JobsTab会复用SparkUI的killEnabled,SparkContext,jobProgressListener,包括AllJobsPage和JobPage两个页面,代码如下:
```
private[ui] class JobsTab(parent: SparkUI) extends SparkUITab(parent, "jobs") {
  val sc = parent.sc
  val killEnabled = parent.killEnabled
  val jobProgresslistener = parent.jobProgressListener
  val executorListener = parent.executorsListener
  val operationGraphListener = parent.operationGraphListener

  def isFairScheduler: Boolean =
    jobProgresslistener.schedulingMode.exists(_ == SchedulingMode.FAIR)

  attachPage(new AllJobsPage(this))
  attachPage(new JobPage(this))
}

```

 AllJobsPage由render方法渲染,利用JobProgressListener中的统计监控数据生成:激活,完成,失败等job的状态摘要信息,并调用jobsTable方法生成表格等html元素,最终使用UIUtils的headerSparkPage封装好css,js,header及页面布局等,代码如下:

下面是AllJobsPage.render方法
```

  def render(request: HttpServletRequest): Seq[Node] = {
    val listener = parent.jobProgresslistener
    listener.synchronized {
      val startTime = listener.startTime
      val endTime = listener.endTime
      val activeJobs = listener.activeJobs.values.toSeq
      val completedJobs = listener.completedJobs.reverse.toSeq
      val failedJobs = listener.failedJobs.reverse.toSeq

      val activeJobsTable =
        jobsTable(activeJobs.sortBy(_.submissionTime.getOrElse(-1L)).reverse)
      val completedJobsTable =
        jobsTable(completedJobs.sortBy(_.completionTime.getOrElse(-1L)).reverse)
      val failedJobsTable =
        jobsTable(failedJobs.sortBy(_.completionTime.getOrElse(-1L)).reverse)

      val shouldShowActiveJobs = activeJobs.nonEmpty
      val shouldShowCompletedJobs = completedJobs.nonEmpty
      val shouldShowFailedJobs = failedJobs.nonEmpty

      val completedJobNumStr = if (completedJobs.size == listener.numCompletedJobs) {
        s"${completedJobs.size}"
      } else {
        s"${listener.numCompletedJobs}, only showing ${completedJobs.size}"
      }

      val summary: NodeSeq =
        <div>
          <ul class="unstyled">
            <li>
              <strong>Total Uptime:</strong>
              {
                if (endTime < 0 && parent.sc.isDefined) {
                  UIUtils.formatDuration(System.currentTimeMillis() - startTime)
                } else if (endTime > 0) {
                  UIUtils.formatDuration(endTime - startTime)
                }
              }
            </li>
            <li>
              <strong>Scheduling Mode: </strong>
              {listener.schedulingMode.map(_.toString).getOrElse("Unknown")}
            </li>
            {
              if (shouldShowActiveJobs) {
                <li>
                  <a href="#active"><strong>Active Jobs:</strong></a>
                  {activeJobs.size}
                </li>
              }
            }
            {
              if (shouldShowCompletedJobs) {
                <li id="completed-summary">
                  <a href="#completed"><strong>Completed Jobs:</strong></a>
                  {completedJobNumStr}
                </li>
              }
            }
            {
              if (shouldShowFailedJobs) {
                <li>
                  <a href="#failed"><strong>Failed Jobs:</strong></a>
                  {listener.numFailedJobs}
                </li>
              }
            }
          </ul>
        </div>

      var content = summary
      val executorListener = parent.executorListener
      content ++= makeTimeline(activeJobs ++ completedJobs ++ failedJobs,
          executorListener.executorIdToData, startTime)

      if (shouldShowActiveJobs) {
        content ++= <h4 id="active">Active Jobs ({activeJobs.size})</h4> ++
          activeJobsTable
      }
      if (shouldShowCompletedJobs) {
        content ++= <h4 id="completed">Completed Jobs ({completedJobNumStr})</h4> ++
          completedJobsTable
      }
      if (shouldShowFailedJobs) {
        content ++= <h4 id ="failed">Failed Jobs ({failedJobs.size})</h4> ++
          failedJobsTable
      }

      val helpText = """A job is triggered by an action, like count() or saveAsTextFile().""" +
        " Click on a job to see information about the stages of tasks inside it."

      UIUtils.headerSparkPage("Spark Jobs", content, parent, helpText = Some(helpText))
    }
  }



```


jobsTable用来生成表格数据,jobsTable(job),将传过来的job信息生成对应的表格,如下:
```

  private def jobsTable(jobs: Seq[JobUIData]): Seq[Node] = {
    val someJobHasJobGroup = jobs.exists(_.jobGroup.isDefined)

    val columns: Seq[Node] = {
      <th>{if (someJobHasJobGroup) "Job Id (Job Group)" else "Job Id"}</th>
      <th>Description</th>
      <th>Submitted</th>
      <th>Duration</th>
      <th class="sorttable_nosort">Stages: Succeeded/Total</th>
      <th class="sorttable_nosort">Tasks (for all stages): Succeeded/Total</th>
    }
	
	//表格中每行数据又是通过makeRow方法渲染的
    def makeRow(job: JobUIData): Seq[Node] = {
      val (lastStageName, lastStageDescription) = getLastStageNameAndDescription(job)
      val duration: Option[Long] = {
        job.submissionTime.map { start =>
          val end = job.completionTime.getOrElse(System.currentTimeMillis())
          end - start
        }
      }
      val formattedDuration = duration.map(d => UIUtils.formatDuration(d)).getOrElse("Unknown")
      val formattedSubmissionTime = job.submissionTime.map(UIUtils.formatDate).getOrElse("Unknown")
      val basePathUri = UIUtils.prependBaseUri(parent.basePath)
      val jobDescription = UIUtils.makeDescription(lastStageDescription, basePathUri)

      val detailUrl = "%s/jobs/job?id=%s".format(basePathUri, job.jobId)
      <tr id={"job-" + job.jobId}>
        <td sorttable_customkey={job.jobId.toString}>
          {job.jobId} {job.jobGroup.map(id => s"($id)").getOrElse("")}
        </td>
        <td>
          {jobDescription}
          <a href={detailUrl} class="name-link">{lastStageName}</a>
        </td>
        <td sorttable_customkey={job.submissionTime.getOrElse(-1).toString}>
          {formattedSubmissionTime}
        </td>
        <td sorttable_customkey={duration.getOrElse(-1).toString}>{formattedDuration}</td>
        <td class="stage-progress-cell">
          {job.completedStageIndices.size}/{job.stageIds.size - job.numSkippedStages}
          {if (job.numFailedStages > 0) s"(${job.numFailedStages} failed)"}
          {if (job.numSkippedStages > 0) s"(${job.numSkippedStages} skipped)"}
        </td>
        <td class="progress-cell">
          {UIUtils.makeProgressBar(started = job.numActiveTasks, completed = job.numCompletedTasks,
           failed = job.numFailedTasks, skipped = job.numSkippedTasks,
           total = job.numTasks - job.numSkippedTasks)}
        </td>
      </tr>
    }

    <table class="table table-bordered table-striped table-condensed sortable">
      <thead>{columns}</thead>
      <tbody>
        {jobs.map(makeRow)}
      </tbody>
    </table>
  }

```



## 4.5.SparkUI的启动
SparkUI创建好后,需要调用父类WebUI的bind方法,绑定服务和端口,bind方法中主要的代码实现如下:
```
serverInfo = Some(startJettyServer("0.0.0.0", port, handlers, conf, name))
```
最终启动了Jetty提供的服务,默认端口是4040



# 5.Hadoop相关配置及Executor环境变量

## 5.1.Hadoop相关配置信息
默认情况下,Spark使用HDFS作为分布式文件系统,所以需要获取Hadoop相关配置信息的,在SparkContext的相关代码如下:
```
_hadoopConfiguration = SparkHadoopUtil.get.newConfiguration(_conf)

```

获取的配置信息包括:
* 将Amazon S3文件系统的AccessKeyId和SecretAccessKey加载到Hadoop的Configuration
* 将SparkConf中所有以spark.hadoop.开头的属性都复制到Hadoop的Configuration
* 将SparkConf的属性spark.buff.size复制为Hadoop的Configuration的配置io.file.buffer.size

```
  def newConfiguration(conf: SparkConf): Configuration = {
    val hadoopConf = new Configuration()

    // Note: this null check is around more than just access to the "conf" object to maintain
    // the behavior of the old implementation of this code, for backwards compatibility.
    if (conf != null) {
      // Explicitly check for S3 environment variables
      if (System.getenv("AWS_ACCESS_KEY_ID") != null &&
          System.getenv("AWS_SECRET_ACCESS_KEY") != null) {
        val keyId = System.getenv("AWS_ACCESS_KEY_ID")
        val accessKey = System.getenv("AWS_SECRET_ACCESS_KEY")

        hadoopConf.set("fs.s3.awsAccessKeyId", keyId)
        hadoopConf.set("fs.s3n.awsAccessKeyId", keyId)
        hadoopConf.set("fs.s3a.access.key", keyId)
        hadoopConf.set("fs.s3.awsSecretAccessKey", accessKey)
        hadoopConf.set("fs.s3n.awsSecretAccessKey", accessKey)
        hadoopConf.set("fs.s3a.secret.key", accessKey)
      }


      // Copy any "spark.hadoop.foo=bar" system properties into conf as "foo=bar"
      conf.getAll.foreach { case (key, value) =>
        if (key.startsWith("spark.hadoop.")) {
          hadoopConf.set(key.substring("spark.hadoop.".length), value)
        }
      }
	
      val bufferSize = conf.get("spark.buffer.size", "65536")
      hadoopConf.set("io.file.buffer.size", bufferSize)
    }

    hadoopConf
  }

```
注意:如果指定了SPARK_YARN_MODE属性,则会使用YarnSparkHadoopUtil,否则默认为SparkHadoopUtil

```
object SparkHadoopUtil {

//2种模式
  private lazy val hadoop = new SparkHadoopUtil
  private lazy val yarn = try {
    Utils.classForName("org.apache.spark.deploy.yarn.YarnSparkHadoopUtil")
      .newInstance()
      .asInstanceOf[SparkHadoopUtil]
  } catch {
    case e: Exception => throw new SparkException("Unable to load YARN support", e)
  }



  val SPARK_YARN_CREDS_TEMP_EXTENSION = ".tmp"

  val SPARK_YARN_CREDS_COUNTER_DELIM = "-"

//SPARK_YARN_MODE选择对应的Utils
  def get: SparkHadoopUtil = {
    // Check each time to support changing to/from YARN
    val yarnMode = java.lang.Boolean.valueOf(
        System.getProperty("SPARK_YARN_MODE", System.getenv("SPARK_YARN_MODE")))
    if (yarnMode) {
      yarn  //
    } else {
      hadoop
    }
  }
}

```

## 5.2.Executor环境变量
对Executor的环境变量的处理,如下:

```
 _executorMemory = _conf.getOption("spark.executor.memory")
   .orElse(Option(System.getenv("SPARK_EXECUTOR_MEMORY")))
   .orElse(Option(System.getenv("SPARK_MEM"))
   .map(warnSparkMem))
   .map(Utils.memoryStringToMb)
   .getOrElse(1024)

 // Convert java options to env vars as a work around
 // since we can't set env vars directly in sbt.
 for { (envKey, propKey) <- Seq(("SPARK_TESTING", "spark.testing"))
   value <- Option(System.getenv(envKey)).orElse(Option(System.getProperty(propKey)))} {
   executorEnvs(envKey) = value
 }
 Option(System.getenv("SPARK_PREPEND_CLASSES")).foreach { v =>
   executorEnvs("SPARK_PREPEND_CLASSES") = v
 }
 // The Mesos scheduler backend relies on this environment variable to set executor memory.
 // TODO: Set this only in the Mesos scheduler.
 executorEnvs("SPARK_EXECUTOR_MEMORY") = executorMemory + "m"
 executorEnvs ++= _conf.getExecutorEnv
 executorEnvs("SPARK_USER") = sparkUser

```

executorEnvs包含的环境变量将在7.2.2节中介绍的注册应用的过程中发送给Master,Master给Worker发送调度后,Worker最终使用executorEnvs提供的信息启动Executor,可以通过配置spark.executor.memory指定Executor占用的内存大小,也可以配置系统变量SPARK_EXECUTOR_MEMORY或者SPARK_MEM对其大小进行设置



# 6.创建任务调度器TaskScheduler

TaskScheduler也是SparkContext的重要组成部分,负责任务的提交,并且请求集群管理器对任务调度,TaskScheduler也可以看做任务调度的客户端,创建TaskScheduler的代码如下:
```
 val (sched, ts) = SparkContext.createTaskScheduler(this, master)
 _schedulerBackend = sched
 _taskScheduler = ts

```

createTaskScheduler方法会根据master的配置匹配部署模式,创建TaskSchedulerImpl,并生成不同的SchedulerBackend,本章为了使读者更容易理解Spark的初始化流程,故以local模式为例,其余模式将在第7章详解,master匹配local模式的代码如下:
```
private def createTaskScheduler(
    sc: SparkContext,
    master: String): (SchedulerBackend, TaskScheduler) = {
  import SparkMasterRegex._

  // When running locally, don't try to re-execute tasks on failure.
  val MAX_LOCAL_TASK_FAILURES = 1

  master match {
    case "local" =>
      val scheduler = new TaskSchedulerImpl(sc, MAX_LOCAL_TASK_FAILURES, isLocal = true)
      val backend = new LocalBackend(sc.getConf, scheduler, 1)
      scheduler.initialize(backend)
      (backend, scheduler)
//...

```


## 6.1.创建TaskSchedulerImpl
TaskSchedulerImpl的构造过程如下:
1.从SparkConf中读取配置信息,包括每个任务分配的CPU数,调度模式(调度模式有fair和fifo两种,默认为fifo,可以修改属性spark.scheduler.mode来改变)等
2.创建TaskSchedulerGetter,他的作用是通过线程池(Executor.newFixedThreadPool创建的,默认4个线程,线程名字以task-result-getter开头,线程工厂默认是Executors.defaultThreadFactory)对Worker上的Executor发送的Task的执行结果进行处理


```

  // Listener object to pass upcalls into
  var dagScheduler: DAGScheduler = null

  var backend: SchedulerBackend = null

  val mapOutputTracker = SparkEnv.get.mapOutputTracker

  var schedulableBuilder: SchedulableBuilder = null
  var rootPool: Pool = null
  // default scheduler is FIFO
  private val schedulingModeConf = conf.get("spark.scheduler.mode", "FIFO")
  val schedulingMode: SchedulingMode = try {
    SchedulingMode.withName(schedulingModeConf.toUpperCase)
  } catch {
    case e: java.util.NoSuchElementException =>
      throw new SparkException(s"Unrecognized spark.scheduler.mode: $schedulingModeConf")
  }

  // This is a var so that we can reset it for testing purposes.
  private[spark] var taskResultGetter = new TaskResultGetter(sc.env, this)

```

TaskSchedulerImpl的调度模式有fair和fifo两种,任务的最终调度实际都是落实到接口SchedulerBackend的具体实现上的,为方便分析,我们先来看看local模式中SchedulerBackend的实现LocalBackend,LocalBackend依赖localEndpoint与ActorSystem进行消息通信,LocalBackend的实现如下:
```
private[spark] class LocalBackend(
    conf: SparkConf,
    scheduler: TaskSchedulerImpl,
    val totalCores: Int)
  extends SchedulerBackend with ExecutorBackend with Logging {

  private val appId = "local-" + System.currentTimeMillis
  private var localEndpoint: RpcEndpointRef = null
  
  //....  

  override def start() {
    val rpcEnv = SparkEnv.get.rpcEnv
    val executorEndpoint = new LocalEndpoint(rpcEnv, userClassPath, scheduler, this, totalCores)
    localEndpoint = rpcEnv.setupEndpoint("LocalBackendEndpoint", executorEndpoint)
    listenerBus.post(SparkListenerExecutorAdded(
      System.currentTimeMillis,
      executorEndpoint.localExecutorId,
      new ExecutorInfo(executorEndpoint.localExecutorHostname, totalCores, Map.empty)))
    launcherBackend.setAppId(appId)
    launcherBackend.setState(SparkAppHandle.State.RUNNING)
  }

  override def stop() {
    stop(SparkAppHandle.State.FINISHED)
  }

  override def reviveOffers() {
    localEndpoint.send(ReviveOffers)
  }

  override def defaultParallelism(): Int =
    scheduler.conf.getInt("spark.default.parallelism", totalCores)

  override def killTask(taskId: Long, executorId: String, interruptThread: Boolean) {
    localEndpoint.send(KillTask(taskId, interruptThread))
  }

  override def statusUpdate(taskId: Long, state: TaskState, serializedData: ByteBuffer) {
    localEndpoint.send(StatusUpdate(taskId, state, serializedData))
  }

  override def applicationId(): String = appId

  private def stop(finalState: SparkAppHandle.State): Unit = {
    localEndpoint.ask(StopExecutor)
    try {
      launcherBackend.setState(finalState)
    } finally {
      launcherBackend.close()
    }
  }
}

```

## 6.2.TaskSchedulerImpl的初始化
```
  private def createTaskScheduler(
      sc: SparkContext,
      master: String): (SchedulerBackend, TaskScheduler) = {
    import SparkMasterRegex._

    // When running locally, don't try to re-execute tasks on failure.
    val MAX_LOCAL_TASK_FAILURES = 1

    master match {
      case "local" =>
        val scheduler = new TaskSchedulerImpl(sc, MAX_LOCAL_TASK_FAILURES, isLocal = true)
        val backend = new LocalBackend(sc.getConf, scheduler, 1)
        scheduler.initialize(backend)
        (backend, scheduler)

//....

```
创建完TaskSchedulerImpl和LocalBackend后对TaskSchedulerImpl调用方法initialize进行初始化,以默认的fifo调度为例,TaskSchedulerImpl的初始化过程如下:
1.使TaskSchedulerImpl持有LocalBackend的引用
2.创建Pool,Pool中缓存了调度队列,调度算法及TaskSetManager集合等信息
3.创建FIFOSchedulableBuilder,FIFOSchedulableBuilder用来操作Pool中的调度队列

initialize方法的实现如下:

```
  def initialize(backend: SchedulerBackend) {
    this.backend = backend
    // temporarily set rootPool name to empty
    rootPool = new Pool("", schedulingMode, 0, 0)
    schedulableBuilder = {
      schedulingMode match {
        case SchedulingMode.FIFO =>
          new FIFOSchedulableBuilder(rootPool)
        case SchedulingMode.FAIR =>
          new FairSchedulableBuilder(rootPool, conf)
      }
    }
    schedulableBuilder.buildPools()
  }

```


# 7.创建和启动DAGScheduler
DAGScheduler主要在任务正式交给TaskSchedulerImpl提交之前做一些准备工作,包括:创建Job,将DAG中的RDD划分到不同的stage,提交stage等等,创建DAGScheduler的代码在SparkContext中如下:
```
_dagScheduler = new DAGScheduler(this)

```

DAGScheduler的数据结构主要维护jobId和stageId的关系,Stage,ActiveJob,以及缓存的RDD的Partitions的位置信息,代码如下:
```

  private[scheduler] val nextJobId = new AtomicInteger(0)
  private[scheduler] def numTotalJobs: Int = nextJobId.get()
  private val nextStageId = new AtomicInteger(0)

  private[scheduler] val jobIdToStageIds = new HashMap[Int, HashSet[Int]]
  private[scheduler] val stageIdToStage = new HashMap[Int, Stage]
  private[scheduler] val shuffleToMapStage = new HashMap[Int, ShuffleMapStage]
  private[scheduler] val jobIdToActiveJob = new HashMap[Int, ActiveJob]

  // Stages we need to run whose parents aren't done
  private[scheduler] val waitingStages = new HashSet[Stage]

  // Stages we are running right now
  private[scheduler] val runningStages = new HashSet[Stage]

  // Stages that must be resubmitted due to fetch failures
  private[scheduler] val failedStages = new HashSet[Stage]

  private[scheduler] val activeJobs = new HashSet[ActiveJob]

  /**
   * Contains the locations that each RDD's partitions are cached on.  This map's keys are RDD ids
   * and its values are arrays indexed by partition numbers. Each array value is the set of
   * locations where that RDD partition is cached.
   *
   * All accesses to this map should be guarded by synchronizing on it (see SPARK-4454).
   */
  private val cacheLocs = new HashMap[Int, IndexedSeq[Seq[TaskLocation]]]

  // For tracking failed nodes, we use the MapOutputTracker's epoch number, which is sent with
  // every task. When we detect a node failing, we note the current epoch number and failed
  // executor, increment it for new tasks, and use this to ignore stray ShuffleMapTask results.
  //
  // TODO: Garbage collect information about failure epochs when we know there are no more
  //       stray messages to detect.
  private val failedEpoch = new HashMap[String, Long]

  private [scheduler] val outputCommitCoordinator = env.outputCommitCoordinator

  // A closure serializer that we reuse.
  // This is only safe because DAGScheduler runs in a single thread.
  private val closureSerializer = SparkEnv.get.closureSerializer.newInstance()

  private[scheduler] val eventProcessLoop = new DAGSchedulerEventProcessLoop(this)

```

上面的代码中有一个DAGSchedulerEventProcessLoop,他继承了EventLoop,EventLoop的实现如下:
```

private[spark] abstract class EventLoop[E](name: String) extends Logging {

  private val eventQueue: BlockingQueue[E] = new LinkedBlockingDeque[E]()

  private val stopped = new AtomicBoolean(false)

  private val eventThread = new Thread(name) {
    setDaemon(true)

    override def run(): Unit = {
      try {
        while (!stopped.get) {
          val event = eventQueue.take()
          try {
            onReceive(event)
          } catch {
            case NonFatal(e) => {
              try {
                onError(e)
              } catch {
                case NonFatal(e) => logError("Unexpected error in " + name, e)
              }
            }
          }
        }
      } catch {
        case ie: InterruptedException => // exit even if eventQueue is not empty
        case NonFatal(e) => logError("Unexpected error in " + name, e)
      }
    }

  }

  def start(): Unit = {
    if (stopped.get) {
      throw new IllegalStateException(name + " has already been stopped")
    }
    // Call onStart before starting the event thread to make sure it happens before onReceive
    onStart()
    eventThread.start()
  }

  def stop(): Unit = {
    if (stopped.compareAndSet(false, true)) {
      eventThread.interrupt()
      var onStopCalled = false
      try {
        eventThread.join()
        // Call onStop after the event thread exits to make sure onReceive happens before onStop
        onStopCalled = true
        onStop()
      } catch {
        case ie: InterruptedException =>
          Thread.currentThread().interrupt()
          if (!onStopCalled) {
            // ie is thrown from `eventThread.join()`. Otherwise, we should not call `onStop` since
            // it's already called.
            onStop()
          }
      }
    } else {
      // Keep quiet to allow calling `stop` multiple times.
    }
  }

  /**
   * Put the event into the event queue. The event thread will process it later.
   */
  def post(event: E): Unit = {
    eventQueue.put(event)
  }


```
他会有一个线程从队列中循环取Event,然后调用onReceive(event)去处理事件,onReceive在DAGSchedulerEventProcessLoop类中实现,最终是match去匹配处理不同的事件
```
  override def onReceive(event: DAGSchedulerEvent): Unit = {
    val timerContext = timer.time()
    try {
      doOnReceive(event)
    } finally {
      timerContext.stop()
    }
  }

  private def doOnReceive(event: DAGSchedulerEvent): Unit = event match {
    case JobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties) =>
      dagScheduler.handleJobSubmitted(jobId, rdd, func, partitions, callSite, listener, properties)

    case MapStageSubmitted(jobId, dependency, callSite, listener, properties) =>
      dagScheduler.handleMapStageSubmitted(jobId, dependency, callSite, listener, properties)

    case StageCancelled(stageId) =>
      dagScheduler.handleStageCancellation(stageId)

    case JobCancelled(jobId) =>
      dagScheduler.handleJobCancellation(jobId)

    case JobGroupCancelled(groupId) =>
      dagScheduler.handleJobGroupCancelled(groupId)

    case AllJobsCancelled =>
      dagScheduler.doCancelAllJobs()

    case ExecutorAdded(execId, host) =>
      dagScheduler.handleExecutorAdded(execId, host)

    case ExecutorLost(execId) =>
      dagScheduler.handleExecutorLost(execId, fetchFailed = false)

    case BeginEvent(task, taskInfo) =>
      dagScheduler.handleBeginEvent(task, taskInfo)

    case GettingResultEvent(taskInfo) =>
      dagScheduler.handleGetTaskResult(taskInfo)

    case completion @ CompletionEvent(task, reason, _, _, taskInfo, taskMetrics) =>
      dagScheduler.handleTaskCompletion(completion)

    case TaskSetFailed(taskSet, reason, exception) =>
      dagScheduler.handleTaskSetFailed(taskSet, reason, exception)

    case ResubmitFailedStages =>
      dagScheduler.resubmitFailedStages()
  }

```

# 8.TaskScheduler的启动

在3.6节介绍了任务调度器TaskScheduler的创建,要想TaskScheduler发挥作用,必须要启动它,如下代码:
```

  // start TaskScheduler after taskScheduler sets DAGScheduler reference in DAGScheduler's constructor
  _taskScheduler.start()

```
TaskScheduler在启动的时候,实际调用了Backend的start方法,以TaskSchedulerImpl为例
```
  override def start() {
    backend.start()

    if (!isLocal && conf.getBoolean("spark.speculation", false)) {
      logInfo("Starting speculative execution thread")
      speculationScheduler.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = Utils.tryOrStopSparkContext(sc) {
          checkSpeculatableTasks()
        }
      }, SPECULATION_INTERVAL_MS, SPECULATION_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }
  }

```

以LocalBackend为例,启动LocalBackend时向rpcEnv注册了LocalEndpoint,下面是LocalBackend的start方法
```
  override def start() {
    val rpcEnv = SparkEnv.get.rpcEnv
    val executorEndpoint = new LocalEndpoint(rpcEnv, userClassPath, scheduler, this, totalCores)
    localEndpoint = rpcEnv.setupEndpoint("LocalBackendEndpoint", executorEndpoint)
    listenerBus.post(SparkListenerExecutorAdded(
      System.currentTimeMillis,
      executorEndpoint.localExecutorId,
      new ExecutorInfo(executorEndpoint.localExecutorHostname, totalCores, Map.empty)))
    launcherBackend.setAppId(appId)
    launcherBackend.setState(SparkAppHandle.State.RUNNING)
  }

```

## 8.1.创建LocalEndpoint
LocalEndpoint的创建过程主要是构建本地的Executor,如下:

```
private[spark] class LocalEndpoint(
    override val rpcEnv: RpcEnv,
    userClassPath: Seq[URL],
    scheduler: TaskSchedulerImpl,
    executorBackend: LocalBackend,
    private val totalCores: Int)
  extends ThreadSafeRpcEndpoint with Logging {

  private var freeCores = totalCores

  val localExecutorId = SparkContext.DRIVER_IDENTIFIER
  val localExecutorHostname = "localhost"

  private val executor = new Executor(
    localExecutorId, localExecutorHostname, SparkEnv.get, userClassPath, isLocal = true)

  override def receive: PartialFunction[Any, Unit] = {
    case ReviveOffers =>
      reviveOffers()

    case StatusUpdate(taskId, state, serializedData) =>
      scheduler.statusUpdate(taskId, state, serializedData)
      if (TaskState.isFinished(state)) {
        freeCores += scheduler.CPUS_PER_TASK
        reviveOffers()
      }

    case KillTask(taskId, interruptThread) =>
      executor.killTask(taskId, interruptThread)
  }

//...
}
```

Executor的构建代码如下:
```

  // Start worker thread pool
  private val threadPool = ThreadUtils.newDaemonCachedThreadPool("Executor task launch worker")
  private val executorSource = new ExecutorSource(threadPool, executorId)

  if (!isLocal) {
    env.metricsSystem.registerSource(executorSource)
    env.blockManager.initialize(conf.getAppId)
  }

  // Whether to load classes in user jars before those in Spark jars
  private val userClassPathFirst = conf.getBoolean("spark.executor.userClassPathFirst", false)

  // Create our ClassLoader
  // do this after SparkEnv creation so can access the SecurityManager
  private val urlClassLoader = createClassLoader()
  private val replClassLoader = addReplClassLoaderIfNeeded(urlClassLoader)

  // Set the classloader for serializer
  env.serializer.setDefaultClassLoader(replClassLoader)

  // Akka's message frame size. If task result is bigger than this, we use the block manager
  // to send the result back.
  private val akkaFrameSize = AkkaUtils.maxFrameSizeBytes(conf)

  // Limit of bytes for total size of results (default is 1GB)
  private val maxResultSize = Utils.getMaxResultSize(conf)

  // Maintains the list of running tasks.
  private val runningTasks = new ConcurrentHashMap[Long, TaskRunner]



```
Executor的构建,主要包括以下步骤:
1.创建并注册ExecutorSource,ExecutorSource是做什么的呢?在8.2节会有介绍
2.获取SparkEnv,如果是非local模式,Worker上的CoarseGrainedExecutorBackend向Driver的CoarseGrainedExecutorBackend注册Executor时,则需要新建SparkEnv
3.创建并注册ExecutorActor,Executor负责接收发送给Executor的消息(在spark1.6中没有找到)
4.urlClassLoader的创建,为什么需要创建这个urlClassLoader?在非local模式中,Driver或者Worker上都会有多个Executor,每个Executor都设置自身的urlClassLoader,用于加载任务上传的jar包中的类,有效的对任务的类加载环境进行隔离
5.创建Executor执行的Task的线程池,此线程池用于执行任务
6.启动Executor的心跳线程,此线程用于向Driver发送心跳

此外还包括Akka发送消息的帧大小,结果总大小的字节限制,正在运行的task的列表,设置serializer的默认ClassLoader为创建的ClassLoader等


## 8.2.ExecutorSource的创建与注册

ExecutorSource用于测量系统,通过metriRegistry的register方法注册计量,这些计量信息包括threadpool.activeTasks,threadpool.completeTasks,threadpool.currentPool_size,threadpool.maxPool_size等,详见代码:
```

class ExecutorSource(threadPool: ThreadPoolExecutor, executorId: String) extends Source {

  private def fileStats(scheme: String) : Option[FileSystem.Statistics] =
    FileSystem.getAllStatistics.asScala.find(s => s.getScheme.equals(scheme))

  private def registerFileSystemStat[T](
        scheme: String, name: String, f: FileSystem.Statistics => T, defaultValue: T) = {
    metricRegistry.register(MetricRegistry.name("filesystem", scheme, name), new Gauge[T] {
      override def getValue: T = fileStats(scheme).map(f).getOrElse(defaultValue)
    })
  }

  override val metricRegistry = new MetricRegistry()

  override val sourceName = "executor"

  // Gauge for executor thread pool's actively executing task counts
  metricRegistry.register(MetricRegistry.name("threadpool", "activeTasks"), new Gauge[Int] {
    override def getValue: Int = threadPool.getActiveCount()
  })

  // Gauge for executor thread pool's approximate total number of tasks that have been completed
  metricRegistry.register(MetricRegistry.name("threadpool", "completeTasks"), new Gauge[Long] {
    override def getValue: Long = threadPool.getCompletedTaskCount()
  })

  // Gauge for executor thread pool's current number of threads
  metricRegistry.register(MetricRegistry.name("threadpool", "currentPool_size"), new Gauge[Int] {
    override def getValue: Int = threadPool.getPoolSize()
  })

  // Gauge got executor thread pool's largest number of threads that have ever simultaneously
  // been in th pool
  metricRegistry.register(MetricRegistry.name("threadpool", "maxPool_size"), new Gauge[Int] {
    override def getValue: Int = threadPool.getMaximumPoolSize()
  })


```

创建完ExecutorSource后,调用MetricsSystem的registerSource方法将ExecutorSource注册到MetricsSystem
```
  if (!isLocal) {
    env.metricsSystem.registerSource(executorSource)
    env.blockManager.initialize(conf.getAppId)
  }



  def registerSource(source: Source) {
    sources += source
    try {
      val regName = buildRegistryName(source)
      registry.register(regName, source.metricRegistry)
    } catch {
      case e: IllegalArgumentException => logInfo("Metrics already registered", e)
    }
  }
```



## 8.4.spark自身ClassLoader的创建

获取要创建的ClassLoader的父加载器currentLoader,然后根据currentJars生成URL数组,spark.files.userClassPathFirst属性指定加载类时是否先从用户的classpath下加载,最后创建MutableURLClassLoader或者ChildExecutorURLClassLoader

```

  private def createClassLoader(): MutableURLClassLoader = {
    // Bootstrap the list of jars with the user class path.
    val now = System.currentTimeMillis()
    userClassPath.foreach { url =>
      currentJars(url.getPath().split("/").last) = now
    }

    val currentLoader = Utils.getContextOrSparkClassLoader

    // For each of the jars in the jarSet, add them to the class loader.
    // We assume each of the files has already been fetched.
    val urls = userClassPath.toArray ++ currentJars.keySet.map { uri =>
      new File(uri.split("/").last).toURI.toURL
    }
    if (userClassPathFirst) {
      new ChildFirstURLClassLoader(urls, currentLoader)
    } else {
      new MutableURLClassLoader(urls, currentLoader)
    }
  }

```

## 8.5.启动Executor的心跳线程
Executor的心跳由startDriverHeadrtbeater启动,在Executor类中的代码如下:
```
  // Executor for the heartbeat task.
  private val heartbeater = ThreadUtils.newDaemonSingleThreadScheduledExecutor("driver-heartbeater")

  // must be initialized before running startDriverHeartbeat()
  private val heartbeatReceiverRef =
    RpcUtils.makeDriverRef(HeartbeatReceiver.ENDPOINT_NAME, conf, env.rpcEnv)

  startDriverHeartbeater()


  private def startDriverHeartbeater(): Unit = {
    val intervalMs = conf.getTimeAsMs("spark.executor.heartbeatInterval", "10s")

    // Wait a random interval so the heartbeats don't end up in sync
    val initialDelay = intervalMs + (math.random * intervalMs).asInstanceOf[Int]

    val heartbeatTask = new Runnable() {
      override def run(): Unit = Utils.logUncaughtExceptions(reportHeartBeat())
    }
    heartbeater.scheduleAtFixedRate(heartbeatTask, initialDelay, intervalMs, TimeUnit.MILLISECONDS)
  }


```

Executor心跳线程的间隔由属性spark.executor.heartbeatInterval配置,默认是10s,此外还有一个延迟时间,最终是调用run方法中的reportHeartBeat方法,该方法代码如下:
```

  /** Reports heartbeat and metrics for active tasks to the driver. */
  private def reportHeartBeat(): Unit = {
    // list of (task id, metrics) to send back to the driver
    val tasksMetrics = new ArrayBuffer[(Long, TaskMetrics)]()
    val curGCTime = computeTotalGcTime()

    for (taskRunner <- runningTasks.values().asScala) {
      if (taskRunner.task != null) {
        taskRunner.task.metrics.foreach { metrics =>
          metrics.updateShuffleReadMetrics()
          metrics.updateInputMetrics()
          metrics.setJvmGCTime(curGCTime - taskRunner.startGCTime)
          metrics.updateAccumulators()

          if (isLocal) {
            // JobProgressListener will hold an reference of it during
            // onExecutorMetricsUpdate(), then JobProgressListener can not see
            // the changes of metrics any more, so make a deep copy of it
            val copiedMetrics = Utils.deserialize[TaskMetrics](Utils.serialize(metrics))
            tasksMetrics += ((taskRunner.taskId, copiedMetrics))
          } else {
            // It will be copied by serialization
            tasksMetrics += ((taskRunner.taskId, metrics))
          }
        }
      }
    }

    val message = Heartbeat(executorId, tasksMetrics.toArray, env.blockManager.blockManagerId)
    try {
      val response = heartbeatReceiverRef.askWithRetry[HeartbeatResponse](
          message, RpcTimeout(conf, "spark.executor.heartbeatInterval", "10s"))
      if (response.reregisterBlockManager) {
        logInfo("Told to re-register on heartbeat")
        env.blockManager.reregister()
      }
    } catch {
      case NonFatal(e) => logWarning("Issue communicating with driver in heartbeater", e)
    }
  }

```


这个心跳的作用有两个
1.更新正在处理的任务的测量信息
2.通知BlockManagerMaster,此Executor上的BlockManager依然活着



下面是对心跳线程的实现详细分析
初始化TaskSchedulerImpl后会创建心跳接收器HeartbeatReceiver,HeartbeatReceiver接收所有分配给当前DriverApplication的Executor的心跳,并将Task,Task计量信息,心跳等交给TaskSchedulerImpl和DAGScheduler作进一步处理,创建心跳接收器的代码在SparkContext中如下
```
// We need to register "HeartbeatReceiver" before "createTaskScheduler" because Executor will
// retrieve "HeartbeatReceiver" in the constructor. (SPARK-6640)
_heartbeatReceiver = env.rpcEnv.setupEndpoint(
  HeartbeatReceiver.ENDPOINT_NAME, new HeartbeatReceiver(this))

```

HeartbeatReceiver在接收到心跳消息后,会调用TaskScheduler的executorHeartbeatReceived方法
```
  override def executorHeartbeatReceived(
      execId: String,
      taskMetrics: Array[(Long, TaskMetrics)], // taskId -> TaskMetrics
      blockManagerId: BlockManagerId): Boolean = {

    val metricsWithStageIds: Array[(Long, Int, Int, TaskMetrics)] = synchronized {
      taskMetrics.flatMap { case (id, metrics) =>
        taskIdToTaskSetManager.get(id).map { taskSetMgr =>
          (id, taskSetMgr.stageId, taskSetMgr.taskSet.stageAttemptId, metrics)
        }
      }
    }
    dagScheduler.executorHeartbeatReceived(execId, metricsWithStageIds, blockManagerId)
  }

```

这段程序通过遍历taskMetrices,依据taskIdToTaskSetId和activeTaskSets找到TaskSetManager,然后将taskId,TaskSetManager.stageId,TaskSetManager.taskSet.attempt,TaskMetrices封装到Array[(Long, Int, Int, TaskMetrics)]的数组中,最后调用dagScheduler的executorHeartbeatReceived方法
```
  def executorHeartbeatReceived(
      execId: String,
      taskMetrics: Array[(Long, Int, Int, TaskMetrics)], // (taskId, stageId, stateAttempt, metrics)
      blockManagerId: BlockManagerId): Boolean = {
    listenerBus.post(SparkListenerExecutorMetricsUpdate(execId, taskMetrics))
    blockManagerMaster.driverEndpoint.askWithRetry[Boolean](
      BlockManagerHeartbeat(blockManagerId), new RpcTimeout(600 seconds, "BlockManagerHeartbeat"))
  }
```
dagScheduler将executorId,metricsWithStageIds封装为SparkListenerExecutorMetricsUpdate事件,并post到listenerBus中,此事件用于更新stage的各种测量数据,最后给BlockManagerMaster持有的driverEndpoint发送BlockManagerHeartbeat,在local模式下Executor的心跳通信过程如下图:


![](/assert/img/bigdata/深入理解spark核心思想与源码分析/2/heartbeat.png)


在非local模式下,Executor发送心跳的过程是一样的,主要的区别是Executor进程与Driver不再同一个进程,甚至不再同一个节点上





# 9.启动测量系统MetricsSystem

MetricsSystem使用codahale提供的第三方测量仓库Metrics,MetricsSystem中有三个概念:
1.Instance:指定了谁在使用测量系统
2.Source:指定了从哪里收集测量数据
3.Sink:指定了往哪里输出测量数据


Spark按照Instance的不同,区分为Master,Worker,Application,Driver和Executor

Spark目前提供的Sink有ConsoleSink,CsvSink,jmxSink,MetricsServlet,GraphiteSink等

Spark中使用MetriceServlet作为模式的Sink

MetricsSystem的启动代码在SparkContext中如下:
```

def metricsSystem: MetricsSystem = if (_env != null) _env.metricsSystem else null

metricsSystem.start()


```
MetricsSystem的启动过程包括以下的步骤:
1.注册Source
2.注册Sinks
3.给Sinks增加Jetty的ServletContextHandler

MetricsSystem启动完毕后,会遍历与Sinks有关的ServletContextHandler,并调用attachHandle将他们绑定到SparkUI上

```
// Attach the driver metrics servlet handler to the web ui after the metrics system is started.
metricsSystem.getServletHandlers.foreach(handler => ui.foreach(_.attachHandler(handler)))

```
start方法的实现如下:

```
  def start() {
    require(!running, "Attempting to start a MetricsSystem that is already running")
    running = true
    registerSources()
    registerSinks()
    sinks.foreach(_.start)
  }

```



## 9.1.注册Sources
registerSources方法用于注册Sources,告诉测量系统从哪里收集测量数据,代码实现如下:
```
  private def registerSources() {
    val instConfig = metricsConfig.getInstance(instance)
    val sourceConfigs = metricsConfig.subProperties(instConfig, MetricsSystem.SOURCE_REGEX)

    // Register all the sources related to instance
    sourceConfigs.foreach { kv =>
      val classPath = kv._2.getProperty("class")
      try {
        val source = Utils.classForName(classPath).newInstance()
        registerSource(source.asInstanceOf[Source])
      } catch {
        case e: Exception => logError("Source class " + classPath + " cannot be instantiated", e)
      }
    }
  }

```

注册Sources的过程分为以下步骤
1.从metricsConfig获取Driver的Properties,默认为创建MetricsSystem的过程中解析的
2.用正则匹配Driver的Properties中以source.开头的属性,然后将属性中的Source反射得到的实例加入ArrayBuffer[Source]
3.将每个source的metricRegistry注册到ConcurrentMap<String,Metric>metrics



## 9.2.注册Sinks

registerSinks方法用于注册Sinks,即告诉测量系统MetricsSystem往哪里输出测量数据,他的实现代码如下:

```

  private def registerSinks() {
    val instConfig = metricsConfig.getInstance(instance)
    val sinkConfigs = metricsConfig.subProperties(instConfig, MetricsSystem.SINK_REGEX)

    sinkConfigs.foreach { kv =>
      val classPath = kv._2.getProperty("class")
      if (null != classPath) {
        try {
          val sink = Utils.classForName(classPath)
            .getConstructor(classOf[Properties], classOf[MetricRegistry], classOf[SecurityManager])
            .newInstance(kv._2, registry, securityMgr)
          if (kv._1 == "servlet") {
            metricsServlet = Some(sink.asInstanceOf[MetricsServlet])
          } else {
            sinks += sink.asInstanceOf[Sink]
          }
        } catch {
          case e: Exception => {
            logError("Sink class " + classPath + " cannot be instantiated")
            throw e
          }
        }
      }
    }
  }

```

注册Sinks的步骤如下:
1.从Driver的Properties中用正则匹配以sink.开头的属性,如
```
{sink.servlet.class=org.apache.spark.metrics.sink.MetricsServlet，sink.servlet.path=/metrics/json}
```
将其转换为Map（servlet->{class=org.apache.spark.metrics.sink.MetricsServlet，path=/metrics/json}）
2.将子属性class对应的类metricsServlet反射得到MetricsServlet实例,如果属性的key是servlet,将其设置为metricsServlet,如果是sink,则加入到ArrayBuffer[Sink]中


## 9.3.给Sinks增加Jetty的ServletContextHandler
为了能够在SparkUI(网页)访问到测量数据,所以需要给Sinks增加Jetty的ServletContextHandler,这里主要用到MetricsSystem的getServletHandlers方法,实现如下:
```
/**
 * Get any UI handlers used by this metrics system; can only be called after start().
 */
def getServletHandlers: Array[ServletContextHandler] = {
  require(running, "Can only call getServletHandlers on a running MetricsSystem")
  metricsServlet.map(_.getHandlers(conf)).getOrElse(Array())
}

```
可以看到调用了metricsServlet的getHandlers,其实现如下
```
def getHandlers(conf: SparkConf): Array[ServletContextHandler] = {
  Array[ServletContextHandler](
    createServletHandler(servletPath,
      new ServletParams(request => getMetricsSnapshot(request), "text/json"), securityMgr, conf)
  )
}

```

最终生成处理/metrics/json请求的ServletContextHandler,而请求的真正处理由getMetricsSnapshot方法,利用fastjson解析,生成的ServletContextHandler通过SparkUI的attachHandler方法,也被绑定到SparkUI,最红我们可以使用以下这些地址来访问测量数据:
```
http://localhost:4040/metrics/applications/json
http://localhost:4040/metrics/json
http://localhost:4040/metrics/master/json

```

# 10.创建和启动ExecutorAllocationManager

ExecutorAllocationManager用于对已分配的Executor进行管理,创建和启动ExecutorAllocationManager的代码如下
```
 _executorAllocationManager =
   if (dynamicAllocationEnabled) {
     Some(new ExecutorAllocationManager(this, listenerBus, _conf))
   } else {
     None
   }

 _executorAllocationManager.foreach(_.start())


```

默认情况下不会创建ExecutorAllocationManager,可以修改属性spark.dynamicAllocation.enabled为true来创建,ExecutorAllocationManager可以设置动态分配最小Executor数量,动态分配最大Executor数量,每个Executor可以运行的Task数量等配置信息,并对配置信息进行校验,start方法将ExecutorAllocationListener加入listenerBus中,ExecutorAllocationListener通过监听listenBus里的事件,动态添加,删除Executor,并且通过Thread不断添加Executor,遍历Executor,将超时的Executor杀掉并移除,ExecutorAllocationListener的实现与其他SparkListener类似,ExecutorAllocationManager的关键代码如下:
```
  // Clock used to schedule when executors should be added and removed
  private var clock: Clock = new SystemClock()

  // Listener for Spark events that impact the allocation policy
  private val listener = new ExecutorAllocationListener



  /**
   * Register for scheduler callbacks to decide when to add and remove executors, and start
   * the scheduling task.
   */
  def start(): Unit = {
    listenerBus.addListener(listener)

    val scheduleTask = new Runnable() {
      override def run(): Unit = {
        try {
          schedule()
        } catch {
          case ct: ControlThrowable =>
            throw ct
          case t: Throwable =>
            logWarning(s"Uncaught exception in thread ${Thread.currentThread().getName}", t)
        }
      }
    }
    executor.scheduleAtFixedRate(scheduleTask, 0, intervalMillis, TimeUnit.MILLISECONDS)
  }

```



# 11.ContextCleaner的创建与启动
ContextCleaner用于清理那些超出应用范围的RDD,ShuffleDependency和Broadcast对象,由于配置属性spark.cleaner.referenceTracking默认是true,所以会构造并请ContextCleaner,代码如下:
```
_cleaner =
  if (_conf.getBoolean("spark.cleaner.referenceTracking", true)) {
    Some(new ContextCleaner(this))
  } else {
    None
  }

_cleaner.foreach(_.start())

```

ContextCleaner的组成如下:
1.referenceQueue:缓存顶级的AnyRef引用
2.referenceBuffer:缓存AnyRef的虚引用
3.listeners:缓存清理工作的监听器数组
4.cleaningThread:用于具体清理工作的线程

```
private[spark] class ContextCleaner(sc: SparkContext) extends Logging {

  private val referenceBuffer = new ArrayBuffer[CleanupTaskWeakReference]
    with SynchronizedBuffer[CleanupTaskWeakReference]

  private val referenceQueue = new ReferenceQueue[AnyRef]

  private val listeners = new ArrayBuffer[CleanerListener]
    with SynchronizedBuffer[CleanerListener]

  private val cleaningThread = new Thread() { override def run() { keepCleaning() }}

///....

}

```

ContextCleaner的工作原理和listenerBus一样,也采用监听器模式,由线程来处理,此线程实际只是调用keepCleaning方法,该方法的实现如下:
```

  /** Keep cleaning RDD, shuffle, and broadcast state. */
  private def keepCleaning(): Unit = Utils.tryOrStopSparkContext(sc) {
    while (!stopped) {
      try {
        val reference = Option(referenceQueue.remove(ContextCleaner.REF_QUEUE_POLL_TIMEOUT))
          .map(_.asInstanceOf[CleanupTaskWeakReference])
        // Synchronize here to avoid being interrupted on stop()
        synchronized {
          reference.map(_.task).foreach { task =>
            logDebug("Got cleaning task " + task)
            referenceBuffer -= reference.get
            task match {
              case CleanRDD(rddId) =>
                doCleanupRDD(rddId, blocking = blockOnCleanupTasks)
              case CleanShuffle(shuffleId) =>
                doCleanupShuffle(shuffleId, blocking = blockOnShuffleCleanupTasks)
              case CleanBroadcast(broadcastId) =>
                doCleanupBroadcast(broadcastId, blocking = blockOnCleanupTasks)
              case CleanAccum(accId) =>
                doCleanupAccum(accId, blocking = blockOnCleanupTasks)
              case CleanCheckpoint(rddId) =>
                doCleanCheckpoint(rddId)
            }
          }
        }
      } catch {
        case ie: InterruptedException if stopped => // ignore
        case e: Exception => logError("Error in cleaning thread", e)
      }
    }
  }

```


# 12.Spark环境更新

在SparkContext的初始化过程中,可能对其环境造成影响,所以需要更新环境,代码如下:
```
postEnvironmentUpdate()
postApplicationStart()

```
SparkContext初始化过程中,如果设置了spark.jars属性,spark.jars指定的jar包将有addJar方法假如HttpFileServer的jarDir变量指定的路径下,spark.files指定的文件将由addFile方法假如HttpFileServer的fileDir变量指定的路径下,如下:
```
  /** Post the environment update event once the task scheduler is ready */
  private def postEnvironmentUpdate() {
    if (taskScheduler != null) {
      val schedulingMode = getSchedulingMode.toString
      val addedJarPaths = addedJars.keys.toSeq
      val addedFilePaths = addedFiles.keys.toSeq
      val environmentDetails = SparkEnv.environmentDetails(conf, schedulingMode, addedJarPaths,
        addedFilePaths)
      val environmentUpdate = SparkListenerEnvironmentUpdate(environmentDetails)
      listenerBus.post(environmentUpdate)
    }
  }

```

httpFileServer的addFile和addJar方法,如下:
```
  def addFile(file: File) : String = {
    addFileToDir(file, fileDir)
    serverUri + "/files/" + Utils.encodeFileNameToURIRawPath(file.getName)
  }

  def addJar(file: File) : String = {
    addFileToDir(file, jarDir)
    serverUri + "/jars/" + Utils.encodeFileNameToURIRawPath(file.getName)
  }

```

postEnvironmentUpdate的实现见代码
```
  private def postEnvironmentUpdate() {
    if (taskScheduler != null) {
      val schedulingMode = getSchedulingMode.toString
      val addedJarPaths = addedJars.keys.toSeq
      val addedFilePaths = addedFiles.keys.toSeq
      val environmentDetails = SparkEnv.environmentDetails(conf, schedulingMode, addedJarPaths,
        addedFilePaths)
      val environmentUpdate = SparkListenerEnvironmentUpdate(environmentDetails)
      listenerBus.post(environmentUpdate)
    }
  }

```

其处理步骤如下:
1.通过调用SparkEnv的方法environmentDetails最终影响环境的JVM参数,/Spark属性,系统属性,classpath等
2.生成时间SparkListenerEnvironmentUpdate,并post到listenerBus,此事件被EnvironmentListener监听,最终影响EnvironmentPage页面中的额输出内容


environmentDetails的代码实现如下:
```

  def environmentDetails(
      conf: SparkConf,
      schedulingMode: String,
      addedJars: Seq[String],
      addedFiles: Seq[String]): Map[String, Seq[(String, String)]] = {

    import Properties._
    val jvmInformation = Seq(
      ("Java Version", s"$javaVersion ($javaVendor)"),
      ("Java Home", javaHome),
      ("Scala Version", versionString)
    ).sorted

    // Spark properties
    // This includes the scheduling mode whether or not it is configured (used by SparkUI)
    val schedulerMode =
      if (!conf.contains("spark.scheduler.mode")) {
        Seq(("spark.scheduler.mode", schedulingMode))
      } else {
        Seq[(String, String)]()
      }
    val sparkProperties = (conf.getAll ++ schedulerMode).sorted

    // System properties that are not java classpaths
    val systemProperties = Utils.getSystemProperties.toSeq
    val otherProperties = systemProperties.filter { case (k, _) =>
      k != "java.class.path" && !k.startsWith("spark.")
    }.sorted

    // Class paths including all added jars and files
    val classPathEntries = javaClassPath
      .split(File.pathSeparator)
      .filterNot(_.isEmpty)
      .map((_, "System Classpath"))
    val addedJarsAndFiles = (addedJars ++ addedFiles).map((_, "Added By User"))
    val classPaths = (addedJarsAndFiles ++ classPathEntries).sorted

    Map[String, Seq[(String, String)]](
      "JVM Information" -> jvmInformation,
      "Spark Properties" -> sparkProperties,
      "System Properties" -> otherProperties,
      "Classpath Entries" -> classPaths)
  }
}

```


 postApplicationStart()方法很简单,只是向listenerBus发送了SparkListenerApplicationStart事件,代码如下:
```
  private def postApplicationStart() {

    listenerBus.post(SparkListenerApplicationStart(appName, Some(applicationId),
      startTime, sparkUser, applicationAttemptId, schedulerBackend.getDriverLogUrls))
  }

```



# 13.创建DAGSchedulerSource和BlockManagerSource

在创建DAGSchedulerSource,BlockManagerSource之前首先会调用TaskScheduler的postStartHook方法,其目的是为了等待Backend就绪,如下代码:
```
// Post init
_taskScheduler.postStartHook()
_env.metricsSystem.registerSource(_dagScheduler.metricsSource)
_env.metricsSystem.registerSource(new BlockManagerSource(_env.blockManager))

_executorAllocationManager.foreach { e =>
  _env.metricsSystem.registerSource(e.executorAllocationManagerSource)
}

```
postStartHook方法如下
```
  override def postStartHook() {
    waitBackendReady()
  }

  private def waitBackendReady(): Unit = {
    if (backend.isReady) {
      return
    }
    while (!backend.isReady) {
      synchronized {
        this.wait(100)
      }
    }
  }

```




# 14.将SparkContext标记为激活

SparkContext初始化的最后将当前SparkContext的状态从contextBeingConstructed(正在构建中)改为activeContext(已激活),代码如下:
```
  // In order to prevent multiple SparkContexts from being active at the same time, mark this
  // context as having finished construction.
  // NOTE: this must be placed at the end of the SparkContext constructor.
  SparkContext.setActiveContext(this, allowMultipleContexts)

```

setActiveContext的方法实现如下:
```

  /**
   * Called at the end of the SparkContext constructor to ensure that no other SparkContext has
   * raced with this constructor and started.
   */
  private[spark] def setActiveContext(
      sc: SparkContext,
      allowMultipleContexts: Boolean): Unit = {
    SPARK_CONTEXT_CONSTRUCTOR_LOCK.synchronized {
      assertNoOtherContextIsRunning(sc, allowMultipleContexts)
      contextBeingConstructed = None
      activeContext.set(sc)
    }
  }


```