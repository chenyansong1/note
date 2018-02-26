---
title: redis运维常用命令
categories: redis   
toc: true  
tags: [redis]
---



# 1.启动
## 1.1.启动redis
```
$ redis-server redis.conf
 
常见选项：
 
./redis-server (run the server with default conf)
 
./redis-server /etc/redis/6379.conf
 
./redis-server --port 7777
 
./redis-server --port 7777 --slaveof 127.0.0.1 8888
 
./redis-server /etc/myredis.conf --loglevel verbose
```

## 1.2 启动redis-sentinel
```
./redis-server /etc/sentinel.conf –sentinel
 
./redis-sentinel /etc/sentinel.conf
 
部署后可以使用sstart对redis 和sentinel进行拉起，使用sctl进行supervisorctl的控制。（两个alias）
```
# 2.停止
```
127.0.0.1:6379> shutdown
not connected> 
 
sentinel方法一样，只是需要执行sentinel的连接端口
```

# 3.检测服务是否可用
```
127.0.0.1:6379> ping
PONG
127.0.0.1:6379>

'返回PONG说明正常'
 
'如果将服务shutdown'
127.0.0.1:6379> ping
Could not connect to Redis at 127.0.0.1:6379: Connection refused
not connected>

```

# 4.查看redis数据库统计信息
```
Mrds:6379> info
 
# Server
 
redis_version:2.8.19 ###redis版本号
 
redis_git_sha1:00000000 ###git SHA1
 
redis_git_dirty:0 ###git dirty flag
 
redis_build_id:78796c63e58b72dc
 
redis_mode:standalone ###redis运行模式
 
os:Linux 2.6.32-431.el6.x86_64 x86_64 ###os版本号
 
arch_bits:64 ###64位架构
 
multiplexing_api:epoll ###调用epoll算法
 
gcc_version:4.4.7 ###gcc版本号
 
process_id:25899 ###服务器进程PID
 
run_id:eae356ac1098c13b68f2b00fd7e1c9f93b1c6a2c ###Redis的随机标识符(用于sentinel和集群)
 
tcp_port:6379 ###Redis监听的端口号
 
uptime_in_seconds:6419 ###Redis运行时长(s为单位)
 
uptime_in_days:0 ###Redis运行时长(天为单位)
 
hz:10
 
lru_clock:10737922 ###以分钟为单位的自增时钟,用于LRU管理
 
config_file:/etc/redis/redis.conf ###redis配置文件
 
# Clients
 
connected_clients:1 ###已连接客户端的数量（不包括通过从属服务器连接的客户端）
 
client_longest_output_list:0 ###当前连接的客户端中最长的输出列表
 
client_biggest_input_buf:0 ###当前连接的客户端中最大的输出缓存
 
blocked_clients:0 ###正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客户端的数量 需监控
 
# Memory
 
used_memory:2281560 ###由 Redis 分配器分配的内存总量，以字节（byte）为单位
 
used_memory_human:2.18M ###以更友好的格式输出redis占用的内存
 
used_memory_rss:2699264 ###从操作系统的角度，返回 Redis 已分配的内存总量（俗称常驻集大小）。这个值和 top 、 ps 等命令的输出一致
 
used_memory_peak:22141272 ### Redis 的内存消耗峰值（以字节为单位）
 
used_memory_peak_human:21.12M ###以更友好的格式输出redis峰值内存占用
 
used_memory_lua:35840 ###LUA引擎所使用的内存大小
 
mem_fragmentation_ratio:1.18 ###used_memory_rss 和 used_memory 之间的比率
 
mem_allocator:jemalloc-3.6.0
 
###在理想情况下， used_memory_rss 的值应该只比 used_memory 稍微高一点儿。当 rss > used ，且两者的值相差较大时，表示存在（内部或外部的）内存碎片。内存碎片的比率可以通过 mem_fragmentation_ratio 的值看出。
 
当 used > rss 时，表示 Redis 的部分内存被操作系统换出到交换空间了，在这种情况下，操作可能会产生明显的延迟。
 
# Persistence
 
loading:0 ###记录服务器是否正在载入持久化文件
 
rdb_changes_since_last_save:0 ###距离最近一次成功创建持久化文件之后，经过了多少秒
 
rdb_bgsave_in_progress:0 ###记录了服务器是否正在创建 RDB 文件
 
rdb_last_save_time:1420023749 ###最近一次成功创建 RDB 文件的 UNIX 时间戳
 
rdb_last_bgsave_status:ok ###最近一次创建 RDB 文件的结果是成功还是失败
 
rdb_last_bgsave_time_sec:0 ###最近一次创建 RDB 文件耗费的秒数
 
rdb_current_bgsave_time_sec:-1 ###如果服务器正在创建 RDB 文件，那么这个域记录的就是当前的创建操作已经耗费的秒数
 
aof_enabled:1 ###AOF 是否处于打开状态
 
aof_rewrite_in_progress:0 ###服务器是否正在创建 AOF 文件
 
aof_rewrite_scheduled:0 ###RDB 文件创建完毕之后，是否需要执行预约的 AOF 重写操作
 
aof_last_rewrite_time_sec:-1 ###最近一次创建 AOF 文件耗费的时长
 
aof_current_rewrite_time_sec:-1 ###如果服务器正在创建 AOF 文件，那么这个域记录的就是当前的创建操作已经耗费的秒数
 
aof_last_bgrewrite_status:ok ###最近一次创建 AOF 文件的结果是成功还是失败
 
aof_last_write_status:ok
 
aof_current_size:176265 ###AOF 文件目前的大小
 
aof_base_size:176265 ###服务器启动时或者 AOF 重写最近一次执行之后，AOF 文件的大小
 
aof_pending_rewrite:0 ###是否有 AOF 重写操作在等待 RDB 文件创建完毕之后执行
 
aof_buffer_length:0 ###AOF 缓冲区的大小
 
aof_rewrite_buffer_length:0 ###AOF 重写缓冲区的大小
 
aof_pending_bio_fsync:0 ###后台 I/O 队列里面，等待执行的 fsync 调用数量
 
aof_delayed_fsync:0 ###被延迟的 fsync 调用数量
 
# Stats
 
total_connections_received:8466 ###服务器已接受的连接请求数量
 
total_commands_processed:900668 ###服务器已执行的命令数量
 
instantaneous_ops_per_sec:1 ###服务器每秒钟执行的命令数量
 
total_net_input_bytes:82724170
 
total_net_output_bytes:39509080
 
instantaneous_input_kbps:0.07
 
instantaneous_output_kbps:0.02
 
rejected_connections:0 ###因为最大客户端数量限制而被拒绝的连接请求数量
 
sync_full:2
 
sync_partial_ok:0
 
sync_partial_err:0
 
expired_keys:0 ###因为过期而被自动删除的数据库键数量
 
evicted_keys:0 ###因为最大内存容量限制而被驱逐（evict）的键数量。
 
keyspace_hits:0 ###查找数据库键成功的次数。
 
keyspace_misses:500000 ###查找数据库键失败的次数。
 
pubsub_channels:0 ###目前被订阅的频道数量
 
pubsub_patterns:0 ###目前被订阅的模式数量
 
latest_fork_usec:402 ###最近一次 fork() 操作耗费的毫秒数
 
# Replication
 
role:master ###如果当前服务器没有在复制任何其他服务器，那么这个域的值就是 master ；否则的话，这个域的值就是 slave 。注意，在创建复制链的时候，一个从服务器也可能是另一个服务器的主服务器
 
connected_slaves:2 ###2个slaves
 
slave0:ip=192.168.65.130,port=6379,state=online,offset=1639,lag=1
 
slave1:ip=192.168.65.129,port=6379,state=online,offset=1639,lag=0
 
master_repl_offset:1639
 
repl_backlog_active:1
 
repl_backlog_size:1048576
 
repl_backlog_first_byte_offset:2
 
repl_backlog_histlen:1638
 
# CPU
 
used_cpu_sys:41.87 ###Redis 服务器耗费的系统 CPU
 
used_cpu_user:17.82 ###Redis 服务器耗费的用户 CPU
 
used_cpu_sys_children:0.01 ###后台进程耗费的系统 CPU
 
used_cpu_user_children:0.01 ###后台进程耗费的用户 CPU
 
# Keyspace
 
db0:keys=3101,expires=0,avg_ttl=0 ###keyspace 部分记录了数据库相关的统计信息，比如数据库的键数量、数据库已经被删除的过期键数量等。对于每个数据库，这个部分都会添加一行以下格式的信息

'
只看其中一部分：info Replication
 
重新统计：config resetstat
'

```


