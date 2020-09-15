---
title: flume采集案例
categories: flume   
toc: true  
tag: [flume]

---

[TOC]




下面是flume的几种使用案例,更多的案例可以参见[flume官网](http://flume.apache.org/FlumeUserGuide.html)
<!--more-->

# 1.采集文件到HDFS

## 1.1.采集需求
比如业务系统使用log4j生成的日志，日志内容不断增加，需要把追加到日志文件中的数据实时采集到hdfs

根据需求，首先定义以下3大要素
* 采集源，即source——监控文件内容更新 :  exec  ‘tail -F file’
* 下沉目标，即sink——HDFS文件系统  :  hdfs sink
* Source和sink之间的传递通道——channel，可用file channel 也可以用 内存channel

&emsp;就是通过去执行（exec)一个命令(tail) 看文件的内容是否有更新，如果有就将更新的内容添加到hdfs中


## 1.2.配置文件
用tail命令获取数据，下沉到hdfs
vim ./conf/tail-hdfs.conf

```
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
#exec 指的是命令
# Describe/configure the source
a1.sources.r1.type = exec
#F根据文件名追中, f根据文件的nodeid追中
a1.sources.r1.command = tail -F /home/hadoop/log/test.log
a1.sources.r1.channels = c1
 
# Describe the sink
#下沉目标
a1.sinks.k1.type = hdfs
a1.sinks.k1.channel = c1
#指定目录, flum帮做目的替换
a1.sinks.k1.hdfs.path = /flume/events/%y-%m-%d/%H%M/                    #采集到hdfs中, 文件中的目录不用自己建的
#文件的命名, 前缀
a1.sinks.k1.hdfs.filePrefix = events-
 
#10 分钟就改目录
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = minute
 
#文件滚动之前的等待时间(秒)
a1.sinks.k1.hdfs.rollInterval = 3
 
#文件滚动的大小限制(bytes)
a1.sinks.k1.hdfs.rollSize = 500
 
#写入多少个event数据后滚动文件(事件个数)
a1.sinks.k1.hdfs.rollCount = 20
 
#5个事件就往里面写入
a1.sinks.k1.hdfs.batchSize = 5
 
#用本地时间格式化目录
a1.sinks.k1.hdfs.useLocalTimeStamp = true
 
#下沉后, 生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
a1.sinks.k1.hdfs.fileType = DataStream
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```


## 1.3.提供测试数据
创建目录，循环向文件添加内容
```
mkdir /home/hadoop/log
 
while true
do
echo 111111 >> /home/hadoop/log/test.log
sleep 0.5
done
 
```


## 1.4.启动命令
```
bin/flume-ng agent -c conf -f conf/tail-hdfs.conf -n a1
```


## 1.5.前端页面查看
在： master:50070, 文件目录: /flum/events/


# 2.采集目录到HDFS

## 2.1.采集需求
&emsp;采集需求：某服务器的某特定目录下，会不断产生新的文件(而不是以存在的文件中的内容的变化)，每当有新文件出现，就需要把文件采集到HDFS中去，根据需求，首先定义以下3大要素：
* 采集源，即source——监控文件目录 :  spooldir
* 下沉目标，即sink——HDFS文件系统  :  hdfs sink
* source和sink之间的传递通道——channel，可用file channel 也可以用内存channel


