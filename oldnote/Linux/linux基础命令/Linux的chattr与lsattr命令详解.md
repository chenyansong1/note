**PS：有时候你发现用root权限都不能修改某个文件，大部分原因是曾经用chattr命令锁定该文件了。chattr命令的作用很大，其中一些功能是由Linux内核版本来支持的，不过现在生产绝大部分跑的linux系统都是2.6以上内核了。通过chattr命令修改属性能够提高系统的安全性，但是它并不适合所有的目录。chattr命令不能保护/、/dev、/tmp、/var目录。lsattr命令是显示chattr命令设置的文件属性。**

这两个命令是用来查看和改变文件、目录属性的，与chmod这个命令相比，chmod只是改变文件的读写、执行权限，更底层的属性控制是由chattr来改变的。

chattr命令的用法：chattr [ -RVf ] [ -v version ] [ mode ] files…
最关键的是在[mode]部分，[mode]部分是由+-=和[ASacDdIijsTtu]这些字符组合的，这部分是用来控制文件的
属性。
```
+ ：在原有参数设定基础上，追加参数。
+ - ：在原有参数设定基础上，移除参数。
= ：更新为指定参数设定。
A：文件或目录的 atime (access time)不可被修改(modified), 可以有效预防例如手提电脑磁盘I/O错误的发生。
S：硬盘I/O同步选项，功能类似sync。
a：即append，设定该参数后，只能向文件中添加数据，而不能删除，多用于服务器日志文件安全，只有root才能设定这个属性。
c：即compresse，设定文件是否经压缩后再存储。读取时需要经过自动解压操作。
d：即no dump，设定文件不能成为dump程序的备份目标。
i：设定文件不能被删除、改名、设定链接关系，同时不能写入或新增内容。i参数对于文件 系统的安全设置有很大帮助。
j：即journal，设定此参数使得当通过mount参数：data=ordered 或者 data=writeback 挂 载的文件系统，文件在写入时会先被记录(在journal中)。如果filesystem被设定参数为 data=journal，则该参数自动失效。s：保密性地删除文件或目录，即硬盘空间被全部收回。
u：与s相反，当设定为u时，数据内容其实还存在磁盘中，可以用于undeletion。各参数选项中常用到的是a和i。a选项强制只可添加不可删除，多用于日志系统的安全设定。而i是更为严格的安全设定，只有superuser (root) 或具有CAP_LINUX_IMMUTABLE处理能力（标识）的进程能够施加该选项。`


```

**应用举例：**
```

1、用chattr命令防止系统中某个关键文件被修改：
# chattr +i /etc/resolv.conf

然后用mv /etc/resolv.conf等命令操作于该文件，都是得到Operation not permitted 的结果。vim编辑该文件时会提示W10: Warning: Changing a readonly file错误。
要想修改此文件就要把i属性去掉： chattr -i /etc/resolv.conf

# lsattr /etc/resolv.conf
会显示如下属性
----i-------- /etc/resolv.conf

2、让某个文件只能往里面追加数据，但不能删除，适用于各种日志文件：
# chattr +a /var/log/messages

```



```
补充几个参数的

参数	作用
i	将无法对文件进行修改,若对目录设置后则仅能修改子文件而不能新建或删除。
a	仅允许补充（追加）内容.无法覆盖/删除(Append Only)。
S	文件内容变更后立即同步到硬盘(sync)。
s	彻底从硬盘中删除，不可恢复(用0填充原文件所在硬盘区域)。
A	不再修改这个文件的最后访问时间(atime)。
b	不再修改文件或目录的存取时间。
D	检查压缩文件中的错误。
d	当使用dump命令备份时忽略本文件/目录。
c	默认将文件或目录进行压缩。
u	当删除此文件后依然保留其在硬盘中的数据，方便日后恢复。
t	让文件系统支持尾部合并（tail-merging）。
X	可以直接访问压缩文件的内容。
```

