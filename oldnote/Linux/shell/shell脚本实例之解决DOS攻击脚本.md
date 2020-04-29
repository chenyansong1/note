---
title: shell脚本实例之解决DOS攻击脚本
categories: shell   
toc: true  
tags: [shell]
---



> 写一个脚本解决DOS攻击产生案例

&emsp;提示:根据web日志或者网络连接数,监控当某个IP并发连接数或者短时间内PV达到100,即调用防火墙命令封掉对应的IP,监控频率每隔3分钟,防火墙命令为: iptables -A INPUT -s 10.0.1.10 -j DROP

```
[root@lamp01 ~]# cat dos.sh
#!/bin/sh
 
netstat -an|grep ESTABLISHED|awk -F "[ :]+" '{print $6}'|sort|uniq -c>b.log
 
exec<b.log
while read line;do
        pv=`echo $line|awk '{print $1}'`
        ip=`echo $line|awk '{print $2}'`
        if [ $pv -gt 4]&&[ `iptables -L -n|grep "$ip"|wc -l` -eq 0 ];then
                iptables -A INPUT -s $ip -j DROP
        fi
done
 
sleep 180

/*
说明:
1.netstat -an|grep ESTABLISHED 拿到所有的已经建立连接的IP
2.awk -F "[ :]+" '{print $6}' 取IP
3.sort|uniq -c>b.log 进行IP汇总统计,并输出到一个文件中
4.iptables -L -n：list 列出指定链上的所有的规则,n表示数字
5.grep "$ip" 取跟指定ip相关的规则,如果没有(-eq 0)就执行if
6.  iptables -A INPUT -s $ip -j DROP添加一条规则, -s, --source address 检查报文中的源ip地址是否符号位此处理指定的地址或范围
7.-j DROP # -j后面接处理动作,DROP:丢弃
*/

```
