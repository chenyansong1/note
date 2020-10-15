[toc]



转自：https://blog.csdn.net/fanrenxiang/article/details/84999554



本文出处：https://blog.csdn.net/arkblue/article/details/6229637，在原文基础上做了部分修改。

作用：JVM和监控的应用程序运行在不同的服务器上，减轻应用程序的负担，特别是HeapDupm的时候，应用常能够续负担很大。


1、为 10.87.40.141 上的 tomcat 配置 jvm 启动参数。在 tomcat 的 catalina.sh 中添 加如下参数:

```bash
JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=10.200.116.152
                      -Dcom.sun.management.jmxremote=true
                      -Dcom.sun.management.jmxremote.port=18999
                      -Dcom.sun.management.jmxremote.ssl=false 
                      -Dcom.sun.management.jmxremote.authenticate=false"
```

其中-Dcom.sun.management.jmxremote.port=18999 指定了 JMX 启动的代理端口;这个端口就是 Visual VM 要连接的端口

其中-Dcom.sun.management.jmxremote.ssl ="false" 指定远程连接 JMX 是否启用ssl;

其中-Dcom.sun.management.jmxremote.authenticate =false 指定远程连接 JMX 不启用身份认证;

其中-Djava.rmi.server.hostname=10.200.116.152 指定远程连接机器ip，这里就是tomcat所在的机器IP;

鉴权(需要用户名,密码鉴权)，这里忽略了。

2、 运行JVisualVM，选中左边的Remote节点，右键点击Add Remote Host弹出对话框

![img](https://img-blog.csdnimg.cn/20181214110235108.gif)

 

3、 填写远程的Tomcat的主机IP，点击OK

![img](https://img-blog.csdnimg.cn/20181214110409722.png)

4 在左侧的Remote节点下面增加了刚才添加的节点。选中10.200.116.152 这个节点，右键弹出菜单选择“Add JMX Connetcion..”.

![img](https://img-blog.csdnimg.cn/20181214110535534.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnJlbnhpYW5n,size_16,color_FFFFFF,t_70)

5、在弹出的对话框的填写要连接的Tomcat所在的主机IP和端口，端口是在Catalina.sh设置的端口18999，点击OK。

![img](https://img-blog.csdnimg.cn/20181214110737887.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnJlbnhpYW5n,size_16,color_FFFFFF,t_70)

6 在左侧树节点添加了JMX节点，双击这个节点或者右键弹出菜单，点击“Open”

![img](https://img-blog.csdnimg.cn/20181214110836771.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnJlbnhpYW5n,size_16,color_FFFFFF,t_70)

7、接着就可以使用Visual VM监控10.200.116.152机器上的Tomcat机器了。

![img](https://img-blog.csdnimg.cn/2018121411101173.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbnJlbnhpYW5n,size_16,color_FFFFFF,t_70)

 



