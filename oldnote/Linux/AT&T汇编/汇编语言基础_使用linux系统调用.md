[TOC]



# 系统调用

## 查看系统调用

如果是汇编程序，是在unistd.h中配置的，如下文件

```shell
cat /usr/include/asm/unistd.h
```

![1582091623678](E:\git-workspace\note\images\linux\assemly\1582091623678.png)

## 查找系统调用定义

```shell
#在man页的第2部分
man 2 exit


#Name: 显示这个系统调用的名称
#Synopsis:提要，显示如何使用这个系统调用
#Description：描述，对这个系统调用的简要描述
#Return Value：返回值，系统调用完成时返回的值
```



## 常用的系统调用

* 内存访问内核系统调用

  ![1582092139435](E:\git-workspace\note\images\linux\assemly\1582092139435.png)

* 设备访问内核系统调用

  ![1582092199303](E:\git-workspace\note\images\linux\assemly\1582092199303.png)

* 文件系统调用

  ![1582092222208](E:\git-workspace\note\images\linux\assemly\1582092222208.png)

* 进程系统调用

  ![1582092245369](E:\git-workspace\note\images\linux\assemly\1582092245369.png)



# 使用系统调用

## 系统调用值

对应的是系统调用号，这个值要存放到EAX中，如下：

```assembly
movl $1, %eax
int 0x80

;unistd.h中exit的系统调用如下：
#define __NR_exit 1
```



## 系统调用输入值

前面EAX已经使用，EIP，EBP，ESP不能使用，那么能够使用的寄存器如下：

* EBX（第一个参数）
* ECX（第二个参数）
* EDX（第三个参数）
* ESI（第四个参数）
* EDI（第五个参数）

当输入参数超过6个时，使用EBX寄存器保存指向输入参数的内存位置的指针，输入参数按照连续的顺序存储，系统调用使用这个指针访问内存位置以便读取参数

参数对应问题：当系统调用中的函数参数和上面的寄存器参数的对应关系，对于write系统调用，如下：

```c
ssize_t write(int fd, const void *buf, size_t count);

/**
fd:输出设备的文件描述符的整数值
buf:输入设备的字符串的指针
count:写的字符串的长度
*/
```

* EBX（第一个参数）：整数文件描述符
* ECX（第二个参数）：指向要写入的字符串的指针（内存地址）
* EDX（第三个参数）：要写入的字符串的长度

```assembly
.section .data
output:
	.ascii "thsi is a test message.\n"
output_end:
	.equ len, output_end - output
.section .text
.globl _start
_start:
	movl $4, %eax
	movl $1, %ebx
	movl $output, %ecx
	movl $len, %edx
	int $0x80
	
	movl $1, %eax
	movl $0, %ebx
	movl $0x80
	
;0(STDIN):标准输入（一般是键盘）
;1(STDOUT):标准输出（一般是屏幕）
;2(STDERR):标准错误输出（一般是终端屏幕）
```

## 系统调用返回值

系统调用的返回值存放在EAX寄存器中





![](C:\Users\landun\AppData\Roaming\Typora\typora-user-images\1582074737804.png)

