---
title: storm的wordcount程序现实及执行流程
categories: storm   
toc: true  
tag: [storm]
---


# 1.代码结构示意图

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount/1.png)

<!--more-->

# 2.代码实现
## 2.1.主程序WordCountTopologMain
```
package cn.itcast.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;



/**
 * Created by maoxiangyi on 2016/4/27.
 */
public class WordCountTopologMain {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        //1、准备一个TopologyBuilder
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("mySpout",new MySpout(),2);
        topologyBuilder.setBolt("mybolt1",new MySplitBolt(),2).shuffleGrouping("mySpout");
        /*
         * 在MySplitBolt类中声明发送的Tuple都是<word,num>的形式，
         *  public void declareOutputFields(OutputFieldsDeclarer declarer) {
        		declarer.declare(new Fields("word","num"));
    		}
    		
         * 而：topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).fieldsGrouping("mybolt1", new Fields("word"));
         * 中设置由mybolt1发送到mybolt2的Tuple是按照Tuple<word,num>中的word来进行分组的，即：word字段相同的发送到一个task中
         * 有如下的Tuple，如：
         * Tuple1=<"aaa",1>
         * Tuple2=<"abc",2>
         * Tuple3=<"dbe",2>
         * Tuple4=<"aaa",5>
         * 那么Tuple1和Tuple4将发送到一个task中作为同组，因为他们的word字段都是"aaa"
        */
        topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).fieldsGrouping("mybolt1", new Fields("word"));
        /**
         * 这里用fieldsGrouping分组的原因是对相同的单词，将发射到同一个count-bolt线程中，那么统计才会有效，
         * 而shuffleGrouping（随机）可能将同一个单词发送到不同的count-bolt中，那么没法实现单词的统计
         */
        //topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).shuffleGrouping("mybolt1");
        //  config.setNumWorkers(2);


        //2、创建一个configuration，用来指定当前topology 需要的worker的数量
        Config config =  new Config();
        config.setNumWorkers(2);

        //3、提交任务  -----两种模式 本地模式和集群模式
//        StormSubmitter.submitTopology("mywordcount",config,topologyBuilder.createTopology());
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mywordcount",config,topologyBuilder.createTopology());
    }
}

```
## 2.2.MySpout
```
package cn.itcast.storm;
import java.util.Arrays;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
/**
 * Created by maoxiangyi on 2016/4/27.
 */
public class MySpout extends BaseRichSpout {
    SpoutOutputCollector collector;
    //初始化方法
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }
    //storm 框架在 while(true) 调用nextTuple方法
    public void nextTuple() {
        collector.emit(new Values("i am lilei love hanmeimei"));
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
       //声明发往下一个blot的字段标识
       declarer.declare(new Fields("love"));//Fields就是一个数组：new Fields("love", "aaa", "bbb")
       
       /*
            public Fields(String... fields) {
                this(Arrays.asList(fields));
            }
            
            //这里的Values也是一个数组，其元素和Fields中的元素相对应
            collector.emit(new Values("i am lilei love hanmeimei", "message-aaa", "message-bbb"));
            
       */
    }
}
```

## 2.3. MySplitBolt（切分） 
```
package cn.itcast.storm;
import java.util.ArrayList;
import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
/**
 * Created by maoxiangyi on 2016/4/27.
 */
public class MySplitBolt extends BaseRichBolt {
    OutputCollector collector;
    //初始化方法
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }
    // 被storm框架 while(true) 循环调用  传入参数tuple
    public void execute(Tuple input) {
        /*    
         public String getString(int i) {
             return (String) values.get(i);//values是一个List
         }
        */
        String line = input.getString(0);
        /*
            input.getStringByField("love");那么只是拿到的Values数组中Fields.index["love"]下标的内容
            
            public String getStringByField(String field) {
                return (String) values.get(fieldIndex(field));
              }
         */
        
        String[] arrWords = line.split(" ");
        for (String word:arrWords){
            collector.emit(new Values(word,1));//Values extends ArrayList
        }
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word","num"));
        /*
            这里声明的字段word和num和上面collector.emit(new Values(word,1));中的对应
            这样在下一个bolt中可以使用input.getStringByField("word");去get不同的部分
         */
        
    }
}
```
 
