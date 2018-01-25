---
title: es配置jmx监控
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---




转自：https://gist.github.com/icedfish/5cbaf3c7d93931634772


ES Official Doc : http://www.elasticsearch.org/guide/en/elasticsearch/reference/0.90/modules-jmx.html

#1.JMX support
## @elasticsearch.yml

```
jmx.create_connector: true
```

## @elasticsearch.in.sh

```
# ensures JMX accessible from outside world
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=9999"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=$(hostname -i)"
```
*Note: 用$(hostname -i)可以避免每台机器都要修改不同的配置文件，方便一个集群统一配置文件。*


如果启动时候出现 java.net.MalformedURLException: Local host name unknown:xxx ,那么确保修改/etc/hosts,确保自身机器名和自己的ip存在映射关系。

#2.Jstat support

##start jstatd

添加默认配置文件：  

```
cd $JAVA_HOME/bin && vi ./jstatd.all.policy```

```
grant codebase "file:${java.home}/../lib/tools.jar" {
	permission java.security.AllPermission;
};
```

需要时启动jstatd，无需重启ES：

```
cd $JAVA_HOME/bin && ./jstatd -J-Djava.security.policy=jstatd.all.policy -p 1099
```
