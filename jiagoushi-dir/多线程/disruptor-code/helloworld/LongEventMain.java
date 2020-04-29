package bhz.base;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class LongEventMain {

	public static void main(String[] args) throws Exception {
		//创建缓冲池
		ExecutorService  executor = Executors.newCachedThreadPool();
		//创建工厂
		LongEventFactory factory = new LongEventFactory();
		//创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
		int ringBufferSize = 1024 * 1024; // 

		/**
		//BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现
		WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
		//SleepingWaitStrategy 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景
		WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
		//YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于CPU逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性
		WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
		*/
		
		//创建disruptor
		/**
		 * 第一个参数为工厂类对象，用于创建一个一个的LongEvent对象，LongEvent对象是实际的消费数据
		 * 第二个参数为缓冲区大小
		 * 第三个参数为线程池，进行disruptor内部线程的调度
		 * 第四个参数ProducerType.SINGLE和ProducerType.MULTI，用来表示当前的生产者是一个或者是多个
		 * 第五个参数是一种策略：WaitStrategy，详见上面的解释
		 */
		Disruptor<LongEvent> disruptor = 
				new Disruptor<LongEvent>(factory, ringBufferSize, executor, ProducerType.SINGLE, new YieldingWaitStrategy());
		// 连接消费事件方法；可以将Handler理解为消费者，这里相当于注册
		disruptor.handleEventsWith(new LongEventHandler());
		
		// 启动
		disruptor.start();

		//######### 上面的过程是将disruptor进行了一些配置，然后启动起来，下面的步骤就是向disruptor发送数据#################
		
		//Disruptor 的事件发布过程是一个两阶段提交的过程：
		//发布事件：获取disruptor的RingBuffer
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

		//这是用户自定义的一个生产者
		LongEventProducer producer = new LongEventProducer(ringBuffer); 
		//LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		for(long l = 0; l<100; l++){
			byteBuffer.putLong(0, l);
			//生产者产生数据，其实就是向RingBuffer中放入数据
			producer.onData(byteBuffer);
			//Thread.sleep(1000);
		}

		
		disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
		executor.shutdown();//关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；		
		
		
		
		
		
		
		
	}
}
