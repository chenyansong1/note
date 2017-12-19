因为kafka中的数据默认只是保留7天，所以kafka中保存的数据用offset来表示的话，那么将会是一个范围


下面是Java实现

```
package com.aipai.apm.test_kafka_offset;

import kafka.common.TopicAndPartition;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.KafkaCluster;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka.OffsetRange;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConversions;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.Media.print;

/** * KafkaOffsetExample * * @author Shuai YUAN * @date 2015/10/28 */
public class TestKafkaOffset2Zk {

    private static KafkaCluster kafkaCluster = null;

    private static HashMap<String, String> kafkaParam = new HashMap<String, String>();

    private static Broadcast<HashMap<String, String>> kafkaParamBroadcast = null;

    private static scala.collection.immutable.Set<String> immutableTopics = null;

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.ERROR);
        System.setProperty("hadoop.home.dir", "D:\\down_soft\\hadoop-common-2.2.0-bin-master");


        SparkConf sparkConf = new SparkConf().setAppName("tachyon-test-consumer");
        sparkConf.setMaster("local[3]");
        Set<String> topicSet = new HashSet<String>();
        topicSet.add("first");

        kafkaParam.put("metadata.broker.list", "hdp-node-01:9092,hdp-node-02:9092,hdp-node-03:9092");
        kafkaParam.put("group.id", "com.xueba207.test");

        // transform java Map to scala immutable.map
        scala.collection.mutable.Map<String, String> testMap = JavaConversions.mapAsScalaMap(kafkaParam);
        scala.collection.immutable.Map<String, String> scalaKafkaParam =
                testMap.toMap(new Predef.$less$colon$less<Tuple2<String, String>, Tuple2<String, String>>() {
                    public Tuple2<String, String> apply(Tuple2<String, String> v1) {
                        return v1;
                    }
                });

        // init KafkaCluster
        kafkaCluster = new KafkaCluster(scalaKafkaParam);

        scala.collection.mutable.Set<String> mutableTopics = JavaConversions.asScalaSet(topicSet);
        immutableTopics = mutableTopics.toSet();
        scala.collection.immutable.Set<TopicAndPartition> topicAndPartitionSet2 = kafkaCluster.getPartitions(immutableTopics).right().get();

        // kafka direct stream 初始化时使用的offset数据
        Map<TopicAndPartition, Long> consumerOffsetsLong = new HashMap<TopicAndPartition, Long>();

        // 没有保存offset时（该group首次消费时）, 各个partition offset 默认为0
        if (kafkaCluster.getConsumerOffsets(kafkaParam.get("group.id"), topicAndPartitionSet2).isLeft()) {

            System.out.println(kafkaCluster.getConsumerOffsets(kafkaParam.get("group.id"), topicAndPartitionSet2).left().get());

            Set<TopicAndPartition> topicAndPartitionSet1 = JavaConversions.setAsJavaSet(topicAndPartitionSet2);

            for (TopicAndPartition topicAndPartition : topicAndPartitionSet1) {
				//这里会有问题，因为kafka默认是保存7天的数据，那么kafka中的offset就不可能是从0开始的，所以这里设置为0，将会出现越界
                consumerOffsetsLong.put(topicAndPartition, 0L);
            }

        } else {// offset已存在, 使用保存的offset

            scala.collection.immutable.Map<TopicAndPartition, Object> consumerOffsetsTemp = kafkaCluster.getConsumerOffsets(kafkaParam.get("group.id"), topicAndPartitionSet2).right().get();

            Map<TopicAndPartition, Object> consumerOffsets = JavaConversions.mapAsJavaMap(consumerOffsetsTemp);

            Set<TopicAndPartition> topicAndPartitionSet1 = JavaConversions.setAsJavaSet(topicAndPartitionSet2);

            for (TopicAndPartition topicAndPartition : topicAndPartitionSet1) {
                Long offset = (Long)consumerOffsets.get(topicAndPartition);
                consumerOffsetsLong.put(topicAndPartition, offset);
            }

        }

        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(10000));
        kafkaParamBroadcast = jssc.sparkContext().broadcast(kafkaParam);

        // create direct stream
        JavaInputDStream<String> message = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                String.class,
                kafkaParam,
                consumerOffsetsLong,
                new Function<MessageAndMetadata<String, String>, String>() {
                    public String call(MessageAndMetadata<String, String> v1) throws Exception {
                        return v1.message();
                    }
                }
        );

        // 得到rdd各个分区对应的offset, 并保存在offsetRanges中
        final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<OffsetRange[]>();
        JavaDStream<String> javaDStream = message.transform(new Function<JavaRDD<String>, JavaRDD<String>>() {
            public JavaRDD<String> call(JavaRDD<String> rdd) throws Exception {
                OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
                offsetRanges.set(offsets);
                return rdd;
            }
        });

        // output
        javaDStream.foreachRDD(new Function<JavaRDD<String>, Void>() {

            public Void call(JavaRDD<String> rdd) throws Exception {
                if (rdd.isEmpty()) return null;

                //处理rdd数据
                System.out.println("rdd数据：================" + rdd.collect() + "===================");


                for (OffsetRange o : offsetRanges.get()) {

                    // 封装topic.partition 与 offset对应关系 java Map
                    TopicAndPartition topicAndPartition = new TopicAndPartition(o.topic(), o.partition());
                    Map<TopicAndPartition, Object> topicAndPartitionObjectMap = new HashMap<TopicAndPartition, Object>();
                    topicAndPartitionObjectMap.put(topicAndPartition, o.untilOffset());

                    // 转换java map to scala immutable.map
                    scala.collection.mutable.Map<TopicAndPartition, Object> testMap = JavaConversions.mapAsScalaMap(topicAndPartitionObjectMap);
                    scala.collection.immutable.Map<TopicAndPartition, Object> scalatopicAndPartitionObjectMap =
                            testMap.toMap(new Predef.$less$colon$less<Tuple2<TopicAndPartition, Object>, Tuple2<TopicAndPartition, Object>>() {
                                public Tuple2<TopicAndPartition, Object> apply(Tuple2<TopicAndPartition, Object> v1) {
                                    return v1;
                                }
                            });

                    // 更新offset到kafkaCluster
                    kafkaCluster.setConsumerOffsets(kafkaParamBroadcast.getValue().get("group.id"), scalatopicAndPartitionObjectMap);

// System.out.println(
// o.topic() + " " + o.partition() + " " + o.fromOffset() + " " + o.untilOffset()
// );
                }
                return null;
            }
        });

        jssc.start();
        jssc.awaitTermination();
    }

}

```


