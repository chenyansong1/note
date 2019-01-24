[TOC]



ssl：Secure Socket Layer 安全的套接字层，只是一个库



TLS: Transport Layer Security	传输层安全，相当于SSLv3



# 1.加密的分类



对称加密算法：

​	DES：data encrption standard , 56bit

​	3DES:

​	AES:Advanced encrption standard , 128bit

​		AES192, AES256, AES512

​	Blowfish:



实现对称加密的工具：

​	OpenSSL，gpg



单向加密：

​	MD4

​	MD5

​	SHA1

​		SHA192, SHA256, SHA384

​	CRC-32：校验



公钥加密：（加密，签名）

​	身份认证（数字签名）

​	数据加密

​	秘钥交换

​	RSA：加密，签名

​	DSA：签名

​	ElGamal

# 2.openssl

## 2.1.openssl说明

OpenSSL：SSL的开源实现

​	libcrpto:通用加密库

​	libssl:SSL/TLS的实现

​		基于会话的，实现了身份认证，数据机密性和会话完整性的TLS/SSL库

​	openssl:命令行工具（能够实现单向加密，对称加密，非对称加密）

​		实现私有证书颁发机构

​		openssl的子命令

## 2.2.openssl的帮助命令



```
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl ?
openssl:Error: '?' is an invalid command.

#基础命令
Standard commands
asn1parse      ca             ciphers        crl            crl2pkcs7      
dgst           dh             dhparam        dsa            dsaparam       
ec             ecparam        enc            engine         errstr         
gendh          gendsa         genrsa         nseq           ocsp           
passwd         pkcs12         pkcs7          pkcs8          prime          
rand           req            rsa            rsautl         s_client       
s_server       s_time         sess_id        smime          speed          
spkac          verify         version        x509           

#单向加密相关(特征码)
Message Digest commands (see the `dgst' command for more details)
md2            md4            md5            mdc2           rmd160         
sha            sha1           

