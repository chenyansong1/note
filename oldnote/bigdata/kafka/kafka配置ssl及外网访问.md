[TOC]



# 1.ssl生成步骤



生成ssl的步骤，大致流程如下：



![](E:\git-workspace\note\images\bigdata\kafka\kafka-ssl.png)



# 2.ssl生成脚本



这里准备了一个一键生成脚本

```shell
#!/bin/bash

################################## 设置环境变量   ##############################
BASE_DIR=/mnt/disk/huxitest # SSL各种生成文件的基础路径
CERT_OUTPUT_PATH="$BASE_DIR/certificates" # 证书文件生成路径
PASSWORD=kafka1234567 # 密码
KEY_STORE="$CERT_OUTPUT_PATH/kafka.keystore"   # Kafka keystore文件路径
TRUST_STORE="$CERT_OUTPUT_PATH/kafka.truststore" # Kafka truststore文件路径
KEY_PASSWORD=$PASSWORD # keystore的key密码
STORE_PASSWORD=$PASSWORD # keystore的store密码
TRUST_KEY_PASSWORD=$PASSWORD  # truststore的key密码
TRUST_STORE_PASSWORD=$PASSWORD # truststore的store密码
CLUSTER_NAME=test-cluster	# 指定别名
CERT_AUTH_FILE="$CERT_OUTPUT_PATH/ca-cert" # CA证书文件路径
CLUSTER_CERT_FILE="$CERT_OUTPUT_PATH/${CLUSTER_NAME}-cert" # 集群证书文件路径
DAYS_VALID=365 # key有效期
DNAME="CN=Xi Hu, OU=YourDept, O=YourCompany, L=Beijing, ST=Beijing, C=CN"  # distinguished name
##############################################################################

mkdir -p $CERT_OUTPUT_PATH

echo "1. 创建集群证书到keystore......"
keytool -keystore $KEY_STORE -alias $CLUSTER_NAME -validity $DAYS_VALID -genkey -keyalg RSA \
-storepass $STORE_PASSWORD -keypass $KEY_PASSWORD -dname "$DNAME"

echo "2. 创建CA......"
openssl req -new -x509 -keyout $CERT_OUTPUT_PATH/ca-key -out "$CERT_AUTH_FILE" -days "$DAYS_VALID" \
-passin pass:"$PASSWORD" -passout pass:"$PASSWORD" \
-subj "/C=CN/ST=Beijing/L=Beijing/O=YourCompany/CN=Xi Hu"

echo "3. 导入CA文件到truststore......"
keytool -keystore "$TRUST_STORE" -alias CARoot \
-import -file "$CERT_AUTH_FILE" -storepass "$TRUST_STORE_PASSWORD" -keypass "$TRUST_KEY_PASS" -noprompt

echo "4. 从key store中导出集群证书......"
keytool -keystore "$KEY_STORE" -alias "$CLUSTER_NAME" -certreq -file "$CLUSTER_CERT_FILE" -storepass "$STORE_PASSWORD" -keypass "$KEY_PASSWORD" -noprompt

echo "5. 签发证书......"
openssl x509 -req -CA "$CERT_AUTH_FILE" -CAkey $CERT_OUTPUT_PATH/ca-key -in "$CLUSTER_CERT_FILE" \
-out "${CLUSTER_CERT_FILE}-signed" \
-days "$DAYS_VALID" -CAcreateserial -passin pass:"$PASSWORD"

echo "6. 导入CA文件到keystore......"
keytool -keystore "$KEY_STORE" -alias CARoot -import -file "$CERT_AUTH_FILE" -storepass "$STORE_PASSWORD" \
 -keypass "$KEY_PASSWORD" -noprompt

echo "7. 导入已签发证书到keystore......"
keytool -keystore "$KEY_STORE" -alias "${CLUSTER_NAME}" -import -file "${CLUSTER_CERT_FILE}-signed" \
 -storepass "$STORE_PASSWORD" -keypass "$KEY_PASSWORD" -noprompt

```



下面，我们在Kafka broker机器上运行setup_ssl_for_servers.sh脚本，结果输出如下： 




![](E:\git-workspace\note\images\bigdata\kafka\kafka-ssl-2.png)



如上图可见，setup_ssl_for_servers.sh脚本执行成功了，现在去到对应的目录下去检查生成的文件列表：

- ca-cert：CA文件，**不要把该文件拷贝到别的broker机器上！**
- test-cluster-cert-signed：CA已签发的Kafka证书文件，**不要把该文件拷贝到别的broker机器上！**
- test-cluster-cert：Kafka认证文件（包含公钥和私钥），**不要把该文件拷贝到别的broker机器上！**
- kafka.keystore：Kafka的keystore文件，**所有clients端和broker机器上都需要！**
- kafka.truststore：Kafka的truststore文件，**所有clients端和broker机器上都需要！**



# 3.配置clients端参数



## 3.1.生产者

本例中我们使用console-producer和console-consumer脚本来从Mac笔记本上给位于阿里云机器上的broker收发消息 ，下面首先演示如何配置producer。首先，我们创建一个producer.config文件，里面的内容如下：

```shell
bootstrap.servers=kafka1:9093   # 指定9093端口，即使用SSL监听器端口

security.protocol=SSL

ssl.truststore.location=/Users/huxi/Downloads/kafka.truststore # 指定truststore文件

ssl.truststore.password=kafka1234567   

ssl.keystore.password=kafka1234567

ssl.keystore.location=/Users/huxi/Downloads/kafka.keystore # 指定keystore文件

```



保存之后，我们运行console-producer来生产消息：

```
$ bin/kafka-console-producer.sh --broker-list kafka1:9093 --topic test --producer.config producer.config 
>hello, world
>hello, Kafka
>a test message
......
```



## 3.2.消费者

同样地，我们创建一个consumer.config文件，内容如下：

```shell
security.protocol=SSL

group.id=test-group

ssl.truststore.location=/Users/huxi/Downloads/kafka.truststore # 指定truststore文件 

ssl.truststore.password=kafka1234567 

ssl.keystore.password=kafka1234567

ssl.keystore.location=/Users/huxi/Downloads/kafka.keystore # 指定keystore文件

```



保存之后，我们运行console-consumer来消费消息：

```
$ bin/kafka-console-consumer.sh --bootstrap-server kafka1:9093 --topic test --from-beginning --consumer.config consumer.config 
hello, world
hello, Kafka
a test message
```






参考：

https://www.cnblogs.com/huxi2b/p/7427815.html



