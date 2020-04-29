---
title: storm内部通信机制2(转)
categories: storm   
toc: true  
tag: [storm]
---


转自:[Apache Storm内部原理分析](http://shiyanjun.cn/archives/1472.html)

一个Topology提交到Storm集群上运行，具体的处理流程非常微妙，有点复杂。首先，我们通过要点的方式来概要地说明：

* 每个Executor可能存在一个Incoming Queue和一个Outgoing Queue，这两个队列都是使用的LMAX Disruptor Queue（可以通过相关资料来了解）
* 两个LMAX Disruptor Queue的上游和下游，都会有相关线程去存储/取出Tuple
* 每个Executor可能存在一个Send Thread，用来将处理完成生成的新的Tuple放到属于该Executor的Outgoing Queue队列
* 每个Executor一定存在一个Main Thread，用来处理衔接Spout Task/Bolt Task与前后的Incoming Queue、Outgoing Queue
* 每个Worker进程内部可能存在一个Receive Thread，用来接收由上游Worker中的Transfer Thread发送过来的Tuple，在一个Worker内部Receive Thread是被多个Executor共享的
* 每个Worker进程内部可能存在一个Outgoing Queue，用来存放需要跨Worker传输的Tuple（其内部的Transfer Thread会从该队列中读取Tuple进行传输）
* 每个Worker进程内部可能存在一个Transfer Thread，用来将需要在Worker之间传输的Tuple发送到下游的Worker内




上面，很多地方我使用了“可能”，实际上大部分情况下是这样的，注意了解即可。下面，我们根据Spout Task/Bolt Task运行时分布的不同情况，分别阐述如下：

<!--more-->


# Spout Task在Executor内部运行
Spout Task和Bolt Task运行时在Executor中运行有一点不同，如果Spout Task所在的同一个Executor中没有Bolt Task，则该Executor中只有一个Outgoing Queue用来存放将要传输到Bolt Task的队列，因为Spout Task需要从一个给定的数据源连续不断地读入数据而形成流。在一个Executor中，Spout Task及其相关组件的执行流程，如下图所示：

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-spout-task-in-executor.png)

上图所描述的数据流处理流程，如下所示：

* Spout Task从外部数据源读取消息或事件，在实现的Topology中的Spout中调用nextTuple()方法，并以Tuple对象的格式emit()读取到的数据
* Main Thread处理输入的Tuple，然后放入到属于该Executor的Outgoing Queue中
* 属于该Executor的Send Thread从Outgoing Queue中读取Tuple，并传输到下游的一个或多个Bolt Task去处理




# Bolt Task在Executor内部运行

前面说过，Bolt Task运行时在Executor中与Spout Task有一点不同，一个Bolt Task所在的Executor中有Incoming Queue和Outgoing Queue这两个队列，Incoming Queue用来存放数据流处理方向上，该Bolt Task上游的组件（可能是一个或多个Spout Task/Bolt Task）发射过来的Tuple数据，Outgoing Queue用来存放将要传输到下游Bolt Task的队列。如果该Bolt Task是数据流处理方向上最后一个组件，而且对应execute()方法没有再进行emit()创建的Tupe数据，那么该Bolt Task就没有Outgoing Queue这个队列。在一个Executor中，一个Bolt Task用来衔接上游（Spout Task/Bolt Task）和下游（Bolt Task）的组件，在该Bolt Task所在的Executor内其相关组件的执行流程，如下图所示：

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-bolt-task-in-executor.png)


上图所描述的数据流处理流程，如下所示：

* Spout Task/Bolt Task将Tupe传输到下游该Bolt Task所在的Executor的Incoming Queue中
* Main Thread从该Executor的Incoming Queue中取出Tuple，并将Tupe发送给Bolt Task去处理
* Bolt Task执行execute()方法中的逻辑处理该Tuple数据，并生成新的Tuple，然后调用emit()方法将Tuple发送给下一个Bolt Task处理（这里，实际上是Main Thread将新生成的Tuple放入到该Executor的Outgoing Queue中）
* 属于该Executor的Send Thread从Outgoing Queue中读取Tuple，并传输到下游的一个或多个Bolt Task去处理


