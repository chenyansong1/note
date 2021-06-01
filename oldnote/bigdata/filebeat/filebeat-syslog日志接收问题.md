[toc]

filebeat接收日志过程中拿不到对端的IP



# 测试情况

在本地测试，如果是使用filebeat的配置为syslog，那么可以接收到发送过来的日志，数据也能正常output，但是如果数据不是标准的RFC3164格式的，那么会报如下的错误

![image-20210601154713462](../../..\images\bigdata\filebeat\image-20210601154713462.png)

output到kafka的数据如下：**此时是拿不到发送方的IP**

![image-20210601154810584](F:\git_note\note\images\bigdata\filebeat\image-20210601154810584.png)

# filebeat-syslog日志接收问题

目前filebeat只是支持syslog的格式是RFC3164(这个是一个老的格式)，新的格式是RFC5424，而RFC5424只是在beta版中支持

![](../../..\images\bigdata\filebeat\image-20210601154104673.png)





# 使用udp替换syslog

```shell
#vim filebeat_udp.yml 
filebeat.inputs:

- type: udp
  max_message_size: 10KiB
  host: "0.0.0.0:8080"
```

接收格式如下

![](../../..\images\bigdata\filebeat\image-20210601155118967.png)

