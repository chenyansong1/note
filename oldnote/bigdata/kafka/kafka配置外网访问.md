```
# 这里为Kafka broker配置了两个listeners，一个是明文传输；另一个使用SSL加密进行数据传输
listeners=PLAINTEXT://:9092,SSL://:9093   

# 因为是云上环境，如果clients通过公网（或外网）去连接broker，那advertiesd.listeners就必须配置成所在机器的公网IP
advertised.listeners=PLAINTEXT://公网IP:9092,SSL://公网IP:9093  



***********************************************************************************************

Kafka外网访问涉及到的几个配置项有：

host.name：deprecated，仅当listeners属性未配置时被使用，可以使用listeners属性代替，表示broker的hostname。若配置，则绑定到该地址，否则绑定所有网卡。
advertised.host.name：deprecated，仅当advertised.listeners或者listeners属性未配置时被使用，可以使用listeners属性代替，表示发布到ZooKeeper上的hostname，供客户端访问时使用，具体可以使用命令get /brokers/ids/{brokerId}在ZooKeeper上查看。若未配置，则使用配置过的host.name属性，如果host.name也没有配置，则使用java.net.InetAddress.getCanonicalHostName()函数的的返回值。
advertised.listeners：发布到ZooKeeper上供客户端使用的监听器，若未配置，则使用配置的listeners属性，如果listeners属性也没有配置，则使用java.net.InetAddress.getCanonicalHostName()函数的的返回值。
listeners：逗号分隔的需要监听的URL和协议，如PLAINTEXT://myhost:9092,TRACE://:9091,PLAINTEXT://0.0.0.0:9092,TRACE://localhost:9093，可以不指定hostname来绑定默认网卡。可指定hostname为0.0.0.0来绑定所有网卡，若未配置，则使用java.net.InetAddress.getCanonicalHostName()函数的的返回值。

```



参考：

https://hacpai.com/article/1517731232605

https://blog.csdn.net/miqi770/article/details/77776213