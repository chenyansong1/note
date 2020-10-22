[toc]

UOS服务器配置IP地址和route

```shell
#nmcli connection modify 有线连接 ipv4.method manual ipv4.addresses 192.168.3.125 connection.autoconnect yes

nmcli connection modify enp5s0f2  ipv4.method manual ipv4.addresses 172.16.110.173 connection.autoconnect yes


#配置之后，会有如下的文件产生
# ls -l /etc/NetworkManager/system-connections/enp5s0f2.nmconnection 
```



配置默认路由

```shell
route -n
route add -net 192.168.200.0 netmask 255.255.255.0 gw 192.168.200.2
route del -net 192.168.200.0 netmask 255.255.255.0 gw 192.168.200.2

#配置默认路由
route add default gw 172.16.110.254
```



参考：

https://my.oschina.net/u/4339087/blog/3306301

https://my.oschina.net/u/4339087/blog/3306301
