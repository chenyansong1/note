---
title: storm的ack机制
categories: storm   
toc: true  
tag: [storm]
---

# ack实现原理

我们知道，Storm保证发出的每条消息都能够得到完全处理，也就是说，对于从Spout发出的每个tuple，该tuple和其产生的所有tuple（整棵tuple树）都被成功处理。如果在应用设定的超时时间之内，这个tuple没有处理成功，则认为这个tuple处理失败了。tuple处理成功还是失败，Storm又是怎么知道的呢？

原来Storm中有一类叫Acker的task，它会对tuple树进行跟踪，并检测相应的spout tuple是否处理完成了。当一个tuple被创建时，不管是在Spout还是Bolt中创建，它都会被赋予一个tuple-id（随机生成的64位数字），这些tuple-id就是Acker用来跟踪每个spout tuple产生的tuple树的。如果一个spout tuple被完全处理了，它会给创建这个spout tuple的那个task发送一个成功消息，否则发送一个失败消息。


在Spout创建一个新tuple时，会生成一个root-id（也是随机的64位数字），并且这个root-id会传递给这个spout tuple所生成的tuple树中的每个tuple，因此有了这个root-id，我们就可以追踪这棵tuple树了。如果一个tuple被完全处理了，Storm就会调用Spout对应task的ack方法；否则调用Spout对应的fail方法。每个tuple都必须被ack或者fail，因为Storm追踪每个tuple需要占用内存，如果你不ack或fail每一个tuple， 那么最终会导致OOM（OutOfMemory）。


Acker跟踪算法的基本思想是：对于从Spout发射出来的每个spout tuple，Acker都保存了一个ack-val（校验值），初始值为0，每当tuple被创建或被ack，这些对应tuple的tuple-id（随机生成的64位整数）都会在某个时刻和保存的ack-val进行按位异或运算，并用异或运算的结果更新ack-val。如果每个spout tuple对应tuple树中的每个tuple都被成功处理，那最终的ack-val必然为0。为何呢？**因为在这个过程中，同一个tuple-id都会被异或两次，而相同值的异或运算结果为0，且异或运算满足结合律，如a^a=0，a^b^a^b=(a^a)^(b^b)=0**

<!--more-->

如图1所示，Acker为了实现自己的跟踪算法，它会维护这样一个数据结构：

`{root-id {:spout-task task-id :val ack-val :failed bool-val …}}`

其实就是一个Map，从上面这个Map中，我们知道，一个Acker存储了一个root-id到一对值的映射关系。这对值的第一个是创建这个tuple的task-id，当这个tuple处理完成进行ack的时候会用到。第二个是一个随机的64位的数字，即ack-val，ack-val表示整棵tuple树的状态，不管这棵tuple树多大，它只是简单地把这棵树上的相应的tuple-id做按位异或运算。因此即使一个spout tuple生成一棵有成千上万tuple的tuple树，Acker进行跟踪时也不会耗费太多的内存，对于每个spout tuple，Acker所需要的内存量都是恒定的20字节。这也是Storm的主要突破。

Acker跟踪算法需要不断更新ack-val，那ack-val又是怎么更新的呢？其实主要就是如下3个环节：
1）Spout创建新tuple的时候会给Acker发送消息。
2）Bolt中的tuple被ack的时候给Acker发送消息(程序中指定ack)
3）Acker根据接收到的消息做按位异或运算，更新自己的ack-val。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/ack/1.jpg)

当Spout创建了一个新的tuple时，会发送消息给Acker，消息的格式为[root-id,tmp-ack-val,task-id]，Acker会根据这个tmp-ack-val更新自己维护的Map中的ack-val值。

在Storm中，当一个spout tuple被完全处理后，会调用Spout中的task的ack或者fail方法，而且这个task必须是创建这个tuple的task。也就是说，如果一个Spout中启动了多个task，消息处理成功还是失败，最终都会通知Spout中发出tuple的那个对应的task。但Acker是如何知道每个spout tuple是由哪个task创建的呢？

从上面Spout给Acker发送的消息格式即可知道，Spout中创建一个新tuple时，它会创建这个tuple的task的task-id告诉Acker，于是当Acker发现一棵tuple树完成处理时，它知道给Spout中的哪个task发送成功消息，或者在处理失败时发送失败消息。


当一个tuple在Bolt中被ack的时候，它也会给Acker发送一个消息，告诉它这棵tuple树发生了什么样的变化。具体来说就是，它告诉Acker，在这棵tuple树中，我这个tuple已经完成了， 但我生成了这些新的tuple，并让Acker去跟踪一下它们。tuple被ack时发送给Acker的消息格式为[root-id,tmp-ack-val]，Acker会根据这个tmp-ack-val更新自己的ack-val值，当检测到ack-val为0时，就表示一个spout tuple被完全处理了。

