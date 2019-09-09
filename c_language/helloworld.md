[TOC]

# 第一个helloworld说明

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



# 系统调用

```c
#include "stdlib.h"

int main()
{
  //执行系统调用
	system("ls");
	return 0;
}
```