#加密相关
Cipher commands (see the `enc' command for more details)
aes-128-cbc    aes-128-ecb    aes-192-cbc    aes-192-ecb    aes-256-cbc    
aes-256-ecb    base64         bf             bf-cbc         bf-cfb         
bf-ecb         bf-ofb         cast           cast-cbc       cast5-cbc      
cast5-cfb      cast5-ecb      cast5-ofb      des            des-cbc        
des-cfb        des-ecb        des-ede        des-ede-cbc    des-ede-cfb    
des-ede-ofb    des-ede3       des-ede3-cbc   des-ede3-cfb   des-ede3-ofb   
des-ofb        des3           desx           rc2            rc2-40-cbc     
rc2-64-cbc     rc2-cbc        rc2-cfb        rc2-ecb        rc2-ofb        
rc4            rc4-40         seed           seed-cbc       seed-cfb       
seed-ecb       seed-ofb   
```



## 2.3.openssl的常见命令（文件加密）

```
#算法的速度测试工具，下面是测试des的速度
openssl speed des


#加密
/*
des3：加密算法
-a:使用base64编码之后，再加密
-in:对哪个文件进行加密
-out：输出到哪里
-salt:加盐
-e  encrypt the input data: this is the default.
-salt	use a salt in the key derivation routines. This is the default.
-d  decrypt the input data.

*/
openssl enc -des3 -salt -a -in /etc/inittab -out /tmp/inittab.des3


#解密:-d实现解密
openssl enc -des3 -d -salt -a -in /tmp/inittab.des3 -out /etc/inittab


```



## 文件的特征码

```
dm5sum file

sha1sum file


openssl dgst -sha1 /etc/inittab

openssl dgst -md5 /etc/inittab
```

![image-20181007190205726](/Users/chenyansong/Documents/note/images/linux/ssh/openssl.png)



## 生成密码串（对称）

```
#使用帮助
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl passwd -2
Usage: passwd [options] [passwords]
where options are
-crypt             standard Unix password algorithm (default)
-1                 MD5-based password algorithm
-apr1              MD5-based password algorithm, Apache variant
-salt string       use provided salt
-in file           read passwords from file
-stdin             read passwords from stdin
-noverify          never verify when reading password from terminal
-quiet             no warnings
-table             format output as table
-reverse           switch table columns
chenyansongdeMacBook-Pro:ssl chenyansong$ 


#默认会使用一个随机的salt进行混合加密，-1（数字）是表示MD5-based
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl passwd -1
Password: 
Verifying - Password: 
$1$LHUIfDG3$t5NP0VxGGEs2Yra8C6Kh5/

#但是如果我们指定salt，那么就可以产生相同的密码
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl passwd -1 -salt LHUIfDG3
Password: 
$1$LHUIfDG3$Id7fzNqBCCnHogLGl/XKE1
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl passwd -1 -salt LHUIfDG3
Password: 
$1$LHUIfDG3$Id7fzNqBCCnHogLGl/XKE1

```



## 随机数(字符串)

```
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl rand -base64 5
oZqeKVE=
chenyansongdeMacBook-Pro:ssl chenyansong$ openssl rand -base64 2
+es=
```



# openssl实现私有CA



## 生成秘钥



```
#生成私钥(RSA)
openssl genrsa

#指定生成的秘钥长度
openssl genrsa 2048

#保存指定的文件中
openssl genrsa -out filename

#文件保存之后，需要修改文件的权限为600
chmod 600 filename
#or
(umask 077; openssl genrsa -out server1024.key 1024)

#根据上面生成的私钥，输出公钥(提取公钥)
openssl rsa -in server1024.key -pubout

```



## 私有CA



```
man req
req - PKCS#10 certificate request and certificate generating utility.

# -x509 表示生成自签证书
# -new 生成一个证书，如果后面没有-key，那么使用一个新的key(私钥)
# -key 指定私钥
# -out 证书输出到的文件
# -days 证书的有效期，默认是30day
openssl req -new -x509 -key server1024.key -out server.crt -days 365
#接下来会让你输入：国家，省份，城市，公司，部门，主机名称(hostname很重要),邮件地址


#查看证书的信息
openssl x509 -text -in server.crt 

```



vim /etc/pki/tls/openssl.cnf ,编辑这个配置文件，可以看到ca的一些文件的目录，已经一些默认的设定（如国家，省份，城市等）



下面是在默认的目录下生成证书

```
cd /etc/pki/CA
#在private 目录下生成一个私钥
(umask 077; openssl genrsa -out private/cakey.pem 2048)

#使用私钥生成一个证书
openssl req -new -x509 -key private/cakey.pem -out cacert.pem

#创建默认的目录和文件
mkdir certs newcerts crl

touch index.txt
touch serial
echo 01 > serial

```



## 向私有CA申请证书

1. 生成私钥
2. 根据私钥，生成一个证书请求
3. 请求CA认证

这样如果我们有一个服务，比如是httpd，我们刚好需要为该服务生成一个证书

```
#1.首先在对应的目录下生成一个私钥
(umask 077; openssl genrsa -out httpd.key 1024)

#2.生成证书，需要：国家，省份，城市等
openssl req -new -key httpd.key -out httpd.csr

#3.向证书机构(我们在上面建立的机构)，申请认证(会根据这个配置文件来签署/etc/pki/tls/openssl.cnf),我们需要将 httpd.csr 这个文件传输到我们的 证书机构所在的服务器，然后执行下面的命令，来生成该机构签署的证书
openssl ca -in httpd.csr -out httpd.crt -days 365
#我们需要输入：国家，省份，城市等，然后让我们选择是否需要签署[y/n]

#查看已经签署的证书
cat /etc/pki/CA/index.txt
```



# ssh原理 



## 基于口令的认证



1. 服务器端发送过来一个自己的公钥
2. 客户端接收（yes)，然后客户端在本地随机生成一个口令
3. 客户端用收到的公钥加密这个口令，然后发送给服务器端，接下来客户端和服务器端就用这个口令对内容进行加密，然后通信




