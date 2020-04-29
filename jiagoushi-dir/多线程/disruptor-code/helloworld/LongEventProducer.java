package bhz.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;
/**
 * 很明显的是：当用一个简单队列来发布事件的时候会牵涉更多的细节，这是因为事件对象还需要预先创建。
 * 发布事件最少需要两步：获取下一个事件槽并发布事件（发布事件的时候要使用try/finnally保证事件一定会被发布）。
 * 如果我们使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。
 * 如果不能发布事件，那么就会引起Disruptor状态的混乱。
 * 尤其是在多个事件生产者的情况下会导致事件消费者失速，从而不得不重启应用才能会恢复。
 */
public class LongEventProducer {

	private final RingBuffer<LongEvent> ringBuffer;
	
	public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
		this.ringBuffer = ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会用过事件传递给消费者
	 */
	public void onData(ByteBuffer bb){//可以将ByteBuffer理解为LongEvent，这里就是传送的数据字节
		//1.可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽（下标）
		long sequence = ringBuffer.next();
		try {
			//2.用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			LongEvent event = ringBuffer.get(sequence);//通过下标拿到具体的数据
			//3.获取要通过事件传递的业务数据：给这个槽设置数据
			event.setValue(bb.getLong(0));
		} finally {
			//4.发布事件，说明该槽已经有啦数据，对应的handler可以用来处理了
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);//注意这里分布的是槽的下标
		}
	}

}
