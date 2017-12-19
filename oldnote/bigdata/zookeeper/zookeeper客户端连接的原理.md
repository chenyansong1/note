---
title: zookeeper客户端连接的原理
categories: hadoop   
tags: [zookeeper]
---


创建zookeeper连接对象时，如何选择哪个服务器进行连接？

* 客户端的connectstring:localhost:2181, localhost:2182, localhost:2183
* 通过类:org.apache.zookeeper.client. StaticHostProvider维护地址列表
* 通过解析connectstring,进行随机排序,形成最终的地址列表
* 每次从形成地址列表中选择第一个地址进行连接,如果连接不上再选择第一个地址
* 如果当前节点是列表的最后一个节点,则再重新选择第一个节点,相当于一个环
* 通过随机排序,每个zk的客户端就会随机的去连接zk服务器,分布相对均匀


举例:

connectstring为:192.168.1.2:2181, 192.168.1.3:2181, 192.168.1.4:2181
随机打乱后的顺序为:192.168.1.3:2181, 192.168.1.2:2181, 192.168.1.4:2181
则第一次连接时选择1.3这个节点,如果连接不上,则重新选择1.2, 然后是1.4,如果1.4连接不上,则开始连接第一个节点1.3

```
/*
在StaticHostProvider类中,有如下的两个方法:
初始化连接地址列表
*/

public StaticHostProvider(Collection<InetSocketAddress> serverAddresses)
        throws UnknownHostException {
    for (InetSocketAddress address : serverAddresses) {
        InetAddress ia = address.getAddress();
        InetAddress resolvedAddresses[] = InetAddress.getAllByName((ia!=null) ? ia.getHostAddress():
            address.getHostName());
        for (InetAddress resolvedAddress : resolvedAddresses) {
           
            if (resolvedAddress.toString().startsWith("/") 
                    && resolvedAddress.getAddress() != null) {
                this.serverAddresses.add(
                        new InetSocketAddress(InetAddress.getByAddress(
                                address.getHostName(),
                                resolvedAddress.getAddress()), 
                                address.getPort()));
            } else {
                this.serverAddresses.add(new InetSocketAddress(resolvedAddress.getHostAddress(), address.getPort()));
            }  
        }
    }
    
    if (this.serverAddresses.isEmpty()) {
        throw new IllegalArgumentException(
                "A HostProvider may not be empty!");
    }
    Collections.shuffle(this.serverAddresses);	//随机排序
}



------选择一个地址返回--------------

public InetSocketAddress next(long spinDelay) {
    ++currentIndex;
	//如果已经是最后一个节点,则从第一个节点开始
    if (currentIndex == serverAddresses.size()) {
        currentIndex = 0;
    }
	//当地址列表只有一个地址时,再次获取之前先sleep一定时间再返回,这算是一个重试时间间隔
    if (currentIndex == lastIndex && spinDelay > 0) {
        try {
            Thread.sleep(spinDelay);
        } catch (InterruptedException e) {
            LOG.warn("Unexpected exception", e);
        }
    } else if (lastIndex == -1) {
        // We don't want to sleep on the first ever connect attempt.
        lastIndex = 0;
    }

    return serverAddresses.get(currentIndex);//返回下一个节点的地址
}


```
