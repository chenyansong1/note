http://www.ywnds.com/?p=1031

[TOC]



# ps

BSD风格

```
ps
	a ：显示所有和终端有关的进程
	u ：显示用户相关信息
	x ：显示所有与终端无关的进程
	
	
进程的分类：
	跟终端相关的进程
	跟终端无关的进程
	
进程状态：
	D:不可中断的睡眠
	R:运行或就绪
	S:可中断的睡眠
	T：停止
	Z:僵尸态
	<:高优先级的进程
	N:低优先级进程
	+：前台进程组中的进程
	l:多线程进程
	s:会话进程首进程
	├─sshd(会话进程首进程)─┬─sshd───sshd───bash
    │      				 └─sshd───sshd───bash───pstree
```

```
[webuser@VM_0_4_centos ~]$ ps aux|head
USER(用户)       PID(pid) %CPU(使用cpu的时间) %MEM(使用的物理内存占比)    VSZ(虚拟内存)   RSS(常驻内存(相当于虚拟内存去掉了共享内存的部分)) TTY      STAT(状态，见上面) START   TIME(在CPU上的使用时长) COMMAND(命令，如果是[]，表示内核的进程)
root         1  0.0  0.0  41152  3560 ?        Ss   8月31   2:27 /usr/lib/systemd/systemd --switched-root --system --deserialize 21
root         2  0.0  0.0      0     0 ?        S    8月31   0:00 [kthreadd]
root         3  0.0  0.0      0     0 ?        S    8月31   0:01 [ksoftirqd/0]
root         5  0.0  0.0      0     0 ?        S<   8月31   0:00 [kworker/0:0H]
root         7  0.0  0.0      0     0 ?        S    8月31   0:01 [migration/0]
root         8  0.0  0.0      0     0 ?        S    8月31   0:00 [rcu_bh]
root         9  0.0  0.0      0     0 ?        S    8月31   1:32 [rcu_sched]
root        10  0.0  0.0      0     0 ?        S    8月31   0:05 [watchdog/0]
root        11  0.0  0.0      0     0 ?        S    8月31   0:03 [watchdog/1]
[webuser@VM_0_4_centos ~]$ 
```



SystemV风格

```
ps -elF


[webuser@VM_0_4_centos ~]$ ps -elF|more
F S UID(用户的UID)        PID  PPID(父pid)  C PRI(优先级)  NI(nice值) ADDR SZ WCHAN    RSS(常驻内存) PSR(使用的是哪个CPU) STIME TTY          TIME CMD
4 S root         1     0  0  80   0 - 10288 ep_pol  3560   0 8月31 ?       00:02:27 /usr/lib/systemd/systemd --switched-root --system --deserialize 21
1 S root         2     0  0  80   0 -     0 kthrea     0   0 8月31 ?       00:00:00 [kthreadd]
1 S root         3     2  0  80   0 -     0 smpboo     0   0 8月31 ?       00:00:01 [ksoftirqd/0]
1 S root         5     2  0  60 -20 -     0 worker     0   0 8月31 ?       00:00:00 [kworker/0:0H]
1 S root         7     2  0 -40   - -     0 smpboo     0   0 8月31 ?       00:00:01 [migration/0]
1 S root         8     2  0  80   0 -     0 rcu_gp     0   0 8月31 ?       00:00:00 [rcu_bh]
1 S root         9     2  0  80   0 -     0 rcu_gp     0   1 8月31 ?       00:01:32 [rcu_sched]
5 S root        10     2  0 -40   - -     0 smpboo     0   0 8月31 ?       00:00:05 [watchdog/0]
```



显示指定的属性

```
ps -o attr

ps -axo pid,command,ni

[webuser@VM_0_4_centos ~]$ ps -axo pid,command,ni
  PID COMMAND                      NI
    1 /usr/lib/systemd/systemd --   0
    2 [kthreadd]                    0
    3 [ksoftirqd/0]                 0
    5 [kworker/0:0H]              -20
    7 [migration/0]                 -
    8 [rcu_bh]                      0
```



# pstree

显示进程树



# pidof

根据进程名 查找pid

