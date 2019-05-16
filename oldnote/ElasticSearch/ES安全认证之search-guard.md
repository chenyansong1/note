转自：https://www.cnblogs.com/shifu204/p/6376683.html




[TOC]

# ES安全认证之search-guard

**下面所有的安装说明都是基于下面的版本进行的**：

* ES版本：2.4.3
* search guard版本
  * search-guard-2：2-2.4.3.12
  * search-guard-ssl：2.4.3.19



## 1.search guard的原理简介



![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard.png)



## 2.search guard安装与ES集成

### 2.1.安装search guard

#### 2.1.1版本选择

需要知道ES和search guard之间的对应关系

github上分支没有2.4.x版本的分支，笔者一度找了好久才找到下面一个版本关系对应表

* Search Guard 2: Latest stable versions

| Elasticsearch | Search Guard | Artifact                              | Support available                |
| ------------- | ------------ | ------------------------------------- | -------------------------------- |
| 2.4.6         | 2.4.6.14     | com.floragunn:search-guard-2:2.4.6.14 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.5         | 2.4.5.14     | com.floragunn:search-guard-2:2.4.5.14 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.4         | 2.4.4.12     | com.floragunn:search-guard-2:2.4.4.12 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.3         | 2.4.3.12     | com.floragunn:search-guard-2:2.4.3.12 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.2         | 2.4.2.12     | com.floragunn:search-guard-2:2.4.2.12 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.1         | 2.4.1.12     | com.floragunn:search-guard-2:2.4.1.12 | no, 2.4.x reached EOL 2018-02-28 |
| 2.4.0         | 2.4.0.12     | com.floragunn:search-guard-2:2.4.0.12 | no, 2.4.x reached EOL 2018-02-28 |
| 2.3.5         | 2.3.5.12     | com.floragunn:search-guard-2:2.3.5.12 | no, 2.3.x reached EOL 2017-09-30 |
| 2.3.4         | 2.3.4.12     | com.floragunn:search-guard-2:2.3.4.12 | no, 2.3.x reached EOL 2017-09-30 |
| 2.3.3         | 2.3.3.12     | com.floragunn:search-guard-2:2.3.3.12 | no, 2.3.x reached EOL 2017-09-30 |



更多版本之间的对应关系可以参见：https://docs.search-guard.com/v5/search-guard-versions

* 可以根据版本去GitHub，或者去search.maven下面下载
  * https://github.com/floragunncom/search-guard 
  * https://search.maven.org/




#### 2.1.2.安装search guard和search ssl



安装search guard插件必须要安装两部分：

①search-guard-xx

②search-guard-ssl

（XX指的是与elasticsearch引擎对应的版本）

github地址：

```
https://github.com/floragunncom/search-guard
```

这里以elasticsearch 2.3.5版本为例

进入到elasticsearch安装目录（如果是用RPM包安装的，默认位置是，也可用命令whereis elasticsearch查看安装位置）

```
cd /usr/share/elasticsearch
```

 

安装方法：

（1）search-guard

elasticsearch版本：elasticsearch 2.x

```
bin/plugin install -b com.floragunn/search-guard-2/<version>
```

elasticsearch 2.3.5版本：

```
bin/plugin install -b com.floragunn/search-guard-2/2.3.5.10
```

elasticsearch版本：elasticsearch 5.x

```
bin/elasticsearch-plugin install -b com.floragunn:search-guard-5:<version>
```

 

（2）search-guard-ssl

elasticsearch 2.x

```
bin/plugin install -b com.floragunn/search-guard-ssl/<version>
```

elasticsearch 2.3.5版本：

```
bin/plugin install -b com.floragunn/search-guard-ssl/2.3.5.19
```

elasticsearch 5.x

```
bin/elasticsearch-plugin install -b com.floragunn:search-guard-ssl:<version>
```

注意事项：

当es的版本大于2.2时，安装过程中可能会有如下提示：

![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-1.png)



这个是正常现象，只要看到有以下提示就表示安装成功：

```
Installed search-guard-ssl into /usr/share/elasticsearch/plugins/search-guard-ssl
Installed search-guard-2 into /usr/share/elasticsearch/plugins/search-guard-2
```



> 这里遇到的坑就是：
>
> 1. 没有选择正确的版本：这个参见：版本对照表
> 2. 选择了版本之后下载不能成功：这个可以从search maven中下载



### 2.2.生成证书

1.下载search guard 源码工具，里面包含证书生成工具

```
git clone https://github.com/floragunncom/search-guard-ssl.git
```

2.切换到search guard ssl 源码目录，进入example-pki-scripts文件夹，里面有3个脚本

```
cd search-guard-ssl/example-pki-scripts
```

gen_client_node_cert.sh  创建客户端证书

gen_node_cert.sh           创建节点证书

gen_root_ca.sh               创建根证书

2.进入example-pki-scripts/etc目录，里面是证书生成时的一些配置文件，可根据需要修改相应的信息

root-ca.conf            根证书配置

signing-ca.conf        签名证书配置

其中自定义的信息如下：

```
0.domainComponent       = "www.test.com”    域名
1.domainComponent       = "www.test.com"    域名
organizationName        = "Test"            组织名称
organizationalUnitName  = "Test Root CA"        组织单位名称
commonName              = "Test Root CA"        通用名称
以上信息随便填写，只要保证生成证书时跟证书、签名证书中的信息一致即可
```

3.生成证书

返回到example-pki-scripts目录下，修改example.sh文件：

修改之后如下：


```
#!/bin/bash

set -e

./clean.sh

./gen_root_ca.sh 12345678 12345678

./gen_node_cert.sh 0 12345678 12345678&& ./gen_node_cert.sh 1 12345678 12345678 &&  ./gen_node_cert.sh 2 12345678 12345678

./gen_client_node_cert.sh test 12345678 12345678

./gen_client_node_cert.sh test 12345678 12345678
```



参数说明：

./gen_root_ca.sh 12345678 12345678

第一个参数为CA_PASS，即CA密码（根证书密码）

第二个参数为TS_PASS，即TS密码（truststore，信任证书密码）

./gen_node_cert.sh 0 12345678 12345678

第一个参数为node编号，生成证书后的文件名为node-0*

第二个参数为KS_PASS（keystore文件密码）

第三个参数为CA_PASS

./gen_client_node_cert.sh test 12345678

第一个参数为客户端节点名称，生成证书后的文件名为test*

第二个参数为KS_PASS

第三个参数为CA_PASS

4.运行example.sh文件，会在当前目录下生成各种证书文件：

```
sh example.sh
```



![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-2.png)

 








### 2.3.search guard配置









### 2.3.search guard 用户、角色、权限










## 3.客户端认证（访问通过search guard）





## 4.遇到的坑





