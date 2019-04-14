# [Linux下使用ps命令查看某个进程文件的启动位置](https://www.cnblogs.com/EasonJim/p/6803375.html)

转自：https://www.cnblogs.com/EasonJim/p/6803375.html



使用ps命令，使用方法如下：

```
ps -ef|grep shutdown
```

其中shutdown为关机命令，但是此时查看到的只是相对路径，没有绝对路径，如：

[![img](https://images2015.cnblogs.com/blog/417876/201705/417876-20170503183430320-1991575852.png)](https://images2015.cnblogs.com/blog/417876/201705/417876-20170503183430320-1991575852.png)

其中4170就是进程ID，此时进入【/proc/4170】，并通过 ls -al查看如下：

```
ls -al /proc/4170
```

[![img](https://images2015.cnblogs.com/blog/417876/201705/417876-20170503183655289-1873419265.png)](https://images2015.cnblogs.com/blog/417876/201705/417876-20170503183655289-1873419265.png)

注意：

- cwd符号链接的是进程运行目录；
- exe符号连接就是执行程序的绝对路径；
- cmdline就是程序运行时输入的命令行命令；
- environ记录了进程运行时的环境变量；
- fd目录下是进程打开或使用的文件的符号连接。

 