解决将初始offset=0，造成Range越界的情况

kafka offset判断


在使用Spark streaming读取kafka数据时，为了避免数据丢失，我们会在zookeeper中保存kafka的topic对应的partition的offset信息（每次执行成功后，才更新zk中的offset信息）；从而保证执行失败的下一轮，可以从特定的offset开始读。

实现方式类似下面文章所示：

http://blog.csdn.net/rongyongfeikai2/article/details/49784785
但，kafka的topic是可能会被删除的，而更糟糕的情况是，用户又新建了一个相同名字的topic。这是，zk中保存的offset信息会已经不再准确了，此时就需要与kafka的broker保存的offset信息进行比对，从而把zk中的offset信息修正成功。

实现方式如下：

1.用一个类来保存特定topic的leader信息，以及partition的offset信息

```
import java.io.Serializable;
import java.util.HashMap;

/**
 * @function:kafka记录类
 */
public class KafkaTopicOffset implements Serializable{
    private String topicName;
    private HashMap<Integer,Long> offsetList;
    private HashMap<Integer,String> leaderList;

    public KafkaTopicOffset(String topicName){
        this.topicName = topicName;
        this.offsetList = new HashMap<Integer,Long>();
        this.leaderList = new HashMap<Integer, String>();
    }

    public String getTopicName() {
        return topicName;
    }

    public HashMap<Integer, Long> getOffsetList() {
        return offsetList;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setOffsetList(HashMap<Integer, Long> offsetList) {
        this.offsetList = offsetList;
    }

    public HashMap<Integer, String> getLeaderList() {
        return leaderList;
    }

    public void setLeaderList(HashMap<Integer, String> leaderList) {
        this.leaderList = leaderList;
    }

    public String toString(){
        return "topic:"+topicName+",offsetList:"+this.offsetList+",leaderList:"+this.leaderList;
    }
}
```

2.从kafka的broker中得到topic-partition的offset信息（主要是利用SimpleConsumer发送相应的Request）

