[toc]

转自：https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/

# CPU使用率过高问题排查

> 一个应用占用CPU很高，除了确实是计算密集型应用之外，通常原因都是出现了死循环。
> 下面我们将一步步定位问题以及排查思路。

## 一、定位占用cpu高的进程

```
#linux下使用top
top
#AIX下使用topas
topas
```

[![image-20200714103622358](https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/image-20200714103622358.png)](https://ysshao.cn/Linux/性能优化/CPU_troubleshoot/image-20200714103622358.png)

通过上图可以明显看出进程PID6359占用cpu过高，明显存在问题，定位到了进程id。

可以使用以下命令top -p pid只观察此进程的CPU和内存以及负载情况。

## 二、定位进程中每个线程占用cpu情况

**1. 使用top查看进程的每一个线程占用CPU情况**

```
top -Hp pid
```

[![image-20200714103744375](https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/image-20200714103744375.png)](https://ysshao.cn/Linux/性能优化/CPU_troubleshoot/image-20200714103744375.png)

通过上图可以看出线程PID6360CPU占用率最高，接下来定位该线程的代码是否出现异常导致cpu占用过高。

**2. 使用ps查看进程的每一个线程占用CPU情况**

```
ps -mp pid -o THREAD,tid,time | sort -rn
```

[![image-20200714104157960](https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/image-20200714104157960.png)](https://ysshao.cn/Linux/性能优化/CPU_troubleshoot/image-20200714104157960.png)

## 三、定位线程Dump定位程序问题

**1. 生产线程Dump文件**

- 使用jstack命令定位问题代码

  > \#输出线程dump文件用于分析
  > jstack pid >jstack.txt

- 用Kill -3 pid 生成

  > 一般是生产在nohup文件 、AIX会生成javacore关键字文件（一般是生产在程序启动目录下）

**2.通过上一步定位线程较高的pid转换为16进制。**

printf “%x\n” 6360 为181d

**3.查找线程dump文件中线程执行的代码**

[![image-20200714110322979](https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/image-20200714110322979.png)](https://ysshao.cn/Linux/性能优化/CPU_troubleshoot/image-20200714110322979.png)

**4.通过程序进行定位对应的执行代码**

发现代码中while循环无法结束，一直抢占cpu，导致程序cpu使用过高，修改代码即可。

[![image-20200714105228482](https://ysshao.cn/Linux/%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96/CPU_troubleshoot/image-20200714105228482.png)](https://ysshao.cn/Linux/性能优化/CPU_troubleshoot/image-20200714105228482.png)

