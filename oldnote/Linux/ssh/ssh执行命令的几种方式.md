[TOC]



# 远程执行命令

如果我们要查看一下某台主机的磁盘使用情况，是不是必须要登录到目标主机上才能执行 df 命令呢？当然不是的，我们可以使用 ssh 命令在远程的主机上执行 df 命令，然后直接把结果显示出来。整个过程就像是在本地执行了一条命令一样：

```
$ ssh nick@xxx.xxx.xxx.xxx "df -h"
```

那么如何一次执行多条命令呢？其实也很简单，使用分号把不同的命令隔起来就 OK 了：

```
$ ssh nick@xxx.xxx.xxx.xxx "pwd; cat hello.txt"
```

第一条命令返回的结果： /home/nick
这说明用这种方式执行命令时的当前目录就是登陆用户的家目录。
第二条命令返回 hello.txt 文件的内容。
注意，当命令多于一个时最好用引号括起来，否则在有的系统中除了第一个命令，其它都是在本地执行的。

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511205818660-70198729.png)

# 执行需要交互的命令

有时候我们需要远程执行一些有交互操作的命令。

```
$ ssh nick@xxx.xxx.xxx.xxx "sudo ls /root"
$ ssh nick@xxx.xxx.xxx.xxx "top"
```

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511205826160-1097167612.png)

这两条命令虽然提示的失败原因不同，但它们有一个共同点：都需要与用户交互(需要 TTY)。所以它们失败的原因也是相同的：
默认情况下，当你执行不带命令的 ssh 连接时，会为你分配一个 TTY。因为此时你应该是想要运行一个 shell 会话。
但是当你通过 ssh 在远程主机上执行命令时，并不会为这个远程会话分配 TTY。此时 ssh 会立即退出远程主机，所以需要交互的命令也随之结束。
好在我们可以通过 -t 参数显式的告诉 ssh，我们需要一个 TTY 远程 shell 进行交互！
添加 -t 参数后，ssh 会保持登录状态，直到你退出需要交互的命令。

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511205847535-1524553729.png)

作为总结，我们看看 -t 参数的官方解释：
"Force pseudo-terminal allocation.  This can be used to execute arbitrary screen-based programs on a remote machine, which can be very useful, e.g. when implementing menu services.  Multiple -t options force tty allocation, even if ssh has no local tty."
好吧，更强悍的是我们居然可以指定多个 -t 参数！

# 执行多行的命令

有时候我们可能需要随手写几行简单的逻辑，这也没有问题，ssh 能轻松搞定！

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210001144-1862919415.png)

你可以用单引号或双引号开头，然后写上几行命令，最后再用相同的引号来结束。
那么如果需要在命令中使用引号该怎么办？
其实针对类似的情况有一条比较通用的规则，就是混合使用单双引号。这条规则在这里也是适用的：

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210052926-1476654551.png)

当我们在命令中引用了变量时会怎么样呢？

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210415176-1557758875.png)

请注意上图中的最后一行，并没有输出我们期望的 nick。这里多少有些诡异，因为如果变量没有被解释的话，输出的应该是 $name 才对。但是这里却什么都没有输出。
对于引用变量的写法，可以通过下面的方式保证变量被正确解释：

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210141910-342481715.png)

注意，我们在上图的命令中为 bash 指定了 -c 参数。

# 远程执行脚本

对于要完成一些复杂功能的场景，如果是仅仅能执行几个命令的话，简直是弱爆了。我们可能需要写长篇累牍的 shell 脚本去完成某项使命！此时 SSH 依然是不辱使命的好帮手(哈哈，前面的内容仅仅是开胃菜啊！)。

## 执行本地的脚本

我们在本地创建一个脚本文件 test.sh，内容为：

```
ls
pwd
```

然后运行下面的命令：

```
$ ssh nick@xxx.xxx.xxx.xxx < test.sh
```

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210630129-478305497.png)

通过重定向 stdin，本地的脚本 test.sh 在远程服务器上被执行。

接下来我们我期望能为脚本 test.sh 传递一个参数，为了验证传入的参数，在 test.sh 文件的末尾添加两行：

```
echo $0
echo $1
```

然后尝试执行下面的命令：

```
$ ssh nick@xxx.xxx.xxx.xxx < test.sh helloworld
$ ssh nick@xxx.xxx.xxx.xxx < "test.sh helloworld"
```

下图显示了执行的结果：

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210738472-145795907.png)

看来上面的方法都无法为脚本传递参数。
要想在这种情况下(远程执行本地的脚本)执行带有参数的脚本，需要为 bash 指定 -s 参数：

```
$ ssh nick@xxx.xxx.xxx.xxx 'bash -s' < test.sh helloworld
```

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210814894-1833396404.png)

在上图的最后两行，输出的是 "bash" 和 "helloworld" 分别对应 $0 和 $1。

## 执行远程服务器上的脚本

除了执行本地的脚本，还有一种情况是脚本文件存放在远程服务器上，而我们需要远程的执行它！
此时在远程服务器上用户 nick 的家目录中有一个脚本 test.sh。文件的内容如下：

```
ls
pwd
```

执行下面的命令：

```
$ ssh nick@xxx.xxx.xxx.xxx "/home/nick/test.sh"
```

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511210937316-1233468950.png)

注意，此时需要指定脚本的绝对路径！

下面我们也尝试为脚本传递参数。在远程主机上的 test.sh 文件的末尾添加两行：

```
echo $0
echo $1
```

然后尝试执行下面的命令：

```
$ ssh nick@xxx.xxx.xxx.xxx /home/nick/test.sh helloworld
```

![img](https://images2015.cnblogs.com/blog/952033/201705/952033-20170511211358113-1784769384.png)

真棒，最后两行 "/home/nick/test.sh" 和 "helloworld" 分别对应 $0 和 $1。

# 总结

本文通过 demo 演示了 ssh 远程操作的基本方式。这些基本用法将为我们在更复杂的场景中完成各种艰巨的任务打下基础。