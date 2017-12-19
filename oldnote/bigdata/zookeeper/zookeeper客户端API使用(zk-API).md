---
title: zookeeper客户端API使用(zk-API)
categories: hadoop   
tags: [zookeeper]
---



# 1.创建zk实例
```
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly)
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd)
ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)


```

* 客户端与服务器端会话的建立是一个异步的过程
* 完成客户端的初始化就返回，此时连接并没有真正的建立，所以主线程中使用：connectedSignal.await()去等待
* 当连接真正建立的时候，客户端会收到一个事件的通知，会触发Watcher的process，所以在process函数中connectedSignal.countDown();//计数器减1，阻塞结束



> Zk构造方法参数说明

参数|说明
---|----
connectString	|1.指zk服务器列表，host:port,示例：192.168.0.50:2180,192.168.0.51:2181 ,<br/>2.也可以在后面跟上目录，表示此客户端的操作都是在此目录下，如：192.168.0.50:2180/zk-book 表示此客户端操作的节点都是在/zk-book根目录下，比如创建/foo/bar，其实完整的路径是/zk-book/foo/bar
sessionTimeout	|会话超时时间，单位是毫秒，当在这个时间内没有收到心跳检测，会话就会失效
watcher	|注ӆ的watcher，null表示不设置
canBeReadOnly	|用于标识当前会话是否支持”read-only”模式ͺ
sessionPasswd 和 sessionId	|分别代表会话ID和会话密钥，这两个参数一起可以唯一确定一个会话，客户端通过这两个参数实现客户端会话复用


# 2.创建节点
```
String create(String path, byte[] data, List<ACL> acl, CreateMode createMode)        
	#以同步的方式创建节点
void create(String path, byte[] data, List<ACL> acl, CreateMode createMode, 
	AsyncCallback.StringCallback cb, Object ctx)      
	#以异步的方式创建节点


```
* 无论是上面的同步或者异步都不支持递归创建节点
* 当创建的节点存在时，抛出异常NodeExistsException
 
> Zk create api参数说明

参数名|说明
----|-----
path|被创建的节点路径，比如：/zk-book/foo
data[]|节点中的数据
acl|acl策略
createMode|节点类型，枚举类型，有四种选择：<br/>持久(PERSISTENT)<br/>持久顺序（PERSISTENT_SEQUENTIAL）<br/>临时（EPHEMERAL）<br/>临时顺序（EPHEMERAL_SEQUENTIAL）
cb|回调函数，需要实现接口StringCallback接口，当服务器端创建完成后，客户端会自动调用这个对象的方法processResult
ctx|用于传递一个对象，可以在回调方法执行的时候用，通常用于传递业务的上下文信息

> 创建节点时的ACL

* 1.通过接口Ids可以预先定义几种scheme模式
	* OPEN_ACL_UNSAFE：相当于world:anyone:cdrwa
	* CREATOR_ALL_ACL：相当于auth:用户名:密码,但是需要通过ZooKeeper的addAuthInfo添加对应的用户和密码对
	* READ_ACL_UNSAFE：相当于world:anyone:r，即所有人拥有读权限
* 2.自己定义,比如:

```
public List<ACL> getDigestAcl(){
	List<ACL> acls = new ArrayList<ACL>();
	Id digestId = new Id("digest", "javaclient2:CGf2ydfsfdsjfsldfsdfsdfs=");
	acls.add(new ACL(Perms.ALL, digestId));
	return acls;
}
```

# 3.删除节点
```
void delete(String path, int version)            
	#以同步的方式删除

void delete(String path, int version, AsyncCallback.VoidCallback cb, Object ctx)    
	#以异步的方式删除，客户端主线程不能退出，否则可能请求没有发到服务器或者是异步回调不成功

```

参数|说明
---|----
Path	|被删除的节点的路径
Version	|知道节点的数据版本，如果指定的版本不是最新版本，将会报错，它的作用类似于hibernate中的乐观锁,if the given version is -1, it matches any node's versions(删除指定版本，-1表示删除所有版本)
cb	|异步回调函数
ctx	|传递上下文信息，即操作之前的信息传递到删除之后的异步回调函数里（可以用于保留删除前的状态）


# 4.获取子节点list
```
List<String> getChildren(String path, boolean watch)        
	#返回path节点的子节点列表

void getChildren(String path, boolean watch, AsyncCallback.Children2Callback cb, Object ctx)   
	#以异步的方式返回子节点，返回path指定节点的状态信息stat

void getChildren(String path, boolean watch, AsyncCallback.ChildrenCallback cb, Object ctx)   
	##以异步的方式返回子节点，不返回path指定节点的状态信息ͧstatͨ

List<String> getChildren(String path, boolean watch, Stat stat)        
	#返回path指定节点的状态信息stat和子节点列表

List<String> getChildren(String path, Watcher watcher)

void getChildren(String path, Watcher watcher, AsyncCallback.Children2Callback  cb, Object ctx)

void getChildren(String path, Watcher watcher, AsyncCallback.ChildrenCallback cb, Object ctx)
```

