tcpdump抓包时IP地址莫名其妙变为bogon，应该是和DNS或者hostname有一定的关系，但是我的电脑hostname又没有修改，应该和我修改了虚拟机配置有关系。

        找到一种办法是在tcpdump中加参数-nn，姑且解决一下问题。

加参数前：

```

root@ubuntu:/home/cling60# tcpdump -vv  -i  eth3  -c 10
tcpdump: listening on eth3, link-type EN10MB (Ethernet), capture size 262144 bytes
17:32:12.476102 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    <span style="color:#ff0000;">bogon.discard > bogon.discard</span>: [no cksum] UDP, length 18
17:32:12.476111 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.476155 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.476158 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.476161 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.476168 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.476170 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.516051 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.516106 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
17:32:12.516109 IP (tos 0x0, ttl 32, id 4, offset 0, flags [none], proto UDP (17), length 46)
    bogon.discard > bogon.discard: [no cksum] UDP, length 18
10 packets captured
1126 packets received by filter
837 packets dropped by kernel

```
加参数后：
```
root@ubuntu:/home/cling60# tcpdump<span style="color:#ff0000;">  -nn </span>-vv  -i  eth3  -c 10
tcpdump: listening on eth3, link-type EN10MB (Ethernet), capture size 262144 bytes
17:35:16.961262 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    <span style="color:#ff0000;">192.168.19.129.9 > 192.168.19.128.9: </span>[no cksum] UDP, length 18
17:35:16.961726 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.962658 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.962710 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.962714 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.962723 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.962730 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.984193 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.984222 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
17:35:16.984225 IP (tos 0x0, ttl 32, id 8, offset 0, flags [none], proto UDP (17), length 46)
    192.168.19.129.9 > 192.168.19.128.9: [no cksum] UDP, length 18
10 packets captured
422 packets received by filter
139 packets dropped by kernel
```