## 基于秘钥的认证（免密码登录）



1. 客户端将自己本地的公钥**复制**到服务器的某个用户的家目录下
2. 在通信的过程中，客户端使用自己的私钥加密数据，然后服务器端使用私钥解密数据



生成秘钥

```

```



# openssh



```
rpm -qa |grep ssh
openssh-server-4.3p2-82.el5
openssh-4.33p2-82.el5
openssh-askpass-4.33p2-82.el5
openssh-clients-4.33p2-82.el5
```



# scp

```
scp -P 333 /path/to/file username@host
	-r #递归复制(目录)
	-p #保持文件的属性信息
	-P #指定端口(大写的P)
	-a #相当于 -rp
	
```



# sftp,ftp

```
sftp [-P 999] [user@]host[:dir[/]]

>get /path/to/file
>mget /path/to/file
get [-afPpr] remote-path [local-path]
```

本地命令前面都加了一个 “l", 而直接执行命令是对远程服务器

```
FTP> ! 从 ftp 子系统退出到外壳。

FTP> ? 显示 ftp 命令说明。? 与 help 相同。

格式：? [command]

说明：[command]指定需要帮助的命令名称。如果没有指定 command，ftp 将显示全部命令的列表。 



FTP> append 使用当前文件类型设置将本地文件附加到远程计算机上的文件。

格式：append local-file [remote-file]

说明：local-file 指定要添加的本地文件。

remote-file 指定要添加 local-file 的远程计算机上的文件。如果省略了 remote-file，本地文件名将被用作远程文件名。



FTP> ascii 将文件传送类型设置为默认的 ASCII。

说明：FTP 支持两种文件传送类型，ASCII 码和二进制图像。在传送文本文件时应该使用ASCII。



FTP> bell 切换响铃以在每个文件传送命令完成后响铃。默认情况下，铃声是关闭的。



FTP> binary（或bi）
 将文件传送类型设置为二进制。



FTP> bye（或by）
 结束与远程计算机的 FTP 会话并退出 ftp。



FTP> cd 更改远程计算机上的工作目录。





格式：cd remote-directory

说明：remote-directory 指定要更改的远程计算机上的目录。



FTP> close 结束与远程服务器的 FTP 会话并返回命令解释程序。



FTP> debug 切换调试。当调试打开时，发送到远程计算机的每个命令都打印，前面是字符串“>”。默认情况下，调试是关闭的。



FTP> delete 删除远程计算机上的文件。

格式：delete remote-file

说明：remote-file 指定要删除的文件。



FTP> dir 显示远程目录文件和子目录列表。

格式：dir [remote-directory] [local-file]

说明：remote-directory 指定要查看其列表的目录。如果没有指定目录，将使用远程计算机中的当前工作目录。Local-file 指定要存储列表的本地文件。如果没有指定，输出将显示在屏幕上。



FTP> disconnect 从远程计算机断开，保留 ftp 提示。



FTP> get 使用当前文件转换类型将远程文件复制到本地计算机。

格式：get remote-file [local-file]

说明：remote-file 指定要复制的远程文件。

Local-file 指定要在本地计算机上使用的名称。如果没有指定，文件将命名为 remote-file。



FTP >glob 切换文件名组合。组合允许在内部文件或路径名中使用通配符（*和?）。默认情况下，组合是打开的。



FTP >hash 切换已传输的每个数据块的数字签名 (#) 打印。数据块的大小是2048 字节。默认情况下，散列符号打印是关闭的。



FTP >help 显示 ftp 命令说明。

格式：help [command]

说明：command 指定需要有关说明的命令的名称。如果没有指定 command，ftp 将显示全部命令的列表。



FTP >lcd 更改本地计算机上的工作目录。默认情况下，工作目录是启动 ftp 的目录。

格式：lcd [directory]

说明：directory 指定要更改的本地计算机上的目录。如果没有指定directory，将显示本地计算机中当前的工作目录。



FTP >literal 将参数逐字发送到远程 FTP 服务器。将返回单个的 FTP 回复代码。

格式：literal argument [ ...]

说明：argument 指定要发送到 FTP 服务器的参数。



FTP >ls 显示远程目录文件和子目录的缩写列表。

格式：ls [remote-directory] [local-file]

说明：remote-directory 指定要查看其列表的目录。如果没有指定目录，将使用远程计算机中的当前工作目录。 local-file 指定要存储列表的本地文件。如果没有指定，输出将显示在屏幕上。



FTP >mdelete 删除远程计算机上的文件。

格式：mdelete remote-files [ ...]

说明：remote-files 指定要删除的远程文件。



FTP >mdir 显示远程目录文件和子目录列表。可以使用 mdir 指定多个文件。

格式：mdir remote-files [ ...] local-file

说明：remote-files 指定要查看列表的目录。必须指定 remote-files。请键入 - 使用远程计算机上的当前工作目录。

local-file 指定要还原列表的本地文件。请键入- 在屏幕上显示列表。



FTP >mget 使用当前文件传送类型将远程文件复制到本地计算机。

格式：mget remote-files [ ...]

说明：remote-files 指定要复制到本地计算机的远程文件。



FTP >mkdir 创建远程目录。

格式：mkdir directory

说明：directory 指定新的远程目录的名称。

FTP >mls 显示远程目录文件和子目录的缩写列表。

格式：mls remote-files [ ...] local-file

说明：remote-files 指定要查看列表的文件。必须指定 remote-files；

请键入- 使用远程计算机上的当前工作目录。

local-file 指定要存储列表的本地文件。请键入 - 以在屏幕上显示列表。



FTP >mput 使用当前文件传送类型将本地文件复制到远程计算机上。

格式：mput local-files [ ...]

说明：local-files 指定要复制到远程计算机的本地文件



FTP >open 与指定的 FTP 服务器连接。

格式：open computer [port]

说明：computer 指定要连接的远程计算机。可以通过 IP 地址或计算机名称指定计算机（DNS或主机文件必须可用）。如果自动登录打开（默认），ftp 还将尝试自动将用户登录到 FTP 服务器port 指定用来联系 FTP 服务器的端口号。



FTP >prompt 切换提示。如果关闭提示时 mget 及 mput 传送所有文件，Ftp在多文件传送过程中将提示允许您有选择地检索或存储文件。默认情况下，提示是

打开的。



FTP >put 使用当前文件传送类型将本地文件复制到远程计算机上。

格式：put local-file [remote-file]

说明：local-file 指定要复制的本地文件。

remote-file 指定要在远程计算机上使用的名称。如果没有指定，文件将命名为 local-file。



FTP >pwd 显示远程计算机上的当前目录。



FTP >quit 结束与远程计算机的 FTP 会话并退出 ftp。



FTP >quote 将参数逐字发送到远程 FTP 服务器。将返回单个的 FTP 回复代码。

Quote 与 literal 相同。

格式：quote argument [ ...]

说明：argument 指定要发送到 FTP 服务器的参数。



FTP >recv 使用当前文件传送类型将远程文件复制到本地计算机。Recv 与 get相同。

格式：recv remote-file [local-file]

说明：remote-file 指定要复制的远程文件。

local-file 指定要在本地计算机上使用的名称。如果没有指定，文件将命名为 remote-file。



FTP >remotehelp 显示远程命令帮助。

格式：remotehelp [command]

说明：command 指定需要帮助的命令的名称。如果没有指定 command，ftp将显示全部远程命令的列表。



FTP >rename 重命名远程文件。

格式：rename filename newfilename

说明：filename 指定要重命名的文件。 newfilename 指定新的文件名。



FTP >rmdir 删除远程目录。

格式：rmdir directory

说明：directory 指定要删除的远程目录的名称。



FTP >send 使用当前文件传送类型将本地文件复制到远程计算机上。Send 与put 相同。

格式：send local-file [remote-file]

说明：local-file 指定要复制的本地文件。 remote-file 指定要在远程计算机上使用的名称。如果没有指定，文件将命名为 local-file。



FTP >status 显示 FTP 连接和切换的当前状态。



FTP >trace 切换数据包跟踪。Trace 在运行 ftp 命令时显示每个数据包的路由。



FTP >type 设置或显示文件传送类型。

格式：type [type-name]

说明：type-name 指定文件传送类型。默认设置为 ascii。如果没有指定type-name，将显示当前的类型。



FTP >user 指定远程计算机的用户。

格式：user username [password] [account]

说明：user-name 指定登录到远程计算机所使用的用户名。password 指定 user-name 的密码。如果没有指定，但必须指定，ftp 会提示输入密码。

account 指定登录到远程计算机所使用的帐户。如果没有指定account，但是需要指定，ftp 会提示您输入帐户。



FTP >verbose 切换 verbose 模式。

如果打开，将显示所有 ftp 响应。在文件传送完成后，将同时显示与传送效率有关的统计信息。默认情况下，verbose 是打开的。





sftp 是一个交互式文件传输程式。它类似于 ftp, 但它进行加密传输，比FTP有更高的安全性。下边就简单介绍一下如何远程连接主机，进行文件的上传和下载，以及一些相关操作。







举例，如远程主机的 IP 是 202.206.64.33或者是域名www.hebust.edu.cn,用户名是  fyt
 ,在命令行模式下:sftp fyt@202.206.64.33或者  fyt@www.hebust.edu.cn。回车提示输入密码。进入提示符



sftp>



如果登陆远程机器不是为了上传下载文件，而是要修改远程主机上的某些文件。可以



ssh  fyt@202.206.64.33 （其实sftp就是ssh 的一个程式。）



sftp> get /var/www/fuyatao/index.php  /home/fuyatao/

这条语句将从远程主机的  /var/www/fuyatao/目录下将 index.php 下载到本地  /home/fuyatao/目录下。



sftp> put /home/fuyatao/downloads/Linuxgl.pdf /var/www/fuyatao/

这条语句将把本地 /home/fuyatao/downloads/目录下的 linuxgl.pdf文件上传至远程主机/var/www/fuyatao/ 目录下。



你如果不知道远程主机的目录是什么样， pwd命令可以帮您查询远程主机的当前路径。查询本机当前工作目录 lpwd.



改变路径可以用cd ，改变本机路径可以用 lcd;



ls rm rmdir mkdir 这些命令都可以使用。同理调用本机都是加 l
 , 即 lls lrm.



要离开sftp，用exit 或quit、 bye 均可。详细情况可以查阅 man 
 sftp.



如果觉得在命令行模式下不太方便，可以 sudo apt-get install gftp。在图形界面下操作就简便多了。

```

