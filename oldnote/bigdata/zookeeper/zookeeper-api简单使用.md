[TOC]



# 1.数据存储

dataTree (ConcurrentHashMap)



* 事物日志
  * zoo.cfg (datadir 字段配置)
* 快照日志()
* 运行时日志(bin/zookeeper.out)



# 2.zookeeper的Java api简单使用



## 2.1.创建连接

```Java
public class TestDemo {

    public static void main(String[] args) {
        try {
            final CountDownLatch countDownLatch=new CountDownLatch(1);

            ZooKeeper zooKeeper= new ZooKeeper("192.168.11.153:2181,192.168.11.154:2181,192.168.11.155:2181", 4000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(Event.KeeperState.SyncConnected==event.getState()){ //如果收到了服务端的响应事件，连接成功
                        countDownLatch.countDown();
                    }
                } });

            // 这里需要等待连接成功之后，才能做响应的操作，所以我们需要映入watch机制
            countDownLatch.await();// 等待连接完成
            System.out.println(zooKeeper.getState());//CONNECTING
            zooKeeper.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

```



