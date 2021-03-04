[toc]

转：https://www.cnblogs.com/mch0dm1n/p/5727667.html

# C语言中extern的用法



1. **extern用在变量或函数的声明前，用来说明“此变量/函数是在别处定义的，要在此处引用”。**

2. extern修饰变量的声明。

　　举例：若a.c中需引用b.c中的变量int v，可以在a.c中声明extern int v，然后就可以引用变量v;需要注意的是，被引用的变量v的链接属性必须是外链接(external)的，也就是说a.c要引用到变量v，不只是取决于在a.c中声明extern int v，还取决于变量v本身是能够被引用到的。这里涉及到另外一个话题---变量的作用域。能够被其他模块以extern引用到的变量通常是全局变量。

　　还有一点是，extern int v可以放在a.c中的任何地方，比如可以在a.c中函数func()定义的开头处声明extern int v，然后就可以引用到变量v了，只不过这样只能在func()作用域中引用变量v(这还是变量作用域的问题，对于这一点来说，很多人使用时都心存顾虑，好像extern声明只能用于文件作用域似的)。

 

3. extern修饰函数的声明。

　　本质上讲，变量和函数没有区别。函数名是指向函数二进制块开头处的指针。如果文件a.c要引用b.c中的函数，比如在b.c中原型是int func(int m)，那么就可以在a.c中声明extern int func(int m)，然后就能使用func()来做任何事情。就像变量的声明一样，extern int func(int m)可以放在a.c中的任何位置，而不一定非要放在a.c的文件作用域的范围中，

　　对其他模块中函数的引用，最常用的方法是包含这些函数声明的头文件。使用extern和包含头文件来引用函数的区别：extern的引用方式比包含头文件要间接得多。extern的使用方法是直接了当的，想引用哪个函数就用extern声明哪个函数。这大概是kiss原则的一种体现。这样做的一个明显的好处是，会加速程序的编译(确切地说是预处理)的过程，节省时间。在大型C程序编译过程中，这种差异是非常明显的。

 

4. 此外，extern修饰符可用于指示C或者C++函数的调用规范。比如在C++中调用C库函数，就需要在C++程序中用extern "C"声明要引用的函数。这是给链接器使用的，告诉链接器在链接的时候用C函数规范来链接。主要原因是C++和C程序编译完成后再目标代码中命名规则不同。

 

5. 简要例子：

```c
#include <stdio.h>
#include <stdlib.h>

int x = 0;
int y = 5;
int func1()
{
    extern p, q;
    printf("p is %d, q is %d\n", p, q);
    return 0;
}

int p = 8;
int q = 10;
int main()
{
    func1();
    printf("x is %d, y is %d\n", x, y);
}
```

输出结果

```shell
[cys@localhost c_workspace]$ gcc test.c
[cys@localhost c_workspace]$ ./a.out 
p is 8, q is 10
x is 0, y is 5
```



如果是

```c
#include <stdio.h>
#include <stdlib.h>

int x = 0;
int y = 5;
int func1()
{
    extern p, q;
    printf("p is %d, q is %d\n", p, q);
    return 0;
}

//int p = 8;
//int q = 10;
int main()
{
    func1();
    printf("x is %d, y is %d\n", x, y);
}
```

输出结果如下

```shell
[cys@localhost c_workspace]$ gcc test.c
/tmp/cchtVRIu.o：在函数‘func1’中：
test.c:(.text+0x6)：对‘q’未定义的引用
test.c:(.text+0xc)：对‘p’未定义的引用
collect2: 错误：ld 返回 1
[cys@localhost c_workspace]$ 
```

