CentOS 7 使用shadowsocks和privoxy进行全局代理

转自：http://linderun.com/centos7-shadowsocks-zqa6b

> 安装Shadowsocks客户端

- 安装epel源、安装pip包管理

  ```
  sudo yum -y install epel-release
  sudo yum -y install python-pip
  ```

- 安装Shadowsocks客户端

  ```
  sudo pip install shadowsocks
  ```

> 配置Shadowsocks连接

- 新建配置文件、默认不存在

  ```
  sudo mkdir /etc/shadowsocks
  sudo vi /etc/shadowsocks/shadowsocks.json
  ```

- 添加配置信息：前提是需要有ss服务器的地址、端口等信息

  ```
  {
    "server":"x.x.x.x", # shadowsocks服务器地址
    "server_port":1010, # shadowsocks服务器端口
    "local_address":"127.0.0.1", # 本地IP
    "local_port":1080, # 本地端口
    "password":"password", # shadowsocks连接密码
    "timeout":5, # 等待超时时间
    "method":"aes-256-cfb", # 加密方式
    "fast_open": false, # true或false。开启fast_open以降低延迟，但要求Linux内核在3.7+
    "workers":1 # 工作线程数
  }
  ```

- 配置自启动，新建启动脚本文件

  ```
  /etc/systemd/system/shadowsocks.service
  ```

  ，内容如下：

  ```
  [Unit] 
  Description=Shadowsocks 
  [Service] 
  TimeoutStartSec=0 
  ExecStart=/usr/bin/sslocal -c /etc/shadowsocks/shadowsocks.json 
  [Install] 
  WantedBy=multi-user.target
  ```

- 启动Shadowsocks服务

  ```
  sudo systemctl enable shadowsocks
  sudo systemctl start shadowsocks
  sudo systemctl status shadowsocks
  ```

- 验证Shadowsocks客户端服务是否正常运行

  ```
  curl --socks5 127.0.0.1:1080 http://httpbin.org/ip
  ```

- Shadowsock客户端服务已正常运行，则结果如下：

  ```
  {
        "origin": "x.x.x.x"       #你的Shadowsock服务器IP
  }
  ```

> 通过 privoxy 全局代理

- 安装 privoxy

  ```
  sudo yum install -y privoxy
  ```

- 编辑privoxy配置文件

  ```
  sudo vi /etc/privoxy/config
  # 搜索 socks5t在下面添加一条转发代理ip ，存在则去掉注释即可
  forward-socks5t   /               127.0.0.1:1080 .
  # 搜索listen-address，取消注释的ip地址，或者直接新加下面的信息
  listen-address  127.0.0.1:8118
  ```

- 编辑bashrc配置

  ```
  # 打开
  vim ~/.bashrc
  # 加入以下内容，如想关闭全局代理，则注释掉
  export http_proxy=http://127.0.0.1:8118  
  export https_proxy=http://127.0.0.1:8118  
  export ftp_proxy=http://127.0.0.1:8118 
  # 更新配置
  source ~/.bashrc
  ```

- 启动 privoxy

  ```
  systemctl restart privoxy
  ```



