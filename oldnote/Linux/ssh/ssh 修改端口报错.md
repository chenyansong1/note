[TOC]

# Ssh 修改端口 error: Bind to port xxx on 0.0.0.0 failed: Permission denied.



Linux修改ssh Port端口后，无法启动ssh访问。

具体修改 ?/etc/ssh/sshd_config 里的 Port 端口，然后查日志后出现：

error: Bind to port 77 on 0.0.0.0 failed: Permission denied.

最后发现是SELinux的问题，[关闭SELinux的两种方法](https://www.jiloc.com/41566.html)

然后重启ssh访问即可。



**临时关闭SELINUX**



```
`[root@localhost ~]``# setenforce 0`
```

**永久关闭**,**可以修改配置文件/etc/selinux/config,将其中SELINUX设置为disabled**。



```
`[root@localhost ~]``# cat /etc/selinux/config   ``  ` `# This file controls the state of SELinux on the system.  ``# SELINUX= can take one of these three values:  ``#     enforcing - SELinux security policy is enforced.  ``#     permissive - SELinux prints warnings instead of enforcing.  ``#     disabled - No SELinux policy is loaded.  ``#SELINUX=enforcing  ``SELINUX=disabled  ``# SELINUXTYPE= can take one of three two values:  ``#     targeted - Targeted processes are protected,  ``#     minimum - Modification of targeted policy. Only selected processes are protected.   ``#     mls - Multi Level Security protection.  ``SELINUXTYPE=targeted` `[root@rdo ~]``# sestatus  ``SELinux status:                 disabled`
```









参见：https://github.com/zbinlin/blog/blob/master/change-sshd-port-in-centos7.md

