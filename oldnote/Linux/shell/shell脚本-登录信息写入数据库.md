

脚本位置

```shell
/etc/rc.local
```



脚本

```shell
login_str=`last|grep logged|head -1`
echo $login_str
login_ip=`last|grep logged |head -1|awk -F ' ' '{print $3}'`
echo $login_ip
login_date=`date +%s`
echo $login_date
login_url="http://localhost:8080/SOCWeb/userInfo/sshRemoteLoginLog.action?ip=${login_ip}&time=${login_date}000"
echo $login_url
curl $login_url

```

