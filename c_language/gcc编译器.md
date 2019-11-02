[toc]



# gcc编译的四个阶段

![](/Users/chenyansong/Documents/note/images/linux/command/3_gcc编译的四个阶段.png)

# gcc的一些参数

```shell
ESc
-o 指定文件名

gcc hello.c #默认生成a.out

#指定头文件的路径,比如 头文件在 include中
gcc sum.c -I ./include -o app

#在编译的时候指定宏
gcc sum.c -I ./include -D DEBUG -o app
#这样不用在程序中define DEBUG宏，而是通过编译的时候动态去指定宏
```

sum.c

```c
#include <stdio.h>
#include "head.h"
//#define DEBUG  //编译的时候动态的指定宏

int main(void)
{
    int a = NUM1;
    int aa;
    int b = NUM2;
    int sum = a + b;
    // 小盆友： 这是一个加法运算
#ifdef DEBUG
    printf("The sum value is: %d + %d = %d\n", a, b, sum);
#endif
    return 0;
}
```



```shell
#-O是否进行优化，-O1, -O2, -O3
gcc sum.c -I ./include -O3 -o app 


#比如，下面的优化
int a = 10;
int b = a;
int d =b;
#优化的结果就是
d = 10;


-Wall
#是否输出警告信息
gcc sum.c -I ./include -Wall -o app

-g
#添加调试信息,调试有用
gcc sum.c -I ./include -o app -g
```



# 静态库

* 命名规则

  lib+库的名字(mytest)+.a

  libmytest.a

* 制作步骤

  1. 生成对应的.o文件 （-c就能生成）
  2. 将生成的.o文件打包: ar rcs + 静态库的名字(libMytest.a)+生成的所有的.o

* 发布和使用静态库

  1. 发布静态库
  2. 头文件

* 优缺点



工程的目录结构如下

![image-20191102151843074](/Users/chenyansong/Documents/note/images/linux/command/image-20191102151843074.png)

```shell
cd src

#1.生成.o
gcc *.c -c -I ../include

#2.ar打包.o
ar rcs libMyCalc.a *.o
## libMyCalc.a就是生成的静态库

#3.mv libMyCalc.a ../lib

#4.将lib和include中的内容给到用户，那么用户就可以用到其中的内容了
## 通过include/head.h去调用接口

#5.用户使用我们的发布的库,编写测试程序
#vim main.c
#include <stdio.h>
#include "head.h"

int main(void)
{
    int sum = add(2, 24);
    printf("sum = %d\n", sum);
    return 0;
}


#6.编译
gcc main.c -I include lib/libMyCalc.a -o sum

#other way:
#-L lib 指定静态库的目录
#-l MyCalc 指定库的名字(掐头去尾)
gcc main.c -I include -L lib -l MyCalc -o sum2

```

查看静态库中的内容

nm libMyCalc.a

![image-20191102153404022](/Users/chenyansong/Documents/note/images/linux/command/image-20191102153404022.png)

同理，我们也是可以查看最后生成的可执行程序

nm myapp

![image-20191102153551392](/Users/chenyansong/Documents/note/images/linux/command/image-20191102153551392.png)



使用静态库的优缺点

![](/Users/chenyansong/Documents/note/images/linux/command/6_静态库的打包.png)



# 动态库（共享库）

* 命名规则
  1. lib+库的名字+.so
* 制作步骤
  1. 生成与位置无关的代码（生成与位置无关的.o）
  2. 将.o打包成共享库（动态库）
* 解决程序执行时动态库无法被加载的问题
* 优缺点

```shell
cd src/
#1.生成与位置无关的.o
gcc -fPIC -c *.c -I ../include

#2.打包.o
gcc -shared -o libMyCalc.so *.o -I ../include

#3.移动到lib
mv libMyCalc ../lib/

#4.将lib和include发布给用户

#5.用户使用动态库
gcc main.c lib/libMyCalc.so -o app -I include
#other:-L指定库所在的目录，-l 指定库的名字(掐头去尾)
gcc main.c -L lib -l MyCalc -o app -I include
```

虚拟地址空间

![7_虚拟地址空间](/Users/chenyansong/Documents/note/images/linux/command/虚拟地址空间.png)



```shell

#查看可执行程序的所有的依赖
ldd myapp
```

![image-20191102160906496](/Users/chenyansong/Documents/note/images/linux/command/image-20191102160906496.png)

```shell
#这个就是动态链接器，其本质就是一个动态库
/lib64/ld-linux-x86_64-linux-gnu/libc.so.6

#动态链接库回去环境变量中，查找对应的动态库文件
echo $PATH

#第一种解决方案，将动态库放入到 /lib (这里是系统的动态库)中，不推荐使用
cp lib/libMyCalc.so  /lib/

#第二种方式
#首先搜索的是 LD_LIBRARY_PATH ，然后再去默认的PATH中去搜索(这也只是一种临时的方式，制作动态库的时候，用作临时的测试)
export LD_LIBRARY_PATH=./lib

ldd myapp
```

![](/Users/chenyansong/Documents/note/images/linux/command/image-20191102162319885.png)

```shell
#方式3
#设置当前用户的配置文件中 ~/.bashrc

vi ~/.bashrc
export LD_LIBRARY_PATH=/home/hadoop/lib

#关闭当前终端，然后重新打开，这样就会生效(在终端每次启动的时候，都会读取bashrc的内容)

#方式4(推荐)
##1.找到动态链接器的配置文件，将动态库的路径写入配置文件中
##2.更新 -- sudo ldconfig ; sudo ldconfig -v 查看info
ll /etc/ld.so.conf
sudo vi /etc/ld.so.conf

```

![image-20191102163255113](/Users/chenyansong/Documents/note/images/linux/command/image-20191102163255113.png)

```shell
sudo ldconfig -v
```

![image-20191102163437328](/Users/chenyansong/Documents/note/images/linux/command/image-20191102163437328.png)



优缺点

![image-20191102164356617](/Users/chenyansong/Documents/note/images/linux/command/image-20191102164356617.png)