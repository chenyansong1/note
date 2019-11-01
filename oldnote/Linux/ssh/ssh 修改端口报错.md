[TOC]

# Ssh 修改端口 error: Bind to port xxx on 0.0.0.0 failed: Permission denied.



Linux修改ssh Port端口后，无法启动ssh访问。

具体修改 ?/etc/ssh/sshd_config 里的 Port 端口，然后查日志后出现：

error: Bind to port 77 on 0.0.0.0 failed: Permission denied.

最后发现是SELinux的问题，[关闭SELinux的两种方法](https://www.jiloc.com/41566.html)

然后重启ssh访问即可。



