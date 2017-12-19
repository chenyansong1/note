

并发Queue

在并发队列上，JDK提供了两套实现，一个是以ConcurrentLinkedQueue为代表的高性能队列，一个是以BlockingQueue接口为代表的阻塞队列，无论哪种都继承自Queue

![](/Users/chenyansong/Documents/note/images/multiThread/queue_class.png)



# ConcurrentLinkedQueue


ConcurrentLinkedQueue:是一个适用于高并发场景下的队列，通过无锁的方式，实现了高并发状态下的高性能，通常ConcurrentLinkedQueue性能好于BlockingQueue，他是一个基于链接节点的无界线程安全的队列，该队列的元素遵循**先进先出的原则，头是最先加入的，尾是最近加入的，该队列不允许null元素**

ConcurrentLinkedQueue重要方法：

* add()和offer()都是加入元素的方法（在ConcurrentLinkedQueue中，这两个方法没有任何区别）
* poll()和peek()都是取头部节点，区别在于前者会删除，后者不会

示例代码

```
		//高性能无阻塞无界队列：ConcurrentLinkedQueue
		/**
		ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<String>();
		q.offer("a");
		q.offer("b");
		q.offer("c");
		q.offer("d");
		q.add("e");
		
		System.out.println(q.poll());	//a 从头部取出元素，并从队列里删除
		System.out.println(q.size());	//4
		System.out.println(q.peek());	//b
		System.out.println(q.size());	//4
		*/
```


# BlockingQueue接口


* ArrayBlockingQueue：基于数组的阻塞队列实现，在ArrayBlockingQueue内部，维护了一个定长数组，以便缓存队列的数据对象，其内部没有实现读写分离，也就意味着生产和消费不能完全并行，长度是需要定义的，可以制定先进先出或者先进后出，也叫有界队列，在很多场合非常适合使用

```
		/**
		ArrayBlockingQueue<String> array = new ArrayBlockingQueue<String>(5);
		array.put("a");
		array.put("b");
		array.add("c");
		array.add("d");
		array.add("e");
		array.add("f");//因为array的长度就是5，所以这里再添加就会抛出异常
		array.offer("a", 3, TimeUnit.SECONDS)//offer的作用就是在3秒之后加入，如果没有加入成功，就是返回false，不会像加入“f"一样抛出异常
		//System.out.println(array.offer("a", 3, TimeUnit.SECONDS));
		*/
```


* LinkedBlockingQueue:基于链表的阻塞队列，同ArrrayBlockingQueue类似，其内部也维持着一个数据缓冲队列（该队列由一个链表构成），LinkedBlockingQueue之所以能够高效的处理并发数据，是因为其内部实现采用了分离锁（读写分离两个锁），从而实现生产和消费者操作完全并行运行，他是一个无界队列


```
		/**
		//阻塞队列
		LinkedBlockingQueue<String> q = new LinkedBlockingQueue<String>(5);//这里的5表示的是初始化的长度，但是q仍然是无界的
		q.offer("a");
		q.offer("b");
		q.offer("c");
		q.offer("d");
		q.offer("e");
		q.offer("f");
		//System.out.println(q.size());
		
//		for (Iterator iterator = q.iterator(); iterator.hasNext();) {
//			String string = (String) iterator.next();
//			System.out.println(string);
//		}
		
		List<String> list = new ArrayList<String>();
		System.out.println(q.drainTo(list, 3));//取出队列中的3的值放入list中
		System.out.println(list.size());
		for (String string : list) {
			System.out.println(string);//a,b,c
		}
		*/
```


* PriorityBlockingQueue：基于优先级的阻塞队列（优先级的判断通过构造函数传入的Compator对象来决定，也就是说**传入队列的对象必须实现Comparable接口**），在实现PriorityBlockingQueue时，内部控制线程同步的锁采用的时公平锁，他也是一个无界的队列

```

//必须实现compare中的方法
public class Task implements Comparable<Task>{
	
	private int id ;
	private String name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Task task) {
		return this.id > task.id ? 1 : (this.id < task.id ? -1 : 0);  
	}
	
	public String toString(){
		return this.id + "," + this.name;
	}
	
}
```

