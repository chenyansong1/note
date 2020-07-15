[toc]


导读

> 1. 实例说明没有原子操作存在的问题
> 2. 单CPU和SMP针对原子操作的不同方式
> 3. lock锁总线



```c
int request_printer()
{
    while(printer==0)
        wait();
    printer--;
    //...
}

//这是一段请求打印机的代码
```

```assembly
# printer-- 汇编如下
movl printer %eax
decl %eax
movl %eax printer
```

由于单CPU上存在着中断，以上三条汇编指令在执行的过程中会随时发生中断的可能，这样当多线程执行的时候就会发生错乱

**原因是printer--不是原子操作的，那么我们改成如下的方式**

```assembly
# printer--
decl printer 
#此时是直接读取内存，而不是通过寄存器中间周转
```

这种方式在SMP（多CPU）下还是存在问题，例如多线程在不同的CPU下并行执行时，就会出现问题，解决的方式如下：

```assembly
# printer--
lock decl printer
```

> lock前缀告诉CPU，在执行当前指令期间锁住内存总线，这样在decl操作的微指令执行期间，如果另外的CPU访问printer，**由于得不到总线仲裁的许可**，在decl操作完成之前，不会访问到printer内存变量，因此他保证了在多处理器上的原子性

以下是一个原子操作函数的实例

```c
104 static inline void atomic_dec(atomic_t *v)
105 {
106     asm volatile(LOCK_PREFIX "decl %0"
107              : "=m" (v->counter)
108              : "m" (v->counter));
109 }
```

> 在单CPU上LOCK_PREFIX展开为空，在x86的多CPU上，LOCK_PREFIX展开为lock，如下

```assembly
 30 #ifdef CONFIG_SMP
 31 #define LOCK_PREFIX \
 32         ".section .smp_locks,\"a\"\n"   \
 33         _ASM_ALIGN "\n"         \
 34         _ASM_PTR "661f\n" /* address */ \
 35         ".previous\n"           \
 36         "661:\n\tlock; "                  #主要看这一行
 37 
 38 #else /* ! CONFIG_SMP */
 39 #define LOCK_PREFIX ""                    #单CPU上为空
 40 #endif
```



参考：

[独辟蹊径品内核](https://book.douban.com/subject/3894413/)