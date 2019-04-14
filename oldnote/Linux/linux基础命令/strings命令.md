strings命令



# 由来

转自：

```
Linux/Centos下/lib64/libc.so.6: version `GLIBC_2.14' not found
```

前天，在[Centos](https://www.baidu.com/s?wd=Centos&tn=24004469_oem_dg&rsv_dl=gh_pl_sl_csd)的某个版本下编译了一个可执行程序，复制到另外一个Centos环境下去执行，结果出现了以下错误：

/lib64/libc.so.6: version `GLIBC_2.14' not found

貌似是一个很普遍的错误，去网上搜集了相关的资料并整理了一下



出现这种错误表明程序运行需要GLIBC_2.14，但是系统中却并不存在，因此可以先用strings命令查看下系统中的GLIBC版本

```
strings /lib64/libc.so.6 | grep GLIBC
```
发现系统中最高只支持GLIBC_2.12，解决这个问题有多种方法。



在你准备升级GLIBC库之前，你要好好思考一下，
你真的要升级GLIBC么？
你知道你自己在做什么么？
http://baike.baidu.com/view/1323132.htm?fr=aladdin

glibc是gnu发布的libc库，即c运行库。glibc是[linux系统](https://www.baidu.com/s?wd=linux%E7%B3%BB%E7%BB%9F&tn=24004469_oem_dg&rsv_dl=gh_pl_sl_csd)中最底层的api，几乎其它任何运行库都会依赖于glibc。glibc除了封装[linux操作系统](https://www.baidu.com/s?wd=linux%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F&tn=24004469_oem_dg&rsv_dl=gh_pl_sl_csd)所提供的系统服务外，它本身也提供了许多其它一些必要功能服务的实现…
总的来说，不说运行在linux上的一些应用，或者你之前部署过的产品，就是很多linux的基本命令，比如cp, rm, ll之类，都得依赖于它
网上很多人有惨痛教训，甚至升级失败后系统退出后无法重新进入了。。。。。。



# 使用

转自：https://blog.csdn.net/test1280/article/details/80978717

Linux：strings 工具常用方法
strings - print the strings of printable characters in files.

strings prints the printable character sequences that are at least 4 characters long and are followed by an unprintable character.

strings is mainly useful for determining the contents of non-text files.

string 工具可以对任何文件的内容进行分析，并输出可打印字符长度不小于4的串。

这里“任何文件”，包括文本文件和二进制文件。

其实“文本文件”和“二进制文件“两者并没有啥差别，所有的文件本质都是 Binary，文本文件只不过是一些特殊的 Binary 。

这里“连续的可打印字符最小长度”是可以被选项设置的，并非固定不可修改的4。

常用选项：
```
-f Print the name of the file before each string.
-n Print sequences of characters that are at least min-len characters long, instead of the default 4.
```
Code：
```
/*
 * Filename     : main.c
 * Author       : jiang
 * Description  : test-file
 * Date         : 2018/07/09 22:03:57
 */
char MODULE_VER[] = "REDISDRIVER_VER_0.17_20180709";

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main()
{
    printf("1\n");
    printf("22\n");
    printf("333\n");
    printf("4444\n");
    printf("55555\n");
    printf("666666\n");

    printf("update something\n");

    printf("global unique identifier\n");

    return 0;
}
```


编译 && 运行：
```
[test1280@localhost ~]$ gcc -o main main.c
[test1280@localhost ~]$ ./main
1
22
333
4444
55555
666666
update something
global unique identifier
```

Case 1：
快速查看模块版本。

我们项目组的每个程序模块，都有一个模块子版本号来唯一标识。

例如上面代码中的：
```
char MODULE_VER[] = "REDISDRIVER_VER_0.17_20180709";
```

假如我在一个装有我们项目组研发的软件平台环境中看到一个模块，如何快速地确定这个模块的版本呢？

使用 strings 命令 + grep 命令 可以快速地将版本号过滤查找出来：
```
[test1280@localhost ~]$ strings main | grep "REDISDRIVER"
REDISDRIVER_VER_0.17_20180709
```
Case 2：
快速定位某源文件编译生成哪个 Binary 文件。

例如在 main.c 文件中有：
```
printf("global unique identifier\n");
```
我想要知晓，main.c 源文件最后会编译到哪个 Binary 文件中，如何查找呢？
```
[test1280@localhost ~]$ strings -f * | grep "global unique identifier"
main: global unique identifier
main.c:     printf("global unique identifier\n");
```
从上面的输出可以看到，main.c 源文件最后编译到 main Binary 文件中啦！

注：由于 * 包含了 main.c 源文件，自然输出了源文件 main.c 一项。优化时可以判断是否有 x 权限，等等。

Case 3：
快速确认 重新编译 是否成功。

当项目很庞大时，如果更新单个文件，然后重新 make 可能由于五花八门的各类原因导致编译、链接错误，使得更新过的代码并未正确反映到对应的可执行文件中。

例如：

更新前为
```
    printf("update something\n");
```
```
[test1280@localhost ~]$ strings main | grep "update something"
update something
```
更新后为
```
printf("update something @ 20180709 by jiang\n");
```
```
[test1280@localhost ~]$ strings main | grep "20180709 by jiang"
update something @ 20180709 by jiang
```
这样我们可以在不运行程序之前，确认代码是否更新成功。

注：

通过 -n 可以设置“连续的可打印字符最小长度”。

如果一个可打印字符串满足这个长度才会输出，否则将不输出。

举个栗子：

输出大于等于6的字符串
```
[test1280@localhost ~]$ strings -n 6 main 
/lib64/ld-linux-x86-64.so.2
__gmon_start__
libc.so.6
__libc_start_main
GLIBC_2.2.5
fffff.
666666
update something @ 20180709 by jiang
global unique identifier
REDISDRIVER_VER_0.17_20180709
```
可以观察到，”666666” 被输出出来，其余的可打印字符串由于长度“不达标”故并未输出。
