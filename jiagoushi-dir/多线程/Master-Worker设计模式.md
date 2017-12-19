

Master-Worker模式的核心思想是系统由两类进程协作工作，Master进程和Worker进程，Master负责接收和分配任务，Worker负责处理子任务，当各个Worker子进程处理完成后，会将结果返回给Master，由Master做归纳和总结，其好处是能将一个大任务分解成若干个小任务，并行执行，从而额提高系统的吞吐量


![](/Users/chenyansong/Documents/note/images/multiThread/master-worker.png)


下图是master-worker的具体逻辑实现：

![](/Users/chenyansong/Documents/note/images/multiThread/master-worker2.png)

1.首先Master中维护着存放job的队列，
2.Worker-N中有一个线程，并且Worker—N也有队列的引用
3.Worker通过队列的引用从中取出job，在本地执行，然后将执行的结果放入结果集中


下面是实际的代码

Master

```
package test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Master {

	//1 应该有一个承装任务的集合
	private ConcurrentLinkedQueue<Task> workQueue = new ConcurrentLinkedQueue<Task>();
	
	//2 使用HashMap去承装所有的worker对象
	private HashMap<String, Thread> workers = new HashMap<String, Thread>();
	
	//3 使用一个容器承装每一个worker并非执行任务的结果集
	private ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
	
	//4 构造方法
	public Master(Worker worker, int workerCount){
		// 每一个worker对象都需要有Master的引用 workQueue用于任务的领取，resultMap用于任务的提交
		worker.setWorkerQueue(this.workQueue);
		worker.setResultMap(this.resultMap);
		for(int i = 0 ; i < workerCount; i++){
			//key表示每一个worker的名字, value表示线程执行对象
			workers.put("子节点" + Integer.toString(i), new Thread(worker));
		}
	}
	
	//5 提交方法
	public void submit(Task task){
		this.workQueue.add(task);
	}
	
	//6 需要有一个执行的方法（启动应用程序 让所有的worker工作）
	public void execute(){
		for(Map.Entry<String, Thread> me : workers.entrySet()){
			me.getValue().start();
		}
	}

	//8 判断线程是否执行完毕
	public boolean isComplete() {
		for(Map.Entry<String, Thread> me : workers.entrySet()){
			if(me.getValue().getState() != Thread.State.TERMINATED){
				return false;
			}
		}		
		return true;
	}

	//9 返回结果集数据
	public int getResult() {
		int ret = 0;
		for(Map.Entry<String, Object> me : resultMap.entrySet()){
			//汇总的逻辑..
			ret += (Integer)me.getValue();
		}
		return ret;
	}
	
}

```

Worker

```
package test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Worker implements Runnable {

	private ConcurrentLinkedQueue<Task> workQueue;
	private ConcurrentHashMap<String, Object> resultMap;
	
	public void setWorkerQueue(ConcurrentLinkedQueue<Task> workQueue) {
		this.workQueue = workQueue;
	}

	public void setResultMap(ConcurrentHashMap<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	
	
	@Override
	public void run() {
		while(true){
			Task input = this.workQueue.poll();
			if(input == null) break;//当队列中没有元素可以去取的时候，此时队列为空，我们结束这个这个循环
			//真正的去做业务处理
			Object output = MyWorker.handle(input);
			this.resultMap.put(Integer.toString(input.getId()), output);
		}
	}
	
	public static Object handle(Task input) {
		return null;
	}


}

```

```
package test;

public class MyWorker extends Worker {
	
	public static Object handle(Task input) {
		Object output = null;
		try {
			//表示处理task任务的耗时，可能是数据的加工，也可能是操作数据库...
			Thread.sleep(500);
			output = input.getPrice();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return output;
	}
}

```



Task

```
package test;

public class Task {
	private int id ;
	private String name;
	private int price;
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
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
}

```

Main启动

```
package test;

import java.util.Random;

public class Main {

	public static void main(String[] args) {
		System.out.println("我的机器可用Processor数量:" + Runtime.getRuntime().availableProcessors());
		Master master = new Master(new MyWorker(), Runtime.getRuntime().availableProcessors());
		Random r = new Random();
		for(int i = 1; i<= 100; i++){
			Task t = new Task();
			t.setId(i);
			t.setName("任务"+i);
			t.setPrice(r.nextInt(1000));
			master.submit(t);
		}
		master.execute();
		long start = System.currentTimeMillis();
		while(true){
			if(master.isComplete()){
				long end = System.currentTimeMillis() - start;
				int ret = master.getResult();
				System.out.println("最终结果：" + ret + "， 执行耗时：" + end);
				break;
			}
		}
		
	}
}

```