参数|说明
---|----
path	|数据节点的路径，比如：/zk-book/foo，获取该路径下的子节点列表
watcher	|设置watcher，如果path对应节点的子节点数量发生变化，将会得到通知，允许为null
watch	|是否使用默认的watcher（就是在创建zk实例的时候指定的watcher）
stat	|指定数据节点的状态信息
cb	|异步回调函数
ctx	|用于传递一个对象，可以在回调方法执行的时候用，通常用于传递业务的上下文信息


# 5.获取节点数据
```
void getData(String path, boolean watch, AsyncCallback.DataCallback cb, Object ctx)

byte[] getData(String path, boolean watch, Stat stat)

void getData(String path, Watcher watcher, AsyncCallback.DataCallback cb, Object ctx)

byte[] getData(String path, Watcher watcher, Stat stat)


############ Stat  类的内部（其实是对状态属性的封装）##############
public class Stat implements Record {
  private long czxid;
  private long mzxid;
  private long ctime;
  private long mtime;
  private int version;
  private int cversion;
  private int aversion;
  private long ephemeralOwner;
  private int dataLength;
  private int numChildren;
  private long pzxid;
  public Stat() {
  }
//............
}
```

参数|说明
---|----
path	|数据节点的路径，比如：/zk-book/foo，获取该路径的数据
watcher	|设置watcher后，如果path对应节点的数据发生变化，将会得到通知，允许为null
watch	|是否使用默认的watcher（就是在创建zk实例的时候指定的watcher）
stat	|指定数据节点的状态信息
cb	|异步回调函数
ctx	|用于传递一个对象，可以在回调方法执行的时候用，通常用于传递业务的上下文信息


# 6.修改数据
```
Stat setData(String path, byte[] data, int version)

void setData(String path, byte[] data, int version, AsyncCallback.StatCallback cb, Object ctx)
```

参数|说明
---|----
path	|被修改的节点的路径
data	|新的数据
version	|知道节点的数据版本，如果指定的版本不是最新版本，将会报错，它的作用类似于hibernate中的乐观锁,if the given version is -1, it matches any node's versions
cb	|异步回调函数
ctx	|传递上下文信息，即操作之前的信息传递到操作之后的异步回调函数里


# 7.检查节点是否存在
```
Stat exists(String path, boolean watch)        
	#如果节点不存在就返回为null

void exists(String path, boolean watch, AsyncCallback.StatCallback cb, Object ctx)

Stat exists(String path, Watcher watcher)

void exists(String path, Watcher watcher, AsyncCallback.StatCallback cb, Object ctx)


##############################
    interface StatCallback extends AsyncCallback {
        public void processResult(int rc, String path, Object ctx, Stat stat);
    }


```


参数|说明
---|----
path	|数据节点的路径，如：/zk-book/foo  即API调用的目的是检测该节点是否存在
watcher	|注册的watcher，用于监听以下三个事件：<br/>节点被创建<br/>节点被删除<br/>节点被更新
watch	|是否使用默认的watcher
cb	|异步回调函数
ctx	|传递上下文信息，即操作之前的信息传递到操作之后的异步回调函数里



# 8.异步函数的实现
其实以上所有的异步回调函数都是在AsyncCallback中

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/crud-api/1.png)


# 9.代码实现（*重要*）

