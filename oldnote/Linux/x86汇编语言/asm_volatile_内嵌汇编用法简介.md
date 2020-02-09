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

```c
Instruction List 是汇编指令序列。它可以是空的，比如：__asm__ __volatile__(""); 或 __asm__ ("");都是完全合法的内联汇编表达式，只不过这两条语句没有什么意义。但并非所有Instruction List 为空的内联汇编表达式都是没有意义的，比如：__asm__ ("":::"memory");

就非常有意义，它向GCC 声明：“内存作了改动”，GCC 在编译的时候，会将此因素考虑进去。 当在"Instruction List"中有多条指令的时候，可以在一对引号中列出全部指令，也可以将一条 或几条指令放在一对引号中，所有指令放在多对引号中。如果是前者，可以将每一条指令放在一行，如果要将多条指令放在一行，则必须用分号（；）或换行符（/n）将它们分开. 
```



> 综上述：
>
> (1)每条指令都必须被双引号括起来
>
> (2)两条指令必须用换行或分号分开。



例如： 在ARM系统结构上关闭中断的操作

```c
int disable_interrupts (void) 
{ 
unsigned long old,temp; 
__asm__ __volatile__("mrs %0, cpsr/n" 
"orr %1, %0, #0x80/n" 
"msr cpsr_c, %1" 
: "=r" (old), "=r" (temp) 
: 
: "memory"); 
return (old & 0x80) == 0; 
}
```



# volatile

GCC 关键字volatile 的宏定义**

```c
#define __volatile__ volatile

//__volatile__或volatile 是可选的。如果用了它，则是向GCC 声明不允许对该内联汇编优化，否则当 使用了优化选项(-O)进行编译时，GCC 将会根据自己的判断决定是否将这个内联汇编表达式中的指令优化掉。
```



# Output

Output 用来指定当前内联汇编语句的输出

```c
static unsigned long read_p15_c1 (void) 
{ 
unsigned long value; 
__asm__ __volatile__( 
"mrc p15, 0, %0, c1, c0, 0 @ read control reg/n" 
: "=r" (value) @编译器选择一个R*寄存器 
: 
: "memory"); 
#ifdef MMU_DEBUG 
printf ("p15/c1 is = %08lx/n", value); 
#endif 
return value; 
}
```

# Input

Input 域的内容用来指定当前内联汇编语句的输入Output和Input中，格式为形如“constraint”(variable)的列表（逗号分隔)





GCC_Inline_ASM GCC内联汇编