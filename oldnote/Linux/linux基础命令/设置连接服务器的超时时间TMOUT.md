---
title: Linux基础命令之设置连接服务器的超时时间TMOUT
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 1.临时生效
```
[root@linux-study cys_test]# export TMOUT=120
[root@linux-study cys_test]# echo $TMOUT    
120
```

# 2.永久生效
```
[root@linux-study ~]# echo "export TMOUT=300" >>/etc/profile
[root@linux-study ~]# tail -1 /etc/profile
export TMOUT=300
[root@linux-study ~]# source /etc/profile
[root@linux-study ~]# echo $TMOUT
300
[root@linux-study ~]#
 

```

# 3.查看变量
```
echo $TMOUT
```



