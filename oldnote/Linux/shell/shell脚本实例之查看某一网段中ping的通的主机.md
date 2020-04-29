---
title: shell脚本实例之查看某一网段中ping的通的主机
categories: shell   
toc: true  
tags: [shell]
---



```
#!/bin/bash
HOST="192.168.0.";  #指定默认的网段
main(){
        for host_ip in {0..254} ;do
                {
                ping -c 2 -w 2 ${HOST}"${host_ip}" &>/dev/null;  #-c ping的次数，-w 超时时间（秒）》将输出定位到null
                if [ $? -eq 0 ];then
                        echo "${HOST}${host_ip}">>host.txt;  #将可以ping通的ip放到一个文件中
                fi
                }&  #每一个for循环都在一个新的shell进程中执行，这样执行的速度将非常的快，并行执行的
        done
}
main;

```


