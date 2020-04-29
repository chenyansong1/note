---
title: Linux基础命令之w(显示登录者,以及他们在干什么)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



&emsp;Show who is logged on and what they are doing.
```
[root@lamp01 chenyansong]w
 17:15:06 up 23:17,  2 users,  load average: 0.00, 0.00, 0.00
USER     TTY      FROM              LOGIN@   IDLE   JCPU   PCPU WHAT
root     pts/1    192.168.0.221    17:15    4.00s  0.02s  0.02s -bash    #在执行bash命令
root     pts/0    192.168.0.221    Sun22    0.00s  5.00s  0.00s w    

```


