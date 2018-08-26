[TOC]



# bash变量类型



## 查看变量



```Shell
# 查看所有变量 set 
chenyansongdeMacBook-Pro:shell chenyansong$ set
Apple_PubSub_Socket_Render=/private/tmp/com.apple.launchd.T800LJfjoZ/Render
BASH=/bin/bash
BASH_ARGC=()
BASH_ARGV=()
BASH_LINENO=()
BASH_REMATCH=([0]="l")
BASH_SOURCE=()
BASH_VERSINFO=([0]="3" [1]="2" [2]="57" [3]="1" [4]="release" [5]="x86_64-apple-darwin16")
BASH_VERSION='3.2.57(1)-release'
COLUMNS=76
DIRSTACK=()
EUID=501
GRADLE=/Users/chenyansong/installed_soft/gradle-4.4.1/bin


# 查看环境变量
printenv
env
export

```





## 环境变量



作用域为：当前shell进程 及其 子进程有效

```Shell
# 定义环境变量
export varname=value

# 先定义，再导出
varname=value1
export varname

```



> 脚本在执行时会启动一个子shell进程
>
> ​	命令行中启动的脚本会继承当前shell环境变量
>
> ​	系统自动执行的脚本（非命令行启动）就需要自我定义需要的各种环境变量





## 本地变量

```Shell
[set] varname=value // 整个bash进程

# 局部变量(作用域为当前代码段有效)
local varname=value

# 撤销变量
unset varname

```





## 位置变量



用来引用脚本的参数

```Shell
$1, $2, ....
```



## 特殊变量(系统变量)



```Java
$?: 保存上一个命令的执行状态返回值

/**
程序执行完成之后，有程序状态返回代码：
0： 执行成功
1-255：错误执行(各种可能的不同错误)
*/

```



引用变量：${varchar}  大多数情况下大括号是可以省略的

```Shell
#大括号不能省略
animal=pig
echo "there are some ${animal}s"

# 单引号强引用，不会替换其中的变量
echo 'there are some ${animal}s'
```



脚本为了让系统能够识别，需要在第一行指定一个魔数（magic number)

```Shell
#!/bin/bash

/*
#！是指定魔数
/bin/bash 是指定那种解释器去执行脚本
*/
```

