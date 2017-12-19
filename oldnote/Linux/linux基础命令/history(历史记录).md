---
title: Linux基础命令之history(历史记录)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 1.命令行的历史记录数量
```
[root@linux-study ~]# export HISTSIZE=5
[root@linux-study ~]# history
  568  tail -1 /etc/profile
  569  source /etc/profile
  570  echo $TMOUT
  571  export HISTSIZE=5
  572  history
[root@linux-study ~]# 

```


# 2.历史记录文件的命令数量
```
#在用户的家目录下存在一个.bash_history文件,用于存放历史记录

[root@linux-study ~]# export HISTFILESIZE=5
[root@linux-study ~]# cat ~/.bash_history 
date
history
echo $TMOUT
export TMOUT=120
echo $TMOUT
[root@linux-study ~]# 

```

# 3.清除所有的历史记录
```
[root@linux-study ~]# history -c
```

# 4.清除指定的历史记录
```
[root@linux-study ~]# history
    1  echo $TMOUT
    2  history
    3  ll
    4  fsdfs
    5  history
[root@linux-study ~]# history -d 3
[root@linux-study ~]# history
    1  echo $TMOUT
    2  history
    3  fsdfs
    4  history
    5  history -d 3
    6  history
[root@linux-study ~]#
 
```


# 5.把配置参数放入配置文件,使得配置永久生效
```
echo 'TMOUT=300'    >>/etc/profile
echo 'HISTSIZE=5'    >>/etc/profile
echo 'HISTFILESIZE=5'    >>/etc/profile

tail -3 /etc/profile
source /etc/profile

#其他
HISTCONTROL=ignorespace   
#以空格开头的命令就不会进入到历史记录了
```



# 叹号(历史命令)
|命令|描述|
|-|-|
|!+字母|表示调出最近一次以此字母开头的命令|
|!!|表示使用最近一次操作的命令|
|!+数字|表示调出历史的第几条命令|
