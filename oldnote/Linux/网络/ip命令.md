ip命令:ip - show / manipulate routing, devices, policy routing and tunnels

[TOC]



# 使用说明

```


[webuser@VM_0_4_centos ~]$ ip --help
Usage: ip [ OPTIONS ] OBJECT { COMMAND | help }
       ip [ -force ] -batch filename
where  OBJECT := { link | addr | addrlabel | route | rule | neigh | ntable |
                   tunnel | tuntap | maddr | mroute | mrule | monitor | xfrm |
                   netns | l2tp | tcp_metrics | token }
       OPTIONS := { -V[ersion] | -s[tatistics] | -d[etails] | -r[esolve] |
                    -h[uman-readable] | -iec |
                    -f[amily] { inet | inet6 | ipx | dnet | bridge | link } |
                    -4 | -6 | -I | -D | -B | -0 |
                    -l[oops] { maximum-addr-flush-attempts } |
                    -o[neline] | -t[imestamp] | -b[atch] [filename] |
                    -rc[vbuf] [size] | -n[etns] name | -a[ll] }
[webuser@VM_0_4_centos ~]$ 


#表示操作的对象
where  OBJECT := { link | addr地址 | addrlabel | route路由 | rule 规则| neigh | ntable |
                   tunnel隧道 | tuntap | maddr | mroute | mrule | monitor | xfrm |
                   netns | l2tp | tcp_metrics | token }
                   

#关联的命令

SEE ALSO
       ip-address(8), ip-addrlabel(8), ip-l2tp(8), ip-link(8), ip-maddress(8), ip-monitor(8), ip-mroute(8), ip-neighbour(8), ip-netns(8), ip-ntable(8), ip-route(8), ip-rule(8), ip-
       tcp_metrics(8), ip-tunnel(8), ip-xfrm(8)
       IP Command reference ip-cref.ps


```





# ip link

配置网络接口属性



##ip link帮助

```
#删除
link delete DEVICE type TYPE [ ARGS ]

#设定
ip link set { DEVICE | group GROUP } { up | down | arp { on | off } |
               promisc { on | off } |
               allmulticast { on | off } |
               dynamic { on | off } |
               multicast { on | off } |
               txqueuelen PACKETS |
               name NEWNAME |
               address LLADDR | broadcast LLADDR |
               mtu MTU |
               netns PID |
               netns NETNSNAME |
               alias NAME |
               vf NUM [ mac LLADDR ] [ vlan VLANID [ qos VLAN-QOS ] ] [ rate TXRATE ] [ spoofchk { on | off } ] |
               master DEVICE |
               nomaster  |
               addrgenmode { eui64 | none }
               link-netnsid ID  }

#显示
ip link show [ DEVICE | group GROUP ]

```

## 使用示例

```
ip link show

#等同于ifconfig -a

[webuser@VM_0_4_centos ~]$ ip link show
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT qlen 1
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP mode DEFAULT qlen 1000
    link/ether 52:54:00:f4:54:a1 brd ff:ff:ff:ff:ff:ff
[webuser@VM_0_4_centos ~]$ 

#-s显示统计属性
ip -s link show

[webuser@VM_0_4_centos ~]$ ip -s  link show
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT qlen 1
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    RX: bytes  packets  errors  dropped overrun mcast   
    376        4        0       0       0       0       
    TX: bytes  packets  errors  dropped carrier collsns 
    376        4        0       0       0       0       
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP mode DEFAULT qlen 1000
    link/ether 52:54:00:f4:54:a1 brd ff:ff:ff:ff:ff:ff
    RX: bytes  packets  errors  dropped overrun mcast   
    626106341  7188302  0       0       0       0       
    TX: bytes  packets  errors  dropped carrier collsns 
    1032601999 7396154  0       0       0       0       
[webuser@VM_0_4_centos ~]$ 


#启动eth1
ip link set eth1 up

```



# ip address 

协议地址

## ip address帮助

```

NAME
       ip-address - protocol address management

SYNOPSIS
       ip [ OPTIONS ] address  { COMMAND | help }

       ip address { add | del } IFADDR dev STRING

       ip address { show | flush } [ dev STRING ] [ scope SCOPE-ID ] [ to PREFIX ] [ FLAG-LIST ] [ label PATTERN ]

       IFADDR := PREFIX | ADDR peer PREFIX [ broadcast ADDR ] [ anycast ADDR ] [ label STRING ] [ scope SCOPE-ID ]

       SCOPE-ID := [ host | link | global | NUMBER ]

       FLAG-LIST := [ FLAG-LIST ] FLAG

       FLAG := [ permanent | dynamic | secondary | primary | tentative | deprecated | dadfailed | temporary ]

```



## 使用

```
#添加
ip address { add | del } IFADDR dev STRING
ip address add 10.2.2.2/8 dev eth1

#使用ifconfig看不到这个地址，可以使用 ip link show查看，这个地址为eth1的 secondary eth1称之为非主要地址，或者是辅助地址

#删除
ip address del 10.2.2.2/8 dev eth1


#清空
ip addr flush dev eth1

```





# ip route

路由

```
#查看，相当于route -n
ip route show

#add
ip route add to 10.0.0.0/8 dev eth0 via 172.168.0.1

#flush 10/8开头的所有的路由
ip route flush to 10/8

```

