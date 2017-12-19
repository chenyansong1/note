---
title: Linux基础命令之groups(查看用户所属组)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
#查看当前用户
[root@lamp01 chenyansong]# su - chenyansong
[chenyansong@lamp01 ~]$ groups
chenyansong student_team
[chenyansong@lamp01 ~]$
 
[root@lamp01 chenyansong]# id
uid=0(root) gid=0(root) 组=0(root)

#查看指定用户
[root@lamp01 chenyansong]# groups chenyansong
chenyansong : chenyansong student_team
[root@lamp01 chenyansong]#

[root@lamp01 chenyansong]# id chenyansong
uid=500(chenyansong) gid=500(chenyansong) 组=500(chenyansong),502(student_team)

 
```

