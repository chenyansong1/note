---
title: aof日志持久化
categories: redis   
toc: true  
tags: [redis]
---



# 1.简介
除了Redis的持久化功能外，Redis还提供了AOF（Append Only File ）持久化功能，AOF持久化是通过保存Redis服务器所执行的写命令来记录数据库状态的，如下图：

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/aof/1.png)

aof持久化功能的实现可以分为命令追加（append），文件写入、文件同步（sync）三个步骤

# 2.命令追加
服务器在执行完一个写命令之后，会以协议格式将被执行的写命令追加到服务器状态的aof_buf缓冲区的末尾

```
struct redisServer{
    //....
    //aof缓冲区
    sds aof_buf;
    //.....

}


```

如：如果客户端向服务器发送以下命令：
```
redis> set key value
ok
```
那么在aof_buf缓冲区的末尾：

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/aof/2.png)

# 3.文件写入与同步
Redis的服务器进程就是一个事件循环（loop），这个循环中的文件事件负责接收客户端的命令请求，以及向客户端发送命令回复，而时间事件负责执行向serverCron函数这样需要定时运行的函数, 在服务器每次结束一个事件循环之前，他都会调用flushAppendOnlyFile函数，考虑是否需要将aof_buf缓冲区中的内容写入和保存到AOF文件里面，这个过程可以用下面的伪代码表示：

```
def eventLoop():
    while True:
        #处理文件事件，接受命令请求以及发送命令回复
        #处理命令请求时可能会有新内容被追加到aof_buf缓冲区中
        processFileEvents()

        #处理时间事件（定时任务）
        processTimeEvents()

        #考虑是否要将aof_buf中的内容写入和保存到aof文件里面
        flushAppendOnlyFile()        

```

flushAppendOnlyFile函数的行为有配置文件中的appendfsync选项的值来确定

|appendfsync选项的值|flushAppendOnlyFile函数的行为|
|-|-|
|always|将aof_buf缓冲区中你的所有内容写入到AOF文件中|
|everysec|将aof_buf缓冲区中的所有内容写入到AOF文件中，如果上次同步aof文件的时间距离现在超过一秒，那么再次对aof文件进行同步，并且这个同步操作是由一个线程专门负责执行的|
|no|将aof_buf缓冲区中你的所有内容写入到aof文件中，但并不对aof文件进行同步，何时同步由操作系统决定|


# 4.配置文件中和aof相关的参数
```
appendonly no                #是否打开aof日志功能

appendfilename "appendonly.aof"            #aof文件名

# appendfsync always            #每一个命令都立即同步到aof，安全，速度慢
appendfsync everysec            #每秒写1次aof
# appendfsync no            #由操作系统判断缓冲区的大小统一写入aof

no-appendfsync-on-rewrite no            #正在导出rdb快照的过程中要不要停止同步aof


auto-aof-rewrite-percentage 100            #aof文件大小比上次重写时的大小，增长率100%时重写
auto-aof-rewrite-min-size 64mb            #aof文件至少超过64M时重写

```


# 5.aof文件的载入与数据还原
因为aof文件里面包含了重建数据库状态所需的所有写命令，所以只要服务器读入并重新执行一遍aof文件里面保存的写命令，就可以还原服务器关闭之前的数据库状态，步骤如下（以下步骤都是在服务端启动时完成的）：

1. 创建一个不带网络连接的伪客户端
    因为要执行aof中的命令，而命令只能在客户端中执行
2. 从aof文件中分析并读取一行一条写命令
3. 使用伪客户端执行被读出的写命令
4. 循环执行步骤2和步骤3，知道aof文件中的所有写命令都被处理完毕为止

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/aof/3.png)

# 6.aof重写
随着服务器运行时间的流逝，aof文件中的内容会越来越多，文件的体积会越来越大，并且aof文件的体积越大，使用aof文件来进行数据还原所需的时间就越多

举例：
```
redis>push list "a" "b"         //["a', "b"]

redis>push list "c"               //["a', "b", "c"]

redis>push list "d" "e"      //["a', "b", "c", "d", "e" ]


redis>lpop list

redis>lpoplist

redis>rpush list "f"  "g"            //[c, d ,e ,f, g]

'那么光是为了记录这个list键的状态，aof文件就需要保存6条命令，在实际应用中，写命令执行的次数和频率会比
上面的简单示例要高得多，所以造成的问题也会严重得多，所以就有了aof重写的功能'
```

通过重写，Redis服务器可以创建一个新的aof文件来替代现有的aof文件，新旧两个aof文件所保存的数据库状态想听，但是新aof文件不会包含任何浪费空间的冗余命令，所以以新aof文件的体积通常会比旧aof文件的体积要小得多


# 7.aof文件重写的实现
aof 文件重写并不需要对现有的aof文件进行任何操作、分析或者写入操作，这个功能是通过读取服务器,当前的数据库状态来实现的

