[TOC]



# 1.配置文件的大体结构



配置文件

* main配置段：全局配置段
  * event配置段：定义event模型工作特性
* http{}  配置段：定义HTTP协议相关的协议



配置指令：要以分号结尾，语法格式：

​	directive value1, value2,...

支持使用变量

​	内置变量

​		模块自带

​	自定义变量

​		set var_name value



主配置端的指令

​	用于调试，定位问题

​	正常运行必备的配置

​	优化性能的配置

​	事件相关配置



# 主配置段指令

## 1.正常运行的必备配置

```
 1.user USERNAME [GROUPNAME];		
 #用户指定worker进程的用户和组（这个配置也可以在文档Core functionality.user中找到对应的配置说明）

2.pid  /path/to/pid-file;	
#指定NGINX的守护进程的pid文件

3.worker_rlimit_nofile  numer;	
#worker rlimit number of file 指定所有worker进程所能够打开的最大文件句柄数

4.worker_rlimit_core size;   
#所有的worker进程所能够使用的最大核心文件大小（一般不动）

```



## 2.性能优化相关的配置

```
1. worker_processes #; 
#指定worker进程的个数，通常为物理core的数量-1或者-2(留一个core为系统所用)；

2. worker_cpu_affinity cpumask ...;
#可以将对应的worker绑定到对应的CPU的某一个核上，这样就可以提高CPU缓存的命中率，但是该CPU上还是有其他进程可以上去，所以还是有上下文切换；
cpumask(CPU掩码)：假设我们有4颗CPU，使用八位二进制表示如下：
0000 0001
0000 0010
0000 0100
0000 1000
worker_cpu_affinity 00000001 00000010  #表示只使用前2颗CPU

#For example：
worker_processes    4;
worker_cpu_affinity 0001 0010 0100 1000;
#binds each worker process to a separate CPU, while

worker_processes    2;
worker_cpu_affinity 0101 1010;
#binds the first worker process to CPU0/CPU2, and the second worker process to CPU1/CPU3. The second example is suitable for hyper-threading.

3.timer_resolution	
#时间解析度，减少  gettimeofday() 函数的系统调用
#By default, gettimeofday() is called each time a kernel event is received. With reduced resolution, gettimeofday() is only called once per specified interval.

#Example:
timer_resolution 100ms;


4.worker_priority number;
#指明调度worker进程的优先级的，通过nice值指定，nice值的范围为-20 到 20，对应的优先级为100-140，默认情况下nice=0，对应的优先级为120，优先级越小，越容易被调度
#Defines the scheduling priority for worker processes like it is done by the nice command: a negative number means higher priority. Allowed range normally varies from -20 to 20.

#Example:
worker_priority -10;
```



## 3.事件相关的配置



```
1.accept_mutes on|off;
#当一个新的请求到master的时候，master需要决定这个请求是交给哪一个worker去处理
#on表示让多个worker轮询接收新的请求，这就带来了负载均衡的效应
#off表示多个worker抢占新的连接

2.accept_mutex_delay time;
#当accept_mutes on；当一个新的连接连接来的时候，正好分配到了这个worker上，但是此时这个worker是正在处理，这个就是定义将新连接分配给这个worker时等待的时间

3.lock_file	#上面的参数相当于配置的是互斥锁，这里的参数就是互斥锁对应的锁文件路径(accept_mutex用到的锁文件路径)

4.use [epoll|rtsig|select|poll]
#定义使用的事件模型,建议让NGINX自动选择

5.worker_connecctions #;
#一个worker进程能够接受的最大并发连接数

```



## 4.用户用于调试和定位问题

```
For debug logging to work, nginx needs to be built with --with-debug, see “A debugging log”.


1.daemon {on|off};
#是否以守护进程方式运行NGINX，off表示运行在前台，这样各种日志是打印在前台的

2.master_process {on|off};
#是否以master/worker模型来运行NGINX，调试时可以设置为off

3.error_log file [level];
#错误日志

```



# 配置文件生效的方式

```

./sbin/nginx -s {reload|stop|quit|reopen}

```





# nginx作为web服务器的配置

NGINX除了有自己的核心模块之外，有像HTTP模块，mail模块等等，下面讲的是HTTP模块(`ngx_http_core_module`)



