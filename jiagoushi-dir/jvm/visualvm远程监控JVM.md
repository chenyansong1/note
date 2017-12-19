Visualvm目前支持两种remote connection方式，分别是jstatd和JMX方式；我看了一下官方的说明文档，如果是针对jboss的监控，官方还是推荐使用JMX的方式监控，因此下面我就讲述一下如何在server端以及客户端配置JMX方式监控的visualvm：

修改env.sh
打开env.sh文件，并在JVM的启动配置中添加如下信息：

```
JAVA_OPTS="-Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.port=54321 \
    -Djava.rmi.server.hostname=172.16.12.38"
```

这几个配置的说明如下：

* Dcom.sun.management.jmxremote.port：这个是配置远程connection的端口号的，要确定这个端口没有被占用
* -Dcom.sun.management.jmxremote.ssl=false 
* -Dcom.sun.management.jmxremote.authenticate=false：这两个是固定配置，是JMX的远程服务权限的
* -Djava.rmi.server.hostname：这个是配置server的IP的，要使用server的IP最好在机器上先用hostname –i看一下IP是不是机器本身的IP，如果是127.0.0.1的话要改一下，否则远程的时候连不上

下面是图形界面的操作过程：

![](/Users/chenyansong/Documents/note/images/jvm/visualvm.png)

选择添加JMX连接

添加上面配置的端口
![](/Users/chenyansong/Documents/note/images/jvm/visualvm2.png)

![](/Users/chenyansong/Documents/note/images/jvm/visualvm3.png)

可以看到监听的页面程序
![](/Users/chenyansong/Documents/note/images/jvm/visualvm4.png)

