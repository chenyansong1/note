gcc调试中使用静态连接库和动态链接库以及-I -l L含义

潇雨码农 2017-01-12 22:08:11  5625  收藏
分类专栏： linux c
版权
1）静态链接库与动态链接库都是共享代码的方式，

区别：
       如果采用静态链接库，则无论你愿不愿意，lib 中的指令都全部被直接包含在最终生成的 EXE 文件中了。静态库的代码在编译过程中已经被载入可执行程序，因此体积较大。但是若使用 DLL，该 DLL 不必被包含在最终 EXE 文件中，EXE 文件执行时可以“动态”地引用和卸载这个与 EXE 独立的 DLL 文件。共享库的代码是在可执行程序运行时才载入内存的，在编译过程中仅简单的引用，因此代码体积较小。
       静态链接库和动态链接库的另外一个区别在于静态链接库中不能再包含其他的动态链接库或者静态库，而在动态链接库中还可以再包含其他的动态或静态链接库。

注意：在window下的动态链接：后缀为dll；而linux下多为.so





2）在gcc中添加静态/动态链接库，并且编译



I(大写的i) 是 Include 头文件路径
L 是 link 目录
l （小写的L）是链接文件 ，在L指定的位置找



libtest/include/hello.h

#ifdef _HELLO_H_

#define _HELLO_H_

void hello();

#endif



libtest/lib/hello.c

#include"hello.h"

#include <stdio.h>

void hello()

{

         printf("hello world!\n");

}

libtest/src/main.c

#include"hello.h"

int main()

{

         hello();

}

 

静态库过程如下：

（1）      进入libtest/lib目录，执行命令：

      gcc -c -I../include hello.c

      该命令生成目标文件hello.o，注意：参数-I（大写的i）添加头文件搜索目录，这里因为hello.c中有#include “hello.h”，hello.h在libtest/include目录中，这里需要指定该目录通知gcc，否则出现错误提示“找不到头文件hello.h”。

      这一步将在libtest/lib目录中生成一个hello.o文件。

（2）      在libtest/lib目录，执行命令：

      ar rc libhello.a hello.o

      该命令将hello.o添加到静态库文件libhello.a，ar命令就是用来创建、修改库的，也可以从库中提出单个模块，参数r表示在库中插入或者替换模块，c表示创建一个库，关于ar命令的详细使用规则可以参考文章

这一步将在libtest/lib目录中生成一个libhello.a文件。

（3）      进入libtest/src目录，执行命令：

     gcc main.c -I（大写的i）../include -L../lib- l(小写的L)hello  -o  main

或者

     gcc main.c -I../include../lib/libhello.a - o main

      该命令将编译main.c并链接静态库文件libhello.a生成可执行文件main，注意：参数-L添加库文件搜索目录，因为libhello.a在libtest/lib目录中，这里需要指定该目录通知gcc，参数-l指定链接的库文件名称，名称不用写全名libhello.a，只用写hello即可。（多写也对）

      这一步将在libtest/src目录中生成可执行文件main。

-I(大写的i) ：指定头文件的搜索路径

-l（小写的L）库名

-L库搜索目录

 

动态链接库过程如下：

1)

gcc -c -fpic -I../  hello.c

gcc -shared -o libhello.so hello.o

gcc -o src src.c -L./unix –lhello

GNU连接器会查找标准系统函数目录:它先后搜索1.elf文件的 DT_RPATH段—2.环境变量LD_LIBRARY_PATH—3./etc/ld.so.cache文件列表—4./lib/,/usr/lib目录找到库文件后将其载入内存,但是我们生成的共享库在当前文件夹下，并没有加到上述的4个路径的任何一个中，因此，执行后会出现错误）

# ./src

 ./src:error while loading shared libraries: libmyhello.so: cannot open shared objectfile: No such file or directory

（1）我们将文件 libmyhello.so复制到目录/usr/lib中，再试试。

# mv libmyhello.so /usr/lib

# ./src

（2）既然连接器会搜寻LD_LIBRARY_PATH所指定的目录，那么我们可以将这个环境变量设置成当前目录：

先执行：

export LD_LIBRARY_PATH=$(pwd)

再执行：

./src

3)执行： 

