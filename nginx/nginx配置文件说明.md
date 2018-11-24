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

3.lock_file


```

