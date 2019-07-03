[TOC]

# Docker的CS模式

![1562123974556](E:\git-workspace\note\images\docker\dcs.png)

![1562124010662](E:\git-workspace\note\images\docker\dcs2.png)



除了docker的客户端可以与守护进程（server)进行交互之外，Docker提供了Remote API

* RESTful风格API

* STDIN， STDOUT， STDERR

  ![1562124204621](E:\git-workspace\note\images\docker\dcs3.png)



## 连接方式

* unix://var/run/docker.sock  (默认的客户端与守护进程连接的方式)
* tcp://host:port
* fd://socketfd

![1562124334222](E:\git-workspace\note\images\docker\dcs4.png)

> 此时docker的client可以与server在不同的机器上

```shell
#是否已经启动docker服务
ps -ef|grep docker
```

# Docker守护进程的配置和操作

```shell
#查看docker的状态，启动，停止
systemctl status docker
systemctl start docker
systemctl stop docker

```

* docker的启动配置选项

  ```shell
  docker -d [options]
  
  -D, --debug=false
  -e, --exec-driver="native"
  -g,--graph="/var/lib/docker"
  --icc=true
  -l,--log-level="info"
  --label=[]
  -p,pidfile="/var/run/docker.pid"
  
  -G, --group="docker"
  -H, --host=[]
  --tls=false
  --tlscacert="/home/sven/.docker/ca.pem"
  --tlscert="/home/sven/.docker/cert.pem"
  --tlskey="/home/sven/.docker/key.pem"
  --tlsverify=false
  
  
  #RemoteAPI相关
  --api-enable-cors=false
  
  #存储相关
  -s,--storage-driver=""
  --selinux=enabled=false
  --storage-opt=[]
  
  #Registry相关
  --insecure-registry=[]
  --registry-mirror=[]
  
  #网络配置
  -b,--bridge=""
  -bip=""
  --fixed-cidr=""
  --fixed-cidr-v6=""
  --dns=[]
  --dns-search=[]
  
  --ip=0.0.0.0
  --ip-forward=true
  --ip-masq=true
  --iptables=true
  --ipv6=false
  --mtu=0
  ```

  

* docker的启动配置文件

  ```shell
  vim /etc/default/docker
  
  DOCKER_OPTS="labels name=docker_server_1"
  
  #restart
  systemctl restart docker
  
  docker info
  ```

# Docker的远程访问

* 第二台安装Docker的服务器
* 修改Docker守护进程启动选项，区别服务器
* 保证Client API与Server API版本一致



* 修改Docker守护进程的启动选项

  ```shell
  -H tcp://host:port
  	unix:///path/to/socket
  	fd://* or df://socketfd
  ```