```
 1. sftp user@ip

        你要用sftp, 当然得登录到sftp服务器啊， 在linux的shell中执行上面的命令后， linux shell会提示用户输入密码， 我们就输入password吧。 这样就成功建立了sftp连接。
   2. help

       建立连接后， linux shell中的$编程了sftp>,  这也对。 现在执行以下help, 可以看看sftp支持哪些命令。

   3. pwd和lpwd

       pwd是看远端服务器的目录， 即sftp服务器默认的当前目录。  lpwd是看linux本地目录。

   4. ls和lls

       ls是看sftp服务器下当前目录下的东东， lls是看linux当前目录下的东东。

   5. put a.txt

       这个是把linux当前目录下的a.txt文件上传到sftp服务器的当前目录下。

    6. get b.txt

      这个是把sftp服务器当前目录下的b.txt文件下载到linux当前目录下。

     7. !command

        这个是指在linux上执行command这个命令， 比如!ls是列举linux当前目录下的东东， !rm a.txt是删除linux当前目录下的a.txt文件。

        这个命令非常非常有用， 因为在sftp> 后输入命令， 默认值针对sftp服务器的， 所以执行rm a.txt删除的是sftp服务器上的a.txt文件， 而非本地的linux上的a.txt文件。


     8. exit和quit
     退出。

```

