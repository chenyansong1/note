---
title: 关于Storm Stream grouping的分组策略
categories: storm   
toc: true  
tag: [storm]
---





目前，Storm Streaming Grouping支持如下几种类型：

* Shuffle Grouping：随机分组，跨多个Bolt的Task，能够随机使得每个Bolt的Task接收到大致相同数目的Tuple，但是Tuple不重复
* Fields Grouping：根据指定的Field进行分组 ，同一个Field的值一定会被发射到同一个Task上
* Partial Key Grouping：与Fields grouping 类似，根据指定的Field的一部分进行分组分发，能够很好地实现Load balance，将Tuple发送给下游的Bolt对应的Task，特别是在存在数据倾斜的场景，使用 Partial Key grouping能够更好地提高资源利用率
* All Grouping：所有Bolt的Task都接收同一个Tuple（这里有复制的含义）
* Global Grouping：所有的流都指向一个Bolt的同一个Task（也就是Task ID最小的）
* None Grouping：不需要关心Stream如何分组，等价于Shuffle grouping
* Direct Grouping：由Tupe的生产者来决定发送给下游的哪一个Bolt的Task ，这个要在实际开发编写Bolt代码的逻辑中进行精确控制
* Local or Shuffle Grouping：如果目标Bolt有1个或多个Task都在同一个Worker进程对应的JVM实例中，则Tuple只发送给这些Task


另外，Storm还提供了用户自定义Streaming Grouping接口，如果上述Streaming Grouping都无法满足实际业务需求，也可以自己实现，只需要实现backtype.storm.grouping.CustomStreamGrouping接口，该接口定义了如下方法：

`List<Integer> chooseTasks(int taskId, List<Object> values)`
上面几种Streaming Group的内置实现中，最常用的应该是Shuffle Grouping、Fields Grouping、Direct Grouping这三种，使用其它的也能满足特定的应用需求。