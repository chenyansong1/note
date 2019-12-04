[TOC]

转自：https://www.jianshu.com/p/babe4e5a27c5

# CentOS7 配置登陆前显示IP

通过修改`/etc/issue`

```bash
# cp /etc/issue /etc/issue_standard
```

创建脚本 /etc/init.d/show_ip.sh

```bash
[root@SSA home]# cat  /etc/init.d/show_ip.sh
#!/bin/bash

cp /etc/issue_standard /etc/issue
echo -e "this host ip:\c">>/etc/issue
/usr/sbin/ip addr | grep "inet" |grep -v "inet6" | grep -v "virbr" | grep -v "127.0.0.1" | awk '{print $2}' >> /etc/issue
echo "" >> /etc/issue

echo "web: https://192.168.10.24" >> /etc/issue
```

配置脚本开机自启动。

```ruby
echo "/etc/init.d/show_ip.sh" >> /etc/rc.local
chmod a+x /etc/init.d/show_ip.sh
```

重启服务器，在登录前就可以看到IP了。

![img](E:\git-workspace\note\images\linux\command\show_ip2.png)

注意：rc.local可能没有执行

```shell
[hadoop@SSA ~]$ ll /etc/rc.local       
lrwxrwxrwx 1 root root 13 Nov  1 11:47 /etc/rc.local -> rc.d/rc.local

[hadoop@SSA ~]$ ll /etc/rc.d/rc.local
-rwxr-xr-x 1 root root 597 Nov 26 14:44 /etc/rc.d/rc.local

#1.查看/etc/rc.local是否有执行权限，没有就加上
chmod +x /etc/rc.local
#记住，必须运行“chmod +x/etc/rc.d/rc.local”命令来确保启动过程中执行此脚本 .
```



参考：
 https://type.so/linux/centos-issue-ip.html
 https://blog.csdn.net/wang123459/article/details/79063703