```
pidof -- find the process ID of a running program.

[webuser@VM_0_4_centos ~]$ ps aux|grep mysql|grep -v grep
mysql      614  0.0  5.4 1270332 213140 ?      Sl   8月31   8:02 /usr/sbin/mysqld --daemonize --pid-file=/var/run/mysqld/mysqld.pid
[webuser@VM_0_4_centos ~]$ pidof mysqld
614
[webuser@VM_0_4_centos ~]$ 
```



# top



```
[webuser@VM_0_4_centos ~]$ top
top(当前时间) - 21:25:11 up(运行时长) 19 days,  6:55,  7 users（当前登录的用户）,  load average(过去1分钟，5分钟，15分钟的平均队列长度): 0.00, 0.01, 0.05
Tasks（进程相关）: 101 total（101个进程）,   1 running（1个正在运行，和cpu的核数有关）, 100 sleeping,   0 stopped,   0 zombie(僵尸进程)
%Cpu(s):  0.2 us(用户空间用户进程占用的%）,  0.0 sy,  0.0 ni（nice值）, 99.8 id（空闲）,  0.0 wa（等待IO完成所占用的时间）,  0.0 hi（hard interrupt硬件中断占据的时间),  0.0 si（软中断占据的时间）,  0.0 st(被偷走的时间)
KiB Mem :  3882032 total,   118716 free,   977164 used,  2786152 buff/cache
KiB Swap:        0 total,        0 free,        0 used.  2603116 avail Mem 

  PID USER      PR(优先级)  NI    VIRT(虚拟内存集)    RES(常驻内存集)    SHR(共享内存大小) S(状态)  %CPU %MEM     TIME+ COMMAND                                                                                                             
11335 root      20   0       0      0      0 S   0.3  0.0   0:00.10 kworker/0:0                                                                                                         
    1 root      20   0   41152   3560   2392 S   0.0  0.1   2:28.05 systemd                                                                                                             
    2 root      20   0       0      0      0 S   0.0  0.0   0:00.14 kthreadd                                                                                                            
    3 root      20   0       0      0      0 S   0.0  0.0   0:01.69 ksoftirqd/0                                                                                                         
    5 root       0 -20       0      0      0 S   0.0  0.0   0:00.00 kworker/0:0H                                                                                                        
    7 root      rt   0       0      0      0 S   0.0  0.0   0:01.39 migration/0   
```

交互式子命名

```
M	#安装内存排序
P	#CPU使用时间百分比排序
T	#CPU的使用时间

l	#切换显示平均负载和启动时间
t	#切换显示CPU状态相关信息
m	#切换显示内存相关信息

c	#显示完整的命令行信息
q	#退出top
k	#终止某个进程

```



top相关的选项

```
top -d 1 #1s刷新一次

top -b #向下翻屏幕

top -n #一共翻多少屏幕

```





# 进程间通信



IPC ： Inter Process Communication

* 共享内存
* 信号：Signal
* Semaphore:旗语



# kill

终止一个进程，也就是向其他进程发信号

```
[webuser@VM_0_4_centos ~]$ kill -l
 1) SIGHUP       2) SIGINT       3) SIGQUIT      4) SIGILL       5) SIGTRAP
 6) SIGABRT      7) SIGBUS       8) SIGFPE       9) SIGKILL     10) SIGUSR1
11) SIGSEGV     12) SIGUSR2     13) SIGPIPE     14) SIGALRM     15) SIGTERM
16) SIGSTKFLT   17) SIGCHLD     18) SIGCONT     19) SIGSTOP     20) SIGTSTP
21) SIGTTIN     22) SIGTTOU     23) SIGURG      24) SIGXCPU     25) SIGXFSZ
26) SIGVTALRM   27) SIGPROF     28) SIGWINCH    29) SIGIO       30) SIGPWR
31) SIGSYS      34) SIGRTMIN    35) SIGRTMIN+1  36) SIGRTMIN+2  37) SIGRTMIN+3
38) SIGRTMIN+4  39) SIGRTMIN+5  40) SIGRTMIN+6  41) SIGRTMIN+7  42) SIGRTMIN+8
43) SIGRTMIN+9  44) SIGRTMIN+10 45) SIGRTMIN+11 46) SIGRTMIN+12 47) SIGRTMIN+13
48) SIGRTMIN+14 49) SIGRTMIN+15 50) SIGRTMAX-14 51) SIGRTMAX-13 52) SIGRTMAX-12
53) SIGRTMAX-11 54) SIGRTMAX-10 55) SIGRTMAX-9  56) SIGRTMAX-8  57) SIGRTMAX-7
58) SIGRTMAX-6  59) SIGRTMAX-5  60) SIGRTMAX-4  61) SIGRTMAX-3  62) SIGRTMAX-2
63) SIGRTMAX-1  64) SIGRTMAX
[webuser@VM_0_4_centos ~]$ 

1 :SIGHUP	#让一个进程不用重启，就能重读配置文件，并让新的配置信息生效
2 :SIGINT	#int interrupt 中断一个进程 (ctrl+C)
9 :SIGKILL	#杀死一个进程	，强行杀死
15:SIGTERM	#终止一个进程(默认) ，grateful 优雅的退出

kill -15 pid  # 默认就是15
or
kill -SIGTERM
or
kill -TERM


killall command #杀死所有的进程名
killall -9 command	#可以指定级别

```