## 2.2.配置文件
```
#spooldir-hdfs.conf
#定义三大组件的名称
agent1.sources = source1
agent1.sinks = sink1
agent1.channels = channel1
 
# 配置source组件
#采集源使用的协议类型
agent1.sources.source1.type = spooldir                 
#监控的目录
agent1.sources.source1.spoolDir = /home/hadoop/logs/        
agent1.sources.source1.fileHeader = false
 
#配置拦截器
#agent1.sources.source1.interceptors = i1
#agent1.sources.source1.interceptors.i1.type = host
#agent1.sources.source1.interceptors.i1.hostHeader = hostname
 
# 配置sink组件
#下沉到hdfs
agent1.sinks.sink1.type = hdfs                
 #指定目录, flum帮做目的替换
agent1.sinks.sink1.hdfs.path =hdfs://hdp-node-01:9000/weblog/flume-collection/%y-%m-%d/%H-%M       
 #文件的命名, 前缀 
agent1.sinks.sink1.hdfs.filePrefix = access_log           
agent1.sinks.sink1.hdfs.maxOpenFiles = 5000
 #100个事件就往里面写入
agent1.sinks.sink1.hdfs.batchSize= 100       
  #下沉后, 生成的文件类型，默认是Sequencefile，可用DataStream，则为普通文本
agent1.sinks.sink1.hdfs.fileType = DataStream      
agent1.sinks.sink1.hdfs.writeFormat =Text

 #文件滚动的大小限制(bytes)
agent1.sinks.sink1.hdfs.rollSize = 102400           
#写入多少个event数据后滚动文件(事件个数)
agent1.sinks.sink1.hdfs.rollCount = 1000000        
#文件滚动（生成新文件）之前的等待时间(秒)
agent1.sinks.sink1.hdfs.rollInterval = 60            

#10 分钟就改目录
#agent1.sinks.sink1.hdfs.round = true
#agent1.sinks.sink1.hdfs.roundValue = 10
#agent1.sinks.sink1.hdfs.roundUnit = minute

#用本地时间格式化目录
agent1.sinks.sink1.hdfs.useLocalTimeStamp = true        


# Use a channel which buffers events in memory
#events in memory
agent1.channels.channel1.type = memory        
#event添加到通道中或者移出的允许时间
agent1.channels.channel1.keep-alive = 120               
#默认该通道中最大的可以存储的event数量 
agent1.channels.channel1.capacity = 500000            
#每次最大可以从source中拿到或者送到sink中的event数量
agent1.channels.channel1.transactionCapacity = 600         


# Bind the source and sink to the channel
agent1.sources.source1.channels = channel1
agent1.sinks.sink1.channel = channel1
```

## 2.3.启动命令
```
bin/flume-ng agent -c conf -f conf/spooldir-hdfs.conf  -n agent1
```

 注意：
1. 添加到监控目录中的文件，最后将会被改名（添加后缀：COMPLETED  ） 
2. 前端页面查看
在： master:50070, 文件目录: /weblog/flume-collection/



# 3.目录到控制台


## 3.1.配置文件

源：目录文件的变化
目标：console（控制台）

&emsp;使用spooldir协议去监听指定目录（/home/hadoop/flumespool）是否有变化，如果有就将变化打印到console中

```
#[root@hdp-node-01 flume]# cat ./conf/spooldir-hdfs.conf
#Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1
 
# Describe/configure the source
a1.sources.r1.type = spooldir
a1.sources.r1.spoolDir = /home/hadoop/flumespool
a1.sources.r1.fileHeader = true
 
# Describe the sink
a1.sinks.k1.type = logger
 
# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
 
# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1


```

## 3.2.启动
```
bin/flume-ng agent -c ./conf -f ./conf/spooldir-hdfs.conf -n a1 -Dflume.root.logger=INFO,console
```

## 3.3测试

```
[root@hdp-node-01 hadoop]# cat test.txt
aa_test
bb_test
cc_test

[root@hdp-node-01 hadoop]# mv test.txt /home/hadoop/flumespool/

'console打印信息'
2016-11-24 19:23:33,941 (pool-3-thread-1) [INFO - org.apache.flume.client.avro.ReliableSpoolingFileEventReader.rollCurrentFile(ReliableSpoolingFileEventReader.java:348)] Preparing to move file /home/hadoop/flumespool/test.txt to /home/hadoop/flumespool/test.txt.COMPLETED                #将mv 到目录的文件改名，以后缀：.COMPLETED  结尾

#从下面可以看出，向目录中添加了一个文件，实际上是将文件中的每一行当做一个Event，下沉到console中
2016-11-24 19:23:33,941 (SinkRunner-PollingRunner-'DefaultSinkProcessor') [INFO - org.apache.flume.'sink'.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 61 61 5F 74 65 73 74       'aa_test '}            
2016-11-24 19:23:33,942 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 62 62 5F 74 65 73 74      'bb_test '}
2016-11-24 19:23:33,942 (SinkRunner-PollingRunner-DefaultSinkProcessor) [INFO - org.apache.flume.sink.LoggerSink.process(LoggerSink.java:94)] Event: { headers:{file=/home/hadoop/flumespool/test.txt} body: 63 63 5F 74 65 73 74     ' cc_test' }

```

# Telnet到kafka

```

```


注意:

