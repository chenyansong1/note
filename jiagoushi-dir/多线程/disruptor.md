# 1.disruptor框架介绍与helloworld

disruptor是一个高性能的异步处理并发框架，或者可以任务是最快的消息框架（轻量级JMS），也可以任务是观察者模式的实现或者事件监听模式的实现，他的性能远远高于传统的BlockingQueue容器

这里是一篇翻译的文章：

http://ifeve.com/disruptor-getting-started/

## 1.1disruptor框架介绍

在Disruptor中，我们想实现hello world需要如下几个步骤：

1.建立Event类
2.建立一个工厂Event类，用于**创建一堆Event实例**
3.需要有一个**监听事件**类，用于**处理数据（Event类）**
4.我们需要进行测试代码编写，实例化Disruptor实例，配置一系列参数，然后我们对Disruptor实例**绑定监听事件类**，接收并处理数据
5.在Disruptor中，真正**存储数据的核心叫做RingBuffer**，我们通过Disruptor实例拿到他，然后把数据生产出来，把数据加入到RingBuffer的实例对象中即可



## 1.2.helloworld代码
下面是一个helloworld程序代码


![](/Users/chenyansong/Documents/note/images/multiThread/disruptor-helloworld.png)

> 具体的代码参见：/Users/chenyansong/Documents/note/jiagoushi-dir/多线程/disruptor-code/helloworld



## 1.3.Disruptor术语说明

* RingBuffer：被看做Disruptor最主要的组件，然而从3.0开始**RingBuffer仅仅负责存储和更新在Disruptor中流通的数据**，对一些特殊的使用场景能够被用户（使用其他数据结构）完全替代

* Sequence：Disruptor使用Sequence来表示一个特殊组件处理的序号（通俗的将就是槽的下标），和Disruptor一样，每个消费者（EventProcessor）都维持者一个Sequence，大部分的并发代码以来这些Sequence值的运转，因此Sequence支持多种，当前为AtomicLong类的特性

* Sequencer：这是Disruptor真正的核心，实现了这个接口的两种生产者（单生产者和多生产者）均实现了所有的并发算法，为了在生产者和消费者之间进行准确快速的数据传输

* SequenceBarrier:(对于生产者比消费者要快，或者是消费者比生产者要快的情况，这个barrier就是用于协调生产者和消费者不一致的问题)由Sequencer生成，并且包含了已经发布的Sequence的引用，这些的Sequence源于Sequencer和一些独立的消费者的Sequence，他包含了决定是否供消费者来消费的Event的逻辑

* WaitStategy:决定了一个消费者将如何等待生产者将Event放入Disruptor

* Event：从生产者到消费者过程中所处理的数据单元，Disruptor中没有代码表示Event，因为他完全是由用户定义的

* EventProcessor:处理Disruptor中的Event，并且拥有消费者的Sequence（即消费者消费的下标），他有一个实现类BatchEventProcessor，包含了eventloop有效的实现，并且将回调到一个EventHandler接口的实现对象

* EventHandler：由用户实现，并且代表了Disruptor中的一个消费者接口，可以理解为一个消费者

* Producer：由用户实现，他调用RingBuffer来插入数据（Event），在Disruptor中没有相应的实现代码，由用户实现

* WorkProcessor：确保每个Sequence纸杯一个Processor消费，在同一个workPool中的处理多个WorkProcessor不会消费同样的Sequence

* WorkerPool：一个WorkProcessor池，其中WorkProcessor将消费Sequence，所以任务可以在实现WorkHandler接口的worker池间移交

* LifecycleAware：当BatchEventProcessor启动和停止时，于实现这个接口用于接收通知


![](/Users/chenyansong/Documents/note/images/multiThread/disruptor-ringbuff.png)


# 2.disruptor详细说明与使用


ringbuffer带有一个序号，这个序号指向数组中下一个可用元素


![](/Users/chenyansong/Documents/note/images/multiThread/disruptor-ringbuffer2.png)


随着你不停的填充这个buffer（可能也会有相应的读取），这个序号会一直增长，知道绕过这个环


![](/Users/chenyansong/Documents/note/images/multiThread/disruptor-ringbuffer3.png)



要找到数组中当前序号指向的元素，可以通过mod操作，Sequence mod array_length = array_index(取模操作），以上面的ringbuffer为例：12%10=2

事实上，上图中的RingBuffer只有10个槽完全是个意外，如果槽的个数是2的N次方，更有利于二进制的计算机进行计算



如果你看了维基百科里面的关于环形buffer的词条，你就会发现，我们的实现方式，与其最大的区别在于：**没有尾指针，我们只维护了一个指向下一个可用位置的序号**，这种实现是经过深思熟虑的-我们选择用环形buffer的最初原因就是想要提供可靠的消息传递

我们实现的RingBuffer和大家常用的队列之间的区别是：我们不删除buffer中的数据，也就是受这些数据一直存在buffer中，直到新的数据覆盖他们，这就是和维基百科版本相比，我们不需要尾指针的原因，RingBuffer本身并不控制是否需要重叠

因为他是数组，所以要比链表快，而且有一个容易预测的访问模式

这是对CPU缓存友好的，也就是说在硬件级别，数组中的元素是会被预先加载的，因此RingBuffer当中，CPU无须时不时的去主存加载数组中的下一个元素

其次，你可以为数组预先分配内存，是的数组对象一直存在（除非程序终止），这就意味着不需要花大量的时间用于垃圾回收，此外，不像链表那样，需要为每一个添加到上面的对象创建节点对象一一对应的，当删除节点时，需要执行相应的内存清理操作



# 3.disruptor应用（并发场景示例讲解）



在helloworld程序中，我们创建Disruptor实例，然后调用getRingBuffer方法去获取RingBuffer，其实在很多时候，我们可以直接使用RingBuffer，以及其他的API操作，如下：

## 3.1.场景1

* 使用EventProcessor消息处理器
* 使用WorkerPool消息处理器


![](/Users/chenyansong/Documents/note/images/multiThread/distuptor-multi.png)

## 3.2.场景2

在复杂场景下使用RingBuffer（希望P1生产的数据给C1，C2并行执行，最后C1，C2执行结束后给C3执行

![](/Users/chenyansong/Documents/note/images/multiThread/disruptor_cj.png)



## 3.3.场景3

多生产者，消费者使用



