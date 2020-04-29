---
title: 运行jar脚本设置参数
categories: java   
tags: [java]
---

[TOC]



Java在运行已编译完成的类时，是通过java虚拟机来装载和执行的，java虚拟机通过操作系统命令JAVA_HOME"bin"java –option 来启动，-option为虚拟机参数，JAVA_HOME为JDK安装路径，通过这些参数可对虚拟机的运行状态进行调整，掌握参数的含义可对虚拟机的运行模式有更深入理解。

虚拟机的参数分为两类：

# 1.基本参数列表

> JAVA_HOME/bin/java

```
chenyansongdeMacBook-Pro:bin chenyansong$ java
用法: java [-options] class [args...] (执行类)
   或  java [-options] -jar jarfile [args...] (执行 jar 文件)
   
其中options包括:
    -d32	  使用 32 位数据模型 (如果可用)
    -d64	  使用 64 位数据模型 (如果可用)
    -server	  选择 "server" VM
                  默认 VM 是 server,
                  因为您是在服务器类计算机上运行。


    -cp <目录和 zip/jar 文件的类搜索路径>
    -classpath <目录和 zip/jar 文件的类搜索路径>
                  用 : 分隔的目录, JAR 档案
                  和 ZIP 档案列表, 用于搜索类文件。
    -D<名称>=<值>
                  设置系统属性
    -verbose:[class|gc|jni]
                  启用详细输出
    -version      输出产品版本并退出
```



1. -client，-server
这两个参数用于设置虚拟机使用何种运行模式，client模式启动比较快，但运行时性能和内存管理效率不如server模式，通常用于客户端应用程序。相反，server模式启动比client慢，但可获得更高的运行性能。
在 windows上，缺省的虚拟机类型为client模式，如果要使用 server模式，就需要在启动虚拟机时加-server参数，以获得更高性能，对服务器端应用，推荐采用server模式，尤其是多个CPU的系统。在 Linux，Solaris上缺省采用server模式。 

2. -hotspot
含义与client相同，jdk1.4以前使用的参数，jdk1.4开始不再使用，代之以client。