1. 如果向监听目录中添加一个文件，那么会将文件中的内容<font color=red>以行的形式</font>下沉到console中
2. 添加到监控目录中的文件，最后将会被改名（添加后缀：COMPLETED  ） 



# flume采集目录数据，流程如下

```shell
flume（采集目录） ---------> flume(avro 源)----syslog----->syslog-ng

```

## 采集器端配置文件


```shell
[root@bdsoc conf]# cat flume_client.conf
a1.sources = s1 s2
a1.channels = c1
a1.sinks = k1


#sources
a1.sources.s1.type = spooldir
#配置本地的采集目录
a1.sources.s1.spoolDir = /home/workspace/flume-test-dir/
a1.sources.s1.basenameHeader = true
a1.sources.s1.fileHeader = true
a1.sources.s1.deletePolicy = immediate
a1.sources.s1.recursiveDirectorySearch = true
a1.sources.s1.consumeOrder = random


a1.sources.s2.type = http                 
a1.sources.s2.port = 51400
a1.sources.s2.handler = com.bluedon.flume.HTTPSourceJsonHandler



#channels
a1.channels.c1.type = memory
a1.channels.c1.capacity = 100
a1.channels.c1.transactionCapacity = 100



# source 拦截器
a1.sources.s1.interceptors =i1
a1.sources.s1.interceptors.i1.type = regex_filter
a1.sources.s1.interceptors.i1.regex=(test3333)|(test444)|(test555)
a1.sources.s1.interceptors.i1.excludeEvents = true



#sinks
a1.sinks.k1.type = avro
#日志上报Ip
a1.sinks.k1.hostname=172.16.110.204
a1.sinks.k1.port=44444


#enable ssl
a1.sinks.k1.ssl=true
a1.sinks.k1.trust-all-certs=true
a1.sinks.k1.truststore=/home/workspace/apache-flume-1.8.0-bin/conf/truststore.jks
a1.sinks.k1.truststore-type=JKS
a1.sinks.k1.truststore-password=Admin_1234
a1.sinks.k1.compression-type=deflate


a1.sources.s2.channels = c1
a1.sources.s1.channels = c1
a1.sinks.k1.channel = c1


#启动
 ./bin/flume-ng agent -n a1 -f ./conf/flume_client.conf -Dflume.monitoring.type=http -Dflume.monitoring.port=34546  -Dflume.root.logger=INFO,console
```



## server端（日志接收端）

```shell
[root@bdsoc conf]# cat flume_server.conf 
a1.sources = s1
a1.channels = c1
a1.sinks = k1

#sources
a1.sources.s1.type = avro
a1.sources.s1.bind = 0.0.0.0
a1.sources.s1.port = 44444


#enable SSL
a1.sources.s1.ssl=true
a1.sources.s1.keystore=/home/workspace/apache-flume-1.8.0-bin/conf/keystore.jks
a1.sources.s1.keystore-password=Admin_1234
a1.sources.s1.keystore-type=JKS
a1.sources.s1.compression-type=deflate

#channels
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100


#sinks
#a1.sinks.k1.type = logger
a1.sinks.k1.type = com.bluedon.flume.MySyslogSink
a1.sinks.k1.reportIp = 172.16.110.204
a1.sinks.k1.reportPort = 514 


a1.sources.s1.channels = c1
a1.sinks.k1.channel = c1


#启动
 ./bin/flume-ng agent -n a1 -c conf -f ./conf/flume_server.conf -Dflume.root.logger=INFO,console -Dflume.monitoring.type=http -Dflume.monitoring.port=34546
```



## 自定义的syslogSink

