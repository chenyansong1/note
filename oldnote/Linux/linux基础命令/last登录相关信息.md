[TOC]



# last



主要是显示用户从哪里登录的，什么时间登录，什么时间退出的，现在是否还在线，last命令显示的是 /var/log/wtmp文件中的内容



**显示用户的登录历史，及系统的重启历史**



```
[webuser@VM_0_4_centos ~]$ last
webuser  pts/2        58.62.52.37      Wed Sep  5 21:28   still logged in   
webuser  pts/1        58.62.52.37      Wed Sep  5 20:44   still logged in   
webuser  pts/1        113.67.157.176   Sun Sep  2 22:13 - 00:14  (02:00)    
root     pts/1        112.96.115.51    Fri Aug 31 14:45 - 14:45  (00:00)    
webuser  pts/0        14.18.249.35     Fri Aug 31 14:41 - 15:16  (00:35)    
root     pts/0        14.18.249.35     Fri Aug 31 14:38 - 14:40  (00:02)    
webuser  pts/0        14.18.249.35     Fri Aug 31 14:37 - 14:37  (00:00)  


# 只是显示最近几次的登录信息
last -n # : # 为数字
```



# lastb

显示登录的失败的记录

```
lastb -n
lastb -n # :显示最近登录的失败的记录
[webuser@VM_0_4_centos ~]$ lastb -n 4
root     ssh:notty    203.195.194.42   Wed Sep  5 21:31 - 21:31  (00:00)    
root     ssh:notty    203.195.194.42   Wed Sep  5 21:30 - 21:30  (00:00)    
sysop    ssh:notty    203.195.194.42   Wed Sep  5 21:30 - 21:30  (00:00)    
sysop    ssh:notty    203.195.194.42   Wed Sep  5 21:30 - 21:30  (00:00)    

btmp begins Sat Sep  1 03:19:59 2018
[webuser@VM_0_4_centos ~]$ 
```

# lastlog



显示每一个用户最近一次的成功登录信息



```
lastlog
lastlog -u username : 显示特定的用户的最近登录信息


[webuser@VM_0_4_centos ~]$ lastlog
用户名           端口     来自             最后登陆时间
root             pts/1    112.96.115.51    五 8月 31 14:45:20 +0800 2018
bin                                        **从未登录过**
postfix                                    **从未登录过**
chrony                                     **从未登录过**
sshd                                       **从未登录过**
tcpdump                                    **从未登录过**
syslog                                     **从未登录过**
centos                                     **从未登录过**
webuser          pts/3    58.62.52.37      三 9月  5 21:36:13 +0800 2018
mysql            pts/0                     日 5月 20 18:25:16 +0800 2018
nginx            pts/1                     一 5月 21 14:54:53 +0800 2018
redmine          pts/2    82.78.102.239    四 5月 31 16:54:13 +0800 2018
[webuser@VM_0_4_centos ~]$ 

```