# 5.查看和修改配置
```
查看：
 
config get ：获取服务器配置信息。
 
redis 127.0.0.1:6379> config get dir
 
config get *：查看所有配置

127.0.0.1:6379> config get app*
1) "appendfsync"
2) "everysec"
3) "appendonly"
4) "no"
127.0.0.1:6379> config get appendonly
1) "appendonly"
2) "no"
 
修改：
 
临时设置：config set
 
永久设置：config rewrite，将目前服务器的参数配置写入redis conf.

```

# 6.批量执行操作
## 6.1.nc命令
```
 
gnuhpc@gnuhpc:~$ (echo -en "ping\r\nset key abc\r\nget key\r\n";sleep 1) | nc 127.0.0.1 6379
 
+PONG
 
+OK
 
$3
 
abc

```

## 6.2.pipeline命令
```
在一个脚本中批量执行多个写入操作:
 
先把插入操作放入操作文本insert.dat：
 
set a b
 
set 1 2
 
set h w
 
set f u
 
'然后执行命令:cat insert.bat | ./redis-cli --pipe'
```



# 7.选择数据库
```
select db-index，默认连接的数据库所有是0,默认数据库数是16个。返回1表示成功，0失败
```

# 8.清空数据库

```
 flushdb：删除当前选择数据库中的所有 key。生产上已经禁止。
 
flushall: 删除所有的数据库。生产上已经禁止。
```