## 2.4.MyCountBolt（统计）
```
package cn.itcast.storm;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by maoxiangyi on 2016/4/27.
 */
public class MyCountBolt extends BaseRichBolt {
    OutputCollector collector;
    Map<String, Integer> map = new HashMap<String, Integer>();
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }
    public void execute(Tuple input) {
        String word = input.getString(0);
        Integer num = input.getInteger(1);
        /*
             因为已经知道上一个blot中发送过来的list中第一个字段是String，而list中第二个字段是Internet，所以我们可以直接取
             但是也可以使用:
        input.getStringByField("word");
        input.getIntegerByField("num");
        */
        System.out.println(Thread.currentThread().getId() + "    word:"+word);
        if (map.containsKey(word)){
            Integer count = map.get(word);
            map.put(word,count + num);
        }else {
            map.put(word,num);
        }
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //不輸出
    }
}

```



# 3.打包上传，并在Linux上执行
```
#在分布式上执行
storm jar myStormApp.jar cn.itcast.storm.WordCountTooplogMain

#可以在浏览器上查看UI界面中是否有topology
```




# 4.随机分组和字段分组的区别
```
/*
在主程序中，从mybolt1出来的Tuple是通过字段分组的形式到达mybolt2中的， 这里用fieldsGrouping分组的原因是对相同的单词，按照值的哈希取模，将发射到同一个count-bolt线程中，
那么对全局的统计才会有效，而shuffleGrouping（随机）可能将同一个单词发送到不同的count-bolt中，那么没法实现全局单词的统计
*/
public class WordCountTopologMain {
    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        //1、准备一个TopologyBuilder
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("mySpout",new MySpout(),2);
        topologyBuilder.setBolt("mybolt1",new MySplitBolt(),2).shuffleGrouping("mySpout");
        
        topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).fieldsGrouping("mybolt1", new Fields("word"));//fieldsGrouping:字段分组

        //topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).shuffleGrouping("mybolt1");//随机分组
        //  config.setNumWorkers(2);
    
        //2、创建一个configuration，用来指定当前topology 需要的worker的数量
        Config config =  new Config();
        config.setNumWorkers(2);
        //3、提交任务  -----两种模式 本地模式和集群模式
//        StormSubmitter.submitTopology("mywordcount",config,topologyBuilder.createTopology());
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("mywordcount",config,topologyBuilder.createTopology());
    }
} 
```


# 5.wordcount执行流程图示

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/wordcount/2.png)

说明:
```
config.setNumWorkers(2)
	//所以会分配2个worker

topologyBuilder.setSpout("mySpout",new MySpout(),2);
	//分配2个spout
topologyBuilder.setBolt("mybolt1",new MySplitBolt(),2).shuffleGrouping("mySpout");
	//分配2个splitBolt

topologyBuilder.setBolt("mybolt2",new MyCountBolt(),4).fieldsGrouping("mybolt1", new Fields("word"));
	//分配4个countBolt

```
由上知:
&emsp;&emsp;总的task=(spout+splitBolt+countBolt)=8
则
&emsp;&emsp;每个worker: 8/2=4 个task

再来看每个task是在worker中如何分配的:
在分配spout和bolt中有一个轮询的策略,如:myspout-0分配给worker1,myspout-1分配给worker2,这样轮询


由spout发送到splitBolt的过程是随机的,因为使用的是shuffleGrouping分组,而由splitBolt发送到CountBolt的过程则是按字段分组:
splitBolt发送到CountBolt的Tuple是:
&emsp;&emsp;collector.emit(new Values(word,1))
word会hash取模:
&emsp;&emsp;word.hashcode%4 来决定发送到哪一个CountBolt中

