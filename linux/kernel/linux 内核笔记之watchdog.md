[toc]

# linux 内核笔记之watchdog

转自：https://blog.csdn.net/yhb1047818384/article/details/70833825

简而言之，watchdog是为了保证系统正常运行，或者从死循环，死锁等一场状态退出的一种机制。

看门狗分硬件看门狗和软件看门狗。硬件看门狗是利用一个定时器电路，其定时输出连接到电路的复位端，程序在一定时间范围内对定时器清零(俗称“喂狗”)，因此程序正常工作时，定时器总不能溢出，也就不能产生复位信号。如果程序出现故障，不在定时周期内复位看门狗，就使得看门狗定时器溢出产生复位信号并重启系统。软件看门狗原理上一样，只是将硬件电路上的定时器用处理器的内部定时器代替，这样可以简化硬件电路设计，但在可靠性方面不如硬件定时器，比如系统内部定时器自身发生故障就无法检测到。

软件看门狗分为两种，用于检测soft lockup的普通软狗(基于时钟中断)，以及检测hard lockup的NMI狗（基于NMI中断）。

**注1**：时钟中断优先级小于NMI中断
**注2**：lockup，是指某段内核代码占着CPU不放。Lockup严重的情况下会导致整个系统失去响应。
soft lockup 和 hard lockup，它们的唯一区别是 hard lockup 发生在CPU屏蔽中断的情况下。

#### **软狗**

单个cpu检测线程是否正常调度。

一般软狗的正常流程如下（假设软狗触发的时间为20s）

