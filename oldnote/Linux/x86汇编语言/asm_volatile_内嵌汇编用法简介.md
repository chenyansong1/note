[toc]

转自：https://blog.csdn.net/geekcome/article/details/6216436



```shell

__asm__ __volatile__内嵌汇编用法简述 在阅读C/C++原码时经常会遇到内联汇编的情况，下面简要介绍下__asm__ __volatile__内嵌汇编用法。因为我们华清远见教学平台是ARM体系结构的，所以下面的示例都是用ARM汇编。

```





# 内联汇编格式

带有C/C++表达式的内联汇编格式为

```c
__asm__　__volatile__("Instruction List" : Output : Input : Clobber/Modify);
```



其中每项的概念及功能用法描述如下

# asm

```c
__asm__是GCC 关键字asm 的宏定义：

#define __asm__ asm

__asm__或asm 用来声明一个内联汇编表达式，所以任何一个内联汇编表达式都是以它开头的，是必不可少的。
```



# Instruction List





