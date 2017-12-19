---
title: 自己实现一个类似storm数据执行的框架
categories: storm   
toc: true  
tag: [storm]
---




# 1.storm框架的简单原理



	1,任务分配
		----->Task总数
		----->可用worker数量
	2,通信机制
		----->去zk获取每个组件的任务
		----->启动不同服务
			  nimbus，手动 java -server xxx.jar main-class
			  superv，手动 java -server xxx.jar main-class
			  worker，supervisor启动------java -server xxx.jar main-class(main(Worker.mk_work(new Worker())))
			  Task, Worker启动Task--------Jvm--->Task.mk_Task()
	3,心跳机制
		thread1------tag=true
		thread2------>tag=true------tag=false
		thread1------>tag=true------tag=false------tag=true
		thread2------>tag=true------tag=false------tag=true-----tag=false
	4、任务执行(数据流)
		spout.nextTuple(tuple)----streamGrouping---> incomingQueue-------->bolt1.exeute(tuple)-----streamGrouping----> incomingQueue-------->bolt2.exeute(tuple)
		
	[实现数据执行的框架]	
		spout-----线程1
		incomingQueue------queue
		bolt1-----线程2
		incomingQueue------queue
		bolt2-----线程3
		
		需要技术：
			线程池----->Exeutes.newFixPool(3)
			队列------->ArrayBolckingQueue(1000)
		
		伪代码：
			
			MyStrom{
				main(){
				//1、配置一个线程池
				//2、向线程池中提交任务
					spoutOutPutQueue = new ArrayBolckingQueue(1000)
					submit(new MySpout(spoutOutPutQueue))------collector.emit(tuple)------spoutOutPutQueue
					bolt1OutPutQueue = new ArrayBolckingQueue(1000)
					submit(new MyBolt1(spoutOutPutQueue,bolt1OutPutQueue))------>spoutOutPutQueue---->bolt1.execute(),collector.emit(tuple)------bolt1OutPutQueue
					submit(new MyBolt1(bolt1OutPutQueue))------>spoutOutPutQueue---->bolt1.execute()	
				}
			}



<!--more-->


# 2.尝试自己实现一个类似storm数据执行的框架
 流程图

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/user_define_storm.png)

 

 Spout 类
```
//##################    MySpout 类##############################
class MySpout extends Thread {
    private MyStorm myStorm;
    public MySpout(MyStorm myStorm) {
        this.myStorm = myStorm;
    }
    @Override
    public void run() {
        //storm框架在循环调用spout的netxTuple方法
        while (true) {
            myStorm.nextTuple();
            try {
                this.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}  
```

 BoltSplit
```
//##################    MyBoltSplit 类##############################
class MyBoltSplit extends Thread {
    private MyStorm myStorm;
    @Override
    public void run() {
        while (true) {
            try {
                String sentence = (String) myStorm.getSentenceQueue().take();//从队列中取Tuple
                myStorm.split(sentence);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public MyBoltSplit(MyStorm myStorm) {
        this.myStorm = myStorm;
    }
}  
```

 BoltWordCount
```
//##################    MyBoltWordCount 类##############################
class MyBoltWordCount extends Thread {
    private MyStorm myStorm;
    @Override
    public void run() {
        while (true) {
            try {
                //从队列中取Tuple
                String word = (String) myStorm.getWordQueue().take();
                myStorm.wordcounter(word);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public MyBoltWordCount(MyStorm myStorm) {
        this.myStorm = myStorm;
    }
}  
```


 启动类

```
public class MyStorm {
    private Random random = new Random();
    private BlockingQueue sentenceQueue = new ArrayBlockingQueue(50000);
    private BlockingQueue wordQueue = new ArrayBlockingQueue(50000);
    // 用来保存最后计算的结果key=单词，value=单词个数
    Map<String, Integer> counters = new HashMap<String, Integer>();
    //用来发送句子
    public void nextTuple() {
        String[] sentences = new String[]{"the cow jumped over the moon",
                "an apple a day keeps the doctor away",
                "four score and seven years ago",
                "snow white and the seven dwarfs", "i am at two with nature"};
        String sentence = sentences[random.nextInt(sentences.length)];
        try {
            sentenceQueue.put(sentence);
            System.out.println("send sentence:" + sentence);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //用来切割句子
    public void split(String sentence) {
        System.out.println("resv sentence" + sentence);
        String[] words = sentence.split(" ");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                //collector.emit()
                wordQueue.add(word);
                System.out.println("split word:" + word);
            }
        }
    }
    //用来计算单词
    public void wordcounter(String word) {
        if (!counters.containsKey(word)) {
            counters.put(word, 1);
        } else {
            Integer c = counters.get(word) + 1;
            counters.put(word, c);
        }
        System.out.println("print map:" + counters);
    }
    public static void main(String[] args) {
        //线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        MyStorm myStorm = new MyStorm();
        //发射句子到sentenceQuequ
        executorService.submit(new MySpout(myStorm));
        //接受一个句子，并将句子切割
        executorService.submit(new MyBoltSplit(myStorm));
        //接受一个单词，并进行据算
        executorService.submit(new MyBoltWordCount(myStorm));
    }
    public BlockingQueue getSentenceQueue() {
        return sentenceQueue;
    }
    public void setSentenceQueue(BlockingQueue sentenceQueue) {
        this.sentenceQueue = sentenceQueue;
    }
    public BlockingQueue getWordQueue() {
        return wordQueue;
    }
    public void setWordQueue(BlockingQueue wordQueue) {
        this.wordQueue = wordQueue;
    }
}  
```


