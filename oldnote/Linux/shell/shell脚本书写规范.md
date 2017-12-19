---
title: shell脚本书写规范
categories: shell   
toc: true  
tags: [shell]
---



# 1.脚本第一行指定脚本解释器
```
#!/bin/sh   

#or   

#!/bin/bash


```

# 2.脚本开头加版本版权等信息
```
#Date: 16:20 2011-11-11
#Author: Created by chenyansong
#Mail:  xxx.qq.com
#Function: This script function is ....
#Version: 1.1

#提示：可以配置vim编辑文件时自动加上以上信息，方法是修改~/.vimrc配置文件


```

# 3.脚本中不要出现中文注释

防止本机或者系统切换环境后出现中文乱码的困扰


# 4.脚本以.sh为扩展名


# 5.代码书写优秀习惯技巧
```
#成对的符号内容尽量一次写出来，防止遗漏，如：{}、[]、“”等
if语句格式一次写完
if 条件内容
   then
     内容
fi

```

# 6.代码位置规范

在/server/script目录下，有：bin（脚本文件）、conf（配置文件）、func（函数文件）

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/15.png)




