

转自：https://ieevee.com/tech/2019/04/16/close-socket.html

# 如何在线关闭一个tcp socket连接

如何在线关闭一个tcp socket连接？

你可能会说，简单，netstat -antp找到连接，kill掉这个进程就行了。

```shell
# netstat -antp|grep 6789
tcp        0      0 1.1.1.1:59950      1.1.1.2:6789        ESTABLISHED 45059/ceph-fuse
# kill 45059
```

连接确实关掉了，进程也跟着一起杀死了。达不到“在线”的要求。

有没有办法不杀死进程，但还是可以关闭socket连接呢？

我们知道，在编码的时候，要关闭一个socket，只要调用 close 函数就可以了，但是进程在运行着呢，怎么让它调用 close 呢？

在[superuser](https://superuser.com/)上看到一个很棒的方法，原理就是 gdb attach 到进程上下文，然后 `call close($fd)`。

1、 使用 netstat 找到进程

```shell
# netstat -antp|grep 6789
tcp        0      0 1.1.1.1:59950      1.1.1.2:6789        ESTABLISHED 45059/ceph-fuse
```

如上，进程pid为45059。

2、 使用 lsof 找到进程45059打开的所有文件描述符，并找到对应的socket连接

```shell
lsof -np 45059
COMMAND     PID USER   FD   TYPE             DEVICE SIZE/OFF       NODE NAME
ceph-fuse 45059 root  rtd    DIR                8,2     4096          2 /
ceph-fuse 45059 root  txt    REG                8,2  6694144    1455967 /usr/bin/ceph-fuse
ceph-fuse 45059 root  mem    REG                8,2   510416    2102312 /usr/lib64/libfreeblpriv3.so
...
ceph-fuse 45059 root   12u  IPv4         1377072656      0t0        TCP 1.1.1.1:59950->1.1.1.2:smc-https (ESTABLISHED)
```

其中 `12u` 就是上面对应socket连接的文件描述符。

3、 gdb 连接到进程

```shell
gdb -p 45059
```

4、 关闭socket连接

```shell
(gdb) call close(12u)
```

socket连接就可以关闭了，但是进程 45059 还是好好着的。

你可能会问，什么时候会用到这个特性呢？场景还是比较多的，**比如你想测试下应用是否会自动重连mysql**，通过这个办法就可以比较方便的测试了。

Ref:

- [Manually closing a port from commandline](https://superuser.com/questions/127863/manually-closing-a-port-from-commandline)