```java
package com.bluedon.flume;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;
import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by landun on 2020/9/11.
 */
public class MySyslogSink extends AbstractSink implements Configurable {
    private static final Logger logger = LoggerFactory.getLogger(MySyslogSink.class);

    private String reportIp;
    private int reportPort;
    private UdpSyslogMessageSender messageSender;

    @Override
    public void configure(Context context) {
        reportIp = context.getString("reportIp");
        reportPort = context.getInteger("reportPort");
        if (StringUtils.isNotBlank(this.reportIp) || StringUtils.isNotBlank(Integer.toString(this.reportPort))) {
            logger.info("sink configure reportIp, reportPort");
        } else {
            logger.error("sink configure reportIp is empty...");
        }

    }


    @Override
    public void start(){

        logger.info("MySyslogSink start............");

        // Initialise sender
        messageSender = new UdpSyslogMessageSender();
        //messageSender.setDefaultMessageHostname("flume"); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("flumeAgent");
        messageSender.setDefaultFacility(Facility.SYSLOG);
        messageSender.setDefaultSeverity(Severity.INFORMATIONAL);
        messageSender.setSyslogServerHostname(reportIp);
        messageSender.setSyslogServerPort(reportPort);
        messageSender.setMessageFormat(MessageFormat.RFC_5424);

        logger.info("MySyslogSink start end ...............");

    }


    @Override
    public Status process() throws EventDeliveryException {
        logger.info("MySyslogSink process start ........... ");

        Status result = Status.READY;
        Channel channel = getChannel();
        Transaction transaction = null;
        Event event = null;

        try {
            transaction = channel.getTransaction();
            transaction.begin();

            event = channel.take();
            if(event!=null){
                byte[] eventBody = event.getBody();
                String syslogMsg = new String(eventBody);
                messageSender.sendMessage(syslogMsg);
            }

            logger.info("MySyslogSink process messageSender  start ........... ");

            transaction.commit();

        } catch (Exception ex) {
            String errorMsg = "Failed to publish events";
            logger.info("MySyslogSink send syslog exceptions");
            result = Status.BACKOFF;
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    logger.error("Transaction rollback failed", e);
                    throw Throwables.propagate(e);
                }
            }
            throw new EventDeliveryException(errorMsg, ex);
        } finally {
            if (transaction != null) {
                transaction.close();
            }
        }

        logger.info("MySyslogSink process end ........... ");

        return result;
    }


    public static void main(String[] args) {

        // Initialise sender
        UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultMessageHostname("myhostname"); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("myapp");
        messageSender.setDefaultFacility(Facility.USER);
        messageSender.setDefaultSeverity(Severity.INFORMATIONAL);
        messageSender.setSyslogServerHostname("127.0.0.1");
        messageSender.setSyslogServerPort(1234);
        messageSender.setMessageFormat(MessageFormat.RFC_5424);

        // send a Syslog message
        try {
            messageSender.sendMessage("This is a test message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```

## 自定义HTTPSourceJsonHandler

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluedon.flume;

