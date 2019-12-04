kafka同时监听两个IP

```shell
listeners=PLAINTEXT://0.0.0.0:9092

# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
advertised.listeners=PLAINTEXT://kafka:9092

# root directory for all kafka znodes.
zookeeper.connect=zookeeper:2181/kafka

```



这样配置之后，查看kafka监听的端口

```shell
[root@spark ~]# netstat -pan|grep 4423
tcp6       0      0 :::9092                 :::*                    LISTEN      4423/java           
tcp6       0      0 :::44712                :::*                    LISTEN      4423/java
```

然后查看zookeeper中保存的kafka的元数据信息

```shell
[zk: localhost:2181(CONNECTED) 3] get  /kafka/brokers/ids/0
{"listener_security_protocol_map":{"PLAINTEXT":"PLAINTEXT"},"endpoints":["PLAINTEXT://kafka:9092"],"jmx_port":-1,"host":"kafka","timestamp":"1575428197310","port":9092,"version":4}
cZxid = 0x37a
ctime = Wed Dec 04 10:56:37 CST 2019
mZxid = 0x37a
mtime = Wed Dec 04 10:56:37 CST 2019
pZxid = 0x37a
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x16eceb42d030004
dataLength = 180
numChildren = 0
[zk: localhost:2181(CONNECTED) 4] 
```

这样外网访问的时候，指定的是外网Ip，但是本地需要配置kafka的host主机