# 9.模拟宕机
```
 redis-cli debug segfault
```
# 10.模拟hang
```
 redis-cli -p 6379 DEBUG sleep 30
```
# 11.重命名命令
```
 rename-command，例如：rename-command FLUSHALL ""。必须重启。
```
# 12.执行lua脚本
```
 - -eval 。例如：
 
redis-cli --eval myscript.lua key1 key2 , arg1 arg2 arg3
```

# 13.设置密码
```
config set requirepass [passw0rd] 
```
# 14.验证密码
```
 auth passw0rd

#登录执行shell
echo -en  "auth 123456\r\nping\r\n"|nc 172.16.14.26 6379
echo -en  "auth 123456\r\ninfo\r\n"|nc 172.16.14.26 6379
```


# 15.性能测试命令
```
 redis-benchmark -n 100000
```
# 16.获取慢查询
```
127.0.0.1:6379> slowlog get 10
1) 1) (integer) 1
   2) (integer) 1476779945
   3) (integer) 65593
   4) 1) "bgrewriteaof"
2) 1) (integer) 0
   2) (integer) 1476779879
   3) (integer) 127857
   4) 1) "bgsave"
127.0.0.1:6379>
 
结果为查询ID、发生时间、运行时长和原命令

'查看慢查询的设置时间'
127.0.0.1:6379> config get slow*
1) "slowlog-log-slower-than"                        #多慢才会被记录（单位微妙）
2) "10000"
3) "slowlog-max-len"                                #服务器存储多少条慢查询的记录
4) "128"

```