在Topology中，Acker的个数我们是可以自己设置的。既然Acker可能有多个，那么当一个tuple需要被ack的时候，它怎么知道选择哪个Acker来发送这个消息呢？

Storm使用mod hashing将一个spout tuple的root-id映射到一个Acker，因为同一棵tuple树中的所有tuple都保存了相同的root-id，那么当一个tuple被ack的时候，它自然就知道应该给哪个Acker发送消息了。

下面我们结合一个具体的例子来揭开Acker实现机制的神秘面纱。这个例子的功能很简单，Spout从外界消息队列中获取句子，Bolt1接收从Spout中发送过来的句子并拆分成单词，其它Bolt（如下图中的Bolt2和Bolt3）会对相应的单词个数做统计。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/ack/2.jpg)


在图2-1中，Spout创建了一个新的tuple，于是它发消息给Acker，消息内容为[66,8,11]，其中66为这个tuple对应的root-id，8为这个tuple的tuple-id，11为创建这个tuple的task的task-id。Acker接收到这条消息后更新自己维护的数据结构，更新后为{66 {11 8}}（见图左下角），即root-id为66，task-id为11，ack-val为8。


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/ack/3.jpg)

在图2-2中，Bolt1将”good idea”这个输入tuple拆分成”good”和”idea”两个输出tuple，处理完后它给Acker发送消息[66,11]。我们知道66是root-id，但这个11是怎么计算出来的呢？在Storm的实现中，首先会将这个输入tuple生成的所有输出tuple的tuple-id进行异或运算，这里两个输出tuple的tuple-id分别为4和7，4 XOR 7 = 3；然后再将这个结果和输入tuple的tuple-id进行异或，输入tuple的tuple-id为8，即3 XOR 8 = 11。因此它发送Acker的消息为[66,11]。Acker接收到这个消息后更新自己的ack-val，8 XOR 11 = 3，更新后Acker维护的数据结构变为{66 {11 3}}。



![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/ack/4.jpg)

在图2-3中，Bolt2中不再生成新的tuple，处理完后它给Acker发送消息[66,4]。Acker接收到这个消息后更新自己的ack-val，3 XOR 4 = 7，更新后Acker维护的数据结构变为{66 {11 7}}。

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/ack/5.jpg)

在图2-4中，同样，Bolt3中不再生成新的tuple，处理完后它给Acker发送消息[66,7]。Acker接收到这个消息后更新自己的ack-val，7 XOR 7 = 0，更新后Acker维护的数据结构变为{66 {11 0}}，这个时候Acker发现ack-val变成0了，它就给Spout中对应的task发送一条成功消息，表明对应的spout tuple被完全处理了。


因为tuple-id是随机的64位数字，所以ack-val碰巧变成0（而不是因为所有创建的tuple都处理完成）的概率可以忽略不计。举个例子， 就算每秒发生10000个ack， 那么也需要50 000 000年才可能发生一个错误。并且就算发生了一个错误，也只有在这个tuple处理失败的时候才会造成数据丢失。


# 关于ack的api的使用
 启动类
```

package cn.itcast.storm;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
public class MyAckFailTopology {
    public static void main(String[] args) throws Exception {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("mySpout", new MySpout(), 1);
        topologyBuilder.setBolt("mybolt1", new MyBolt1(), 1).shuffleGrouping("mySpout");
        Config conf = new Config();
        String name = MyAckFailTopology.class.getSimpleName();
        
        if (args != null && args.length > 0) {//如果指定了参数，就用集群模式运行
            String nimbus = args[0];
            conf.put(Config.NIMBUS_HOST, nimbus);
            conf.setNumWorkers(1);
            StormSubmitter.submitTopologyWithProgressBar(name, conf, topologyBuilder.createTopology());
        } else {//没有指定参数，就用本地模式运行
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(name, conf, topologyBuilder.createTopology());
            Thread.sleep(60 * 60 * 1000);
            cluster.shutdown();
        }
    }
}
```
 Spout类
