---
title: Linux基础命令之last(显示登录,退出信息)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
#显示登录，退出信息
[root@lamp01 chenyansong]# last
root     pts/0        192.168.0.221    Sun Feb 12 22:44   still logged in      #still还在线
root     pts/1        192.168.0.221    Sun Feb 12 14:04 - 22:28  (08:24)      # -表示已经退出
root     pts/0        192.168.0.221    Sun Feb 12 13:18 - 22:28  (09:10)   
root     pts/1        192.168.0.221    Sun Feb 12 09:14 - 13:18  (04:03) 


#显示所有用户的登录信息
[root@lamp01 chenyansong]# lastlog
用户名           端口     来自             最后登陆时间
root             pts/0    192.168.0.221    日 2月 12 22:44:16 +0800 2017
bin                                        **从未登录过**
daemon                                     **从未登录过**
adm                                        **从未登录过**
lp                                         **从未登录过**
sync                                       **从未登录过**
shutdown                                   **从未登录过**


```