# 17.查看日志
```
 日志位置在/redis/log下，redis.log为redis主日志，sentinel.log为sentinel监控日志。
```
# 18.Redis-cli命令行其他操作
```

在远程服务上执行命令
$ redis-cli -h host -p port -a password
$ redis-cli -h 127.0.0.1 -p 6379 -a "mypass"
redis 127.0.0.1:6379>
redis 127.0.0.1:6379> PING
PONG

1. echo ：在命令行打印一些内容
 
redis 127.0.0.1:6379> echo HongWan
 
"HongWan"
 
2. quit ：退出连接。
 
redis 127.0.0.1:6379> quit
 
3. -x选项从标准输入（stdin）读取最后一个参数。 比如从管道中读取输入：
 
echo -en "chen.qun" | redis-cli -x set name
 
4. -r -i
 
-r 选项重复执行一个命令指定的次数。
 
-i 设置命令执行的间隔。
 
比如查看redis每秒执行的commands（qps）
 
redis-cli -r 100 -i 1 info stats | grep instantaneous_ops_per_sec
 
5. -c：开启reidis cluster模式，连接redis cluster节点时候使用。
 
6. --rdb：获取指定redis实例的rdb文件,保存到本地。
 
redis-cli -h 192.168.44.16 -p 6379 --rdb 6379.rdb
 
7. --slave
 
模拟slave从master上接收到的commands。slave上接收到的commands都是update操作，记录数据的更新行为。
 
8. - -pipe
 
这个一个非常有用的参数。发送原始的redis protocl格式数据到服务器端执行。比如下面的形式的数据（linux服务器上需要用unix2dos转化成dos文件）。
 
linux下默认的换行是\n,windows系统的换行符是\r\n，redis使用的是\r\n.
 
echo -en '*3\r\n$3\r\nSET\r\n$3\r\nkey\r\n$5\r\nvalue\r\n' | redis-cli --pipe 
```
 


# 19.查看时间戳与微妙数
```
127.0.0.1:6379> time
1) "1476779679"
2) "313469"

```

# 20.查看当前库中的key数量
```
127.0.0.1:6379> dbsize
(integer) 1
```

# 21.手动保存rdb
```
127.0.0.1:6379> save            #阻塞式
OK
127.0.0.1:6379> bgsave            #后台进行
Background saving started
127.0.0.1:6379>

```


# 22.手动重写aof
```
127.0.0.1:6379> bgrewriteaof
Background append only file rewriting started
127.0.0.1:6379> 

```

# 23.上次保存时间
```

127.0.0.1:6379> lastsave
(integer) 1476779879
```

#24.设置为slave
```
127.0.0.1:6379> slaveof host port

```
# 25.sync主从同步

# 26.客户端列表
```
127.0.0.1:6379> client list
id=3 addr=127.0.0.1:59050 fd=6 name= age=1653 idle=1442 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=config
id=6 addr=127.0.0.1:59053 fd=5 name= age=512 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
127.0.0.1:6379> 
```

# 27.客户端名字
```
# 新连接默认没有名字
 
redis 127.0.0.1:6379> CLIENT GETNAME
(nil)
 
# 设置名字
 
redis 127.0.0.1:6379> CLIENT SETNAME hello-world-connection
OK
 
# 返回名字
 
redis 127.0.0.1:6379> CLIENT GETNAME
"hello-world-connection"
 
# 在客户端列表中查看
 
redis 127.0.0.1:6379> CLIENT LIST
addr=127.0.0.1:36851
fd=5
name=hello-world-connection     # <- 名字
age=51
...
 
# 清除名字
 
redis 127.0.0.1:6379> CLIENT SETNAME        # 只用空格是不行的！
(error) ERR Syntax error, try CLIENT (LIST | KILL ip:port)
 
redis 127.0.0.1:6379> CLIENT SETNAME ""     # 必须双引号显示包围
OK
 
redis 127.0.0.1:6379> CLIENT GETNAME        # 清除完毕
(nil)
```

