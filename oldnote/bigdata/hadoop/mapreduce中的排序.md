---
title: mapreduce中的排序
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---



# 1.应用场景
统计每一个用户（手机号）所耗费的总流量倒序排序
```
1363157985066 	13726230503(手机号)	00-FD-07-A4-72-B8:CMCC	120.196.100.82	i02.c.aliimg.com		24	27	2481（上行流量）	24681（上行流量）	200

1363157995052 	13826544101	5C-0E-8B-C7-F1-E0:CMCC	120.197.40.4			4	0	264	0	200

1363157991076 	13926435656	20-10-7A-28-CC-0A:CMCC	120.196.100.99			2	4	132	1512	200

```

**思路分析**

基本思路：实现自定义的bean来封装流量信息，并将bean作为map输出的key来传输

MR程序在处理数据的过程中会对数据排序(map输出的kv对传输到reduce之前，会排序)，排序的依据是map输出的key,所以，我们如果要实现自己需要的排序规则，则可以考虑将排序因素放到key中，让key实现接口：WritableComparable,然后重写key的compareTo方法


<!--more-->


# 2.实现步骤
## 2.1.求出单个手机号对应的总流量
```
137xxx    66    66    132
127xx    88    11    99
157xx    88    22    100
....
```

## 2.2.对步骤1中的结果再进行mapreduce
### 2.2.1.mapredce
```
package cn.itcast.bigdata.mr.flowsum;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.itcast.bigdata.mr.flowsum.FlowCount.FlowCountMapper;
import cn.itcast.bigdata.mr.flowsum.FlowCount.FlowCountReducer;

public class FlowCountSort {

	static class FlowCountSortMapper extends Mapper<LongWritable, Text, FlowBean, Text> {
		//将new 的工作放在map的外面，这样不用每次map函数中new 对象
		FlowBean bean = new FlowBean();
		Text v = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			// 拿到的是上一个统计程序的输出结果，已经是各手机号的总流量信息
			String line = value.toString();

			String[] fields = line.split("\t");

			String phoneNbr = fields[0];

			long upFlow = Long.parseLong(fields[1]);
			long dFlow = Long.parseLong(fields[2]);

			bean.set(upFlow, dFlow);
			v.set(phoneNbr);

			//以bean作为key，那么内部会以bean来排序，所以bean要重写比较器方法
			context.write(bean, v);
			/*context.write是将bean此时的数据序列化了，所以对于所有的map调用，尽管都是使用的是同一个bean，但是因为
			写一次就序列化一次，那么最终经过map之后的写出去的值是不一样的，所以不用担心bean的引用问题
			*/

		}
	}

	/**
	 * 根据key来掉, 传过来的是对象, 每个对象都是不一样的, 所以每个对象都调用一次reduce方法
	 */
	static class FlowCountSortReducer extends Reducer<FlowBean, Text, Text, FlowBean> {

		// <bean(),phonenbr>
		@Override
		protected void reduce(FlowBean bean, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			//values中只有一个值了
			context.write(values.iterator().next(), bean);

		}

	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		/*conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.resoucemanager.hostname", "mini1");*/
		Job job = Job.getInstance(conf);
		
		/*job.setJar("/home/hadoop/wc.jar");*/
		//指定本程序的jar包所在的本地路径
		job.setJarByClass(FlowCountSort.class);
		
		//指定本业务job要使用的mapper/Reducer业务类
		job.setMapperClass(FlowCountSortMapper.class);
		job.setReducerClass(FlowCountSortReducer.class);
		
		//指定mapper输出数据的kv类型
		job.setMapOutputKeyClass(FlowBean.class);
		job.setMapOutputValueClass(Text.class);
		
		//指定最终输出的数据的kv类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowBean.class);
		
		//指定job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//指定job的输出结果所在目录
		
		Path outPath = new Path(args[1]);
		/*FileSystem fs = FileSystem.get(conf);
		if(fs.exists(outPath)){
			fs.delete(outPath, true);
		}*/
		FileOutputFormat.setOutputPath(job, outPath);
		
		//将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
		/*job.submit();*/
		boolean res = job.waitForCompletion(true);
		System.exit(res?0:1);
		
	}
}


```


### 2.2.2.可序列化，可排序的bean
```
package cn.itcast.bigdata.mr.flowsum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

//实现了比较器和Writer接口
public class FlowBean implements WritableComparable<FlowBean>{
	
	private long upFlow;
	private long dFlow;
	private long sumFlow;
	
	//反序列化时，需要反射调用空参构造函数，所以要显示定义一个
	public FlowBean(){}
	
	public FlowBean(long upFlow, long dFlow) {
		this.upFlow = upFlow;
		this.dFlow = dFlow;
		this.sumFlow = upFlow + dFlow;
	}
	
	
	public void set(long upFlow, long dFlow) {
		this.upFlow = upFlow;
		this.dFlow = dFlow;
		this.sumFlow = upFlow + dFlow;
	}
	
	
	public long getUpFlow() {
		return upFlow;
	}
	public void setUpFlow(long upFlow) {
		this.upFlow = upFlow;
	}
	public long getdFlow() {
		return dFlow;
	}
	public void setdFlow(long dFlow) {
		this.dFlow = dFlow;
	}

	public long getSumFlow() {
		return sumFlow;
	}


	public void setSumFlow(long sumFlow) {
		this.sumFlow = sumFlow;
	}


	/**
	 * 序列化方法
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(upFlow);
		out.writeLong(dFlow);
		out.writeLong(sumFlow);
	}


	/**
	 * 反序列化方法
	 * 注意：反序列化的顺序跟序列化的顺序完全一致
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		 upFlow = in.readLong();
		 dFlow = in.readLong();
		 sumFlow = in.readLong();
	}
	
	@Override
	public String toString() {	 
		return upFlow + "\t" + dFlow + "\t" + sumFlow;
	}
	
	//实现比较方法
	@Override
	public int compareTo(FlowBean o) {
		return this.sumFlow>o.getSumFlow()?-1:1;	//从大到小, 当前对象和要比较的对象比, 如果当前对象大, 返回-1, 交换他们的位置(自己的理解)
	}
}

```

