3. -classpath,-cp
**虚拟机在运行一个类时，需要将其装入内存**，虚拟机搜索类的方式和顺序如下：
Bootstrap classes，Extension classes，User classes。
Bootstrap 中的路径是虚拟机自带的jar或zip文件，虚拟机首先搜索这些包文件，用System.getProperty("sun.boot.class.path")可得到虚拟机搜索的包名。
Extension是位于jre"lib"ext目录下的jar文件，虚拟机在搜索完Bootstrap后就搜索该目录下的jar文件。用System. getProperty("java.ext.dirs”)可得到虚拟机使用Extension搜索路径。
User classes搜索顺序为当前目录、环境变量 CLASSPATH、-classpath。

4. -classpath
告知虚拟机搜索目录名、jar文档名、zip文档名，之间用分号;分隔。
例如当你自己开发了公共类并包装成一个common.jar包，在使用 common.jar中的类时，就需要用-classpath common.jar 告诉虚拟机从common.jar中查找该类，否则虚拟机就会抛出java.lang.NoClassDefFoundError异常，表明未找到类定义。 
在运行时可用System.getProperty(“java.class.path”)得到虚拟机查找类的路径。
使用-classpath后虚拟机将不再使用CLASSPATH中的类搜索路径，如果-classpath和CLASSPATH都没有设置，则虚拟机使用当前路径(.)作为类搜索路径。
推荐使用-classpath来定义虚拟机要搜索的类路径，而不要使用环境变量 CLASSPATH的搜索路径，以减少多个项目同时使用CLASSPATH时存在的潜在冲突。例如应用1要使用a1.0.jar中的类G，应用2要使用 a2.0.jar中的类G,a2.0.jar是a1.0.jar的升级包，当a1.0.jar，a2.0.jar都在CLASSPATH中，虚拟机搜索到第一个包中的类G时就停止搜索，如果应用1应用2的虚拟机都从CLASSPATH中搜索，就会有一个应用得不到正确版本的类G。

5. -D<propertyName>=value
在虚拟机的系统属性中设置属性名/值对，运行在此虚拟机之上的应用程序可用System.getProperty(“propertyName”)得到value的值。
如果value中有空格，则需要用双引号将该值括起来，如-Dname=”space string”。
该参数通常用于设置系统级全局变量值，如配置文件路径，应为该属性在程序中任何地方都可访问。

6. -verbose[:class|gc|jni]
在输出设备上显示虚拟机运行信息。
verbose和verbose:class含义相同，输出虚拟机装入的类的信息，显示的信息格式如下：
[Loaded java.io.FilePermission$1 from shared objects file]
当虚拟机报告类找不到或类冲突时可用此参数来诊断来查看虚拟机从装入类的情况。

7. -verbose:gc
在虚拟机发生内存回收时在输出设备显示信息，格式如下：
[Full GC 268K->168K(1984K), 0.0187390 secs]
该参数用来监视虚拟机内存回收的情况。

8. -verbose:jni
在虚拟机调用native方法时输出设备显示信息，格式如下：
[Dynamic-linking native method HelloNative.sum ... JNI]
该参数用来监视虚拟机调用本地方法的情况，在发生jni错误时可为诊断提供便利。

9. -version
显示可运行的虚拟机版本信息然后退出。一台机器上装有不同版本的JDK时



# 2.扩展参数列表

> JAVA_HOME/bin/java –X 

```
chenyansongdeMacBook-Pro:bin chenyansong$ java -X
    -Xmixed           混合模式执行 (默认)
    -Xint             仅解释模式执行
    -Xbootclasspath:<用 : 分隔的目录和 zip/jar 文件>
                      设置搜索路径以引导类和资源
    -Xbootclasspath/a:<用 : 分隔的目录和 zip/jar 文件>
                      附加在引导类路径末尾
    -Xbootclasspath/p:<用 : 分隔的目录和 zip/jar 文件>
                      置于引导类路径之前
    -Xdiag            显示附加诊断消息
    -Xnoclassgc       禁用类垃圾收集
    -Xincgc           启用增量垃圾收集
    -Xloggc:<file>    将 GC 状态记录在文件中 (带时间戳)
    -Xbatch           禁用后台编译
    -Xms<size>        设置初始 Java 堆大小
    -Xmx<size>        设置最大 Java 堆大小
    -Xss<size>        设置 Java 线程堆栈大小
    -Xprof            输出 cpu 配置文件数据
    -Xfuture          启用最严格的检查, 预期将来的默认值
    -Xrs              减少 Java/VM 对操作系统信号的使用 (请参阅文档)
    -Xcheck:jni       对 JNI 函数执行其他检查
    -Xshare:off       不尝试使用共享类数据
    -Xshare:auto      在可能的情况下使用共享类数据 (默认)
    -Xshare:on        要求使用共享类数据, 否则将失败。
    -XshowSettings    显示所有设置并继续
    -XshowSettings:all
                      显示所有设置并继续
    -XshowSettings:vm 显示所有与 vm 相关的设置并继续
    -XshowSettings:properties
                      显示所有属性设置并继续
    -XshowSettings:locale
                      显示所有与区域设置相关的设置并继续

-X 选项是非标准选项, 如有更改, 恕不另行通知。

```

# 3.替换class文件



1、把X.jar包中的class用jd-gui、luyten反编译得到源码xxx.java

2、javac -cp A.jar;B.jar;C.jar xxx.java 得到修改后的xxx.class文件 (其中A、B、C是依赖jar包，一般直接依赖一个原始解压的X.jar包即可)

3、建立目录结构，把xxx.class放在原始目录层下，具体是哪个层下可以用jar tf X.jar | find "LicenseV"查看X.jar的目录结构。也可以直接解压X.jar并替换那个xxx.class

4、jar -uvf  X.jar com/p1/p2/p3/xxx.class





# 解压jar中的文件出来

```
Linux shell 中提取zip或jar文件中的某个文件

假如有个压缩包 abc.jar, 里面文件如下 （可以用unzip -l abc.jar 查看）：

data/1.txt
data/2.txt


那就可以如下提取里面指定的文件到指定的位置，但上级目录将不会被创建。不加-d参数就解压到当前目录，-d参数可以指定不存在的目录，会自动创建。解压得到的文件名不变。

unzip -j abc.jar data/2.txt -d /tmp/data_in_abc
```






# Example

## 运行class文件 

执行带main方法的class文件，命令行为：
> java <CLASS文件名>

注意：CLASS文件名不要带文件后缀.class ,例如：

> java Test

如果执行的class文件是带包的，即在类文件中使用了：
> package <包名>

那应该在包的基路径下执行，命令行为：
> java <包名>.CLASS文件名

例如：

```
PackageTest.java中，其包名为：com.ee2ee.test，对应的语句为：
package com.ee2ee.test;
PackageTest.java及编译后的class文件PackageTest.class的存放目录如下：
classes
  |__com
      |__ee2ee
           |__test
                |__PackageTest.java
                |__PackageTest.class

要运行PackageTest.class，应在classes目录下执行：
java com.ee2ee.test.PackageTest 

```


## 运行jar文件

格式如下：

```
java -classpath xxx.jar 运行类的全路径 参数

/*
xxx.jar表示Jar包的名称
运行类的全路径指包名+类名，如:com.xxx.xxx.main
参数可以是多个，多个参数之间用空格隔开
*/
```

命令行编译运行Java程序时，加载指定目录中的Jar包：

```
java -Djava.ext.dirs=./lib Test  

./lib  是指存放第三方jar文件的目录。
圆点：表示要编译运行的java文件所在的当前目录
```

运行jar程序时添加vm参数

```
java  [-Xms128m -Xmx512m]   -jar   *.jar   参数1   参数2 ……      
//[ ]中内容可有可无
```

下面是一个完整的例子

```
java -cp xxx.jar MainClass

#or
java -cp -Djava.ext.dirs=./lib MainClass

```

```

java -Xmx1g -Xms512m -Xmn128m \
     -cp . \
     -Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN \
     -Djava.ext.dirs=./lib \
     -Drun_dir=${MY_ROOT} \
     -Dlog_file="logs/${APP_NAME}.log" \
     com.aipai.solr.indexing.MyPostData applicationContext_${APP_NAME}.xml $@ 1>logs/ok.log 2>logs/error.log &

```


## 增加虚拟机可以使用的最大内存

java虚拟机可使用的最大内存是有限制的，缺省值通常为64MB或128MB。如果一个应用程序为了提高性能而把数据加载内存中而占用较大的内存，比如超过了默认的最大值128MB，需要加大java虚拟机可使用的最大内存，否则会出现Out of Memory（系统内存不足）的异常。启动java时，需要使用如下两个参数：

* -Xms java虚拟机初始化时使用的内存大小
* -Xmx java虚拟机可以使用的最大内存

以上两个参数中设置的size，可以带单位，例如：256m表示256MB,举例说明：

> java -Xms128m -Xmx256m ...


## 配置远程监控参数

设置远程端口为8999，不需要用户名密码验证，初始化堆内存为64M、最大堆内存为128M、新生代为20M，Survivor区与Eden区内存为2:8

```
java \
-Djava.rmi.server.hostname=主机ip \
-Dcom.sun.management.jmxremote.port=8999 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-jar \
-Xms64M -Xmx128M -Xmn20M \
-XX:SurvivorRatio=2 \
./live-scheduled-0.0.1-SNAPSHOT.jar

```

此时就可以用jconsole工具进行远程监控了，只需要使用主机ip+端口就可以连接成功