![这里写图片描述](https://img-blog.csdn.net/20170430170428338?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhiMTA0NzgxODM4NA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

可能产生软狗的原因：
1.频繁处理硬中断以至于没有时间正常调度
2.长期处理软中断
3.对于非抢占式内核，某个线程长时间执行而不触发调度
4.以上all

### **NMI watchdog**

单个CPU检测中断是否能够正常上报
当CPU处于关中断状态达到一定时间会被判定进入hard lockup

NMI检测流程：

![这里写图片描述](https://img-blog.csdn.net/20170430170510093?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhiMTA0NzgxODM4NA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

可能产生NMI狗的原因：
1.长期处理某个硬中断
2.长时间在禁用本地中断下处理

NMI狗机制也是用一个percpu的hrtimer来喂狗，为了能够及时检测到hard lockup状态，在比中断优先级更高的NMI上下文进行检测。

#### **硬狗**

用于检测所有CPU是否正常运行
任何一个CPU都可以喂硬狗，当在一定时间内没有核喂狗，触发硬狗复位

硬狗检测流程：

![这里写图片描述](https://img-blog.csdn.net/20170430170550480?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveWhiMTA0NzgxODM4NA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

可能产生硬狗的原因：
1.CPU（没有软狗，NMI狗触发条件）全部挂死
2.CPU之间存在硬件依赖关系，某一个CPU挂死，有软件层面的共享资源

**基于内核代码watchdog.c分析soft lockup以及hard lockup的实现机制**（kernel/watchdog.c）

#### **soft lockup**

1. 每一个CPU上都有一个watchdog线程（线程名为watchdog/0,watchdog/1 …）

```c
static struct smp_hotplug_thread watchdog_threads = {
    .store          = &softlockup_watchdog,
    .thread_should_run  = watchdog_should_run,
    .thread_fn      = watchdog,
    .thread_comm        = "watchdog/%u",
    .setup          = watchdog_enable,
    .park           = watchdog_disable,
    .unpark         = watchdog_enble,
};
```



2.该线程定期调用watchdog函数

```c
static void __touch_watchdog(void)
{   
    /*更新watchdog运行时间戳*/
    __this_cpu_write(watchdog_touch_ts, get_timestamp());
}

static void watchdog(unsigned int cpu)
{
    /*更新softlock hrtimer cnt = hrtimer interrupts*/
    __this_cpu_write(soft_lockup_hrtimer_cnt,
             __this_cpu_read(hrtimer_interrupts));
    __touch_watchdog();
}12345678910111213
```

3.时间中断

```c
static void watchdog_enable(unsigned int cpu)
{
    struct hrtimer *hrtimer = &__raw_get_cpu_var(watchdog_hrtimer);

    /* kick off the timer for the hardlockup detector */
    hrtimer_init(hrtimer, CLOCK_MONOTONIC, HRTIMER_MODE_REL);
    hrtimer->function = watchdog_timer_fn;

    /* done here because hrtimer_start can only pin to smp_processor_id() */
    hrtimer_start(hrtimer, ns_to_ktime(sample_period),
              HRTIMER_MODE_REL_PINNED);
}
}12345678910111213
```

该函数主要功能就是初始化一个高精度timer，唤醒watchdog 喂狗线程。
hrtimer的时间处理函数为：

```c
static enum hrtimer_restart watchdog_timer_fn(struct hrtimer *hrtimer)
{
    //watchdog上次运行的时间戳
    unsigned long touch_ts = __this_cpu_read(watchdog_touch_ts);
    struct pt_regs *regs = get_irq_regs();
    int duration;
    //在唤醒watchdog kthread之前递增hrtimer_interrupts，保证kthread更新其时间戳
    watchdog_interrupt_count();
    //唤醒watchdog kthread，保证kthread与timer相同的运行频率
    wake_up_process(__this_cpu_read(softlockup_watchdog));
    //再次调度hrtimer下一个周期运行
    hrtimer_forward_now(hrtimer, ns_to_ktime(sample_period));

    ...

    //检测是否发生soft lockup
    duration = is_softlockup(touch_ts);
    if (unlikely(duration)) {
        printk(KERN_EMERG "BUG: soft lockup - CPU#%d stuck for %us! [%s:%d]\n",
            smp_processor_id(), duration,
            current->comm, task_pid_nr(current));
        print_modules();
        print_irqtrace_events(current);
        //dump 寄存器和堆栈
        if (regs)
            show_regs(regs);
        else
            dump_stack();

        if (softlockup_panic)
            panic("softlockup: hung tasks");
    } 
    return HRTIMER_RESTART;
}
//检查抢占被关闭的时间间隔
//watchdog kthread在watchdog timer的中断上下文中被唤醒，
//当中断退出时，kthread会抢占cpu上的当前进程。如果
//抢占被关闭的话，则不会发生抢占，watchdog便无法更新时
//间戳，当抢占关闭的时间超过阈值时，核心认为发生了soft
//lock up。
//注：soft lockup阈值 watchdog_thresh * 2 (20s)
3.2 static int is_softlockup(unsigned long touch_ts)
{
    //当前时间戳
    unsigned long now = get_timestamp(smp_processor_id());
    //watchdog在 watchdog_thresh * 2 时间内未被调度过
    if (time_after(now, touch_ts + get_softlockup_thresh()))
        return now - touch_ts;

    return 0;
}123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051
```

函数主要任务：
(1)获取watchdog上次运行的时间戳
(2)递增watchdog timer运行次数
(3)检查watchdog时间戳，是否发生了soft lockup(如果发生了，dump堆栈，打印信息)
(4)重调度timer

lockup 检测函数：

```c
static int is_softlockup(unsigned long touch_ts)
{
    //当前时间戳
    unsigned long now = get_timestamp(smp_processor_id());
    //watchdog在 watchdog_thresh * 2 时间内未被调度过
    if (time_after(now, touch_ts + get_softlockup_thresh()))
        return now - touch_ts;

    return 0;
}12345678910
```

#### **hard lockup**

hard lock主要在NMI中断中就行检测
1.初始化并使能hard lockup检测

```c
static int watchdog_nmi_enable(unsigned int cpu)
{
    //hard lockup事件
    struct perf_event_attr *wd_attr;
    struct perf_event *event = per_cpu(watchdog_ev, cpu);
    ....
    wd_attr = &wd_hw_attr;
    //hard lockup检测周期，10s
    wd_attr->sample_period = hw_nmi_get_sample_period(watchdog_thresh);
    //向performance monitoring注册hard lockup检测事件
    event = perf_event_create_kernel_counter(wd_attr, cpu, NULL, watchdog_overflow_callback, NULL);
    ....
    //使能hard lockup的检测
    per_cpu(watchdog_ev, cpu) = event;
    perf_event_enable(per_cpu(watchdog_ev, cpu));
    return 0;
}1234567891011121314151617
```

perf_event_create_kernel_counter函数主要是注册了一个硬件的事件。
这个硬件在x86里叫performance monitoring，这个硬件有一个功能就是在cpu clock经过了多少个周期后发出一个NMI中断出来。

2.当cpu全负荷跑完20秒后，就会有一个NMI中断发出，对应watchdog_overflow_callback。

```c
static void watchdog_overflow_callback(struct perf_event *event,
         struct perf_sample_data *data,
         struct pt_regs *regs)
{
    //判断是否发生hard lockup
    if (is_hardlockup()) {
        int this_cpu = smp_processor_id();

        //打印hard lockup信息
        if (hardlockup_panic)
            panic("Watchdog detected hard LOCKUP on cpu %d", this_cpu);
        else
            WARN(1, "Watchdog detected hard LOCKUP on cpu %d", this_cpu);

        return;
    }
    return;
}123456789101112131415161718
```

检测是否有hard lockup

```c
static int is_hardlockup(void)
{
    //获取watchdog timer的运行次数
    unsigned long hrint = __this_cpu_read(hrtimer_interrupts);
    //在一个hard lockup检测时间阈值内，如果watchdog timer未运行，说明cpu中断被屏蔽时间超过阈值
    if (__this_cpu_read(hrtimer_interrupts_saved) == hrint)
        return 1;
    //记录watchdog timer运行的次数
    __this_cpu_write(hrtimer_interrupts_saved, hrint);
    return 0;
}1234567891011
```

关闭hard lockup检测

```c
static void watchdog_nmi_disable(unsigned int cpu)
{
    struct perf_event *event = per_cpu(watchdog_ev, cpu);
    if (event) {
        //向performance monitoring子系统注销hard lockup检测控制块
        perf_event_disable(event);
        //清空per-cpu hard lockup检测控制块
        per_cpu(watchdog_ev, cpu) = NULL;
        //释放hard lock检测控制块
        perf_event_release_kernel(event);
    }
    return;
}
```


