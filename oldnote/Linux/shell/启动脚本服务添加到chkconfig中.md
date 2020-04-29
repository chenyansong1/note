---
title: 启动脚本服务添加到chkconfig中
categories: shell   
toc: true  
tags: [shell]
---



# 1.脚本添加复制到/etc/init.d/目录下
```
-rwxr-xr-x  1 root root  1413 8月  19 21:56 nginxd

#注意：sh脚本要有 +x 的权限：chmod +x nginxd

```

# 2.在脚本下加入
```
#!/bin/sh
# chkconfig: 2345 54 65
# description: stop/start nginx scripts
//上面的数字不能重复:2345 是在2345级别下启动，54 ，65 是启动顺序和关闭顺序
```

# 3.添加到chkconfig
```
[root@lnmp02 init.d]# chkconfig --add nginxd
[root@lnmp02 init.d]# chkconfig nginxd on
[root@lnmp02 init.d]# chkconfig --list nginxd
nginxd          0:关闭  1:关闭  2:启用  3:启用  4:启用  5:启用  6:关闭
[root@lnmp02 init.d]#
 

```