# 同一Worker内2个Spout Task/Bolt Task之间传输tuple


在同一个Worker JVM实例内部，可能创建多个Executor实例，那么我们了解一下，一个Tuple是如何在两个Task之间传输的，可能存在4种情况，在同一个Executor中的情况有如下2种：
* 1个Spout Task和1个Bolt Task在同一个Executor中
* 2个Bolt Task在同一个Executor中

我们后面会对类似这种情况详细说明，下面给出的是，2个不同的Executor中Task运行的情况，分别如下图所示：

* 1个Spout Task和1个Bolt Task在不同的2个Executor中


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-transfer-tuples-between-spout-and-bolt-task-in-same-worker-different-executor.png)

* 2个Bolt Task在不同的2个Executor中

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-transfer-tuples-between-2-bolt-tasks-in-same-worker-different-executor.png)

通过前面了解一个Spout Task和一个Bolt Task运行的过程，对上面两种情况便很好理解，不再累述。

# 不同Worker内2个Executor之间传输tuple

如果是在不同的Worker进程内，也就是在两个隔离的JVM实例上，无论是否在同一个Supervisor节点上，Tuple的传输的逻辑是统一的。这里，以一个Spout Task和一个Bolt Task分别运行在两个Worker进程内部为例，如下图所示：
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-transfer-tuples-between-spout-and-bolt-task-in-different-worker.png)


处理流程和前面的类似，只不过如果两个Worker进程分别在两个Supervisor节点上，这里Transfer Thread传输Tuple走的是网络，而不是本地。



# Tuple在Task之间路由过程
下面，我们关心每一个Tuple是如何在各个Bolt的各个Task之间传输，如何将一个Tuple路由（Routing）到下游Bolt的多个Task呢？
这里，我们应该了解一下，作为在Task之间传输的消息的表示形式，定义TaskMessage类的代码，如下所示：
```
package backtype.storm.messaging;

import java.nio.ByteBuffer;

public class TaskMessage {
    private int _task;//编号
    private byte[] _message;

    public TaskMessage(int task, byte[] message) {
        _task = task;
        _message = message;
    }

    public int task() {
        return _task;
    }

    public byte[] message() {
        return _message;
    }

    public ByteBuffer serialize() {
        ByteBuffer bb = ByteBuffer.allocate(_message.length + 2);
        bb.putShort((short) _task);
        bb.put(_message);
        return bb;
    }

    public void deserialize(ByteBuffer packet) {
        if (packet == null)
            return;
        _task = packet.getShort();
        _message = new byte[packet.limit() - 2];
        packet.get(_message);
    }
}

```

可见，每一个Task都给定一个Topology内部唯一的编号，就能够将任意一个Tuple正确地路由到下游需要处理该Tuple的Bolt Task。

假设，存在一个Topology，包含3个Bolt，分别为Bolt1、Bolt2、Bolt3，他们之间的关系也是按照编号的顺序设置的，其中Bolt1有个2个Task，Bolt2有2个Task，Bolt3有2个Task，这里我们只关心Bolt1 Task到Bolt2 Task之间的数据流。具体的路由过程，如下图所示：

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/storm/storm_tongxin/storm-routing-tuples.png)

上图中，Bolt2的两个Task分布到两个Worker进程之内，所以，当上游的Bolt1的2个Task处理完输入的Tuple并生成新的Tuple后，会有根据Bolt2的Task的编号，分别进行如下处理：
* Bolt2 Task4分布在第一个Worker进程内，则Bolt1生成的新的Tupe直接由该Executor的Send Thread，放到第一个Worker内部的另一个Executor的Incoming Queue
* Bolt2 Task5分布在第二个Worker进程内，则Bolt1生成的新的Tupe被Executor的Send Thread放到该Executor所在的第一个Worker的Outgoing Queue中，由第一个Worker的Transfer Thread发送到另一个Worker内（最终路由给Bolt2 Task5去处理）


通过上面处理流程可以看出，每个Executor应该维护Task与所在的Executor之间的关系，这样才能正确地将Tuple传输到目的Bolt Task进行处理。





