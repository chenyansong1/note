转自：https://www.cnblogs.com/shifu204/p/6376683.html

参见：https://blog.csdn.net/zmx729618/article/details/81095832


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

 

> 从上图生成的证书中可以看到，生成了三种类型的证书：
>
> 1. ES节点证书
> 2. 客户端证书
> 3. 根证书






### 2.3.search guard配置



#### 2.3.1 证书上传到elasticsearch
将example-pki-scripts文件夹中的node-0-keystore.jks和truststore.jks复制到elasticsearch的配置目录中（/etc/elasticsearch）

```
cp node-0-keystore.jks /etc/elasticsearch

cp truststore.jks /etc/elasticsearch 
```

将example-pki-scripts文件夹中的test-keystore.jks和truststore.jks复制到elasticsearch程序目录下的plugins/search-guard-2/sgconfig下，如果这个节点是主节点，则所有节点的search guard配置都从这个节点中配置，然后分发到其它节点中

```
cp test-keystore.jks /usr/share/elasticsearch/plugins/search-guard-2/sgconfig/

cp truststore.jks /usr/share/elasticsearch/plugins/search-guard-2/sgconfig/
```

#### 2.3.2.修改elasticsearch配置文件

```
#vim /etc/elasticsearch/elasticsearch.yml

17  cluster.name: test

23  node.name: node-0

54  network.host: 0.0.0.0 
```

增加以下配置：

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

```
# search-guard配置

# 配置ssl

 searchguard.ssl.transport.enabled: true

 searchguard.ssl.transport.keystore_filepath: node-0-keystore.jks

 searchguard.ssl.transport.keystore_password: 12345678

 searchguard.ssl.transport.truststore_filepath: truststore.jks

 searchguard.ssl.transport.truststore_password: 12345678

 searchguard.ssl.transport.enforce_hostname_verification: false

 searchguard.ssl.transport.resolve_hostname: false

 

# 配置http

# http配置，这里我只是为了测试方便，配置完，应该设置为true

 searchguard.ssl.http.enabled: false

 searchguard.ssl.http.keystore_filepath: node-0-keystore.jks

 searchguard.ssl.http.keystore_password: 12345678

 searchguard.ssl.http.truststore_filepath: truststore.jks

 searchguard.ssl.http.truststore_password: 12345678


 searchguard.allow_all_from_loopback: true

 

# 这里注意，下面的配置一定要和签的客户端证书一致，否则不能插入配置

 searchguard.authcz.admin_dn:

 - CN=test, OU=client, O=client, L=Test, C=DE
```