## 配置框架



![image-20181125103435813](/Users/chenyansong/Documents/note/images/nginx/http-module.png)



ngx_http_core_module模块可以作为静态的web服务器工作


```
http {
	#负载均衡使用
	upstream {
        ...
	}
	
	server {#每个server类似于httpd中的<VirtualHost>
        location URL {#类似于 httpd中的documentRoot，用于定义URL与本地文件系统的映射关系
        	root "/path/to/somedir";
            ...
        }
        location URL {
        	if ...{#条件判断的功能
                
        	}
            ...
        }
	} 
	
	server {
        ...
	}
}

#与HTTP相关的指令，仅能够放置于http, server ,location, upstream,if 上下文，但是有些指令仅能应用于这5中指令中的某些中，而非全部

当我们查看文档的时候，可以看到每一个指令的用法如下
Syntax:	root path;	#root指令
Default:	root html;
Context:	http, server, location, if in location #说明root指令只能用在这些地方

```



## 配置指令

```
1.server {}
#定义一个虚拟主机

#基于端口的虚拟主机
server {
    listen 8080;
    server_name www.baidu.com;
    root "/vhosts/web1";
}

2.listen
listen address[:port]
listen port;
#指定监听的地址和端口，如下
listen 127.0.0.1:8000;
listen 127.0.0.1;
listen 8000;
listen *:8000;
listen localhost:8000;

3.server_name

Syntax:	server_name name ...;	#支持多个主机，名称可以使用正则表达式（需要以 ~ 开头）或者通配符
Default:	server_name "";
Context:	server

多个server匹配法则：
1.先做精确匹配检查
2.左侧通配符匹配检查
3.右侧通配符匹配检查
4.正则表达式匹配检查
5.default_server


4.root
#用于设置资源路径映射，用于指明请求的URL所对应的资源所在的文件系统上的根路径
Syntax:	root path;	#root指令
Default:	root html;
Context:	http, server, location, if in location #说明root指令只能用在这些地方

5.location
#根据用户请求的URI来匹配location(因为一个server中可以有多个location)，当匹配到时，此请求将会被相应的location配置块所处理，例如做访问控制等功能
Syntax:	
	location [ = | ~ | ~* | ^~ ] uri { ... }
	location @name { ... }
Default:	—
Context:	server, location

######################
= 表示精确匹配
~ 正则表达式模式匹配检查，区分大小写
~* 正则表达式模式匹配检查，不分区大小写
^~ URI前半部分匹配，不支持正则表达式

优先级：= > ^~  > ~ > ~* > 不带任何符号
######################



server {
    listen 80;
    server_name www.baidu.com;
    location / {
        root "/vhosts/web1";
    }
    location /images {
        root "/vhosts/imags";
    }
    location ~* \.php$ {#正则
        fcgipass
    }
    
    
}


location = / {
    [ configuration A ]
}

location / {
    [ configuration B ]
}

location /documents/ {
    [ configuration C ]
}

location ^~ /images/ {
    [ configuration D ]
}

location ~* \.(gif|jpg|jpeg)$ {
    [ configuration E ]
}

#The “/” request will match configuration A, the “/index.html” request will match configuration B, the “/documents/document.html” request will match configuration C, the “/images/1.gif” request will match configuration D, and the “/documents/1.jpg” request will match configuration E.


6.alias
#用来实现路径映射，他和root的区别在于，alias可以拿到URI中匹配的变量，然后使用这些变量
Syntax:	alias path;
Default:	—
Context:	location

#拿到URI中的匹配
location ~ ^/users/(.+\.(?:gif|jpe?g|png))$ {
    alias /data/w3/images/$1;#使用拿到的匹配
}

#alias和root的区别：
#root是指明根的位置，对应URL中的/
#alias表示路径映射，是一个完整的路径
When location matches the last part of the directive’s value:

#www.baidu.com/images/a.jpg <--- /www/pictures/a.jpg
location /images/ {#alias就完全是一个映射，注意反斜线需要一一对应
    alias /www/pictures/;
}

it is better to use the root directive instead:
#www.baidu.com/images/a.jpg <--- /vhosts/web1/images/a.jpg
location /images/ {#root还要替换掉根
    root /vhosts/web1;
}

```

