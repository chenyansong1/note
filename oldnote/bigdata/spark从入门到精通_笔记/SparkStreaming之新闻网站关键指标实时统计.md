---
title: SparkStreaming之新闻网站关键指标实时统计
categories: spark  
tags: [spark]
---



# 构造模拟的数据(kafka的生产者)
```
package cn.spark.study.streaming.upgrade.news;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * 访问日志Kafka Producer
 * @author Administrator
 *
 */
public class AccessProducer extends Thread {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Random random = new Random();
	private static String[] sections = new String[] {"country", "international", "sport", "entertainment", "movie", "carton", "tv-show", "technology", "internet", "car"};
	private static int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	private static String date;
	
	private Producer<Integer, String> producer;
	private String topic;
	
	public AccessProducer(String topic) {
		this.topic = topic;
		producer = new Producer<Integer, String>(createProducerConfig());  
		date = sdf.format(new Date());  
	}
	
	private ProducerConfig createProducerConfig() {
		Properties props = new Properties();
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "192.168.0.103:9092,192.168.0.104:9092");
		return new ProducerConfig(props);  
	}
	
	public void run() {
		int counter = 0;

		while(true) {
			for(int i = 0; i < 100; i++) {
				String log = null;
				
				if(arr[random.nextInt(10)] == 1) {
					log = getRegisterLog();
				} else {
					log = getAccessLog();
				}
				
				producer.send(new KeyedMessage<Integer, String>(topic, log));
				
				counter++;
				if(counter == 100) {
					counter = 0;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}  
				}
			}
		}
	}
	
	private static String getAccessLog() {
		StringBuffer buffer = new StringBuffer("");  
		
		// 生成时间戳
		long timestamp = new Date().getTime();
		
		// 生成随机userid（默认1000注册用户，每天1/10的访客是未注册用户）
		Long userid = 0L;
		
		int newOldUser = arr[random.nextInt(10)];
		if(newOldUser == 1) {
			userid = null;
		} else {
			userid = (long) random.nextInt(1000);
		}
		
		// 生成随机pageid（总共1k个页面）
		Long pageid = (long) random.nextInt(1000);  
		
		// 生成随机版块（总共10个版块）
		String section = sections[random.nextInt(10)]; 
		
		// 生成固定的行为，view
		String action = "view"; 
		
		return buffer.append(date).append(" ")  
				.append(timestamp).append(" ")
				.append(userid).append(" ")
				.append(pageid).append(" ")
				.append(section).append(" ")
				.append(action).toString();
	}
	
	private static String getRegisterLog() {
		StringBuffer buffer = new StringBuffer("");
		
		// 生成时间戳
		long timestamp = new Date().getTime();
		
		// 新用户都是userid为null
		Long userid = null;

		// 生成随机pageid，都是null
		Long pageid = null;  
		
		// 生成随机版块，都是null
		String section = null; 
		
		// 生成固定的行为，view
		String action = "register"; 
		
		return buffer.append(date).append(" ")  
				.append(timestamp).append(" ")
				.append(userid).append(" ")
				.append(pageid).append(" ")
				.append(section).append(" ")
				.append(action).toString();
	}
	
	public static void main(String[] args) {
		AccessProducer producer = new AccessProducer("news-access");    
		producer.start();
	}
	
}


```

# 创建topic,进行"消费"测试

```
kafka-topics.sh --zookeeper 192.168.0.103:2181,192.168.0.104:2181 --topic news-access --replication-factor 1 --partitions 1 --create
kafka-console-consumer.sh --zookeeper 192.168.0.103:2181,192.168.0.104:2181 --topic news-access --from-beginning
```


消费到的数据的格式为:
```
2016-02-22 1442343424234234 125 115 car view
2016-02-22 1442343424234235 null null null register
```








