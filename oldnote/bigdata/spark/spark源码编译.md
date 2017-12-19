---
title: spark源码编译
categories: spark  
tags: [spark]
---


```
#解压源码包
tar -zxvf spark-1.6.1.tgz -C /usr/local/src/
cd /usr/local/src/spark-1.6.1/
 
#设置内存2G
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512m"
 
#编译前安装一些压缩解压缩工具
yum install -y snappy snappy-devel bzip2 bzip2-devel lzo lzo-devel lzop openssl openssl-devel
 
 
#需要使用maven,所以要安装maven
 
#仅仅是为了编译源码, 编译后可以导入idea中
mvn clean package -Phadoop-2.6 -Dhadoop.version=2.6.4 -Phive -Phive-thriftserver -Pyarn -DskipTests
 
#编译后并打包, 打包后可以丢到生产环境了
./make-distribution.sh --tgz -Phadoop-2.6 -Dhadoop.version=2.6.4 -Phive -Phive-thriftserver -Pyarn -DskipTests
 
 
 
 

```





