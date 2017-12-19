* volatile概念：主要作用是使变量在多个线程间可用,即：变量在多个线程间保持一致性，这就是共享变量
* 早期的做法：在需要共享的变量上加锁，实现变量的共享，这样的方案性能就比较的低，因为同一时间内只能有一个线程去操作变量，其他的线程需要等待

Example:

```
package com.bjsxt.base.sync007;

public class RunThread extends Thread{

	private boolean isRunning = true;
	private void setRunning(boolean isRunning){
		this.isRunning = isRunning;
	}
	
	public void run(){
		System.out.println("进入run方法..");
		int i = 0;
		while(isRunning == true){
			//..
		}
		System.out.println("线程停止");
	}
	
	public static void main(String[] args) throws InterruptedException {
		RunThread rt = new RunThread();
		rt.start();
		Thread.sleep(1000);
		rt.setRunning(false);
		System.out.println("isRunning的值已经被设置了false");
		
		Thread.sleep(1000);
		System.out.println(rt.isRunning);
		
	}
	
	
}

```


从图中可以看出：当isRunning被置为false的时候，rt这个线程还是在运行，而且控制台打印的isRunning也是false，那么这是什么原因呢？

原因是：当一个线程中，将需要用到的变量，拷贝了一份到当前的线程中，对应到上面的示例，是这样的：当我们的线程rt需要用到isRunning这个变量的时候，会将主内存中的isRunning这个变量拷贝一份到自己的内存中，然后随着程序的运行，我们在主内存中进行了如下的设置：rt.setRunning(false);这个只是将主内存的isRunning设置为false，但是线程中拷贝的isRunning这个变量并没有被修改，这就是线程rt一直在运行的原因


**解决**

我们在线程rt需要用到的变量isRunning前加上一个关键字volatile,如下的代码：

```
package com.bjsxt.base.sync007;

public class RunThread extends Thread{

	private volatile boolean isRunning = true;
	private void setRunning(boolean isRunning){
		this.isRunning = isRunning;
	}
	
	public void run(){
		System.out.println("进入run方法..");
		int i = 0;
		while(isRunning == true){
			//..
		}
		System.out.println("线程停止");
	}
	
	public static void main(String[] args) throws InterruptedException {
		RunThread rt = new RunThread();
		rt.start();
		Thread.sleep(1000);
		rt.setRunning(false);
		System.out.println("isRunning的值已经被设置了false");
	}
	
	
}

```

使用volatile的原理：

当我们使用了volatile修饰变量isRunning的时候，在线程rt需要使用的到变量isRunning的时候，他不会从当前线程的内存中去读取变量的值，他会从主内存中去读取，这样就能取到主内存中被改变的变量

|
|   <--------   //这里有个isRunning变量
|
|------------   //（开启了一个rt线程）
|           |
|           |
|           |
|           |
(main)      (rt线程)

> 如果没有volatile关键字修饰isRunning变量，那么在rt线程中会有一份isRunning变量的拷贝，此时在主线程中修改isRunning是对rt线程没有影响的；
但是如果我们将isRunning修饰为volatile的，那么在rt线程中需要获取到isRunning的时候，会从主线程中去读取isRunning变量的值，这样就实现了主线程和rt线程的共享isRunning的情况


# volatile并不具备原子性

olatile关键字不具备synchronized关键字的原子性（同步）

```
package com.bjsxt.base.sync007;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile关键字不具备synchronized关键字的原子性（同步）
 */
public class VolatileNoAtomic extends Thread{
	private static volatile int count;
	private static void addCount(){
		for (int i = 0; i < 1000; i++) {
			count++ ;
			//count.incrementAndGet();
		}
		System.out.println(count);
	}
	
	public void run(){
		addCount();
	}
	
	public static void main(String[] args) {
		
		VolatileNoAtomic[] arr = new VolatileNoAtomic[100];
		for (int i = 0; i < 10; i++) {
			arr[i] = new VolatileNoAtomic();
		}
		
		for (int i = 0; i < 10; i++) {
			arr[i].start();
		}
	}
	
}
//这里最后的打印的值：并不是10000，所以说volatile并步具备原子性，他只是具备多线程间的可见
```

使用AtomicInteger去实现自增，这个类是jdk自带的，可以实现原子性

```
package com.bjsxt.base.sync007;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile关键字不具备synchronized关键字的原子性（同步）
 *
 */
public class VolatileNoAtomic extends Thread{
	//private static volatile int count;
	private static AtomicInteger count = new AtomicInteger(0);
	private static void addCount(){
		for (int i = 0; i < 1000; i++) {
			//count++ ;
			count.incrementAndGet();
		}
		System.out.println(count);
	}
	
	public void run(){
		addCount();
	}
	
	public static void main(String[] args) {
		
		VolatileNoAtomic[] arr = new VolatileNoAtomic[100];
		for (int i = 0; i < 10; i++) {
			arr[i] = new VolatileNoAtomic();
		}
		
		for (int i = 0; i < 10; i++) {
			arr[i].start();
		}
	}

	
}

```


atomic类只能保证本身方法的原子性，不能保证多次操作的原子性

多个addAndGet在一个方法内是非原子性的，需要加synchronized进行修饰，保证4个addAndGet整体原子性

```
package com.bjsxt.base.sync007;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicUse {

	private static AtomicInteger count = new AtomicInteger(0);
	
	//多个addAndGet在一个方法内是非原子性的，需要加synchronized进行修饰，保证4个addAndGet整体原子性
	/**synchronized*/
	public synchronized int multiAdd(){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count.addAndGet(1);
			count.addAndGet(2);
			count.addAndGet(3);
			count.addAndGet(4); //+10 这里加上synchronized就可以保证四次count是一个原子性的，即：整10的处理数据
			return count.get();
	}
	
	
	public static void main(String[] args) {
		
		final AtomicUse au = new AtomicUse();

		List<Thread> ts = new ArrayList<Thread>();
		for (int i = 0; i < 100; i++) {
			ts.add(new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(au.multiAdd());
				}
			}));
		}

		for(Thread t : ts){
			t.start();
		}
	}
}

```




