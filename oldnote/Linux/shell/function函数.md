---
title: function函数
categories: shell   
toc: true  
tags: [shell]
---

[TOC]



# 1.语法
```
语法：

[ function ] funname [()]
{
    action;
    [return int;]

}


#简单语法格式:
函数名(){
    //指令...
    return n
}


#规范语法格式:
function 函数名(){
    //指令...
    return n
}


```


# 2.函数的调用

* 直接执行函数名即可(不带括号),函数定义及函数体必须在要执行的函数名的前面定义(即:先定义后使用)
* 带参数的函数执行方法
```
函数名    参数1    参数2 ..

/*
shell的位置参数( $1,$2,...${10}...) , $10 不能获取第十个参数，获取第十个参数需要${10}。当n>=10时，需要使用${n}来获取参数
$0   比较特殊,他仍然是父脚本的名称
$#    传递到脚本的参数个数
$*    以一个单字符串显示所有向脚本传递的参数
$!    后台运行的最后一个进程的ID号
$$    脚本运行的当前进程ID号
$@ 与$*相同，但是使用时加引号，并在引号中返回每个参数
$? 显示最后命令的退出状态。0表示没有错误，其他任何值表明有错误

在shell函数里面,return命令功能与shell里面的exit类似,但是shell函数体里使用exit会退出整个shell脚本,而不是shell脚本,return语句会返回一个退出值(返回值)给函数的调用者
*/
```

# 3.获取函数返回值

* 函数执行结果 \`mytest\`
* 函数执行的状态返回结果（return)

```
#!/bin/bash -  
function mytest()  
{  
    echo "arg1 = $1"  
    if [ $1 = "1" ] ;then  
        return 1  
    else  
        return 0  
    fi  
}  
 
mytest 1  
echo $?         # print return result 

#函数的执行结果，函数的执行状态结果

#函数的执行结果
echo `mytest` 

#函数的执行状态返回结果
mytest
echo $?

#return就是上面两句的合并
return 0

```

# 4.书写规范
&emsp;一般在一个shell脚本中将代码写在一个模块中，即写在函数中，这样可以根据函数的名称来确定该代码块在进行何种操作，然后就是在所有函数定义的下方调用函数，我们一般将这个总体的调用函数写在main函数中，这是在c中的写法，这里可以参考



# 注意

* 1.对于有空格的传参的解决方式

```shell
cmd="ls /tmp"
sshUtils $cmd

function sshUtils(){
	cmd=${@:1}
	echo "execute==== "$cmd
}

```












