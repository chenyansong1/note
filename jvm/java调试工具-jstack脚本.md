[toc]

-------------------------------

关于`Java`排错与诊断，力荐️`Arthas` ❤️

- [alibaba/arthas: Alibaba Java诊断利器 - github.com](https://github.com/alibaba/arthas)
- `Arthas`用户文档 https://alibaba.github.io/arthas/

`Arthas`功能异常(😜)强劲，且在阿里巴巴线上支持使用多年。我自己也常用，一定要看看用用！

`Arthas`是通过`Agent`方式来连接运行的`Java`进程、主要通过交互式来完成功能，与之对应的脚本方式也有其优势，如：

1. 可以在进程不能启动的情况下完成诊断（如依赖中的重复类分析、`ClassPath`上的资源或类查找）
1. 开销少；简单少依赖（就纯文本的一个脚本文件）
1. 方便与（已有的）工具（如`awk`、`sed`、`cron`）、流程或设施集成，进一步编程/自动化

请按需按场景选用。

-------------------------------



🍺 [show-busy-java-threads](../bin/show-busy-java-threads)
----------------------

### 常见的排查思路

用于快速排查`Java`的`CPU`性能问题(`top us`值过高)，自动查出运行的`Java`进程中消耗`CPU`多的线程，并打印出其线程栈，从而确定导致性能问题的方法调用。  
目前只支持`Linux`。原因是`Mac`、`Windows`的`ps`命令不支持列出进程的线程`id`，更多信息参见[#33](https://github.com/oldratlee/useful-scripts/issues/33)，欢迎提供解法。

PS，如何操作可以参见[@bluedavy](http://weibo.com/bluedavy)的[《分布式Java应用》](https://book.douban.com/subject/4848587/)的【5.1.1 `CPU`消耗分析】一节，说得很详细：

1. `top`命令找出消耗`CPU`高的`Java`进程及其线程`id`：
    1. 开启线程显示模式（`top -H`，或是打开`top`后按`H`）
    1. 按`CPU`使用率排序（`top`缺省是按`CPU`使用降序，已经合要求；打开`top`后按`P`可以显式指定按`CPU`使用降序）
    1. 记下`Java`进程`id`及其`CPU`高的线程`id`
1. 查看消耗`CPU`高的线程栈：
    1. 用进程`id`作为参数，`jstack`出有问题的`Java`进程
    1. 手动转换线程`id`成十六进制（可以用`printf %x 1234`）
    1. 在`jstack`输出中查找十六进制的线程`id`（可以用`vim`的查找功能`/0x1234`，或是`grep 0x1234 -A 20`）
1. 查看对应的线程栈，分析问题

查问题时，会要多次上面的操作以分析确定问题，这个过程**太繁琐太慢了**。

### 脚本下载

[脚本](https://github.com/oldratlee/useful-scripts/blob/dev-2.x/bin/show-busy-java-threads)

### 用法

```bash
show-busy-java-threads
# 从所有运行的Java进程中找出最消耗CPU的线程（缺省5个），打印出其线程栈

# 缺省会自动从所有的Java进程中找出最消耗CPU的线程，这样用更方便
# 当然你可以手动指定要分析的Java进程Id，以保证只会显示你关心的那个Java进程的信息
show-busy-java-threads -p <指定的Java进程Id>

show-busy-java-threads -c <要显示的线程栈数>

show-busy-java-threads <重复执行的间隔秒数> [<重复执行的次数>]
# 多次执行；这2个参数的使用方式类似vmstat命令

show-busy-java-threads -a <运行输出的记录到的文件>
# 记录到文件以方便回溯查看

show-busy-java-threads -S <存储jstack输出文件的目录>
# 指定jstack输出文件的存储目录，方便记录以后续分析

##############################
# 注意：
##############################
# 如果Java进程的用户 与 执行脚本的当前用户 不同，则jstack不了这个Java进程
# 为了能切换到Java进程的用户，需要加sudo来执行，即可以解决：
sudo show-busy-java-threads

show-busy-java-threads -s <指定jstack命令的全路径>
# 对于sudo方式的运行，JAVA_HOME环境变量不能传递给root，
# 而root用户往往没有配置JAVA_HOME且不方便配置，
# 显式指定jstack命令的路径就反而显得更方便了

# -m选项：执行jstack命令时加上-m选项，显示上Native的栈帧，一般应用排查不需要使用
show-busy-java-threads -m
# -F选项：执行jstack命令时加上 -F 选项（如果直接jstack无响应时，用于强制jstack），一般情况不需要使用
show-busy-java-threads -F
# -l选项：执行jstack命令时加上 -l 选项，显示上更多相关锁的信息，一般情况不需要使用
# 注意：和 -m -F 选项一起使用时，可能会大大增加jstack操作的耗时
show-busy-java-threads -l

# 帮助信息
$ show-busy-java-threads -h
Usage: show-busy-java-threads [OPTION]... [delay [count]]
Find out the highest cpu consumed threads of java processes,
and print the stack of these threads.

Example:
  show-busy-java-threads       # show busy java threads info
  show-busy-java-threads 1     # update every 1 second, (stop by eg: CTRL+C)
  show-busy-java-threads 3 10  # update every 3 seconds, update 10 times

Output control:
  -p, --pid <java pid>      find out the highest cpu consumed threads from
                            the specified java process.
                            default from all java process.
  -c, --count <num>         set the thread count to show, default is 5.
                            set count 0 to show all threads.
  -a, --append-file <file>  specifies the file to append output as log.
  -S, --store-dir <dir>     specifies the directory for storing
                            the intermediate files, and keep files.
                            default store intermediate files at tmp dir,
                            and auto remove after run. use this option to keep
                            files so as to review jstack/top/ps output later.
  delay                     the delay between updates in seconds.
  count                     the number of updates.
                            delay/count arguments imitates the style of
                            vmstat command.

jstack control:
  -s, --jstack-path <path>  specifies the path of jstack command.
  -F, --force               set jstack to force a thread dump. use when jstack
                            does not respond (process is hung).
  -m, --mix-native-frames   set jstack to print both java and native frames
                            (mixed mode).
  -l, --lock-info           set jstack with long listing.
                            prints additional information about locks.

CPU usage calculation control:
  -d, --top-delay           specifies the delay between top samples.
                            default is 0.5 (second). get thread cpu percentage
                            during this delay interval.
                            more info see top -d option. eg: -d 1 (1 second).
  -P, --use-ps              use ps command to find busy thread(cpu usage)
                            instead of top command.
                            default use top command, because cpu usage of
                            ps command is expressed as the percentage of
                            time spent running during the *entire lifetime*
                            of a process, this is not ideal in general.

Miscellaneous:
  -h, --help                display this help and exit.
```

### 示例

```bash
$ show-busy-java-threads
[1] Busy(57.0%) thread(23355/0x5b3b) stack of java process(23269) under user(admin):
"pool-1-thread-1" prio=10 tid=0x000000005b5c5000 nid=0x5b3b runnable [0x000000004062c000]
   java.lang.Thread.State: RUNNABLE
    at java.text.DateFormat.format(DateFormat.java:316)
    at com.xxx.foo.services.common.DateFormatUtil.format(DateFormatUtil.java:41)
    at com.xxx.foo.shared.monitor.schedule.AppMonitorDataAvgScheduler.run(AppMonitorDataAvgScheduler.java:127)
    at com.xxx.foo.services.common.utils.AliTimer$2.run(AliTimer.java:128)
    at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908)
    at java.lang.Thread.run(Thread.java:662)

[2] Busy(26.1%) thread(24018/0x5dd2) stack of java process(23269) under user(admin):
"pool-1-thread-2" prio=10 tid=0x000000005a968800 nid=0x5dd2 runnable [0x00000000420e9000]
   java.lang.Thread.State: RUNNABLE
    at java.util.Arrays.copyOf(Arrays.java:2882)
    at java.lang.AbstractStringBuilder.expandCapacity(AbstractStringBuilder.java:100)
    at java.lang.AbstractStringBuilder.append(AbstractStringBuilder.java:572)
    at java.lang.StringBuffer.append(StringBuffer.java:320)
    - locked <0x00000007908d0030> (a java.lang.StringBuffer)
    at java.text.SimpleDateFormat.format(SimpleDateFormat.java:890)
    at java.text.SimpleDateFormat.format(SimpleDateFormat.java:869)
    at java.text.DateFormat.format(DateFormat.java:316)
    at com.xxx.foo.services.common.DateFormatUtil.format(DateFormatUtil.java:41)
    at com.xxx.foo.shared.monitor.schedule.AppMonitorDataAvgScheduler.run(AppMonitorDataAvgScheduler.java:126)
    at com.xxx.foo.services.common.utils.AliTimer$2.run(AliTimer.java:128)
    at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908)
    at java.lang.Thread.run(Thread.java:662)

......
```

上面的线程栈可以看出，`CPU`消耗最高的2个线程都在执行`java.text.DateFormat.format`，业务代码对应的方法是`shared.monitor.schedule.AppMonitorDataAvgScheduler.run`。可以基本确定：

- `AppMonitorDataAvgScheduler.run`调用`DateFormat.format`次数比较频繁。
- `DateFormat.format`比较慢。（这个可以由`DateFormat.format`的实现确定。）

多执行几次`show-busy-java-threads`，如果上面情况高概率出现，则可以确定上面的判定。  
因为调用越少代码执行越快，则出现在线程栈的概率就越低。  
脚本有自动多次执行的功能，指定 重复执行的间隔秒数/重复执行的次数 参数。

分析`shared.monitor.schedule.AppMonitorDataAvgScheduler.run`实现逻辑和调用方式，以优化实现解决问题。