```

#
		
		PriorityBlockingQueue<Task> q = new PriorityBlockingQueue<Task>();
		
		Task t1 = new Task();
		t1.setId(3);
		t1.setName("id为3");
		Task t2 = new Task();
		t2.setId(4);
		t2.setName("id为4");
		Task t3 = new Task();
		t3.setId(1);
		t3.setName("id为1");
		
		//return this.id > task.id ? 1 : 0;
		q.add(t1);	//3
		q.add(t2);	//4
		q.add(t3);  //1
		
		// 1 3 4 每次调用take方法的时候去队列中拿到Priority最大的值，比如有如下的值:1，4，8，5，2，每次调用take方法，然后从中找到最小的元素1，此时的q=4,8,5,2;第二次调用take方法，然后找到2，此时q=4,8,5如此循环
		System.out.println("容器：" + q);
		System.out.println(q.take().getId());
		System.out.println("容器：" + q);
//		System.out.println(q.take().getId());
//		System.out.println(q.take().getId());

```



* DeplayQueue：带有延迟时间的队列，其中的元素只有当其指定的延迟时间到了，才能够从队列中获取到该元素，DeplayQueue中的元素必须实现Deplay接口，DeplayQueue时一个没有大小限制的队列，应用场景很多，比如：对缓存超时的数据进行移除，任务超时处理，空闲连接的关闭等等


```
package com.bjsxt.base.coll013;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Wangmin implements Delayed {  
    
    private String name;  
    //身份证  
    private String id;  
    //截止时间  
    private long endTime;  
    //定义时间工具类
    private TimeUnit timeUnit = TimeUnit.SECONDS;
      
    public Wangmin(String name,String id,long endTime){  
        this.name=name;  
        this.id=id;  
        this.endTime = endTime;  
    }  
      
    public String getName(){  
        return this.name;  
    }  
      
    public String getId(){  
        return this.id;  
    }  
      
    /** 
     * 用来判断是否到了截止时间 
     */  
    @Override  
    public long getDelay(TimeUnit unit) { 
        //return unit.convert(endTime, TimeUnit.MILLISECONDS) - unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    	return endTime - System.currentTimeMillis();
    }  
  
    /** 
     * 相互批较排序用 
     */  
    @Override  
    public int compareTo(Delayed delayed) {  
    	Wangmin w = (Wangmin)delayed;  
        return this.getDelay(this.timeUnit) - w.getDelay(this.timeUnit) > 0 ? 1:0;  
    }  
  
}  
```


```
package com.bjsxt.base.coll013;

import java.util.concurrent.DelayQueue;

public class WangBa implements Runnable {  
    
    private DelayQueue<Wangmin> queue = new DelayQueue<Wangmin>();  
    
    public boolean yinye =true;  
      
    public void shangji(String name,String id,int money){  
        Wangmin man = new Wangmin(name, id, 1000 * money + System.currentTimeMillis());  
        System.out.println("网名"+man.getName()+" 身份证"+man.getId()+"交钱"+money+"块,开始上机...");  
        this.queue.add(man);  
    }  
      
    public void xiaji(Wangmin man){  
        System.out.println("网名"+man.getName()+" 身份证"+man.getId()+"时间到下机...");  
    }  
  
    @Override  
    public void run() {  
        while(yinye){  
            try {  
                Wangmin man = queue.take();  
                xiaji(man);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
    public static void main(String args[]){  
        try{  
            System.out.println("网吧开始营业");  
            WangBa siyu = new WangBa();  
            Thread shangwang = new Thread(siyu);  
            shangwang.start();  
              
            siyu.shangji("路人甲", "123", 1);  
            siyu.shangji("路人乙", "234", 10);  
            siyu.shangji("路人丙", "345", 5);  
        }  
        catch(Exception e){  
            e.printStackTrace();
        }  
  
    }  
}  
```


* SynchronousQueue:一种没有缓冲的队列，生产者生产的数据直接会被消费者获取并消费

```
	
		final SynchronousQueue<String> q = new SynchronousQueue<String>();
		//q.add("xxx");//直接向该队列中添加元素，会报异常
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(q.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t1.start();
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				q.add("asdasd");
			}
		});
		t2.start();	
```
