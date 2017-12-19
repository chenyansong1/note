---
title: 自定义GroupingComparator
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---



# 1.GroupingComparator的工作原理
首先取第一个元素，然后将其的key作为一个基础，然后通过迭代器去取第一个后面的元素，取对应元素的key和第一个元素的key比较，如果相同则将元素的value拿过来，接着去迭代后面的元素，直到取到的元素的
key和第一个元素不同，那么此时就将<key1,    <value1, value2, value3..>> 传递到reduce函数中

实现示意图如下：
  
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/groupingComparator/groupingComparator.png)


# 2.需求

有如下订单数据，现在需要求出每一个订单中成交金额最大的一笔交易


订单id			|商品id	|	成交金额
:---------------:|:-----:|:-------:
Order_0000001	|Pdt_01	|222.8
Order_0000001	|Pdt_05	|25.8
Order_0000002	|Pdt_03	|522.8
Order_0000002	|Pdt_04	|122.4
Order_0000002	|Pdt_05	|722.4
Order_0000003	|Pdt_01	|222.8


# 3.分析
1、利用“订单id和成交金额”作为key，可以将map阶段读取到的所有订单数据按照id分区，按照金额排序，发送到reduce
2、在reduce端利用groupingcomparator将订单id相同的kv聚合成组，然后取第一个即是最大值



<!--more-->


# 4.实现


自定义groupingcomparator

```
package cn.itcastcat.bigdata.secondarysort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 利用reduce端的GroupingComparator来实现将一组bean看成相同的key
 * 用于控制shuffle过程中reduce端对kv对的聚合逻辑
 * @author duanhaitao@itcast.cn
 *
 */
public class ItemidGroupingComparator extends WritableComparator {

	//传入作为key的bean的class类型，以及制定需要让框架做反射获取实例对象,然后去比较
	protected ItemidGroupingComparator() {
		super(OrderBean.class, true);
	}
	

	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		OrderBean abean = (OrderBean) a;
		OrderBean bbean = (OrderBean) b;
		
		//比较两个bean时，指定只比较bean中的orderid
		//将item_id相同的bean都视为相同，从而聚合为一组，从而去取一组中的第一个bean作为key
		return abean.getItemid().compareTo(bbean.getItemid());
		
	}

}

```

定义订单信息bean
```
package cn.itcastcat.bigdata.secondarysort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

/**
 * @author duanhaitao@itcast.cn
 *
 */
public class OrderBean implements WritableComparable<OrderBean>{

	private Text itemid;
	private DoubleWritable amount;

	public OrderBean() {
	}

	public OrderBean(Text itemid, DoubleWritable amount) {
		set(itemid, amount);

	}

	public void set(Text itemid, DoubleWritable amount) {

		this.itemid = itemid;
		this.amount = amount;

	}



	public Text getItemid() {
		return itemid;
	}

	public DoubleWritable getAmount() {
		return amount;
	}



	@Override
	public int compareTo(OrderBean o) {
		//先比较ID，如果ID相同，那么比较金额
		int cmp = this.itemid.compareTo(o.getItemid());
		if (cmp == 0) {
			cmp = -this.amount.compareTo(o.getAmount());
		}
		return cmp;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(itemid.toString());
		out.writeDouble(amount.get());
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String readUTF = in.readUTF();
		double readDouble = in.readDouble();
		
		this.itemid = new Text(readUTF);
		this.amount= new DoubleWritable(readDouble);
	}


	@Override
	public String toString() {

		return itemid.toString() + "\t" + amount.get();
		
	}

}

```


自定义Partitioner
```
package cn.itcastcat.bigdata.secondarysort;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;


public class ItemIdPartitioner extends Partitioner<OrderBean, NullWritable>{

	@Override
	public int getPartition(OrderBean bean, NullWritable value, int numReduceTasks) {
		//相同id的订单bean，会发往相同的partition
		//而且，产生的分区数，是会跟用户设置的reduce task数保持一致
		return (bean.getItemid().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
	}
}

```




编写mapreduce处理流程

```
package cn.itcastcat.bigdata.secondarysort;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.sun.xml.bind.v2.schemagen.xmlschema.List;

/**
 * 
 * @author duanhaitao@itcast.cn
 *
 */
public class SecondarySort {
	
	static class SecondarySortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable>{
		
		OrderBean bean = new OrderBean();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			String[] fields = StringUtils.split(line, ",");
			//封装成bean对象
			bean.set(new Text(fields[0]), new DoubleWritable(Double.parseDouble(fields[2])));
			
			context.write(bean, NullWritable.get());
		}
	}
	
	static class SecondarySortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable>{
		
		
		//到达reduce时，相同id的所有bean已经被看成一组，且金额最大的那个一排在第一位
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.write(key, NullWritable.get());
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(SecondarySort.class);
		
		job.setMapperClass(SecondarySortMapper.class);
		job.setReducerClass(SecondarySortReducer.class);
		
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.setInputPaths(job, new Path("c:/wordcount/gpinput"));
		FileOutputFormat.setOutputPath(job, new Path("c:/wordcount/gpoutput"));
		
		//在此设置自定义的Groupingcomparator类 
		job.setGroupingComparatorClass(ItemidGroupingComparator.class);
		//在此设置自定义的partitioner类
		job.setPartitionerClass(ItemIdPartitioner.class);
		
		job.setNumReduceTasks(2);
		
		job.waitForCompletion(true);
		
	}
}


```


