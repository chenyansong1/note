



```shell
#服务器端
##install
sudo apt-get install vsftpd
##修改配置文件
vi /etc/vsftpd.conf

##重启服务
sudo service vsftpd restart

#客户端
##实名用户登录
ftp + IP(server)
输入用户名(server)
输入密码

##example
ftp 192.168.40.119
hadoop
123456

ftp>pwd  #显示当前目录
ftp>ls	 #查看当前目录的文件
ftp>bye  #退出
ftp>quit #退出

##文件的上传和下载
##登录ftp的时候，所在的目录，就是你的上传目录，下载也是一样的
ftp>put local_file  #上传本地的当前目录到remote
ftp>get remote_file #下载remote文件到本地目录

#不允许操作目录，只能对文件进行操作，如果想操作目录的话，就只能打包了

##匿名用户登录(实名用户登录，暴露了密码，并且可以切换到任意目录)
ftp + ServerIP
用户名:anonymous
密码：回车
#不允许匿名用户在任意目录间切换，只能在指定的目录下工作，需要在ftp服务器上创建一个匿名用户目录(匿名用户的根目录)
##修改配置文件,指定匿名用户的根目录
anon_root=/home/Robin/nFtp
##这里需要主要目录权限是否对anonymous用户操作权限问题(其他人权限)

##lftp客户端工具访问ftp服务器
#install
sudo apt-get install lftp

#查看登录之前的本地目录
lftp 192.168.1.11:/aa>lpwd
/home/hadoop
#ftp的pwd
lftp 192.168.1.11:/aa>pwd
ftp://192.168.1.11/aa

#切换本地的local目录
lftp 192.168.1.11:/aa>lcd /home/luffy

#lftp可以上传或者下载多个文件
#可以上传目录或者下载整个目录(这是ftp自带的ftp是没有的)

```

![](/Users/chenyansong/Documents/note/images/linux/command/vsftpd.png)

![](/Users/chenyansong/Documents/note/images/linux/command/image-20191102085303295.png)

![image-20191102091756877](/Users/chenyansong/Documents/note/images/linux/command/image-20191102091756877.png)

![image-20191102091848129](/Users/chenyansong/Documents/note/images/linux/command/image-20191102091848129.png)