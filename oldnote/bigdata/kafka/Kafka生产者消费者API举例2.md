---
title: Kafka生产者消费者API举例2
categories: kafka   
toc: true  
tag: [kafka]
---


下面是kafka生产者,消费者和自定义分区的代码示例

<!--more-->


# 1.生产者
```
package cn.itcast.storm.kafka.simple;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.UUID;

/**
 * 这是一个简单的Kafka producer代码
 * 包含两个功能:
 * 1、数据发送
 * 2、数据按照自定义的partition策略进行发送
 *
 *
 * KafkaSpout的类
 */
public class KafkaProducerSimple {
    public static void main(String[] args) {
        /**
         * 1、指定当前kafka producer生产的数据的目的地
         *  创建topic可以输入以下命令，在kafka集群的任一节点进行创建。
         *  bin/kafka-topics.sh --create --zookeeper zk01:2181 --replication-factor 1 --partitions 1 --topic test
         */
        String TOPIC = "orderMq";
        /**
         * 2、读取配置文件
         */
        Properties props = new Properties();
        /*
         * key.serializer.class默认为serializer.class
		 */
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        /*
		 * kafka broker对应的主机，格式为host1:port1,host2:port2，这样生产者知道向哪里生产
		 */
        props.put("metadata.broker.list", "kafka01:9092,kafka02:9092,kafka03:9092");
        /*
         * request.required.acks,设置发送数据是否需要服务端的反馈,有三个值0,1,-1
		 * 0，意味着producer永远不会等待一个来自broker的ack，这就是0.7版本的行为。
		 * 这个选项提供了最低的延迟，但是持久化的保证是最弱的，当server挂掉的时候会丢失一些数据。
		 * 1，意味着在leader replica已经接收到数据后，producer会得到一个ack。
		 * 这个选项提供了更好的持久性，因为在server确认请求成功处理后，client才会返回。
		 * 如果刚写到leader上，还没来得及复制leader就挂了，那么消息才可能会丢失。
		 * -1，意味着在所有的ISR都接收到数据后，producer才得到一个ack。
		 * 这个选项提供了最好的持久性，只要还有一个replica存活，那么数据就不会丢失
		 */
        props.put("request.required.acks", "1");
        /*
		 * 可选配置，如果不配置，则使用默认的partitioner partitioner.class
		 * 默认值：kafka.producer.DefaultPartitioner
		 * 用来把消息分到各个partition中，默认行为是对key进行hash。
		 */
        props.put("partitioner.class", "cn.itcast.storm.kafka.MyLogPartitioner");
//        props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
        /**
         * 3、通过配置文件，创建生产者
         */
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
        /**
         * 4、通过for循环生产数据
         */
        for (int messageNo = 1; messageNo < 100000; messageNo++) {
            String messageStr = new String(messageNo + "  用来配合自定义的MyLogPartitioner进行数据分发");

            /**
             * 5、调用producer的send方法发送数据
             * 注意：这里需要指定 partitionKey，用来配合自定义的MyLogPartitioner进行数据分发，自定义partition拿这个partitionKey来hash取模，参见下面的 “自定义分区类”
             */
            producer.send(new KeyedMessage<String, String>(TOPIC, messageNo + "", "appid" + UUID.randomUUID() + "itcast"));
        }
    }
}

```

  
# 2.消费者
```
 package cn.itcast.storm.kafka.simple;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumerSimple implements Runnable {
    public String title;
    public KafkaStream<byte[], byte[]> stream;
    public KafkaConsumerSimple(String title, KafkaStream<byte[], byte[]> stream) {
        this.title = title;
        this.stream = stream;
    }
    @Override
    public void run() {
        System.out.println("开始运行 " + title);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        /**
         * 不停地从stream读取新到来的消息，在等待新的消息时，hasNext()会阻塞
         * 如果调用 `ConsumerConnector#shutdown`，那么`hasNext`会返回false
         * */
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> data = it.next();
            String topic = data.topic();
            int partition = data.partition();
            long offset = data.offset();
            String msg = new String(data.message());
            System.out.println(String.format(
                    "Consumer: [%s],  Topic: [%s],  PartitionId: [%d], Offset: [%d], msg: [%s]",
                    title, topic, partition, offset, msg));
        }
        System.out.println(String.format("Consumer: [%s] exiting ...", title));
    }

    public static void main(String[] args) throws Exception{
        Properties props = new Properties();
		//组名
        props.put("group.id", "dashujujiagoushi");
        props.put("zookeeper.connect", "zk01:2181,zk02:2181,zk03:2181");
        props.put("auto.offset.reset", "largest");
        props.put("auto.commit.interval.ms", "1000");
        props.put("partition.assignment.strategy", "roundrobin");
        ConsumerConfig config = new ConsumerConfig(props);
        String topic1 = "orderMq";
        String topic2 = "paymentMq";
        //只要ConsumerConnector还在的话，consumer会一直等待新消息，不会自己退出
        ConsumerConnector consumerConn = Consumer.createJavaConsumerConnector(config);
        //定义一个map
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic1, 3);//指定在topic上创建多少个KafkaStream，而每个KafkaStream对应一个partition
        //Map<String, List<KafkaStream<byte[], byte[]>> 中String是topic， List<KafkaStream<byte[], byte[]>是对应的流
        Map<String, List<KafkaStream<byte[], byte[]>>> topicStreamsMap = consumerConn.createMessageStreams(topicCountMap);
        //取出 名字为 `orderMq`的topic 对应的分区
        List<KafkaStream<byte[], byte[]>> streams = topicStreamsMap.get(topic1);
        //创建一个容量为4的线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //创建consumer threads
        for (int i = 0; i < streams.size(); i++)
            executor.execute(new KafkaConsumerSimple("消费者" + (i + 1), streams.get(i)));//streams.get(i)表示每个分区对应的流
    }
}
```

# 3.自定义分区类
```
package cn.itcast.storm.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;


public class MyLogPartitioner implements Partitioner {
    private static Logger logger = Logger.getLogger(MyLogPartitioner.class);

    public MyLogPartitioner(VerifiableProperties props) {
    }

    public int partition(Object obj, int numPartitions) {
        return Integer.parseInt(obj.toString())%numPartitions;//hash取模之后，就可以确定消息发送到哪一个分区
//        return 1;
    }

}

```






