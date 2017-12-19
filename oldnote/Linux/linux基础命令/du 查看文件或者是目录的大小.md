---
title: Linux基础命令之du 查看文件或者是目录的大小
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



``` shell
#du 查看文件或者是目录的大小
-s sum
-h human

[root@lamp01 application]# ll
总用量 4
lrwxrwxrwx  1 root root   25 8月   4 2016 nginx -> /application/nginx-1.6.3/
drwxr-xr-x 11 root root 4096 8月   1 2016 nginx-1.6.3

#查看目录大小
[root@lamp01 application]# du -s nginx-1.6.3/
44232   nginx-1.6.3/
[root@lamp01 application]# du -sh nginx-1.6.3/
44M     nginx-1.6.3/

#查看文件大小
[root@lamp01 application]# du -h /root/install.log
24K     /root/install.log

```




