---
title: shell脚本执行
categories: shell   
toc: true  
tags: [shell]
---


# 1.shell脚本的执行

shell脚本的执行通常可以采用如下的几种方式:
* bash sript-name    或    sh  script-name(推荐使用)
* path/script-name    或    ./script-name    (当前路径下执行脚本)
* source script-name    或 .  script-name    # 注意 "." 点号
* sh<script-name   或 cat script-name|bash    (同样适用于sh)

# 2.sh(bash)和source(.)去执行脚本的区别

## 2.1. sh和bash 和 ./filename
```
sh   FileName    
bash FileName
#该filename文件可以无"执行权限"


./FileName      
#该filename文件需要"执行权限"

#作用:打开一个子shell来读取并执行FileName中命令。
#注：运行一个shell脚本时会启动另一个命令解释器.

```

## 2.2.source filename 和 . filename
```
source FileName    #source的程序主体是bash，脚本中的$0变量的值是bash，而且由于作用于当前bash环境，脚本中set的变量将直接起效

. FileName        #作用:在当前bash环境下读取并执行FileName中的命令。该filename文件可以无"执行权限"

```

## 2.3.父shell和子shell示意图
&emsp;其中a图是在当前bash下执行脚本；b图是在一个子shell中执行脚本，然后返回到父shell中；c图是在一个子shell中执行，然后在后台运行（&）。假定你有一个简单的shell脚本alice，它包含了命令hatter和gryphon。


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/18.png)
 


## 2.4.举例
```
#已知如下命令及返回结果,请问echo $user的返回结果为()

[root@lamp01 ~]# cat test.sh
user=`whoami`
[root@lamp01 ~]# sh test.sh
[root@lamp01 ~]# echo $user

/*
注释：因为使用的是sh去执行脚本，所以是启用一个子shell执行，然后返回结果到父shell，但是echo $user 是在父shell中执行的，所以结果打印为空
*/

```






