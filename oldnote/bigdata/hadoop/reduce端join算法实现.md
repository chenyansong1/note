---
title: reduce端join算法实现
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---


# 场景

假如数据量巨大，两表的数据是以文件的形式存储在HDFS中，需要用mapreduce程序来实现一下SQL查询运算

# 原理阐述
通过将关联的条件作为map输出的key，将两表满足join条件的数据并携带数据所来源的文件信息，发往同一个reduce task，在reduce中进行数据的串联

<!--more-->

# 案例


实现两张表的left join 查询

订单数据表t_order：

id 	|date 			| pid |	amount
:---:|:-----------:|:-----:|:----:
1001|	20150710 	|P0001| 	2
1002|	20150710 	|P0001| 	3
1002|	20150710 	|P0002| 	3
                          
 
商品信息表t_product：

id  |pname | category_id|	price
:---:|:-----------:|:-----:|:----:
P0001 |小米5	|1000 		|	2
P0002 |锤子T1	|1000 		|	3


SQL语句：
```
select  a.id,a.date,b.name,b.category_id,b.price from t_order a left join t_product b on a.pid = b.id
```


实现原理:
将两张表的以相同的pid(商品id)作为key发送到reduce,这样在reduce,订单表里所有pid相同的在一个reduce中(这样reduce中还有一个bean是商品信息表),因为在map发送前将bean用flag区分(flag=1为商品表,flag=0为订单表),所以在reduce端将所有的flag=0的bean放入一个list中,将flag=1的封装成一个bean,然后进行遍历list拼接商品表和订单表



代码实现
mapreduce
```
package cn.itcast.bigdata.mr.rjoin;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 订单表和商品表合到一起
order.txt(订单id, 日期, 商品编号, 数量)
	1001	20150710	P0001	2
	1002	20150710	P0001	3
	1002	20150710	P0002	3
	1003	20150710	P0003	3
product.txt(商品编号, 商品名字, 分类id, 价格)
	P0001	小米5	1001	2
	P0002	锤子T1	1000	3
	P0003	锤子	1002	4

 */
public class RJoin {

	static class RJoinMapper extends Mapper<LongWritable, Text, Text, InfoBean> {
		InfoBean bean = new InfoBean();
		Text k = new Text();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			//获取切片，可以从切片中获取文件信息
			FileSplit inputSplit = (FileSplit) context.getInputSplit();
			String name = inputSplit.getPath().getName();
			
			// 通过文件名判断是哪种数据
			String pid = "";
			if (name.startsWith("order")) {
				String[] fields = line.split("\t");
				// id date pid amount
				pid = fields[2];
				bean.set(Integer.parseInt(fields[0]), fields[1], pid, Integer.parseInt(fields[3]), "", 0, 0, "0");

			} else {
				String[] fields = line.split("\t");
				// id pname category_id price
				pid = fields[0];
				bean.set(0, "", pid, 0, fields[1], Integer.parseInt(fields[2]), Float.parseFloat(fields[3]), "1");
			}
			
			k.set(pid);
			context.write(k, bean);
		}

	}

	static class RJoinReducer extends Reducer<Text, InfoBean, InfoBean, NullWritable> {

		@Override
		protected void reduce(Text pid, Iterable<InfoBean> beans, Context context) throws IOException, InterruptedException {
			//商品信息
			InfoBean pdBean = new InfoBean();
			
			//订单信息列表
			ArrayList<InfoBean> orderBeans = new ArrayList<InfoBean>();

			for (InfoBean bean : beans) {
				if ("1".equals(bean.getFlag())) {	//产品的
					try {
						BeanUtils.copyProperties(pdBean, bean);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					InfoBean odbean = new InfoBean();
					try {
						BeanUtils.copyProperties(odbean, bean);
						orderBeans.add(odbean);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			// 拼接两类数据形成最终结果
			for (InfoBean bean : orderBeans) {

				bean.setPname(pdBean.getPname());
				bean.setCategory_id(pdBean.getCategory_id());
				bean.setPrice(pdBean.getPrice());

				context.write(bean, NullWritable.get());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		conf.set("mapred.textoutputformat.separator", "\t");
		
		Job job = Job.getInstance(conf);

		// 指定本程序的jar包所在的本地路径
		// job.setJarByClass(RJoin.class);
//		job.setJar("c:/join.jar");

		job.setJarByClass(RJoin.class);
		// 指定本业务job要使用的mapper/Reducer业务类
		job.setMapperClass(RJoinMapper.class);
		job.setReducerClass(RJoinReducer.class);

		// 指定mapper输出数据的kv类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(InfoBean.class);

		// 指定最终输出的数据的kv类型
		job.setOutputKeyClass(InfoBean.class);
		job.setOutputValueClass(NullWritable.class);

		// 指定job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		// 指定job的输出结果所在目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		// 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
		/* job.submit(); */
		boolean res = job.waitForCompletion(true);
		System.exit(res ? 0 : 1);

	}
}

```

可序列化Bean
```
package cn.itcast.bigdata.mr.rjoin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class InfoBean implements Writable {

	private int order_id;
	private String dateString;
	private String p_id;
	private int amount;
	private String pname;
	private int category_id;
	private float price;

	// flag=0表示这个对象是封装订单表记录
	// flag=1表示这个对象是封装产品信息记录
	private String flag;

	public InfoBean() {
	}

	public void set(int order_id, String dateString, String p_id, int amount, String pname, int category_id, float price, String flag) {
		this.order_id = order_id;
		this.dateString = dateString;
		this.p_id = p_id;
		this.amount = amount;
		this.pname = pname;
		this.category_id = category_id;
		this.price = price;
		this.flag = flag;
	}

	//get/set方法

	/**
	 * private int order_id; private String dateString; private int p_id;
	 * private int amount; private String pname; private int category_id;
	 * private float price;
	 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(order_id);
		out.writeUTF(dateString);
		out.writeUTF(p_id);
		out.writeInt(amount);
		out.writeUTF(pname);
		out.writeInt(category_id);
		out.writeFloat(price);
		out.writeUTF(flag);

	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.order_id = in.readInt();
		this.dateString = in.readUTF();
		this.p_id = in.readUTF();
		this.amount = in.readInt();
		this.pname = in.readUTF();
		this.category_id = in.readInt();
		this.price = in.readFloat();
		this.flag = in.readUTF();

	}

	@Override
	public String toString() {
		return "order_id=" + order_id + ", dateString=" + dateString + ", p_id=" + p_id + ", amount=" + amount + ", pname=" + pname + ", category_id=" + category_id + ", price=" + price + ", flag=" + flag;
	}


}

```






