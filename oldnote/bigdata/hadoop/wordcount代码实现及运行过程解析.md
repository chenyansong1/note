---
title: wordcount代码实现
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---


# 1.wordcount程序实现

## 1.1.map
```
package cn.itcast.bigdata.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * KEYIN: 默认情况下，是mr框架所读到的一行文本的起始偏移量，Long,
 * 但是在hadoop中有自己的更精简的序列化接口，所以不直接用Long，而用LongWritable
 * 
 * VALUEIN:默认情况下，是mr框架所读到的一行文本的内容，String，同上，用Text	（序列化需要）
 * 
 * KEYOUT：是用户自定义逻辑处理完成之后输出数据中的key，在此处是单词，String，同上，用Text（序列化需要）
 * VALUEOUT：是用户自定义逻辑处理完成之后输出数据中的value，在此处是单词次数，Integer，同上，用IntWritable（序列化需要）
 * 
 * @author
 *
 */

public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

	/**
	 * map阶段的业务逻辑就写在自定义的map()方法中
	 * maptask会对每一行输入数据调用一次我们自定义的map()方法
	 */
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		//将maptask传给我们的文本内容先转换成String
		String line = value.toString();
		//根据空格将这一行切分成单词
		String[] words = line.split(" ");
		
		//将单词输出为<单词，1>
		for(String word:words){
			//将单词作为key，将次数1作为value，以便于后续的数据分发，可以根据单词分发，以便于相同单词会到相同的reduce task
			context.write(new Text(word), new IntWritable(1));
		}
	}
	
}

```

<!--more-->


## 1.2.reduce
```
package cn.itcast.bigdata.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * KEYIN, VALUEIN 对应  mapper输出的KEYOUT,VALUEOUT类型对应
 * 
 * KEYOUT, VALUEOUT 是自定义reduce逻辑处理结果的输出数据类型
 * KEYOUT是单词
 * VLAUEOUT是总次数
 * @author
 *
 */
public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{

	/**
	 * <angelababy,1><angelababy,1><angelababy,1><angelababy,1><angelababy,1>
	 * <hello,1><hello,1><hello,1><hello,1><hello,1><hello,1>
	 * <banana,1><banana,1><banana,1><banana,1><banana,1><banana,1>
	 * 入参key，是一组相同单词kv对的key
	 */
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

		int count=0;
		/*Iterator<IntWritable> iterator = values.iterator();
		while(iterator.hasNext()){
			count += iterator.next().get();
		}*/
		
		for(IntWritable value:values){
		
			count += value.get();
		}
		
		context.write(key, new IntWritable(count));
		
	}
	
}

```

## 1.3.启动程序
```
package cn.itcast.bigdata.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 相当于一个yarn集群的客户端:分配硬件资源，启动程序
 * 需要在此封装我们的mr程序的相关运行参数，指定jar包
 * 最后提交给yarn
 * @author
 *
 */
public class WordcountDriver {
	
	public static void main(String[] args) throws Exception {
		
		if (args == null || args.length == 0) {
			args = new String[2];
			args[0] = "hdfs://master:9000/wordcount/input/wordcount.txt";
			args[1] = "hdfs://master:9000/wordcount/output8";
		}
		
		Configuration conf = new Configuration();
		
		//设置的没有用!  ??????
//		conf.set("HADOOP_USER_NAME", "hadoop");
//		conf.set("dfs.permissions.enabled", "false");
		
		
		/*conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.resoucemanager.hostname", "mini1");*/
		Job job = Job.getInstance(conf);
		
		/*job.setJar("/home/hadoop/wc.jar");*/
		//指定本程序的jar包所在的本地路径（因为最后是要交给yarn来跑jar的，所以这里是指定jar的路径）
		job.setJarByClass(WordcountDriver.class);
		
		//指定本业务job要使用的mapper/Reducer业务类
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		
		//指定mapper输出数据的kv类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		//指定最终输出的数据的kv类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		//指定job的输入原始文件所在目录
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		//指定job的输出结果所在目录
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
		/*job.submit();*/
		boolean res = job.waitForCompletion(true);
		System.exit(res?0:1);
		
	}
	
}

```





# 2.在linux上运行一个jar

```
#启动
java    -cp    wordcount.jar    cn.itcast.bigdata.mr.wcdemo.wordcountDriver    /wordcount/input    /wordcount/output
/*
-cp 和 -classpath 一样，是指定类运行所依赖其他类的路径，通常是类库，jar包之类，
需要全路径到jar包，window上分号“;”分隔，linux上是分号“:”分隔。不支持通配符，需要列出所有jar包，用一点“.”代表当前路径。  

wordcount.jar：是启动的jar文件

cn.itcast.bigdata.mr.wcdemo.wordcountDriver ：是启动的main函数所在的类

/wordcount/input    /wordcount/output：参数
*/
```

使用java -cp 去启动一个jar，需要指定所有的依赖的jar文件 ,而使用hadoop命令则会帮我们加上依赖的jar和配置文件到classpath
```
hadoop jar wordcount.jar cn.itcast.bigdata.mr.wcdemo.WordcountDriver  /wordcount/input   /wordcount/output

``` 

在MyEclipse中打jar包，如果是打成的是runnable的jar，那么会将依赖打到jar包中去, 如果是打包成普通的jar，那么运行时需要指定运行程序所依赖的其他jar文件



