kafka程序指定servers的问题



```
Properties props = new Properties();
       
        //props.put("bootstrap.servers", "soc60:9092");// 如果只是指定一个节点，那么打的数据只是到了这一个节点上，所以需要指定所有的节点(这个kafka设计的不太合理)
        props.put("bootstrap.servers", "soc60:9092,soc61:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer(props);
        for(int i = 0; i < 10; i++)
            producer.send(new ProducerRecord<String, String>("events-topic", Integer.toString(i), "test from chen out test size 10"+Integer.toString(i)));

        producer.close();
```