[![复制代码](https://common.cnblogs.com/images/copycode.gif)](javascript:void(0);)

注意事项：

配置文件中的所有配置项开头必须要留一个空格符，否则会启动不了elasticsearch，这个是配置文件的格式





#### 2.3.3.配置完后重启elasticsearch

```
systemctl restart elasticsearch

systemctl status elasticsearc
```

#### 2.3.3.将配置写入运行中的elasticsearch

进入到elasticsearch安装目录中

```
cd /usr/share/elasticsearch/
```

运行如下命令将配置写入到elasticsearch中：

```
./plugins/search-guard-2/tools/sgadmin.sh -cn 集群名称 -h hostname -cd plugins/search-guard-2/sgconfig -ks plugins/search-guard-2/sgconfig/admin-keystore.jks -kspass password -ts plugins/search-guard-2/sgconfig/truststore.jks -tspass password -nhnv
```

hostname：指的是elasticsearch的elasticsearch.yml配置文件中 network.host 设置的值

根据上面的配置，输入的命令如下：

```
./plugins/search-guard-2/tools/sgadmin.sh -cn test -h 0.0.0.0 -cd plugins/search-guard-2/sgconfig -ks plugins/search-guard-2/sgconfig/test-keystore.jks -kspass 12345678 -ts plugins/search-guard-2/sgconfig/truststore.jks -tspass 12345678 -nhnv
```

需要注意：

```
如果提示没有操作权限，则必须先把hash.sh文件的权限开放
chmod -R 777 plugins/search-guard-2/tools/sgadmin.sh
```

的是这时候elasticsearch的服务必须是运行状态。如果插入配置失败，检查配置文件，比如前面提到的，生成客户端证书的时候dname的参数 必须与配置文件中searchguard.authcz.admin_dn:下的认证列表进行对应。

 

如成功写入配置，则会显示以下信息：

![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-3.png)



### 2.3.search guard 用户、角色、权限配置文件说明

search-guard中的用户权限管理

相关配置文件的介绍

searchguard 主要有5个配置文件在plugins/search-guard-2/sgconfig 下：

1、sg_config.yml：主配置文件不需要做改动。

2、sg_internal_users.yml：本地用户文件，定义用户密码以及对应的权限。

3、sg_roles.yml：权限配置文件

4、sg_roles_mapping.yml:定义用户的映射关系

5、sg_action_groups.yml：定义权限

 

修改内置用户密码，然后再运行一次search guard 配置写入命令。

1.则先用plugins/search-guard-2/tools/hash.sh生成hash字符串，生成密码：

```
cd /usr/share/elasticsearch/

plugins/search-guard-2/tools/hash.sh -p 123456
如果提示没有操作权限，则必须先把hash.sh文件的权限开放
chmod -R 777 plugins/search-guard-2/tools/hash.sh
```

获得哈希生成后的密码


![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-4.png)


2.将字符串复制到sg_internal_users.yml文件的对应用户密码位置，在密码下面记得写入原密码的提示，难保你那天忘记了。

```
vim plugins/search-guard-2/sgconfig/sg_internal_users.yml
```


![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-5.png)


3.添加用户权限

```
vim /usr/share/elasticsearch/plugins/search-guard-2/sgconfig/sg_roles_mapping.yml
```

在39行处的sg_all_access添加你新增的用户名，就获得所有权限了


![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-6.png)


4.重新写入配置

先回到elasticsearch的安装文件夹

```
cd /usr/share/elasticsearch/
./plugins/search-guard-2/tools/sgadmin.sh -cn test -h 0.0.0.0 -cd plugins/search-guard-2/sgconfig -ks plugins/search-guard-2/sgconfig/test-keystore.jks -kspass 12345678 -ts plugins/search-guard-2/sgconfig/truststore.jks -tspass 12345678 -nhnv
```

5.测试

```
curl -XGET "http://shifu:123456@127.0.0.1:9200"
```

如果密码设置成功则显示


![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-7.png)


现在每次想访问你网站的9200端口都必须要有搜索认证的保护了。


![searchguard](E:\git-workspace\note\images\bigdata\es\searchguard-install-8.png)





```shell
#带密码去创建索引
[hadoop@spark01 sgconfig]$ curl -X PUT 'http://admin:Admin_1234@172.16.110.173:9200/accounts/person/1' -d ' 
{
  "user": "张三",
  "title": "工程师",
  "desc": "数据库管理"
}' 
{"_index":"accounts","_type":"person","_id":"1","_version":1,"_shards":{"total":2,"successful":1,"failed":0},"created":true}[hadoop@spark01 sgconfig]$ 
[hadoop@spark01 sgconfig]$ 

```









## 3.客户端认证（访问通过search guard）

* java客户端访问：

  https://search-guard.com/searchguard-elasicsearch-transport-clients/

  https://blog.csdn.net/eff666/article/details/52916355

* http访问：

  <https://www.techcoil.com/blog/how-to-send-an-http-request-to-a-http-basic-authentication-endpoint-in-java-without-using-any-external-libraries/>



## 4.遇到的坑



各个文件的安装放置情况：

1. search-ssl
2. search-guard
3. es/config



|                                  | client-证书       | node-证书                                   | 信任-证书                      |
| -------------------------------- | ----------------- | ------------------------------------------- | ------------------------------ |
| plugins/search-guard-2/sgconfig/ | test-keystore.jks |                                             | truststore.jks                 |
| es/config                        |                   | node-x-keystore.jks (**x对应的是每个节点**) | truststore.jks （all节点一样） |



