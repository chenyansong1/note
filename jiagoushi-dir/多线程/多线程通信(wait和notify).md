# wait和notify简单举例说明

使用wait和notify方法实现线程间的通信，（注意这两个方法都是object的类方法，换句话说java为所有的对象都提供了这两个方法）

* 1.wait和notify必须配合synchronized关键字使用
* 2.wait方法释放锁，notify方法不释放锁

|
|
|(main)    t2          t1
|-----------|-----------|
|           |           |
|           |           |
|           |           |
|           |           |
|           |           |
|           |           |
|           |           |

执行的代码如下：

```
//listAdd2.java

```

* 1.首先让t2线程先于t1线程执行，t2获取锁，t1进入等待，然后t2使用wait释放锁，此时线程进入等待的状态；
* 2.在t2线程wait之后，t1线程获取到了锁，开始去执行，然后在执行了5次之后，t1发出notify的通知，但是此时锁是被t1线程占用，t2线程是获取不到锁的，只能在t1线程执行结束之后，释放锁，此时t2获得锁，然后执行


上面的代码存在的问题：当t1线程发送notify的通知的时候，t2线程并不能实时的去接到通知，然后去执行，而是在等待t1线程执行完之后，t2才开始去执行


**解决方案**

使用CountDownLatch
```
//listAdd2.java

／*

CountDownLatch.await()      //相当于wait
CountDownLatch.countDown()  //相当于notify
*／
```

这样在t1发送通知之后，t2线程马上就去执行了, CountDownLatch后面会说


# 使用wait和notify去模拟一个queue的队列

BlockingQueue:首先它是一个队列，并且支持阻塞的机制，阻塞的放入和得到数据，我们要实现LinkedBlockingQueue下面两个简单的方法put和take

* put(anObject) ：把anObject加入到BlockingQueue里，如果BlockingQueue没有空间，则调用此方法的线程被阻断，直到BlockingQueue里面有空间再继续
* take：取走BlockingQueue里排在首位的对象，如果BlockingQueue为空，阻塞进入等待状态直到BlockingQueue有新的的数据被加入


```
009
//MyQueue.java

```










