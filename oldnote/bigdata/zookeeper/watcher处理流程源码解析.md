---
title: watcher处理流程源码解析
categories: hadoop   
tags: [zookeeper]
---



# 客户端watcher注册流程

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/watcher/2.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/watcher/3.png)

# 服务器端处理watcher


* 在服务器端的FinalRequestProcessor类,判断是否需要注册watcher
```
public class FinalRequestProcessor implements RequestProcessor {
    ZooKeeperServer zks;

    public FinalRequestProcessor(ZooKeeperServer zks) {
        this.zks = zks;
    }

    public void processRequest(Request request) {
        //...
        ServerCnxn cnxn = request.cnxn;

		//...
        try {
            //...
            switch (request.type) {
				
				case OpCode.create: {//创建节点
					lastOp = "CREA";
					rsp = new CreateResponse(rc.path);
					err = Code.get(rc.err);
					break;
				}
				case OpCode.delete: {//删除节点
					lastOp = "DELE";
					err = Code.get(rc.err);
					break;
				}
				
				case OpCode.getData: {//getData
					lastOp = "GETD";
					GetDataRequest getDataRequest = new GetDataRequest();
					ByteBufferInputStream.byteBuffer2Record(request.request,
							getDataRequest);
					DataNode n = zks.getZKDatabase().getNode(getDataRequest.getPath());
					if (n == null) {
						throw new KeeperException.NoNodeException();
					}
					Long aclL;
					synchronized(n) {
						aclL = n.acl;
					}
					PrepRequestProcessor.checkACL(zks, zks.getZKDatabase().convertLong(aclL),
							ZooDefs.Perms.READ,
							request.authInfo);
					Stat stat = new Stat();
					byte b[] = zks.getZKDatabase().getData(getDataRequest.getPath(), stat,
							getDataRequest.getWatch() ? cnxn : null);//请求中watcher对象,则传入cnxn对象
					rsp = new GetDataResponse(b, stat);
					break;
				}
 
    }

}
```
* ServerCnxn类及cnxn对象
	* zk客户端与服务器之间的tcp连接
	* 实现了watcher接口
	* 总结:既包含了连接信息,有包含了watcher信息

```
public abstract class ServerCnxn implements Stats, Watcher {
    // This is just an arbitrary object to represent requests issued by
    // (aka owned by) this class
    final public static Object me = new Object();
    
    protected ArrayList<Id> authInfo = new ArrayList<Id>();

    boolean isOldClient = true;

    abstract int getSessionTimeout();

    abstract void close();

    public abstract void sendResponse(ReplyHeader h, Record r, String tag)
        throws IOException;

    abstract void sendCloseSession();

    public abstract void process(WatchedEvent event);	//watcher的回调函数
	
	//...
	
}
```
* watcherManager
	* zk服务器Watcher的管理者
	* 从两个维度维护watcher
		* watcherTable:从数据节点的粒度来维护
		* watch2Paths:从watcher的粒度来维护

```
public class WatchManager {
    private static final Logger LOG = LoggerFactory.getLogger(WatchManager.class);

    private final HashMap<String, HashSet<Watcher>> watchTable =
        new HashMap<String, HashSet<Watcher>>();//通过数据节点的路径找到watcher

    private final HashMap<Watcher, HashSet<String>> watch2Paths =
        new HashMap<Watcher, HashSet<String>>();//通过watcher找到数据节点
}
```
* watcher触发
	* DataTree类
		* 维护节点目录树的数据结构

在DataTree类中有如下方法

```
    public Stat setData(String path, byte data[], int version, long zxid,
            long time) throws KeeperException.NoNodeException {
        Stat s = new Stat();
        DataNode n = nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        byte lastdata[] = null;
        synchronized (n) {//此处是更新数据
            lastdata = n.data;
            n.data = data;
            n.stat.setMtime(time);
            n.stat.setMzxid(zxid);
            n.stat.setVersion(version);
            n.copyStat(s);
        }
        // now update if the path is in a quota subtree.
        String lastPrefix;
        if((lastPrefix = getMaxPrefixWithQuota(path)) != null) {
          this.updateBytes(lastPrefix, (data == null ? 0 : data.length)
              - (lastdata == null ? 0 : lastdata.length));
        }
		
        dataWatches.triggerWatch(path, EventType.NodeDataChanged);//触发事件NodeDataChanged
        return s;
    }
```

# 客户端回调watcher
* 客户端回调watcher步骤
	* 反序列化,将字节流转换成WatcherEvent对象
	* 处理chrootPath
	* 还原watchedEvent,把WatcherEvent对象转换成WatchedEvent
	* 回调Watcher:把WatchedEvent对象交给EventThread线程处理

* EventThread
	* 从客户端的ZKWatchManager去取出Watcher,并放入waitingEvents队列中

```
class SendThread extends Thread {

	void readResponse(ByteBuffer incomingBuffer) throws IOException {
		ByteBufferInputStream bbis = new ByteBufferInputStream(incomingBuffer);
		BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
		ReplyHeader replyHdr = new ReplyHeader();

		replyHdr.deserialize(bbia, "header");

		if (replyHdr.getXid() == -1) {
			// -1 means notification

			WatcherEvent event = new WatcherEvent();
			event.deserialize(bbia, "response");//反序列化

			// convert from a server path to a client path(转换路径)
			if (chrootPath != null) {
				String serverPath = event.getPath();
				if(serverPath.compareTo(chrootPath)==0)
					event.setPath("/");
				else if (serverPath.length() > chrootPath.length())
					event.setPath(serverPath.substring(chrootPath.length()));
				else {
					LOG.warn("Got server path " + event.getPath()
							+ " which is too short for chroot path "
							+ chrootPath);
				}
			}

			WatchedEvent we = new WatchedEvent(event);//将WatcherEvent封装成WatchedEvent
			if (LOG.isDebugEnabled()) {
				LOG.debug("Got " + we + " for sessionid 0x"
						+ Long.toHexString(sessionId));
			}

			eventThread.queueEvent( we );//添加到事件处理线程
			return;
		}
	}
}
```

