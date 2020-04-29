---
title: map端join算法实现
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---

# 场景

实现两个“表”的join操作，其中一个表数据量小，一个表很大，这种场景在实际中非常常见，比如“订单日志” join “产品信息”

# 原理阐述
* 先在mapper类中预先定义好小表，进行join
* 并用distributedcache机制将小表的数据分发到每一个maptask执行节点，从而每一个maptask节点可以从本地加载到小表的数据，进而在本地即可实现join

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

解决方法:将商品表缓存在map端本地，然后在map端直接拼接
```
package cn.itcast.bigdata.mr.mapsidejoin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapSideJoin {

	public static class MapSideJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
		// 用一个hashmap来加载保存产品信息表
		Map<String, String> pdInfoMap = new HashMap<String, String>();

		Text k = new Text();

		/**
		 * 通过阅读父类Mapper的源码，发现 setup方法是在maptask处理数据之前调用一次 可以用来做一些初始化工作
		 */
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			//因为已经将普通文件加入(addCacheFile)了当前的工作目录，所以可以直接获取到该文件
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("pdts.txt")));
			String line;
			while (StringUtils.isNotEmpty(line = br.readLine())) {
				String[] fields = line.split(",");
				pdInfoMap.put(fields[0], fields[1]);
			}
			br.close();
		}

		// 由于已经持有完整的产品信息表，所以在map方法中就能实现join逻辑了（直接拼接）
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String orderLine = value.toString();
			String[] fields = orderLine.split("\t");
			String pdName = pdInfoMap.get(fields[1]);
			k.set(orderLine + "\t" + pdName);
			context.write(k, NullWritable.get());
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);

		job.setJarByClass(MapSideJoin.class);

		job.setMapperClass(MapSideJoinMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		FileInputFormat.setInputPaths(job, new Path("D:/srcdata/mapjoininput"));
		FileOutputFormat.setOutputPath(job, new Path("D:/temp/output"));

		// 指定需要缓存一个文件到所有的maptask运行节点工作目录
		/* job.addArchiveToClassPath(archive); */// 缓存jar包到task运行节点的classpath中
		/* job.addFileToClassPath(file); */// 缓存普通文件到task运行节点的classpath中
		/* job.addCacheArchive(uri); */// 缓存压缩包文件到task运行节点的工作目录（当前类文件所在的目录）
		/* job.addCacheFile(uri) */// 缓存普通文件到task运行节点的工作目录

		// 将产品表文件缓存到task工作节点的工作目录中去 
//		job.addCacheFile(new URI("hdfs://hpd-node-01:9000/srcdata/mapjoincache/pdts.txt"));
		job.addCacheFile(new URI("file:/D:/srcdata/mapjoincache/pdts.txt"));

		//默认是有一个reduce的，map端join的逻辑不需要reduce阶段，设置reducetask数量为0
		job.setNumReduceTasks(0);
		
		boolean res = job.waitForCompletion(true);
		System.exit(res ? 0 : 1);

	}

}

```