```
package cn.itcast.storm.ackfail;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
/**
 * Created by maoxiangyi on 2016/4/25.
 */
public class MySpout extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private Random rand;
    //用来存放Tuple  
    private Map<String,Values> buffer = new HashMap<>();
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
        rand = new Random();
    }
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }
    @Override
    public void nextTuple() {
        String[] sentences = new String[]{"the cow jumped over the moon",
                "the cow jumped over the moon",
                "the cow jumped over the moon",
                "the cow jumped over the moon", "the cow jumped over the moon"};
        String sentence = sentences[rand.nextInt(sentences.length)];
        String messageId = UUID.randomUUID().toString().replace("-", "");
        Values tuple = new Values(sentence);
        collector.emit(tuple, messageId);//在messageId不为null的情况下，如果Tuple被成功处理，会回调ack，如果失败则回调fail；如果messageId为null，那么将不会回调，无论Tuple是否处理成功
  
        //向buff中存入Tuple
        buffer.put(messageId,tuple);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * 如果Tuple发送成功，那么会调用ack方法
     * */
    @Override
    public void ack(Object msgId) {
        System.out.println("消息处理成功，id= " + msgId);
        //从buff中移除Tuple
        buffer.remove(msgId);
    }
    /*如果Tuple发送失败或者是bolt处理失败，那么会调用fail方法*/
    @Override
    public void fail(Object msgId) {
        System.out.println("消息处理失败，id= " + msgId);
        //取出buffer中的Tuple，重新发送
        Values tuple = buffer.get(msgId);
        collector.emit(tuple,msgId);
    }
}

```

 Bolt类
```
package cn.itcast.storm.ackfail;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Created by maoxiangyi on 2016/4/25.
 */
public class MyBolt1 extends BaseRichBolt {
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String sentence = input.getString(0);
        String[] words = sentence.split(" ");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                collector.emit(input, new Values(word));
            }
        }
        collector.ack(input);//一个Tuple处理完成，要通知Acker处理Tuple成功
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }
}

```


# 可靠性配置
对于ack机制是否使用，我们要考虑业务场景，比如：对大量的点击流日志，我们没有必要开启，因为即使一个Tuple丢失，也不会影响到统计的结果，所以此时不必要care他

有三种方法可以去掉消息的可靠性：
1. 将参数Config.TOPOLOGY_ACKERS设置为0，通过此方法，当Spout发送一个消息的时候，它的ack方法将立刻被调用；
2. Spout发送一个消息时，不指定此消息的messageID。当需要关闭特定消息可靠性的时候，可以使用此方法；
3. 最后，如果你不在意某个消息派生出来的子孙消息的可靠性，则此消息派生出来的子消息在发送时不要做锚定，即在emit方法中不指定输入消息。因为这些子孙消息没有被锚定在任何tuple tree中，因此他们的失败不会引起任何spout重新发送消息。



# Storm怎么处理重复的tuple？
因为Storm要保证tuple的可靠处理，当tuple处理失败或者超时的时候，spout会fail并重新发送该tuple，那么就会有tuple重复计算的问题。这个问题是很难解决的，storm也没有提供机制帮助你解决。一些可行的策略：
（1）不处理，这也算是种策略。因为实时计算通常并不要求很高的精确度，后续的批处理计算会更正实时计算的误差。
（2）使用第三方集中存储来过滤，比如利用mysql,memcached或者redis根据逻辑主键来去重。
（3）使用bloom filter做过滤，简单高效。

问题一：你们有没有想过如果某一个task节点处理的tuple一直失败，消息一直重发会怎么样？

我们都知道，spout作为消息的发送源，在没有收到该tuple来至左右bolt的返回信息前，是不会删除的，那么如果消息一直失败，就会导致spout节点存储的tuple数据越来越多，导致内存溢出。

问题二：有没有想过，如果该tuple的众多子tuple中，某一个子tuple处理failed了，但是另外的子tuple仍然会继续执行，如果子tuple都是执行数据存储操作，那么就算整个消息失败，那些生成的子tuple还是会成功执行而不会回滚的。

这个时候storm的原生api是无法支持这种事务性操作，我们可以使用storm提供的高级api-trident来做到（具体如何我不清楚，目前没有研究它，但是我可以它内部一定是根据分布式协议比如两阶段提交协议等）。向这种业务中要保证事务性功能，我们完全可以根据我们自身的业务来做到，比如这里的入库操作，我们先记录该消息是否已经入库的状态，再入库时查询状态来决定是否给予执行。

问题三：tuple的追踪并不一定要是从spout结点到最后一个bolt,只要是spout开始，可以在任意层次bolt停止追踪做出应答。

Acker task 组件来设置一个topology里面的acker的数量，默认值是一，如果你的topoogy里面的tuple比较多的话，那么请把acker的数量设置多一点，效率会更高一点


参考:
[storm的ack机制](http://www.cnblogs.com/intsmaze/p/5918087.html)
[Storm的ack机制在项目应用中的坑](http://www.cnblogs.com/intsmaze/p/5918087.html)