```
redis>push list "a" "b"         //["a', "b"]
 
redis>push list "c"               //["a', "b", "c"]
 
redis>push list "d" "e"      //["a', "b", "c", "d", "e" ]
 
 
redis>lpop list
 
redis>lpoplist
 
redis>rpush list "f"  "g"            //[c, d ,e ,f, g]

'如果没有使用重写，那么aof中将被记录6条命令
重写：直接从数据库中读取键list的值，然后用一条rpush list "c" "d" "e" "f" "g"命令来替代保存在aof文件中你的
6条命令，这样就可以将保存list键所需的命令从6条减少为1条

'

```


重写的伪代码实现
```
def aof_rewrite(new_aof_file_name):
    #创建新aof文件
    f = create_file(new_aof_file_name)
    
    #遍历数据库
    for db in redisServer.db:
        #忽略空数据库
        if db.is_empty(): continue
        
        #写入select命令，指定数据库号码
        f.write_command("select"+db.id)

        #遍历数据库中的所有的键
        for key in db:
            #忽略已过期的键
            if key.is_expired(): continue
            #根据键的类型对键进行重写
            if key.type == string:
                rewrite_string(key)
            elif    key.type == List:
                rewrite_list(key)
            elif    key.type == Hash:
                rewrite_hash(key)
            elif    key.type == Set:
                rewrite_set(key)
            elif    key.type == SortedSet:
                rewrite_sorted_set(key)

            #如果键带有过期时间，那么过期时间也要被重写
            if key.have_expire_time():
                rewrite_expire_time(key)
    
    #写入完毕，关闭文件
    f.close()

```

# 8.aof后台重写
aof_rewrite函数在重写的时候会大量的写入操作，所以调用这个函数的线程将被长时间的阻塞，因为Redis服务器使用单个线程来处理命令请求，所以如果由服务器直接调用aof_rewrite函数的话
那么在重写aof文件期间，服务器将无法处理客户端发来的请求命令，所以将重写的任务放到子进程里执行
1. 子进程进行aof重写期间，服务器进程（父进程）可以继续处理命令请求
2. 子进程带有服务器进程的数据副本，使用子进程而不是线程，可以避免使用锁的情况下，保证数据的安全性
3. 需要解决的问题：在子进程进行aof重写期间，服务器进程还需要继续处理命令请求，而新的命令可能
对现有的数据库状态进行修改，从而使得服务器当前的数据库状态和重写后的aof文件所保存的数据库状态不一致



 如下：

|时间|服务器进程|子进程|
|-|-|-|
|T1|执行命令: set k1 v1||
|T2|执行命令: set k1 v2||
|T3|执行命令: set k1 v3||
|T4|创建子进程,执行aof文件重写|开始aof文件重写|
|T5|执行命令: set k2 10086|执行重写操作|
|T6|执行命令: set k3 123456|执行重写操作|
|T7|执行命令: set k4 2222|完成aof文件重写|


当子进程进行重写时，数据库中只有一个k1键，但是当子进程完成aof文件重写之后，服务器进程的数据库中已经新设置了k2 , k3 ,k4三个键，因此，重写后的aof文件和服务器当前的数据库状态并不一致
新的aof文件只保存了k1一个键的数据，而服务器数据库现在却有k1,  k2,  k3 , k4四个键

为了解决数据不一致的问题，Redis服务器设置了一个aof重写缓冲区，这个缓冲区在服务器创建子进程之后开始使用，当Redis服务器执行完一个写命令之后，他会同时将这个写命令发送给aof缓冲区和aof重写缓冲区

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/aof/4.png)

在子进程执行aof重写期间，服务器进程需要执行以下三个工作：
1. 执行客户端发送来的命令
2. 将执行后的写命令追加到aof缓冲区
3. 将执行后的写命令追加到aof重写缓冲区

 

当子进程完成aof重写工作之后，他会向父进程发送一个信号，父进程在接到信号之后，会调用一个信号处理函数，并执行以下工作：
1. 将aof重写缓冲区中的所有内容写入到aof文件中，这时新aof文件所保存的数据库状态将和服务器当前的数据库状态一致
2. 对新的aof文件进行改名，原子地覆盖现有的aof文件，完成新旧两个aof文件的替换
3. 在整个aof后台重写过程中，只有信号处理函数执行时会对服务器进程（父进程）造成阻塞，在其他时候

aof后台重写都不会阻塞父进程，这将aof重写对服务器性能造成的影响降到了最低

 
|时间|服务器进程|子进程|
|-|-|-|
|T1|执行命令: set k1 v1||
|T2|执行命令: set k1 v2||
|T3|执行命令: set k1 v3||
|T4|创建子进程,执行aof文件重写|开始aof文件重写|
|T5|执行命令: set k2 10086|执行重写操作|
|T6|执行命令: set k3 123456|执行重写操作|
|T7|执行命令: set k4 2222|完成aof文件重写,向父进程发送信号|
|T8|接收到子进程发来的信号，将命令：<br\> set k2 10086 <br\>set k3 123456 <br\>set k4 2222 <br\>追加到新aof文件的末尾||
|T9|用新aof文件覆盖旧aof文件|| 

