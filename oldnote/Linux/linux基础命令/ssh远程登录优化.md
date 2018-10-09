---
title: Linux基础命令之ssh远程登录优化
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

[TOC]

Linux：openSSH

​	服务器端：sshd，配置文件/etc/ssh/ssd_config

​	客户端：ssh,配置文件 /etc/ssh/ss_config

​	客户端提供的工具：

​		ssh-keygen：key generation,秘钥生成器

​		ssh-copy-id：将公钥传输到远程服务器

​		scp：远程copy



ssh-keygen -t rsa 生成公钥，私钥，如下

​	~/.ssh/id_rsa

​	~/.ssh/id_rsa.pub

公钥复制到远程主机某用户的家目录下的.ssh/authorized_keys（追加）文件或.ssh/authorized_keys2（追加）文件

![image-20181006112615938](/Users/chenyansong/Documents/note/images/linux/ssh/ssh-keygen.png)



一次性执行

```
ssh-keygen -t rsa -f .ssh/id-rsa-xxx -P ''

#-t 指定算法
#-f 指定文件路径
#-P 指定密码

ssh-keygen -t rsa -f .ssh/id-rsa-xxx -N ''
```





复制公钥到指定的主机的指定用户名下

```
ssh-copy-id -i ~/.ssh/id_rsa.pub root@ip

#-i [identity_file]	指定公钥路径

```



/etc/ssh/sshd_config文件中是配置ssh登陆的

下面是优化的步骤:

# 1.优化的配置
## 1.1.修改默认的端口(22)
```
#Port 22    #改成你自己指定的端口
```

## 1.2修改使用DNS 为no
&emsp;不使用DNS的原因是，我们登陆的时候就是使用的是ip，不需要去解析了，所以DNS解析就用不到了，不使用DNS我们的访问将更快
```
#UseDNS yes
UseDNS no
```


## 1.3.修改登录时监听的IP
&emsp;我们将登陆的IP换成我们指定的内网IP，那样外网的ip地址就不能直接访问了，避免了不必要的攻击，那么外面的人如何进行登录呢，我们可以使用VPN，登录了VPN之后，就相当于用户变成了内网用户，我们可以使用指定的IP去登录了
```
#ListenAddress 0.0.0.0 #所有地址
ListenAddress 192.168.36.129
```

## 1.4.不允许root登录
&emsp;我们不允许root进行登录，如果要使用root的功能，可以使用其他用户使用su去切换登录，但是不允许直接连接登录
```
#PermitRootLogin yes
PermitRootLogin no
```

## 1.5.GSSAPIAuthentication 解决SSH远程连接服务慢的问题
```
#GSSAPIAuthentication no    #如果默认是no,那么默认就行了
```

# 2.修改上面提到的配置文件
&emsp;手动修改文件（一个一个的该，最后将他们放在一起，并且注释，标明时间）
```
Port 52113
PermitRootLogin no
PermitEmptyPasswords no
UseDNS no
GSSAPIAuthentication no 
```

&emsp;通过sed的方式修改文件

&emsp;方式一：
``` shell
 
#增加：
[root@oldboy ssh]# sed -i '13 iPort 52113\nPermitRootLogin no\nPermitEmptyPasswords no\nUseDNS no\nGSSAPIAuthentication no'  sshd_config

#查看：
 
[root@oldboy ssh]# sed -n '13,17p' sshd_config
Port 52113
PermitRootLogin no
PermitEmptyPasswords no
UseDNS no
GSSAPIAuthentication no

```


&emsp;方式二（推荐）：他有个好的习惯就是在修改文件之前，将文件备份（并加上时间）
``` shell 
#下面是一个执行脚本
echo "#--------sshConfig修改ssh默认登录端口,禁止root登录----------------------------#"
\cp /etc/ssh/sshd_config /etc/ssh/sshd_config.$(date +"%F"-$RANDOM)
sed -i 's%#Port 22%Port 52113%' /etc/ssh/sshd_config
sed -i 's%#PermitRootLogin yes%PermitRootLogin no%' /etc/ssh/sshd_config
sed -i 's%#PermitEmptyPasswords no%PermitEmptyPasswords no%' /etc/ssh/sshd_config
sed -i 's%#UseDNS yes%UseDNS no%' /etc/ssh/sshd_config
sed -i 's%GSSAPIAuthentication yes%GSSAPIAuthentication no%' /etc/ssh/sshd_config
egrep "UseDNS|52113|RootLogin|EmptyPass|GSSAPIAuthentication" /etc/ssh/sshd_config

```