ldconfig  /usr/zhsoft/lib      

  注:   当用户在某个目录下面创建或拷贝了一个动态链接库,若想使其被系统共享,可以执行一下"ldconfig   目录名"这个命令.此命令的功能在于让ldconfig将指定目录下的动态链接库被系统共享起来,意即:在缓存文件/etc/ld.so.cache中追加进指定目录下的共享库.本例让系统共享了/usr/zhsoft/lib目录下的动态链接库.该命令会重建/etc/ld.so.cache文件

 

3）在其他目录下运行apue源码带的库



gcc -g -I~/apue.2e/include -L~/apue.2e/lib/  -L~/apue.2e/db/ cs.clibapue_db.a ~/apue.2e/lib/libapue.a -o cs

1)-I~/apue.2e/include紧挨着，写入apue.h的搜索目录。这里可以省略，因为把它放在/usr/include;

静态库在编译为静态库时，需要给定头文件的位置。而在使用编译好的静态库时不再需要制定头文件的位置。这里不用再指定apue_db.e的位置。

2)-L~/apue.2e/lib/-L~/apue.2e/db/ cs.c libapue_db.a ~/apue.2e/lib/libapue.a

-L库搜索目录； 引入2个库 按顺序添加

注意libapue.a不可不写。要不然出现错误:


或者

gcc -g -D_DEBUG_ -L~/apue.2e/lib/ -L~/apue.2e/db/ cs.c libapue_db.a -o cs –lapue

 

使用动态链接库

gcc -g -I~/apue.2e/include-L~/apue.2e/db/ cs.c ~/apue.2e/db/libapue_db.so.1 -o cs -lapue

 

总结：

最主要的是GCC命令行的一个选项:

-shared 该选项指定生成动态连接库（让连接器生成T类型的导出符号表，有时候也生成弱连接W类型的导出符号），不用该标志外部程序无法连接。相当于一个可执行文件

 -fPIC：表示编译为位置独立的代码，不用此选项的话编译后的代码是位置相关的所以动态载入时是通过代码拷贝的方式来满足不同进程的需要，而不能达到真正代码段共享的目的。

不加会出错：

 relocation R_X86_64_32 against `……..'can not be used when making a shared object; recompile with -fPIC

 -L.：表示要连接的库在当前目录中

-ltest：编译器查找动态连接库时有隐含的命名规则，即在给出的名字前面加上lib，后面加上.so来确定库的名称，例如–lapue 表示libapue.a

 LD_LIBRARY_PATH：动态库的查找路径。

移动到动态库的目录下执行：

export LD_LIBRARY_PATH=.:$LD_LIBRARY_PATH

当然如果有root权限的话，可以修改

/etc/ld.so.conf文件，然后调用/sbin/ldconfig来达到同样的目的，不过如果没有root权限，那么只能采用输出LD_LIBRARY_PATH的方法了。

ldconfig

调用动态库的时候有几个问题会经常碰到，有时，明明已经将库的头文件所在目录通过 “-I” include进来了，库所在文件通过 “-L”参数引导，并指定了“-l”的库名，但通过ldd命令察看时，就是死活找不到你指定链接的so文件，这时你要作的就是通过修改 LD_LIBRARY_PATH或者/etc/ld.so.conf文件来指定动态库的目录。通常这样做就可以解决库无法链接的问题了

 

4）静态库链接时搜索路径顺序：

1. ld会去找GCC命令中的参数-L

2.再找gcc的环境变量LIBRARY_PATH

3.再找内定目录 /lib /usr/lib /usr/local/lib这是当初compile gcc时写在程序内的

动态链接时、执行时搜索路径顺序:

1. 去找GCC命令中的参数-L

2. 环境变量LD_LIBRARY_PATH指定的动态库搜索路径；

3. 配置文件/etc/ld.so.conf中指定的动态库搜索路径；

4.默认的动态库搜索路径/lib；

5.默认的动态库搜索路径/usr/lib。

有关环境变量：

LIBRARY_PATH环境变量：指定程序静态链接库文件搜索路径

LD_LIBRARY_PATH环境变量：指定程序动态链接库文件搜索路径

注意：

当静态库和动态库同名时， gcc命令将优先使用动态库。

 

原文链接：https://blog.csdn.net/qq_21034239/article/details/54382311
