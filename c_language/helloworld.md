[TOC]

在Windows下使用gcc的条件

1. 必须是提前安装了gcc这个程序
2. 在Windows系统path的环境变量中，添加qt的bin路径
3. 测试`gcc -v`



```c
#include "stdio.h"
//告诉编译器需要包含的头文件

//主函数，是c语言中的入口函数，一个C语言的程序必须有一个，且仅有一个主函数
int main()
{//c语言中所有的函数代码都是在括号中的
  //使用库函数中的函数
	printf("test from cys");

	return 0;
}

/**
多行注释
多行注释
*/

/*多行注释*/
//单行注释
//区分大小写
//不能用C语言的关键字作为变量名称
//printf("\n"); // \n回车换行
//return之后，后面的语句就不会执行
```

# 编译

## 预编译

```c
-o //指定输出文件名
gcc -o test helloworld.c
//默认生成a.out
```

```shell
[root@spark01 ~]# cat test.c
#include<stdio.h>
int main()//this is describe
{
 int a=0;
 {int a=222;}
 printf("test from cys=%d", a);
 return 0;

}
#预编译
gcc -E -o test.e test.c 
```

![1568075512777](E:\git-workspace\note\images\c_languge\1568075512777.png)

![1568075805136](E:\git-workspace\note\images\c_languge\1568075805136.png)

预编译之后生成的test.e的文件

![1568075617530](E:\git-workspace\note\images\c_languge\1568075617530.png)

**只是将stdio.h文件的内容替换了`#include<stdio.h>**`

![1568075681414](E:\git-workspace\note\images\c_languge\1568075681414.png)

**我们查看test.e文件，发现预编译之后注释也没有了**

我们也可以引用一个自定义的文件

![1568076181301](E:\git-workspace\note\images\c_languge\1568076181301.png)

![1568076207436](E:\git-workspace\note\images\c_languge\1568076207436.png)

> 这里要注意：自定义的文件引用，和系统自带文件的引用方式不同
>
> <头文件>表示让C语言编译器去系统目录（/usr/include）下去寻找相关的头文件
>
> "头文件"表示让C语言编译器去用户当前目录下寻找相关头文件

![1568076447419](E:\git-workspace\note\images\c_languge\1568076447419.png)

## 编译

15：00