```
import java.io.Serializable;
import java.util.*;
import com.nsfocus.bsaips.common.Constant;
import com.nsfocus.bsaips.model.KafkaTopicOffset;
import kafka.javaapi.OffsetResponse;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.PartitionMetadata;

/**
 * @function:kafka相关工具类
 */
public class KafkaUtil implements Serializable {
    private static KafkaUtil kafkaUtil = null;

    private KafkaUtil(){}

    public static KafkaUtil getInstance(){
        if(kafkaUtil == null){
            kafkaUtil = new KafkaUtil();
        }
        return kafkaUtil;
    }

    private String[] getIpsFromBrokerList(String brokerlist){
        StringBuilder sb = new StringBuilder();
        String[] brokers = brokerlist.split(",");
        for(int i=0;i<brokers.length;i++){
            brokers[i] = brokers[i].split(":")[0];
        }
        return brokers;
    }

    private Map<String,Integer> getPortFromBrokerList(String brokerlist){
        Map<String,Integer> map = new HashMap<String,Integer>();
        String[] brokers = brokerlist.split(",");
        for(String item:brokers){
            String[] itemArr = item.split(":");
            if(itemArr.length > 1){
                map.put(itemArr[0],Integer.parseInt(itemArr[1]));
            }
        }
        return map;
    }

    public KafkaTopicOffset topicMetadataRequest(String brokerlist,String topic){
        List<String> topics = Collections.singletonList(topic);
        TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(topics);

        KafkaTopicOffset kafkaTopicOffset = new KafkaTopicOffset(topic);
        String[] seeds = getIpsFromBrokerList(brokerlist);
        Map<String,Integer> portMap = getPortFromBrokerList(brokerlist);

        for(int i=0;i<seeds.length;i++){
            SimpleConsumer consumer = null;
            try{
                consumer = new SimpleConsumer(seeds[i],
                        portMap.get(seeds[i]),
                        Constant.TIMEOUT,
                        Constant.BUFFERSIZE,
                        Constant.groupId);
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(topicMetadataRequest);
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        kafkaTopicOffset.getLeaderList().put(part.partitionId(),part.leader().host());
                        kafkaTopicOffset.getOffsetList().put(part.partitionId(),0L);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if(consumer != null){
                    consumer.close();
                }
            }
        }

        return kafkaTopicOffset;
    }

    public KafkaTopicOffset getLastOffsetByTopic(String brokerlist,String topic){
        KafkaTopicOffset kafkaTopicOffset = topicMetadataRequest(brokerlist, topic);
        String[] seeds = getIpsFromBrokerList(brokerlist);
        Map<String,Integer> portMap = getPortFromBrokerList(brokerlist);

        for(int i=0;i<seeds.length;i++){
            SimpleConsumer consumer = null;
            Iterator iterator = kafkaTopicOffset.getOffsetList().entrySet().iterator();

            try{
                consumer = new SimpleConsumer(seeds[i],
                        portMap.get(seeds[i]),
                        Constant.TIMEOUT,
                        Constant.BUFFERSIZE,
                        Constant.groupId);

                while(iterator.hasNext()){
                    Map.Entry<Integer,Long> entry = (Map.Entry<Integer, Long>) iterator.next();
                    int partitonId = entry.getKey();

                    if(!kafkaTopicOffset.getLeaderList().get(partitonId).equals(seeds[i])){
                        continue;
                    }

                    TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
                            partitonId);
                    Map<TopicAndPartition,PartitionOffsetRequestInfo> requestInfo =
                            new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();

                    requestInfo.put(topicAndPartition,
                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(),1)
                    );
                    kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                            requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
                            Constant.groupId);
                    OffsetResponse response = consumer.getOffsetsBefore(request);
                    long[] offsets = response.offsets(topic,partitonId);
                    if(offsets.length > 0){
                        kafkaTopicOffset.getOffsetList().put(partitonId,offsets[0]);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally{
                if(consumer != null){
                    consumer.close();
                }
            }
        }

        return kafkaTopicOffset;
    }

    public Map<String,KafkaTopicOffset> getKafkaOffsetByTopicList(String brokerList,List<String> topics){
        Map<String,KafkaTopicOffset> map = new HashMap<String,KafkaTopicOffset>();
        for(int i=0;i<topics.size();i++){
            map.put(topics.get(i),getLastOffsetByTopic(brokerList, topics.get(i)));
        }
        return map;
    }

    public static void main(String[] args){
        try{
              System.out.println(KafkaUtil.getInstance().getKafkaOffsetByTopicList(
                      ConfigUtil.getInstance().getKafkaConf().get("brokerlist"),
                      Arrays.asList(new String[]{"pj_test_tmp","test"})));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
```

3.再在KafkaCluster从zk中得到offset信息时，与从broker得到的offset信息中比对（假定调用KafkaUtil的getKafkaOffsetByTopicList得到的返回值放在了offsetMap中）：


![](/images/bigdata/spark/offset_manage.jpg)



参考：

http://www.voidcn.com/blog/xueba207/article/p-5958412.html

http://www.voidcn.com/blog/rongyongfeikai2/article/p-4860904.html

http://www.voidcn.com/blog/rongyongfeikai2/article/p-5035375.html