# 3.重新加载配置到内存中
&emsp;因为刚刚修改的数据只是在硬盘中，需要重新加载到内存中
```
[root@lamp01 chenyansong]# /etc/init.d/sshd restart
停止 sshd：                                                [确定]
正在启动 sshd：                                            [确定]

#或者

[root@lamp01 chenyansong]# /etc/init.d/sshd reload
重新载入 sshd：                                            [确定]
[root@lamp01 chenyansong]# 
```



# ssh配置文件说明



```
[root@test ~]# cat /etc/ssh/sshd_config
#       $OpenBSD: sshd_config,v 1.73 2005/12/06 22:38:28 reyk Exp $

# This is the sshd server system-wide configuration file.  See
# sshd_config(5) for more information.

# This sshd was compiled with PATH=/usr/local/bin:/bin:/usr/bin

# The strategy used for options in the default sshd_config shipped with
# OpenSSH is to specify options with their default value where
# possible, but leave them commented.  Uncommented options change a
# default value.

 

#############1. 关于 SSH Server 的整体设定##############
#Port 22    
##port用来设置sshd监听的端口，为了安全起见，建议更改默认的22端口为5位以上陌生端口
#Protocol 2,1
Protocol 2
##设置协议版本为SSH1或SSH2，SSH1存在漏洞与缺陷，选择SSH2
#AddressFamily any
#ListenAddress 0.0.0.0 #指明监控的地址，0.0.0.0表示本机的所有地址  【默认可修改】
#ListenAddress用来设置sshd服务器绑定的IP地址
##监听的主机适配卡，举个例子来说，如果您有两个 IP， 分别是 192.168.0.11 及 192.168.2.20 ，那么只想要
###开放 192.168.0.11 时，就可以设置为：ListenAddress 192.168.0.11
####表示只监听来自 192.168.0.11 这个 IP 的SSH联机。如果不使用设定的话，则预设所有接口均接受 SSH

#############2. 说明主机的 Private Key 放置的档案##########　　　　　　　　　　　　　　　　　
#ListenAddress ::
##HostKey用来设置服务器秘钥文件的路径
# HostKey for protocol version 1
#HostKey /etc/ssh/ssh_host_key
##设置SSH version 1 使用的私钥

# HostKeys for protocol version 2
#HostKey /etc/ssh/ssh_host_rsa_key
##设置SSH version 2 使用的 RSA 私钥

#HostKey /etc/ssh/ssh_host_dsa_key
##设置SSH version 2 使用的 DSA 私钥


#Compression yes　　　　　　
##设置是否可以使用压缩指令

# Lifetime and size of ephemeral version 1 server key
#KeyRegenerationInterval 1h
##KeyRegenerationInterval用来设置多长时间后系统自动重新生成服务器的秘钥，
###（如果使用密钥）。重新生成秘钥是为了防止利用盗用的密钥解密被截获的信息。

#ServerKeyBits 768
##ServerKeyBits用来定义服务器密钥的长度
###指定临时服务器密钥的长度。仅用于SSH-1。默认值是 768(位)。最小值是 512 。


# Logging
# obsoletes QuietMode and FascistLogging
#SyslogFacility AUTH
SyslogFacility AUTHPRIV
##SyslogFacility用来设定在记录来自sshd的消息的时候，是否给出“facility code”

#LogLevel INFO
##LogLevel用来设定sshd日志消息的级别


#################3.安全认证方面的设定################
#############3.1、有关安全登录的设定###############
# Authentication:
##限制用户必须在指定的时限内认证成功，0 表示无限制。默认值是 120 秒。

#LoginGraceTime 2m
##LoginGraceTime用来设定如果用户登录失败，在切断连接前服务器需要等待的时间，单位为妙

#PermitRootLogin yes
##PermitRootLogin用来设置能不能直接以超级用户ssh登录，root远程登录Linux很危险，建议注销或设置为no

#StrictModes yes
##StrictModes用来设置ssh在接收登录请求之前是否检查用户根目录和rhosts文件的权限和所有权，建议开启
###建议使用默认值"yes"来预防可能出现的低级错误。

#RSAAuthentication yes
##RSAAuthentication用来设置是否开启RSA密钥验证，只针对SSH1

#PubkeyAuthentication yes
##PubkeyAuthentication用来设置是否开启公钥验证，如果使用公钥验证的方式登录时，则设置为yes

#AuthorizedKeysFile     .ssh/authorized_keys
##AuthorizedKeysFile用来设置公钥验证文件的路径，与PubkeyAuthentication配合使用,默认值是".ssh/authorized_keys"。
###该指令中可以使用下列根据连接时的实际情况进行展开的符号： %% 表示'%'、%h 表示用户的主目录、%u 表示该用户的用户名
####经过扩展之后的值必须要么是绝对路径，要么是相对于用户主目录的相对路径。

 

#############3.2、安全验证的设定###############
# For this to work you will also need host keys in /etc/ssh/ssh_known_hosts
#RhostsRSAAuthentication no
##是否使用强可信主机认证(通过检查远程主机名和关联的用户名进行认证)。仅用于SSH-1。
###这是通过在RSA认证成功后再检查 ~/.rhosts 或 /etc/hosts.equiv 进行认证的。出于安全考虑，建议使用默认值"no"。

# similar for protocol version 2
#HostbasedAuthentication no
##这个指令与 RhostsRSAAuthentication 类似，但是仅可以用于SSH-2。

# Change to yes if you don't trust ~/.ssh/known_hosts for
# RhostsRSAAuthentication and HostbasedAuthentication

#IgnoreUserKnownHosts no
##IgnoreUserKnownHosts用来设置ssh在进行RhostsRSAAuthentication安全验证时是否忽略用户的“/$HOME/.ssh/known_hosts”文件
# Don't read the user's ~/.rhosts and ~/.shosts files

#IgnoreRhosts yes
##IgnoreRhosts用来设置验证的时候是否使用“~/.rhosts”和“~/.shosts”文件

# To disable tunneled clear text passwords, change to no here!
#PasswordAuthentication yes
##PasswordAuthentication用来设置是否开启密码验证机制(基于口令的认证)，如果用密码登录系统，则设置yes

#PermitEmptyPasswords no
#PermitEmptyPasswords用来设置是否允许用口令为空的账号登录系统，设置no

#PasswordAuthentication yes
##是否允许使用基于密码的认证。默认为"yes"。
PasswordAuthentication yes

# Change to no to disable s/key passwords
##设置禁用s/key密码
#ChallengeResponseAuthentication yes
##ChallengeResponseAuthentication 是否允许质疑-应答(challenge-response)认证
ChallengeResponseAuthentication no

 

########3.3、与 Kerberos 有关的参数设定，指定是否允许基于Kerberos的用户认证########
#Kerberos options
#KerberosAuthentication no
##是否要求用户为PasswdAuthentication提供的密码必须通过Kerberos KDC认证，要使用Kerberos认证，
###服务器必须提供一个可以校验KDC identity的Kerberos servtab。默认值为no

#KerberosOrLocalPasswd yes
##如果Kerberos密码认证失败，那么该密码还将要通过其他的的认证机制，如/etc/passwd
###在启用此项后，如果无法通过Kerberos验证，则密码的正确性将由本地的机制来决定，如/etc/passwd，默认为yes

#KerberosTicketCleanup yes
##设置是否在用户退出登录是自动销毁用户的ticket

#KerberosGetAFSToken no
##如果使用AFS并且该用户有一个Kerberos 5 TGT,那么开启该指令后，
###将会在访问用户的家目录前尝试获取一个AFS token,并尝试传送 AFS token 给 Server 端，默认为no

 

####3.4、与 GSSAPI 有关的参数设定，指定是否允许基于GSSAPI的用户认证，仅适用于SSH2####
##GSSAPI 是一套类似 Kerberos 5 的通用网络安全系统接口。
###如果你拥有一套 GSSAPI库，就可以通过 tcp 连接直接建立 cvs 连接，由 GSSAPI 进行安全鉴别。

# GSSAPI options
#GSSAPIAuthentication no
##GSSAPIAuthentication 指定是否允许基于GSSAPI的用户认证，默认为no

GSSAPIAuthentication yes
#GSSAPICleanupCredentials yes
##GSSAPICleanupCredentials 设置是否在用户退出登录是自动销毁用户的凭证缓存
GSSAPICleanupCredentials yes

# Set this to 'yes' to enable PAM authentication, account processing,
# and session processing. If this is enabled, PAM authentication will
# be allowed through the ChallengeResponseAuthentication mechanism.
# Depending on your PAM configuration, this may bypass the setting of
# PasswordAuthentication, PermitEmptyPasswords, and
# "PermitRootLogin without-password". If you just want the PAM account and
# session checks to run without PAM authentication, then enable this but set
# ChallengeResponseAuthentication=no
#UsePAM no
##设置是否通过PAM验证
UsePAM yes

# Accept locale-related environment variables
##AcceptEnv 指定客户端发送的哪些环境变量将会被传递到会话环境中。
###[注意]只有SSH-2协议支持环境变量的传递。指令的值是空格分隔的变量名列表(其中可以使用'*'和'?'作为通配符)。
####也可以使用多个 AcceptEnv 达到同样的目的。需要注意的是，有些环境变量可能会被用于绕过禁止用户使用的环境变量。
#####由于这个原因，该指令应当小心使用。默认是不传递任何环境变量。

AcceptEnv LANG LC_CTYPE LC_NUMERIC LC_TIME LC_COLLATE LC_MONETARY LC_MESSAGES
AcceptEnv LC_PAPER LC_NAME LC_ADDRESS LC_TELEPHONE LC_MEASUREMENT
AcceptEnv LC_IDENTIFICATION LC_ALL
AllowTcpForwarding yes

##AllowTcpForwarding设置是否允许允许tcp端口转发，保护其他的tcp连接

#GatewayPorts no
##GatewayPorts 设置是否允许远程客户端使用本地主机的端口转发功能，出于安全考虑，建议禁止

 

#############3.5、X-Window下使用的相关设定###############

#X11Forwarding no
##X11Forwarding 用来设置是否允许X11转发
X11Forwarding yes

#X11DisplayOffset 10
##指定X11 转发的第一个可用的显示区(display)数字。默认值是 10 。
###可以用于防止 sshd 占用了真实的 X11 服务器显示区，从而发生混淆。
X11DisplayOffset 10

#X11UseLocalhost yes

 

#################3.6、登入后的相关设定#################

#PrintMotd yes
##PrintMotd用来设置sshd是否在用户登录时显示“/etc/motd”中的信息，可以选在在“/etc/motd”中加入警告的信息

#PrintLastLog no
#PrintLastLog 是否显示上次登录信息,最好不要

#TCPKeepAlive yes
##TCPKeepAlive 是否持续连接，设置yes可以防止死连接
###一般而言，如果设定这项目的话，那么 SSH Server 会传送 KeepAlive 的讯息给 Client 端，以确保两者的联机正常！
####这种消息可以检测到死连接、连接不当关闭、客户端崩溃等异常。在这个情况下，任何一端死掉后， SSH 可以立刻知道，而不会有僵尸程序的发生！

#UseLogin no
##UseLogin 设置是否在交互式会话的登录过程中使用。默认值是"no"。
###如果开启此指令，那么X11Forwarding 将会被禁止，因为login不知道如何处理 xauth cookies 。
####需要注意的是，在SSH底下本来就不接受 login 这个程序的登入，如果指UsePrivilegeSeparation ，那么它将在认证完成后被禁用。
UserLogin no　　　　　　　

#UsePrivilegeSeparation yes
##UsePrivilegeSeparation 设置使用者的权限
#PermitUserEnvironment no
#Compression delayed
#ClientAliveInterval 0
#ClientAliveCountMax 3
#ShowPatchLevel no

#UseDNS yes
##UseDNS是否使用dns反向解析

#PidFile /var/run/sshd.pid

#MaxStartups 10
##MaxStartups 设置同时允许几个尚未登入的联机，当用户连上ssh但并未输入密码即为所谓的联机，
###在这个联机中，为了保护主机，所以需要设置最大值，预设为10个，而已经建立联机的不计算入内，
####所以一般5个即可，这个设置可以防止恶意对服务器进行连接

#MaxAuthTries 6
##MaxAuthTries 用来设置最大失败尝试登陆次数为6，合理设置次数，可以防止攻击者穷举登录服务器
#PermitTunnel no


############3.7、开放禁止用户设定############

#AllowUsers<用户名1> <用户名2> <用户名3> ...
##指定允许通过远程访问的用户，多个用户以空格隔开

#AllowGroups<组名1> <组名2> <组名3> ...
##指定允许通过远程访问的组，多个组以空格隔开。当多个用户需要通过ssh登录系统时，可将所有用户加入一个组中。

#DenyUsers<用户名1> <用户名2> <用户名3> ...
##指定禁止通过远程访问的用户，多个用户以空格隔开

#DenyGroups<组名1> <组名2> <组名3> ...
##指定禁止通过远程访问的组，多个组以空格隔开。

# no default banner path 欢迎标语
#Banner /some/path

# override default of no subsystems
Subsystem       sftp    /usr/libexec/openssh/sftp-server
ClientAliveInterval 3600
ClientAliveCountMax 0
```



```
#登录
ssh username@host -p 3333

#在远程执行命令
ssh username@host -p 3333 'ls /'

```

