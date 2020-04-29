---
title: Kafka-生产者消费者API举例1(转)
categories: kafka   
toc: true  
tag: [kafka]
---



[转自](http://www.cnblogs.com/gnivor/p/4934265.html)



接口 KafkaProperties.java

```
public interface KafkaProperties {
    final static String zkConnect = "192.168.1.160:2181";
    final static String groupId = "group1";
    final static String topic = "topic1";
    // final static String kafkaServerURL = "192.168.1.160";
    // final static int kafkaServerPort = 9092;
    // final static int kafkaProducerBufferSize = 64 * 1024;
    // final static int connectionTimeOut = 20000;
    // final static int reconnectInterval = 10000;
    // final static String topic2 = "topic2";
    // final static String topic3 = "topic3";
    // final static String clientId = "SimpleConsumerDemoClient";
}
```
 
<!--more-->



生产者 KafkaProducer.java

```
import java.util.Properties;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer extends Thread {
    private final kafka.javaapi.producer.Producer<Integer, String> producer;
    private final String topic;
    private final Properties props = new Properties();

    public KafkaProducer(String topic) {
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("metadata.broker.list", "192.168.1.160:9092"); // 配置kafka端口
        producer = new kafka.javaapi.producer.Producer<Integer, String>(new ProducerConfig(props));
        this.topic = topic;
    }

    @Override
    public void run() {
        int messageNo = 1;
        while (true) {
            String messageStr = new String("This is a message, number: " + messageNo);
            System.out.println("Send:" + messageStr);
            producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
            messageNo++;
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block                e.printStackTrace();
            }
        }
    }

}
```
 

消费者 KafkaConsumer.java

```
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


public class KafkaConsumer extends Thread {
    private final ConsumerConnector consumer;
    private final String topic;

    public KafkaConsumer(String topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
        this.topic = topic;
    }

    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", KafkaProperties.zkConnect); // zookeeper的地址
        props.put("group.id", KafkaProperties.groupId); // 组ID

        //zk连接超时
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        
        return new ConsumerConfig(props);
    }

    @Override
    public void run() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap     = consumer.createMessageStreams(topicCountMap);
        
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            System.out.println("receive：" + new String(it.next().message()));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
 

执行函数 KafkaConsumerProducerDemo.java

```
public class KafkaConsumerProducerDemo {
    public static void main(String[] args) {
        KafkaProducer producerThread = new KafkaProducer(KafkaProperties.topic);
        producerThread.start();

        KafkaConsumer consumerThread = new KafkaConsumer(KafkaProperties.topic);
        consumerThread.start();
    }
}
```