---
title: zookeeper会话管理
categories: hadoop   
tags: [zookeeper]
---


# 什么是会话?

* 代表客户端与服务器端的一个zk连接
* 底层通信是通过tcp协议进行连接通信
* zookeeper会在服务器端创建一个会话对象来维护这个连接属性
* 当网络出现断网的抖动现象时,并不代表会话一定断开
* 会话对象的实现是SessionImpl,包括以下四个属性
	* sessionID:唯一标识一个会话,具备全局唯一性
	* Timeout:会话超时时间,创建zookeeper客户端对象时传入,服务器会根据最小会话时间和最大会话时间来明确指定此值具体是什么
	* TickTime:下次会话超时时间
	* isClosing:标记一个会话是否已经被关闭,当服务器端检测到右会话失效时,就会把此会话标记为已关闭


# 会话状态
* Connecting
* connected
* reconnecting
* reconnected
* close
	* 服务器端判断会话超时后,在进行会话清除之前,会把会话的状态置为close

# sessionTracker

服务器端通过此类来管理会话,包括会话的创建,管理和清除

通过三个数据结构从三个维度来管理会话
* sessionById属性:用于根据sessionID来查找session
* sessionWithTimeout:通过sessionID来查找此session的失效时间是什么时候
* sessionSets属性:通过某个时间查询都有哪些会话在这个时间点会失效

```
public class SessionTrackerImpl extends Thread implements SessionTracker {

    HashMap<Long, SessionImpl> sessionsById = new HashMap<Long, SessionImpl>();

    HashMap<Long, SessionSet> sessionSets = new HashMap<Long, SessionSet>();

    ConcurrentHashMap<Long, Integer> sessionsWithTimeout;
}

```

# 分桶策略

把所有的会话按照时间维度来进行分类管理,即同一个时间点失效的会话都在一起管理,即上面的属性ConcurrentHashMap<Long, Integer> sessionsWithTimeout

会话失效时间计算:
* 约定:把所有的时间按照某个单位时间进行等分(默认是服务器的tickTime配置)
* 公式:某次超时时间=((currentTime+sessiontimeout)/ ExpirationInterval +1) x ExpirationInterval

* 举例说明:
	* 由于服务器的tickTime的默认值是2000ms,则ExpirationInterval=2000ms
	* 第一次创建会话时,currenttime=1370907000000
	* 创建会话时,客户端传入的超时时间是15000ms
	* 则,此会话的超时时间为((1370907000000+2000)+1)x2000=1370907016000

当某个会话由于有操作而导致超时时间变化,则会把会话从上一个桶移动到下一个桶中



# 会话激活
* 当此会话一直有操作,则会话就不会失效
* 影响会话失效超时时间的因素
	* 心跳检测,及PING命令
		* 当客户端发现sessionTimeout/3时间范围内还没有任何操作命令产生,就发送一个ping心跳请求
	* 正常业务操作,比如get或者set
* 每次业务操作或者心跳检测,都会重新计算超时时间,然后在桶之间转移会话



# 会话超时检测
* 由于sessionTracker中的一个线程负责检查session是否失效
* 线程检查周期也是ExpirationInterval的倍数
* 当某次检查时,如果在此次的分桶(即前面的ExpirationInterval)之前还有会话,就说明这些会话都超时了,因为会话如果有业务操作或者靠心跳,会不断的从小的分桶迁移到大的分桶

举例:
系统启动时的时间是100001,此时ExpirationInterval=2000ms,则桶的刻度为100001/2000=50,下一次的检查时间为(100001/2000+1)x2000=102000


# 会话清理流程
* 修改会话状态为close
	* 由于清理过程中需要一定时间,为了避免清理期间会话状态发生变化
* 向所有的集群节点发送会话关闭请求
* 收集跟被清理的会话相关的临时节点
* 向集群节点发出删除临时节点的事务请求
* 集群中的所有节点执行删除临时节点事务
* 从sessionTracker的列表中移除会话
* 关闭会话的网络连接,具体类是NIOServerCnxnFactory


# 会话重连

当客户端与服务器端的网络断开后,客户端会不断的重新连接,当连接上后会话的状态时以下两种状态:
* connected:服务器端会话依然存在
* expired:服务器端的会话已经被关闭清除了

注意:网络断开并不代表会话超时




# 三个会话异常:
* connection_loss
* session_expired
* session_moved

connection_loss

网络闪断导致或者是客户端服务器出现问题导致,出现此问题,客户端会重新找地址进行连接,当某个操作过程(比如setData)中出现connection_loss现象,则客户端会收到NoneDisconnected(设置了默认watcher情况下),同时会抛出异常:org.apache.zookeeper.KeeperException$ConnectionLossException,当重新连接上后,客户端会收到事件通知(None-SyncConnected)//在设置了默认watcher时



session_expired

通常发生在connection_loss期间,因为没有网络连接,就不能有操作和心跳进行,会话就会超时,由于重新连接时间较长,导致服务器端关闭了会话,并清除会话,此时会话相关联的watcher等数据都会丢失,watcher失效,出现这种情况,客户端需要重新创建zookeeper对象,并且恢复数据(比如注册watcher),会收到SessionExpiredException



session_moved

出现CONNECTION_LOSS时，客户端尝试重新连接下个节点(connectstring)
例如:客户端刚开始连接的是s1,由于网络中断,客户端尝试连接s2,连接成功之后,s2延续了会话,即会话从s1迁移到了s2
当出现以下业务场景时,服务器端回抛出SessionMovedException异常,由于客户端的连接已经发生了变化(从s1-->s2),所以客户端收不到异常
```
有三台服务器:s1 s2 s3
开始时,客户端连接s1,此时客户端发出一个修改数据的请求r1
在修改数据的请求到达s1之前,客户端重新连接上了s2服务器,此时出现了会话转译
连接s2后,客户端又发起一次数据修改请求r2
r1被s1服务器处理,r2被s2处理(比r1被处理要早)
这样对于客户端来说,请求被处理两次,并且r2被r1的处理结果覆盖了
服务器端通过检查会话的所有者来判断此次会话请求是否合法,不合法就抛出moved异常



```

























