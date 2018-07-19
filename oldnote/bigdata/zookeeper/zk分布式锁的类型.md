---
title: zk分布式锁的类型
categories: hadoop   
tags: [zookeeper]
---



[TOC]



# 排他锁


* 定义:只能允许一个线程获得,其他线程都需要等待已经获取的线程完成才能再次争抢资源
* zk实现:
	* 获得锁:通过构建一个目录,当叶子节点能创建成功,则任务获取到锁,因为一旦一个节点被某个会话创建,其他会话再次创建这个节点时,将会抛出异常,比如目录为:
	![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/lock/1.png)

	* 释放锁:删除节点或者会话失效



这种方式会有惊群效应，惊群效应（所有的节点都监听一个节点，那么当节点变化的时候，所有监听该node的所有节点，都会惊动）





# 共享锁

* 定义:
读锁:如果前面线程使用的是读锁,则后面的线程还可以获取读锁,从而可以继续进行读操作
写锁:如果在线程打算获取锁从而进行操作时,无论前面已经有读锁或者是写锁都必须进入等待


* zk实现:
  * 获得读锁:利用zk节点的顺序性,对于读操作,节点名称带一个R标识,如果前面存在序列数比自己小,并且都是带R标识,则说明前面加的都是读锁,还可以继续获取读锁,否则,等待锁释放后由机会再抢

  * 获得写锁:只有自己创建的节点序列化最小,才能获得写锁,否则,进入等待,直到有锁资源被释放,然后再判断是否有机会得到锁

  * 释放锁:删除节点或者会话失效

    下图：Rthread是读线程； Wthread是写线程

    ![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/lock/2.png)





有序节点的特性

只是监听比自己小一个值的节点



# 原生API去实现锁



```Java
public class DistributedLock implements Lock,Watcher {

    private ZooKeeper zk=null;
    private String ROOT_LOCK="/locks"; //定义根节点
    private String WAIT_LOCK; //等待前一个锁
    private String CURRENT_LOCK; //表示当前的锁

    private CountDownLatch countDownLatch; //


    public DistributedLock() {

        try {
            zk=new ZooKeeper("192.168.11.153:2181",
                    4000,this);
            //判断根节点是否存在
            Stat stat=zk.exists(ROOT_LOCK,false);
            if(stat==null){
                zk.create(ROOT_LOCK,"0".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean tryLock() {

        try {
            //创建临时有序节点
            CURRENT_LOCK=zk.create(ROOT_LOCK+"/","0".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"->"+
                    CURRENT_LOCK+"，尝试竞争锁");
            List<String> childrens=zk.getChildren(ROOT_LOCK,false); //获取根节点下的所有子节点
            SortedSet<String> sortedSet=new TreeSet();//定义一个集合进行排序
            for(String children:childrens){
                sortedSet.add(ROOT_LOCK+"/"+children);
            }
            String firstNode=sortedSet.first(); //获得当前所有子节点中最小的节点
            SortedSet<String> lessThenMe=((TreeSet<String>) sortedSet).headSet(CURRENT_LOCK); //
            if(CURRENT_LOCK.equals(firstNode)){//通过当前的节点和子节点中最小的节点进行比较，如果相等，表示获得锁成功
                return true;
            }
            if(!lessThenMe.isEmpty()){
                WAIT_LOCK=lessThenMe.last();//获得比当前节点更小的最后一个节点，设置给WAIT_LOCK
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void lock() {
        if(this.tryLock()){ //如果获得锁成功
            System.out.println(Thread.currentThread().getName()+"->"+CURRENT_LOCK+"->获得锁成功");
            return;
        }
        try {
            waitForLock(WAIT_LOCK); //没有获得锁，继续等待获得锁
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat=zk.exists(prev,true);//监听当前节点的上一个节点
        if(stat!=null){
            System.out.println(Thread.currentThread().getName()+"->等待锁"+ROOT_LOCK+"/"+prev+"释放");
            countDownLatch=new CountDownLatch(1);
            countDownLatch.await();
            //TODO  watcher触发以后，还需要再次判断当前等待的节点是不是最小的
            System.out.println(Thread.currentThread().getName()+"->获得锁成功");
        }
        return true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"->释放锁"+CURRENT_LOCK);
        try {
            zk.delete(CURRENT_LOCK,-1);
            CURRENT_LOCK=null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if(this.countDownLatch!=null){
            this.countDownLatch.countDown();
        }
    }
}

```



测试代码

```Java
public class App 
{
    public static void main( String[] args ) throws IOException {
        CountDownLatch countDownLatch=new CountDownLatch(10);
        for(int i=0;i<10;i++){
            new Thread(()->{
                try {
                    countDownLatch.await();
                    DistributedLock distributedLock=new DistributedLock();
                    distributedLock.lock(); //获得锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
        System.in.read();
    }
}
```



# Curator实现的锁

curator有对锁提供封装

```Java
public class CuratorDemo {
    public static void main(String[] args) {
        CuratorFramework curatorFramework=CuratorFrameworkFactory.builder().build();
        InterProcessMutex interProcessMutex=new InterProcessMutex(curatorFramework,"/locks");
        try {
            // 获取锁
            interProcessMutex.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

![image-20180719194526227](/Users/chenyansong/Documents/note/images/bigdata/zookeeper/clock.png)



这个Curator提供了locks, leader(leader选举)，barriers