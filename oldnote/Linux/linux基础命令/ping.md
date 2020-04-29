---
title: Linux基础命令之ping
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 实例
```
[root@lamp01 chenyansong]# ping 192.168.0.3
PING 192.168.0.3 (192.168.0.3) 56(84) bytes of data.
64 bytes from 192.168.0.3: icmp_seq=1 ttl=64 time=0.254 ms
64 bytes from 192.168.0.3: icmp_seq=2 ttl=64 time=0.024 ms
.....

#指定参数
[root@lamp01 chenyansong]# ping -c 2 -w 2 192.168.0.3
PING 192.168.0.3 (192.168.0.3) 56(84) bytes of data.
64 bytes from 192.168.0.3: icmp_seq=1 ttl=64 time=0.021 ms
64 bytes from 192.168.0.3: icmp_seq=2 ttl=64 time=0.024 ms
 
--- 192.168.0.3 ping statistics ---
2 packets transmitted, 2 received, 0% packet loss, time 1000ms
rtt min/avg/max/mdev = 0.0

#-c ping的次数，-w 超时时间（秒）》将输出定位到null

```


# 禁止ping
```
#方式1
echo "net.ipv4.icmp_echo_ignore_all=1" >> /etc/sysctl.conf
sysctl -p
#上述方式不推荐使用,因为加上之后,我们自己也是ping不通的

#方式2
#通过防火墙的方式进行


```


# 查看某一个网段中可以ping通的主机shell脚本
```
#!/bin/bash
HOST="192.168.0.";  #指定默认的网段
main(){
        for host_ip in {0..254} ;do
                {
                #-c ping的次数，-w 超时时间（秒）》将输出定位到null
                ping -c 2 -w 2 ${HOST}"${host_ip}" &>/dev/null;  
                
                if [ $? -eq 0 ];then
                		#将可以ping通的ip放到一个文件中
                        echo "${HOST}${host_ip}">>host.txt;   
                fi
                }&  #每一个for循环都在一个新的shell进程中执行，这样执行的速度将非常的快，并行执行的
        done
}
main;

```


# ping不通的可能原因
```
有时候,ssh不能连接,ping的时候不通
开了360的流量防火墙服务，导致了这个恶果，直接把360退了，OK，互ping畅通，开始ssh传输

```