```
package it.com.zk;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
public class WatchExample implements Watcher{
    private static final int SESSION_TIMEOUT=5000;
    
    private ZooKeeper zk;
    private CountDownLatch connectedSignal=new CountDownLatch(1);//初始化计数器为1
    @Override
    public void process(WatchedEvent event) {
        if(event.getState()==KeeperState.SyncConnected){//KeeperState是枚举类型，存放的是状态
            connectedSignal.countDown();//计数器减1，阻塞结束
        }
    }
    
    private void close() throws InterruptedException {
        zk.close();
        System.out.println("close........");
    }

/*-------------------- 连接 start---------------------*/ 
    private void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);    //因为自身实现了Watcher，所以将自身传过来了
        connectedSignal.await();//阻塞等待，直到计数器为0结束(主线程阻塞，登台子线程调用process函数，然后唤醒自己)
        /*
             客户端与服务端会话的建立是一个异步的过程，即
                完成ۨ客户端的初始化后就返回，此时连接并没有真正的建立起来
                当连接真正建立起来后，客户端会收到一个事件通知（即Watcher函数process的调用）
        */
    }
/*---------------------   连接 end -------------------------------*/  
    

/*---------------------   创建节点 start ----------------------*/  
    //创建节点
    private void createSync(String groupName, String dataStr) throws KeeperException, InterruptedException {
        String path="/"+groupName;
        if(zk.exists(path, false)== null){//判断路径是否存在,不存在就创建
            String actual_path = zk.create(path, dataStr.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            /*
            Ids:提供了几种默认的acl，但是也可以通过new Id()来自己指定，Ids内部就是new的
            CreateMode:持久节点、持久顺序节点、临时节点、临时顺序节点
             
            */
            System.out.println(actual_path);
            System.out.println("Created Sync:"+path);
        }
    } 
    //异步创建节点
    private void createAsync(String groupName, String dataStr) throws KeeperException, InterruptedException {
        String path="/"+groupName;
        if(zk.exists(path, false)== null){//判断路径是否存在
            zk.create(path, dataStr.getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT, new StringCallback(){
                @Override
                public void processResult(int rc, String path, Object ctx, String name) {
                    System.out.println("rc:"+rc);
                    System.out.println("path:"+path);
                    System.out.println("ctx:"+ctx);
                    System.out.println("name:"+name);
                }
                
            }, "async_");
            
            String actual_path = zk.create(path, dataStr.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("Created Async success:"+path);
        }
    }
/*-------------------------   创建节点 end---------------------------------*/      



/*-----------------------  删除节点 start ---------------------------------*/  
    //删除
    public void deleteSync(String path) throws InterruptedException, KeeperException{
        path="/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            //if the given version is -1, it matches any node's versions
            zk.delete(path, -1);
            System.out.println("delete sync success......");
        }
    }
    
  //删除
    public void deleteAsync(String path) throws InterruptedException, KeeperException{
        
        path="/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            zk.delete(path, -1, new VoidCallback(){
                @Override
                public void processResult(int rc, String path, Object ctx) {
                    System.out.println("rc:"+rc);
                    System.out.println("path:"+path);
                    System.out.println("ctx:"+ctx);
                }
                
            }, "delete_async");
            System.out.println("delete async success......");
        }
    }

/*------------------  删除节点 end-------------------------------------*/    

  
/*--------------------  获取子节点 start --------------------------------*/  
    //获取子节点(同步)
    public List<String> getChildrenSync(String path) throws KeeperException, InterruptedException{
        path = "/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            return zk.getChildren(path, false);
        }
        return null;
    }
    
    
  //获取子节点(同步+stat)
    public List<String> getChildrenSyncWithStat(String path) throws KeeperException, InterruptedException{
        path = "/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            Stat stat = new Stat();
            List childList = zk.getChildren(path, false, stat);//会将查询到的状态copy到stat中：如DataTree.copyStat(response.getStat(), stat);
            /*下面是所有的状态信息（stat的封装信息）
            [zk: localhost:2182(CONNECTED) 65] get /name_async
            "zshangsna"
            cZxid = 0x200000070
            ctime = Mon Nov 07 22:31:48 CST 2016
            mZxid = 0x200000070
            mtime = Mon Nov 07 22:31:48 CST 2016
            pZxid = 0x200000074
            cversion = 2
            dataVersion = 0
            aclVersion = 0
            ephemeralOwner = 0x0
            dataLength = 11
            numChildren = 2
             */
            System.out.println("stat:"+stat.getNumChildren()+";;;dataLength:"+stat.getDataLength());
            return childList;
        }
        return null;
    }
    
    //获取子节点(异步)
    public void getChildrenASync(String path) throws KeeperException, InterruptedException{
        path = "/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            zk.getChildren(path, false, new AsyncCallback.Children2Callback(){
                @Override
                public void processResult(int rc, String path, Object ctx,List<String> children, Stat stat) {
                    if(children != null){
                        for(String child : children){
                            System.out.println("child-name:"+child);
                        }
                    }
                }}, "async_getchildren");
        }
    }
    
    public void printChildList(String path) throws KeeperException, InterruptedException{
        
//        List<String> childList = this.getChildrenSync(path);
        List<String> childList = this.getChildrenSyncWithStat(path);
        if(childList != null){
            for(String child : childList){
                System.out.println("child-name:"+child);
            }
        }
    }
/*---------------  获取子节点 end-------------------------*/  

    

/*------------------  获取节点数据 start ----------------*/  
    //获取节点数据(异步)
    public void printNodeData(String path) throws KeeperException, InterruptedException{
        path = "/"+path;
        if(zk.exists(path, false)!= null){//判断路径是否存在
            zk.getData(    path, false, new AsyncCallback.DataCallback(){
                @Override
                public void processResult(int rc, String path, Object ctx,byte[] data, Stat stat) {
                    if(data != null){
                        System.out.println("path:"+path);
                        System.out.println("data:"+new String(data));
                        System.out.println("stat:"+stat);
                        System.out.println("ctx:"+ctx);
                    }
                }},"ctx_data");
        }
    }
/*------------------  获取节点数据 end--------------------*/  

    
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        WatchExample watchExample = new WatchExample();
        String path = "name_async";
        //连接
        watchExample.connect("192.168.0.50:2182");
        
        
        //创建
//        watchExample.createSync("name/beijing","zhangsan");
//        watchExample.createAsync(path,"async");
        
        //删除
//        watchExample.deleteSync(path);
//        watchExample.deleteAsync(path);
        
        System.out.println("main.............."); 
        
        //打印子节点列表
//        watchExample.printChildList(path);
//        watchExample.getChildrenASync(path);
        
        //打印节点数据
        watchExample.printNodeData(path);
        
        Thread.sleep(8000);//如果是异步的话，那么就要在子线程结束后，主线程才能推出，不然子线程的结果无法返回，所以这里会sleep
        watchExample.close();
        
    }
}
```