# nice

调整nice值

调整已经启动的进程的nice值

```
#已启动的进程调整nice值
renice -3 pid  # 将pid的进程调整为 -3


#在启动时指定nice值
nice -n NI command

```



# 前台进程，后台进程

```
前台：占据了命令提示符
后台：启动之后，释放命令提示符，后续的操作在后台完成

#查看所有的后台进程
jobs -l
	作业号:
		+命令默认操作的作业
		-命令将默认操作的第二个作业
		


ctrl+z将一个真在前台运行的进程送往后台
or
command &


#将后台进程送往前台


#bg 让后台的停止作业继续运行
bg [[%]jobid]  

#fg让作业重新调回前台
fg [[%]jobid]

#终止某个作业
kill %jobid
```



# vmstat

系统状态查看命令

```
[webuser@VM_0_4_centos ~]$ vmstat
procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
 r(运行队列长度)  b(阻塞队列长度)   swpd   free   buff  cache   si(物理内存放入到交换分区中)   so    bi(有多少个磁盘块被调入到内存中)    bo   in(中断个数)   cs(context switch) us(用户空间占用CPU%比) sy id wa(等待IO) st(被偷走)
 1  0      0 155748 236724 2513368    0    0     1    16    0    8  0  0 100  0  0
[webuser@VM_0_4_centos ~]$

#每隔1秒刷新一次
vmstat 1

#每隔1秒刷新一次,但是只显示5次
vmstat 1 5

```



#  /proc/meminfo 

内存信息

```
[webuser@VM_0_4_centos ~]$ cat /proc/meminfo 
MemTotal:        3882032 kB
MemFree:          146508 kB
MemAvailable:    2594844 kB
Buffers:          236728 kB
Cached:          2309552 kB
//....
```





# /proc/cpuinfo



cpu信息

```
[webuser@VM_0_4_centos ~]$ cat /proc/cpuinfo 
processor       : 0
vendor_id       : GenuineIntel
cpu family      : 6
model           : 79
model name      : Intel(R) Xeon(R) CPU E5-26xx v4
stepping        : 1
microcode       : 0x1
cpu MHz         : 2394.446
cache size      : 4096 KB
physical id     : 0
siblings        : 2
core id         : 0
cpu cores       : 2
apicid          : 0

// ..
```



# 查看某个进程的mem,cpu，maps的文件

```
ll /proc/pid


[webuser@VM_0_4_centos ~]$ ll /proc/614/m
map_files/  maps        mem         mountinfo   mounts      mountstats  

[webuser@VM_0_4_centos ~]$ sudo cat  /proc/614/maps
[sudo] password for webuser: 
00400000-01ad2000 r-xp 00000000 fd:01 147097                             /usr/sbin/mysqld
01cd1000-01dc0000 r--p 016d1000 fd:01 147097                             /usr/sbin/mysqld
01dc0000-01e6b000 rw-p 017c0000 fd:01 147097                             /usr/sbin/mysqld
01e6b000-01f2a000 rw-p 00000000 00:00 0 
0209d000-020be000 rw-p 00000000 00:00 0                                  [heap]
020be000-02c00000 rw-p 00000000 00:00 0                                  [heap]
```

