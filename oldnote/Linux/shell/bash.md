[TOC]



# bash变量类型



变量的设置规则

* 变量与变量内容以一个等号“=”来链接，如下所示： “myname=VBird”
* 等号两边不能直接接空白字符，如下所示为错误： “myname = VBird”或“myname=VBird Tsai”
* 变量名称只能是英文字母与数字，但是开头字符不能是数字，如下为错误：“2myname=VBird”
* 变量内容若有空白字符可使用双引号“"”或单引号“'”将变量内容结合起来，但 
  * 双引号内的特殊字符如 $ 等，可以保有原本的特性，如下所示： “var="lang is $LANG"”则“echo $var”可得“lang is zh_TW.UTF-8”
  *  单引号内的特殊字符则仅为一般字符 （纯文本） ，如下所示： “var='lang is $LANG'”则“echo $var”可得“lang is $LANG”    
* 可用跳脱字符“ \ ”将特殊符号（如 [Enter], $, \, 空白字符, '等） 变成一般字符，如： “myname=VBird\ Tsai”
* 在一串指令的执行中，还需要借由其他额外的指令所提供的信息时，可以使用反单引 号“ 指令 ”或 “$（指令） ”。特别注意，那个 ` 是键盘上方的数字键 1 左边那个按键，而不 是单引号！ 例如想要取得核心版本的设置： “version=$（uname -r） ”再“echo $version”可得“3.10.0-229.el7.x86_64”    
* 若该变量为扩增变量内容时，则可用 "$变量名称" 或 ${变量} 累加内容，如下所示： “PATH="$PATH":/home/bin”或“PATH=${PATH}:/home/bin”    
* 若该变量需要在其他子程序执行，则需要以 export 来使变量变成环境变量： “export PATH”    
* 取消变量的方法为使用 unset ：“unset 变量名称”例如取消 myname 的设置： “unset myname”    







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



# 引用变量

${varchar}  大多数情况下大括号是可以省略的

```Shell
#大括号不能省略
animal=pig
echo "there are some ${animal}s"

# 单引号强引用，不会替换其中的变量
echo 'there are some ${animal}s'
```



# 脚本第一行



脚本为了让系统能够识别，需要在第一行指定一个魔数（magic number)

```Shell
#!/bin/bash

/*
#！是指定魔数
/bin/bash 是指定那种解释器去执行脚本
*/
```



# 执行脚本

当我们写好脚本之后，需要给脚本一个执行的权限(+x)， 其实我们写的脚本是和系统中自带的命令脚本是一样的，如系统的ls就是一个脚本的，因此我们可以执行我们写的脚本，如 first.sh ,我们发现执行不成功，因为和ls一样，系统执行命令的时候去环境变量中寻找脚本的路径，而在环境变量中，我们是没有给first.sh指定路径的，所以我们需要指定脚本的执行路径，如 ./first.sh 

```Shell
执行脚本的两种方式

#1
bash first.sh

#2
+x first.sh
./first.sh

```

