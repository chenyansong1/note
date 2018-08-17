
flume本机idea远程调试

[TOC]

最近又要自定义开发flume source 插件，每次插件打包后运行，不能再编译器里debug 感觉好苦恼。于是今天就看了一下flume 的远程调试。一看感觉挺简单的。所以就分享一下。

远程调试flume其实就四步，第一步开启flume的远程调试，第二步拉取flume 源码导入到idea中。第三步在 idea 中配置远程机器的IP 和调试端口号。第四步：将自定义开发插件源码粘贴到源码中，启动flume 进行调试。

废话不多说粘配置了：

# 1.开启flume远程调试

## 1.1.修改配置文件



修改flume的启动脚本flume-ng ，找到这段代码

```properties
# set default params
FLUME_CLASSPATH=""
FLUME_JAVA_LIBRARY_PATH=""
JAVA_OPTS="-Xmx20m"
LD_LIBRARY_PATH=""
```

修改 JAVA_OPTS 为

```properties
JAVA_OPTS="-Xmx20m -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y"
```

**此时已经打开了flume的远程调试，远程调试端口为 8000**

**其实JAVA_OPTS**这个参数在两个地方可以设置，

> bin/flume-ng
>
> conf/flume-env.sh

而且后者是会覆盖前者的，所以要主要，看在哪里设置JAVA_OPTS，如果只是在 flume-ng中设置JAVA_OPTS参数，那么后者会覆盖前者，所以最好是在后者中设置改参数



## 1.2.重启flume

```
./apache-flume-1.8.0-bin/bin/flume-ng agent -c ./apache-flume-1.8.0-bin/conf -f ./apache-flume-1.8.0-bin/conf/example.conf -n a1 -Dflume.root.logger=DEBUG.console
```





这样我们就可以启动flume了，打印的日志如下：说明flume在8000端口启动了监听

```
Listening for transport dt_socket at address: 8000
```







# 2.从Git 中拉取flume源码

我们调试需要再本地的idea中打断点，而断点的地方可能是：

>1.flume的源码
>
>2.我们写的flume的插件



才是需要满足的条件：

> 1.本地的断点代码，**在远程服务器上必须存在，并且必须一样**





# 3.添加idea Debug 配置

添加配置： 

![](E:\note\images\flume\debug1.jpg)



添加一个remote 配置 



![](E:\note\images\flume\debug2.png)



配置运行flume 远程主机的ip和调试端口号： 



![](E:\note\images\flume\debug3.png)






# 4.添加自定义源码断点调试

这个时候就可以把自己写的flume插件的代码粘贴到flume中(随便位置打上断点 )然后启动flume 进行调试。



![](E:\note\images\flume\debug4.png)

当然 开发的flume插件的jar包应该在远端运行flume的机器上，实际运行的flume配置也是远端flume的配置。 



启动flume ： 

![](E:\note\images\flume\debug5.png)



此时启动idea 的debug 模式就会进入断点。 



![](E:\note\images\flume\debug6.png)





参考：https://blog.csdn.net/u012373815/article/details/60601118


