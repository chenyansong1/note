---
title: 运营商日志增强之自定义outputFormat
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---



# 1.需求

现有一些原始日志需要做增强解析处理，流程：
1、从原始日志文件中读取数据
2、根据日志中的一个URL字段到外部知识库中获取信息增强到原始日志
3、如果成功增强，则输出到增强结果目录；如果增强失败，则抽取原始数据中URL字段输出到待爬清单目录

如图所示：


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/outputFormat/outputFormat.png)



# 2.分析
程序的关键点是要在一个mapreduce程序中根据数据的不同输出两类结果到不同目录，这类灵活的输出需求可以通过自定义outputformat来实现

<!--more-->

# 3.实现
实现要点：
1、在mapreduce中访问外部资源
2、自定义outputformat，改写其中的recordwriter，改写具体输出数据的方法write()
 
代码实现如下：
数据库获取数据的工具

```
package cn.itcast.bigdata.mr.logenhance;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBLoader {

	public static void dbLoader(Map<String, String> ruleMap) throws Exception {

		Connection conn = null;
		Statement st = null;
		ResultSet res = null;
		
		try {
			//注册驱动
			Class.forName("com.mysql.jdbc.Driver");
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldb", "root", "root");
			st = conn.createStatement();
			res = st.executeQuery("select url,content from url_rule");
			while (res.next()) {
				ruleMap.put(res.getString(1), res.getString(2));
			}

		} finally {
			try{
				if(res!=null){
					res.close();
				}
				if(st!=null){
					st.close();
				}
				if(conn!=null){
					conn.close();
				}

			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}

}

```


自定义一个outputformat

```
package cn.itcast.bigdata.mr.logenhance;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * maptask或者reducetask在最终输出时，先调用OutputFormat的getRecordWriter方法拿到一个RecordWriter
 * 然后再调用RecordWriter的write(k,v)方法将数据写出
 */
public class LogEnhanceOutputFormat extends FileOutputFormat<Text, NullWritable> {

	@Override
	public RecordWriter<Text, NullWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {

		FileSystem fs = FileSystem.get(context.getConfiguration());
		
		//指定要写的path
		Path enhancePath = new Path("D:/temp/en/log.dat");
		Path tocrawlPath = new Path("D:/temp/crw/url.dat");

		//指定要写的hdfs流
		FSDataOutputStream enhancedOs = fs.create(enhancePath);
		FSDataOutputStream tocrawlOs = fs.create(tocrawlPath);

		return new EnhanceRecordWriter(enhancedOs, tocrawlOs);
	}

	/**
	 * 构造一个自己的recordwriter
	 */
	static class EnhanceRecordWriter extends RecordWriter<Text, NullWritable> {
		FSDataOutputStream enhancedOs = null;
		FSDataOutputStream tocrawlOs = null;

		public EnhanceRecordWriter(FSDataOutputStream enhancedOs, FSDataOutputStream tocrawlOs) {
			super();
			this.enhancedOs = enhancedOs;
			this.tocrawlOs = tocrawlOs;
		}

		@Override
		public void write(Text key, NullWritable value) throws IOException, InterruptedException {
			String result = key.toString();
			// 如果要写出的数据是待爬的url，则写入待爬清单文件 /logenhance/tocrawl/url.dat
			if (result.contains("tocrawl")) {
				//写流数据
				tocrawlOs.write(result.getBytes());
			} else {
				// 如果要写出的数据是增强日志，则写入增强日志文件 /logenhance/enhancedlog/log.dat
				enhancedOs.write(result.getBytes());
			}

		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			if (tocrawlOs != null) {
				tocrawlOs.close();
			}
			if (enhancedOs != null) {
				enhancedOs.close();
			}

		}

	}

}

```


开发mapreduce处理流程
```
package cn.itcast.bigdata.mr.logenhance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 这个程序是对每个小时不断产生的用户上网记录日志进行增强(将日志中的url所指向的网页内容分析结果信息追加到每一行原始日志后面)
 */
public class LogEnhance {

	static class LogEnhanceMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

		Map<String, String> ruleMap = new HashMap<String, String>();

		Text k = new Text();
		NullWritable v = NullWritable.get();
		/**
		 * maptask在初始化时会先调用setup方法一次 利用这个机制，将外部的知识库加载到maptask执行的机器内存中
		 * 从数据库中加载规则信息倒ruleMap中
		 */
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {

			try {
				DBLoader.dbLoader(ruleMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// 获取一个计数器用来记录不合法的日志行数, 组名, 计数器名称
			Counter counter = context.getCounter("malformed", "malformedline");
			
			String line = value.toString();
			String[] fields = StringUtils.split(line, "\t");
			try {
				String url = fields[26];
				//对这一行日志中的url去知识库中查找内容分析信息
				String content_tag = ruleMap.get(url);

				// 判断内容标签是否为空，如果为空，则只输出url到待爬清单；如果有值，则输出到增强日志
				if (content_tag == null) {
					k.set(url + "\t" + "tocrawl" + "\n");// 输往待爬清单的内容
					context.write(k, v);
				} else {// 输往增强日志的内容
					k.set(line + "\t" + content_tag + "\n");
					context.write(k, v);
				}

			} catch (Exception exception) {
				counter.increment(1);
			}
		}

	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);

		job.setJarByClass(LogEnhance.class);

		job.setMapperClass(LogEnhanceMapper.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		// 要控制不同的内容写往不同的目标路径，可以采用自定义outputformat的方法
		job.setOutputFormatClass(LogEnhanceOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path("D:/srcdata/webloginput/"));

		// 尽管我们用的是自定义outputformat，但是它是继承制fileoutputformat
		// 在fileoutputformat中，必须输出一个_success文件，所以在此还需要设置输出path
		FileOutputFormat.setOutputPath(job, new Path("D:/temp/output/"));

		// 不需要reducer
		job.setNumReduceTasks(0);

		job.waitForCompletion(true);
		System.exit(0);

	}
}


```