# 总体的代码
```
package cn.spark.study.streaming.upgrade.news;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kafka.serializer.StringDecoder;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

/**
 * 新闻网站关键指标实时统计Spark应用程序
 * @author Administrator
 *
 */
public class NewsRealtimeStatSpark {

	public static void main(String[] args) throws Exception {
		// 创建Spark上下文
		SparkConf conf = new SparkConf()
				.setMaster("local[2]")
				.setAppName("NewsRealtimeStatSpark");  
		JavaStreamingContext jssc = new JavaStreamingContext(
				conf, Durations.seconds(5));  
		
		// 创建输入DStream
		Map<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", 
				"192.168.0.103:9092,192.168.0.104:9092");
		
		Set<String> topics = new HashSet<String>();
		topics.add("news-access");  
		
		JavaPairInputDStream<String, String> lines = KafkaUtils.createDirectStream(
				jssc, 
				String.class, 
				String.class, 
				StringDecoder.class, 
				StringDecoder.class, 
				kafkaParams, 
				topics);
		
		// 过滤出访问日志
		JavaPairDStream<String, String> accessDStream = lines.filter(
				
				new Function<Tuple2<String,String>, Boolean>() {
			
					private static final long serialVersionUID = 1L;
		
					@Override
					public Boolean call(Tuple2<String, String> tuple) throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" ");  
						
						String action = logSplited[5];
						if("view".equals(action)) {
							return true;
						} else {
							return false;
						}
					}
					
				});
		
		// 统计第一个指标：实时页面pv(页面pv的实时统计,每10秒内各个页面被访问的pv)
		calculatePagePv(accessDStream);  
		// 统计第二个指标：实时页面uv
		calculatePageUv(accessDStream); 
		// 统计第三个指标：实时注册用户数
		calculateRegisterCount(lines);  
		// 统计第四个指标：实时用户跳出数:注册用户只是访问了一个页面就退出了,那么就属于跳出用户
		calculateUserJumpCount(accessDStream);
		// 统计第五个指标：实时版块pv
		calcualteSectionPv(accessDStream);  
		
		jssc.start();
		jssc.awaitTermination();
		jssc.close();
	}
	
	/**
	 * 计算页面pv
	 * @param accessDStream
	 */
	private static void calculatePagePv(JavaPairDStream<String, String> accessDStream) {
		JavaPairDStream<Long, Long> pageidDStream = accessDStream.mapToPair(
				
				new PairFunction<Tuple2<String,String>, Long, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Tuple2<Long, Long> call(Tuple2<String, String> tuple)
							throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" "); 
						
						Long pageid = Long.valueOf(logSplited[3]);  
						
						return new Tuple2<Long, Long>(pageid, 1L);  
					}
					
				});
		
		JavaPairDStream<Long, Long> pagePvDStream = pageidDStream.reduceByKey(
				
				new Function2<Long, Long, Long>() {
			
					private static final long serialVersionUID = 1L;
		
					@Override
					public Long call(Long v1, Long v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		pagePvDStream.print();  
		
		// 在计算出每10秒钟的页面pv之后，其实在真实项目中，应该持久化
		// 到mysql，或redis中，对每个页面的pv进行累加(或者取原来的值,然后加上此时的pv,再生成一条新的记录,那么就可以做成pv随时间变化的曲线图)
		// javaee系统，就可以从mysql或redis中，读取page pv实时变化的数据，以及曲线图
	}
	
	/**
	 * 计算页面uv
	 * @param <U>
	 * @param accessDStream
	 */
	private static <U> void calculatePageUv(JavaPairDStream<String, String> accessDStream) {
		JavaDStream<String> pageidUseridDStream = accessDStream.map(
				
				new Function<Tuple2<String,String>, String>() {

					private static final long serialVersionUID = 1L;

					@Override
					public String call(Tuple2<String, String> tuple) throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" "); 
						
						Long pageid = Long.valueOf(logSplited[3]);  
						Long userid = Long.valueOf("null".equalsIgnoreCase(logSplited[2]) ? "-1" : logSplited[2]);  
						
						return pageid + "_" + userid;
					}
					
				});
		// 使用transform操作,对DStream中的每个rdd进行distinct操作		
		JavaDStream<String> distinctPageidUseridDStream = pageidUseridDStream.transform(
				
				new Function<JavaRDD<String>, JavaRDD<String>>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public JavaRDD<String> call(JavaRDD<String> rdd) throws Exception {
						return rdd.distinct();
					}
					
				});
		
		JavaPairDStream<Long, Long> pageidDStream = distinctPageidUseridDStream.mapToPair(
				
				new PairFunction<String, Long, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Tuple2<Long, Long> call(String str) throws Exception {
						String[] splited = str.split("_");  
						Long pageid = Long.valueOf(splited[0]);  
						return new Tuple2<Long, Long>(pageid, 1L);   
					}
					
				});
		
		JavaPairDStream<Long, Long> pageUvDStream = pageidDStream.reduceByKey(
				
				new Function2<Long, Long, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Long call(Long v1, Long v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		pageUvDStream.print();
	}
	
	/**
	 * 计算实时注册用户数
	 * @param lines
	 */
	private static void calculateRegisterCount(JavaPairInputDStream<String, String> lines) {
		JavaPairDStream<String, String> registerDStream = lines.filter(
				
				new Function<Tuple2<String,String>, Boolean>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Boolean call(Tuple2<String, String> tuple) throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" ");  
						
						String action = logSplited[5];
						if("register".equals(action)) {
							return true;
						} else {
							return false;
						}
					}
					
				});
		
		JavaDStream<Long> registerCountDStream = registerDStream.count();
		
		registerCountDStream.print();  
		
		// 每次统计完一个最近10秒的数据之后，不是打印出来
		// 去存储（mysql、redis、hbase），选用哪一种主要看你的公司提供的环境，以及你的看实时报表的用户以及并发数量，包括你的数据量
		// 如果是一般的展示效果，就选用mysql就可以
		// 如果是需要超高并发的展示，比如QPS 1w来看实时报表，那么建议用redis、memcached
		// 如果是数据量特别大，建议用hbase
		
		// 每次从存储中，查询注册数量，最近一次插入的记录，比如上一次是10秒前
		// 然后将当前记录与上一次的记录累加，然后往存储中插入一条新记录，就是最新的一条数据
		// 然后javaee系统在展示的时候，可以比如查看最近半小时内的注册用户数量变化的曲线图
		// 查看一周内，每天的注册用户数量的变化曲线图（每天就取最后一条数据，就是每天的最终数据）
	}

	/**
	 * 计算用户跳出数量
	 * @param accessDStream
	 */
	private static void calculateUserJumpCount(JavaPairDStream<String, String> accessDStream) {
		JavaPairDStream<Long, Long> useridDStream = accessDStream.mapToPair(
				
				new PairFunction<Tuple2<String,String>, Long, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Tuple2<Long, Long> call(Tuple2<String, String> tuple)
							throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" ");  
						Long userid = Long.valueOf("null".equalsIgnoreCase(logSplited[2]) ? "-1" : logSplited[2]);    
						return new Tuple2<Long, Long>(userid, 1L); 
					}
					
				});
		
		JavaPairDStream<Long, Long> useridCountDStream = useridDStream.reduceByKey(
				
				new Function2<Long, Long, Long>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Long call(Long v1, Long v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		JavaPairDStream<Long, Long> jumpUserDStream = useridCountDStream.filter(
				
				new Function<Tuple2<Long,Long>, Boolean>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Boolean call(Tuple2<Long, Long> tuple) throws Exception {
						if(tuple._2 == 1) {
							return true;
						} else {
							return false;
						}
					}
					
				});
		
		JavaDStream<Long> jumpUserCountDStream = jumpUserDStream.count();
		
		jumpUserCountDStream.print();  
	}
	
	/**
	 * 版块实时pv
	 * @param accessDStream
	 */
	private static void calcualteSectionPv(JavaPairDStream<String, String> accessDStream) {
		JavaPairDStream<String, Long> sectionDStream = accessDStream.mapToPair(
				
				new PairFunction<Tuple2<String,String>, String, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Tuple2<String, Long> call(Tuple2<String, String> tuple)
							throws Exception {
						String log = tuple._2;
						String[] logSplited = log.split(" ");  
						
						String section = logSplited[4];
						
						return new Tuple2<String, Long>(section, 1L);  
					}
					
				});
		
		JavaPairDStream<String, Long> sectionPvDStream = sectionDStream.reduceByKey(
				
				new Function2<Long, Long, Long>() {

					private static final long serialVersionUID = 1L;
		
					@Override
					public Long call(Long v1, Long v2) throws Exception {
						return v1 + v2;
					}
					
				});
		
		sectionPvDStream.print();
	}
	
}

```
