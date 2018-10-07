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

