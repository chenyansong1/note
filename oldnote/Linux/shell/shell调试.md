---
title: shell调试
categories: shell   
toc: true  
tags: [shell]
---



# 1.vim直接跳到某一行
```
vim for_.sh  +4
```

# 2.sh -x 调试脚本

该方法是调整整个脚本


# 3.set -x
&emsp;在set -x 和set +x 调试部分脚本（在脚本中设置）
&emsp;如果在脚本文件中加入了命令set –x ，那么在set命令之后执行的每一条命令以及加载命令行中的任何参数都会显示出来，每一行都会加上加号（+），提示它是跟踪输出的标识，在子shell中执行的shell跟踪命令会加2个叫号（++）。

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/16.png) ![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/17.png)


# 4.在脚本中打印变量输出

echo 变量

# 5.使用exit在特定的位置退出,只执行部分脚本



