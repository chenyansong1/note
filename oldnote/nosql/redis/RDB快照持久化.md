---
title: RDB快照持久化
categories: redis   
toc: true  
tags: [redis]
---



# 1.简介
因为Redis是内存数据库，他将自己的数据库状态存储在内存里面，所以如果不想办法将存储在内存中的数据库状态保存到磁盘里面，那么一旦服务器进程退出，服务器中的数据库状态也会消失不见，Redis提供了RDB持久化功能

RDB持久化既可以手动执行，也可以根据配置文件（redis.conf）配置选项定期执行，该功能可以将某个时间点上的数据库状态保存到一个RDB文件中，如下：


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/rdb/1.png)

 

# 2.RDB文件的创建
## 2.1.save、bgsave
save命令会阻塞服务器进程，知道rdb文件创建完毕为止，在服务器进程阻塞期间，服务器不能处理任何命令请求
```
127.0.0.1:6379> save        
OK

```

bgsave 命令会派生出一个子进程，然后由子进程负责创建rdb文件，服务器进程（父进程）继续处理命令请求

```
127.0.0.1:6379> bgsave            #派生子进程，然后由子进程创建rdb文件
Background saving started

```

创建rdb文件的实际工作由rdb.c/rdbSave函数完成，save命令和bgsave命令会以不同的方式调用这个函数，以下通过伪代码可以明显看出这两个命令之间的区别：

```
def SAVE():
    #创建rdb文件
    rdbSave()


def BGSAVE();
    #创建子进程
    pid = fork()
    if pid ==0:
        #子进程负责创建rdb文件
        rdbSave()
        #完成工作后向父进程发送信号
    elif pid > 0:
        #父进程继续处理命令请求，并通过轮询等待子进程的信号
        handle_request_and_wait_singal()
    else:
        #处理出错情况
        handle_fork_error()

```

# 3.自动载入
rdb文件的载入工作是在服务器启动时自动执行的，Redis没有专门用于载入rdb文件的命令，只要Redis服务器在启动时检测到rdb文件的存在，他就会自动载入rdb文件，如下是启动过程：DB loaded from disk : 0.018 seconds就是服务器在成功载入rdb文件之后打印的

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/rdb/2.png)
 
* 因为aof文件的更新频率通常比rdb文件的更新频率高，也就是说aof的中对数据的记录时效性相对于rdb来说更加接近真实数据
* 如果服务器开启了aof持久化功能，那么服务器会优先使用aof文件来还原数据库状态
* 只有在aof持久化功能处于关闭状态时，服务器才会使用rdb文件来还原数据库状态，如下图：

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/rdb/3.png)


# 4.自动间隔性保存
因为bgsave命令可以在不阻塞服务器进程的情况下执行，所以redis允许用户通过设置服务器配置文件，让服务器每隔一段时间自动执行一次bgsave命令：vim /etc/redis.conf
```
#3种保存时间，满足一个就会触发保存
save 900 1                    #900秒内，对数据库进行了至少1次修改
save 300 10                     #300秒内，对数据库进行了至少10次修改
save 60 10000                 #60秒内，对数据库进行了至少1000次修改

stop-writes-on-bgsave-error yes        #后台存储错误停止写

rdbcompression yes            #使用LZF压缩rdb文件

rdbchecksum yes            #存储和加载rdb文件时校验

dbfilename dump.rdb        #设置rdb文件名

dir ./                        #设置rdb文件的保存目录


```
  

参考书籍:
[《Redis设计与实现》](https://book.douban.com/subject/25900156/)

