# ssh端口转发

ssh端口转发，其实是两台机器建立隧道的过程

## 本地端口转发

```shell
#ServerA ===================== ServerB
ssh -fNgC -L <listen port>:<remote ip>:<remote port> user@<server-b-ip> -p <server-b-port>
#上述命令执行之后会从ServerA ssh到ServerB (理解这个很重要)，其实我们可以想象，这样在serverA执行的命令，其实是在ServerB本地执行

-N 不执行登录shell
-f 后台运行进程
-g 复用访问时作为网关，支持多主机访问本地侦听端口
-C 压缩传输

#example
ssh -fNg -L <listen port>:<remote ip>:<remote port> user@<server-b-ip> -p <server-b-port>

#访问ServerB的本地端口
<listen port>:localhost:<remote port> user@<server-b-ip> -p <server-b-port>
```



## 远程端口转发

此时端口是侦听在远端

```shell
#ServerA ===================== ServerB
ssh -fNC -R <listen port>:<remote ip>:<remote port> user@<server-b-ip> -p <server-b-port>
#上述命令执行之后会从ServerA ssh到ServerB (理解这个很重要)，其实我们可以想象，这样在serverA执行的命令，其实是在ServerB本地执行,此时B端是有listen端口（理解这个很重要），那么就可以通过ServerB的端口访问ServerA上可以访问的服务

-N 不执行登录shell
-f 后台运行进程
-g #远程端口转发的g参数是失效的
-C 压缩传输

```



## 动态端口转发

如果我们不能确定访问的目标服务器的端口（即目标服务器的端口是变化的），此时是可以使用动态端口转发的

```shell
#ServerA ===================== ServerB
ssh -CfNg -D 7009 root@192.168.11.11 -p 53
#此时在A上listen端口，然后在B上动态的转发
```

## 图形化的连接

```shell
-X #提供图形化的界面
ssh -X user@1.1.1.1 -p 53
```







 

