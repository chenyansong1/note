---
title: shell脚本实例之开机自启动优化使用shell脚本实现
categories: shell   
toc: true  
tags: [shell]
---




```
for serviceName in `chkconfig --list|grep "3:on"|awk '{print $1}'|grep -vE "crond|network|sshd|rsyslog"`;do
        chkconfig $serviceName off
done

```