import com.bluedon.util.PropertiesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.event.JSONEvent;
import org.apache.flume.source.http.HTTPBadRequestException;
import org.apache.flume.source.http.HTTPSourceHandler;
import org.apache.flume.source.http.JSONHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSONHandler for HTTPSource that accepts an array of events.
 *
 * This handler throws exception if the deserialization fails because of bad
 * format or any other reason.
 *
 * Each event must be encoded as a map with two key-value pairs. <p> 1. headers
 * - the key for this key-value pair is "headers". The value for this key is
 * another map, which represent the event headers. These headers are inserted
 * into the Flume event as is. <p> 2. body - The body is a string which
 * represents the body of the event. The key for this key-value pair is "body".
 * All key-value pairs are considered to be headers. An example: <p> [{"headers"
 * : {"a":"b", "c":"d"},"body": "random_body"}, {"headers" : {"e": "f"},"body":
 * "random_body2"}] <p> would be interpreted as the following two flume events:
 * <p> * Event with body: "random_body" (in UTF-8/UTF-16/UTF-32 encoded bytes)
 * and headers : (a:b, c:d) <p> *
 * Event with body: "random_body2" (in UTF-8/UTF-16/UTF-32 encoded bytes) and
 * headers : (e:f) <p>
 *
 * The charset of the body is read from the request and used. If no charset is
 * set in the request, then the charset is assumed to be JSON's default - UTF-8.
 * The JSON handler supports UTF-8, UTF-16 and UTF-32.
 *
 * To set the charset, the request must have content type specified as
 * "application/json; charset=UTF-8" (replace UTF-8 with UTF-16 or UTF-32 as
 * required).
 *
 * One way to create an event in the format expected by this handler, is to
 * use {@linkplain JSONEvent} and use {@linkplain Gson} to create the JSON
 * string using the
 * {@linkplain Gson#toJson(java.lang.Object, java.lang.reflect.Type) }
 * method. The type token to pass as the 2nd argument of this method
 * for list of events can be created by: <p>
 * {@code
 * Type type = new TypeToken<List<JSONEvent>>() {}.getType();
 * }
 */

public class HTTPSourceJsonHandler implements HTTPSourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPSourceJsonHandler.class);
    private final Type listType = new TypeToken<List<JSONEvent>>() {}.getType();
    private final Gson gson;

    public HTTPSourceJsonHandler() {
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Event> getEvents(HttpServletRequest request) throws Exception {
        BufferedReader reader = request.getReader();
        String charset = request.getCharacterEncoding();
        //UTF-8 is default for JSON. If no charset is specified, UTF-8 is to
        //be assumed.
        if (charset == null) {
            LOG.debug("Charset is null, default charset of UTF-8 will be used.");
            charset = "UTF-8";
        } else if (!(charset.equalsIgnoreCase("utf-8")
                || charset.equalsIgnoreCase("utf-16")
                || charset.equalsIgnoreCase("utf-32"))) {
            LOG.error("Unsupported character set in request {}. "
                    + "JSON handler supports UTF-8, "
                    + "UTF-16 and UTF-32 only.", charset);
            throw new UnsupportedCharsetException("JSON handler supports UTF-8, "
                    + "UTF-16 and UTF-32 only.");
        }

        /*
         * Gson throws Exception if the data is not parseable to JSON.
         * Need not catch it since the source will catch it and return error.
         */
        List<Event> eventList = new ArrayList<Event>(0);
        try {
            eventList = gson.fromJson(reader, listType);
            //-----------------form me-----------------------------------
            LOG.info("http rev msg----->"+eventList);

        } catch (JsonSyntaxException ex) {
            throw new HTTPBadRequestException("Request has invalid JSON Syntax.", ex);
        }
//        for (Event e : eventList) {
//            ((JSONEvent) e).setCharset(charset);
//        }
        //return getSimpleEvents(eventList);
        Event event = eventList.get(0);
        byte[] body = event.getBody(); //过滤关键字
        Map<String, String> headers = event.getHeaders();
        String reportIp = headers.get("reportIp");//上报ip
        LOG.info("keyword:{}",body);
        LOG.info("reportIp:{}",reportIp);
        String keyword = new String(body);
        String[] split = keyword.split(",");
        String keyRegex = null;
        if(split.length>0){
            keyRegex = "("+StringUtils.join(split,")|(")+")";
        }else{
            keyRegex = "(test)";
        }
        LOG.info("keyRegex:{}",keyRegex);
        updateFlumeConf("a1.sources.s1.interceptors.i1.regex",keyRegex);
        updateFlumeConf("a1.sinks.k1.hostname",reportIp);
        updateFlumeConf("a1.sinks.k1.port","44444");
        LOG.info("update conf success & restart flume....");
        //return null;

        return getSimpleEvents(null);

        //----------------------------------------------------
    }

    @Override
    public void configure(Context context) {
    }

    private List<Event> getSimpleEvents(List<Event> events) {
        List<Event> newEvents = new ArrayList<Event>(events.size());
        for (Event e:events) {
            newEvents.add(EventBuilder.withBody(e.getBody(), e.getHeaders()));
        }
        return newEvents;
    }


    /**
     * 更新flume配置文件
     * @param
     */
    public static void updateFlumeConf(String key,String value){

        Properties prop = new Properties();

        FileInputStream fis = null;
        OutputStream fos = null;
        Properties flumeConf = new PropertiesUtil();
        try {
            prop.load(MyNetcatUdpSource.class.getResourceAsStream("/filter-ip.properties"));
            //获取flume配置文件绝对路径
            String flumeConfPath = prop.getProperty("flumeConfPath");

            //更新regex
            fis = new FileInputStream(flumeConfPath);
            flumeConf.load(fis);// 将属性文件流装载到Properties对象中
            flumeConf.setProperty(key,value);
            fos = new FileOutputStream(flumeConfPath);
            flumeConf.store(fos,null);
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fos!=null){
                    fos.close();
                }
                if (fis!=null){
                    fis.close();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        String keyword = "aaaa,bbbb,cccc,ddd";
        String[] split = keyword.split(",");
        String keyRegex = null;
        if(split.length>0){
            keyRegex = "("+StringUtils.join(split,")|(")+")";
        }else{
            keyRegex = "(攻击)";
        }

        System.out.println(keyRegex);
    }
}
```





