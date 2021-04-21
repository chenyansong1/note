[toc]

# install

https://docs.docker.com/docker-for-windows/install/



# 错误问题

https://blog.csdn.net/weixin_38556197/article/details/114454176



 ```shell
#error during connect: In the default daemon configuration on Windows, the docker client must be run with elevated privileges to connect.: Get http://%2F%2F.%2Fpipe%2Fdocker_engine/v1.24/containers/json: open //./pipe/docker_engine: The system cannot find the file specified.

cd "C:\Program Files\Docker\Docker"
DockerCli.exe -SwitchDaemon
 ```



# docker出现no matching manifest for windows/amd64 10.0.18363 in the manifest list entries错误



###### 错误如截图![在这里插入图片描述](https://img-blog.csdnimg.cn/20200324003026453.png)

##### 解决方法

将`"experimental"` 设置为`true`，应用并重启

重启
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200324003247543.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zOTMwNTAyOQ==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200324003327224.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zOTMwNTAyOQ==,size_16,color_FFFFFF,t_70)

