



```shell
[root@bdsoc workspace]# cat  /tmp/test.sh    
#!/bin/bash


java_progc=/home/workspace/jdk1.8.0_101/bin/java

case "$1" in 
start)
   nohup $java_progc -jar BDSOCWeb-3.0.0.jar > nohup.out 2>&1 &
#nohup $java_progc -jar /home/workspace/xxl-job/xxl-job-admin-2.1.1-SNAPSHOT.jar  >/home/workspace/xxl-job/admin.log 2>&1 &

   echo $!>/tmp/bd_spring.pid
   ;;
stop)
   kill `cat /tmp/bd_spring.pid`
   rm -f /tmp/bd_spring.pid
   ;;
restart)
   $0 stop
   $0 start
   ;;
status)
   if [ -e /tmp/bd_spring.pid ]; then
      echo BD_spring is running, pid=`cat /var/run/hit.pid`
   else
      echo BD_spring is NOT running
      exit 1
   fi
   ;;
*)
   echo "Usage: $0 {start|stop|status|restart}"
esac

exit 0 

```



后面就是添加执行权限

```shell
chmod +x  /tmp/test.sh 
```



设置开机自启

```shell
#!/bin/sh
# chkconfig: 2345 54 65
# description: stop/start nginx scripts
//上面的数字不能重复:2345 是在2345级别下启动，54 ，65 是启动顺序和关闭顺序
```



# 1.脚本添加复制到/etc/init.d/目录下
```
-rwxr-xr-x  1 root root  1413 8月  19 21:56 nginxd

#注意：sh脚本要有 +x 的权限：chmod +x nginxd

```

# 2.在脚本下加入
```
#!/bin/sh
# chkconfig: 2345 54 65
# description: stop/start nginx scripts
//上面的数字不能重复:2345 是在2345级别下启动，54 ，65 是启动顺序和关闭顺序
```

# 3.添加到chkconfig
```
[root@lnmp02 init.d]# chkconfig --add nginxd
[root@lnmp02 init.d]# chkconfig nginxd on
[root@lnmp02 init.d]# chkconfig --list nginxd
nginxd          0:关闭  1:关闭  2:启用  3:启用  4:启用  5:启用  6:关闭
[root@lnmp02 init.d]#
 
```