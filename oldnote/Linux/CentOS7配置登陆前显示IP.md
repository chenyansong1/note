[TOC]

转自：https://www.jianshu.com/p/babe4e5a27c5

# CentOS7 配置登陆前显示IP

通过修改`/etc/issue`

```bash
# cp /etc/issue /etc/issue_standard
```

创建脚本 /etc/init.d/show_ip.sh

```bash
#!/bin/bash

cp /etc/issue_standard /etc/issue
/usr/sbin/ip addr | grep "inet" |grep -v "inet6" | grep -v "127.0.0.1" | awk '{print $2}' >> /etc/issue
echo "" >> /etc/issue
```

配置脚本开机自启动。

```ruby
echo "/etc/init.d/show_ip.sh" >> /etc/rc.local
chmod a+x /etc/init.d/show_ip.sh
```

重启服务器，在登录前就可以看到IP了。

![img](E:\git-workspace\note\images\linux\command\show_ip2.png)



参考：
 https://type.so/linux/centos-issue-ip.html
 https://blog.csdn.net/wang123459/article/details/79063703

