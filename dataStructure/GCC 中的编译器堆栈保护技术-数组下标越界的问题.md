[toc]

# GCC 中的编译器堆栈保护技术

参见：https://www.ibm.com/developerworks/cn/linux/l-cn-gccstack/index.html

如下的代码

```c
int main(int argc, char* argv[]){ 
	int i = 0; 
	int arr[3] = {1,2,3}; 
	for(; i<=3; i++){ 
		arr[i] = 0; 
		printf("hello world\n"); 
	} 
	return 0;
}
```

这段代码的运行结果并非是打印三行“hello word”，而是会无限打印“hello world”，这是为什么呢？

原因：a[3]也会被定位到某块不属于数组的内存地址上，而这个地址正好是存储变量 i 的内存地址，那么 a[3]=0 就相当于 i=0，所以就会导致代码无限循环。

这里需要引出一个函数参数在堆栈中的存放方式的问题

这段代码无限循环原因有2，以及一个附加条件:

1. 栈空间从高往低依次分配，i占4字节，接着arr占12字节，内存从高往低是这样：存i的4字节|arr[2]|arr[1]|arr[0]，数组访问是通过“baseAddr+index乘typeSize”得到，算下来当index=3时，刚好是i的地址
2. 这里刚好满足字节对齐，系统为64位系统，字长64，那么字节对齐必须是8字节的倍数，刚好i变量和arr变量占了16字节，对齐了，如果这里将arr[3]改为arr[4]，为了对齐，内存从高往低是这样：存i的4字节|空4节|arr[3]|arr[2]|arr[1]|arr[0]，那么arr[4]刚好是空的4字节，无法影响到i的值，则并不会无限循环

附加条件：编译时gcc默认会自动添加越界保护，此处要达到无限循环效果，编译时需加上-fno-stack-protector去除该保护



下面是汇编的代码

```assembly
int main(int argc, char* argv[]){ 
	int i = 0; 
	int arr[3] = {1,2,3}; 
	for(; i<=3; i++){ 
		arr[i] = 0; 
		printf("hello world\n"); 
	} 
	return 0;
}

000000000040051d <main>:
  40051d:       55                      push   %rbp
  40051e:       48 89 e5                mov    %rsp,%rbp
  400521:       48 83 ec 20             sub    $0x20,%rsp
  400525:       89 7d ec                mov    %edi,-0x14(%rbp)
  400528:       48 89 75 e0             mov    %rsi,-0x20(%rbp)
  40052c:       c7 45 f0 01 00 00 00    movl   $0x1,-0x10(%rbp)
  400533:       c7 45 f4 02 00 00 00    movl   $0x2,-0xc(%rbp)
  40053a:       c7 45 f8 03 00 00 00    movl   $0x3,-0x8(%rbp)
  400541:       c7 45 fc 00 00 00 00    movl   $0x0,-0x4(%rbp)
  400548:       eb 1b                   jmp    400565 <main+0x48>
  40054a:       8b 45 fc                mov    -0x4(%rbp),%eax
  40054d:       48 98                   cltq   
  40054f:       c7 44 85 f0 00 00 00    movl   $0x0,-0x10(%rbp,%rax,4)
  400556:       00 
  400557:       bf 10 06 40 00          mov    $0x400610,%edi
  40055c:       e8 9f fe ff ff          callq  400400 <puts@plt>
  400561:       83 45 fc 01             addl   $0x1,-0x4(%rbp)
  400565:       83 7d fc 03             cmpl   $0x3,-0x4(%rbp)
  400569:       7e df                   jle    40054a <main+0x2d>
  40056b:       b8 00 00 00 00          mov    $0x0,%eax
  400570:       c9                      leaveq 
  400571:       c3                      retq   
  400572:       66 2e 0f 1f 84 00 00    nopw   %cs:0x0(%rax,%rax,1)
  400579:       00 00 00 
  40057c:       0f 1f 40 00             nopl   0x0(%rax)
  
  
==================================================================================

int main(int argc, char* argv[]){ 
	int i = 0; 
	int arr[3] = {1,2,3,4}; 
	for(; i<=3; i++){ 
		arr[i] = 0; 
		printf("hello world\n"); 
	} 
	return 0;
}


000000000040051d <main>:
  40051d:       55                      push   %rbp
  40051e:       48 89 e5                mov    %rsp,%rbp
  400521:       48 83 ec 30             sub    $0x30,%rsp
  400525:       89 7d dc                mov    %edi,-0x24(%rbp)
  400528:       48 89 75 d0             mov    %rsi,-0x30(%rbp)
  40052c:       c7 45 e0 01 00 00 00    movl   $0x1,-0x20(%rbp)
  400533:       c7 45 e4 02 00 00 00    movl   $0x2,-0x1c(%rbp)
  40053a:       c7 45 e8 03 00 00 00    movl   $0x3,-0x18(%rbp)
  400541:       c7 45 ec 04 00 00 00    movl   $0x4,-0x14(%rbp)
  400548:       c7 45 fc 00 00 00 00    movl   $0x0,-0x4(%rbp)
  40054f:       eb 1b                   jmp    40056c <main+0x4f>
  400551:       8b 45 fc                mov    -0x4(%rbp),%eax
  400554:       48 98                   cltq   
  400556:       c7 44 85 e0 00 00 00    movl   $0x0,-0x20(%rbp,%rax,4)
  40055d:       00 
  40055e:       bf 10 06 40 00          mov    $0x400610,%edi
  400563:       e8 98 fe ff ff          callq  400400 <puts@plt>
  400568:       83 45 fc 01             addl   $0x1,-0x4(%rbp)
  40056c:       83 7d fc 03             cmpl   $0x3,-0x4(%rbp)
  400570:       7e df                   jle    400551 <main+0x34>
  400572:       b8 00 00 00 00          mov    $0x0,%eax
  400577:       c9                      leaveq 
  400578:       c3                      retq   
  400579:       0f 1f 80 00 00 00 00    nopl   0x0(%rax)
```



