[TOC]

kafka消费者代码

```java
package com.bluedon.flume;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by landun on 2019/9/6.
 */
public class kafkaComsumerDemo {
    private static KafkaConsumer<String, String> consumer;


    static {
        Properties props = new Properties();
        props.put("bootstrap.servers", "soc60:9092,soc61:9092");
        props.put("group.id", "test-cys-id");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        consumer = new KafkaConsumer<String, String>(props);
        String topic = "tanzhanjiang";
        consumer.subscribe(Arrays.asList(topic));
    }

    public static void main(String[] args) {
        int messageNo = 1;
        try {
            for (;;) {
                ConsumerRecords<String, String> msgList = consumer.poll(1000);
                if(null!=msgList&&msgList.count()>0){
                    for (ConsumerRecord<String, String> record : msgList) {
                        //消费100条就打印 ,但打印的数据不一定是这个规律的
                        System.out.println(messageNo+"=======receive: key = " + record.key() + ", value = " + record.value()+" offset==="+record.offset());
                        //if(messageNo%100==0){
                        //    System.out.println(messageNo+"=======receive: key = " + record.key() + ", value = " + record.value()+" offset==="+record.offset());
                        //}
                        ////当消费了1000条就退出
                        //if(messageNo%1000==0){
                        //    break;
                        //}
                        //messageNo++;
                    }
                }else{
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }


    }
}
```



