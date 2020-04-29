---
title: mapreduce组件之combiner
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---

其实combiner是一个特殊的reducer，如果他们的业务逻辑是一样的，那么可以用reducer来替代combiner

<!--more-->


# map
```
package cn.itcast.bigdata.mr.wcdemo;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * KEYIN: 默认情况下，是mr框架所读到的一行文本的起始偏移量，Long,
 * 但是在hadoop中有自己的更精简的序列化接口，所以不直接用Long，而用LongWritable
 * 
 * VALUEIN:默认情况下，是mr框架所读到的一行文本的内容，String，同上，用Text
 * 
 * KEYOUT：是用户自定义逻辑处理完成之后输出数据中的key，在此处是单词，String，同上，用Text
 * VALUEOUT：是用户自定义逻辑处理完成之后输出数据中的value，在此处是单词次数，Integer，同上，用IntWritable
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

# reduce
```
package cn.itcast.bigdata.mr.wcdemo;

import java.io.IOException;
import java.util.Iterator;

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

# 启动mapreduce
```
package cn.itcast.bigdata.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 相当于一个yarn集群的客户端
 * 需要在此封装我们的mr程序的相关运行参数，指定jar包
 * 最后提交给yarn
 * @author
 *
 */
public class WordcountDriver {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		//是否运行为本地模式，就是看这个参数值是否为local，默认就是local
		/*conf.set("mapreduce.framework.name", "local");*/
		
		//本地模式运行mr程序时，输入输出的数据可以在本地，也可以在hdfs上
		//到底在哪里，就看以下两行配置你用哪行，默认就是file:///
		/*conf.set("fs.defaultFS", "hdfs://mini1:9000/");*/
		/*conf.set("fs.defaultFS", "file:///");*/
		
		
		
		//运行集群模式，就是把程序提交到yarn中去运行
		//要想运行为集群模式，以下3个参数要指定为集群上的值
		/*conf.set("mapreduce.framework.name", "yarn");
		conf.set("yarn.resourcemanager.hostname", "mini1");
		conf.set("fs.defaultFS", "hdfs://mini1:9000/");*/
		Job job = Job.getInstance(conf);
		
		job.setJar("c:/wc.jar");
		//指定本程序的jar包所在的本地路径
		/*job.setJarByClass(WordcountDriver.class);*/
		
		//指定本业务job要使用的mapper/Reducer业务类
		job.setMapperClass(WordcountMapper.class);
		job.setReducerClass(WordcountReducer.class);
		
		//指定mapper输出数据的kv类型
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		//指定最终输出的数据的kv类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		'设置combiner'
		/*指定需要使用combiner，以及用哪个类作为combiner的逻辑（WordcountCombiner.class中的逻辑和WordcountReducer中的逻辑是一样的，
		所以使用WordcountReducer来替代WordcountCombiner.class）
		*/
		/*job.setCombinerClass(WordcountCombiner.class);*/
		job.setCombinerClass(WordcountReducer.class);
		
		//如果不设置InputFormat，它默认用的是TextInputformat.class
		job.setInputFormatClass(CombineTextInputFormat.class);
		CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
		CombineTextInputFormat.setMinInputSplitSize(job, 2097152);
		
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


# conbiner
```
package cn.itcast.bigdata.mr.wcdemo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
  * 输如为map的输出
  * @author: 张政
  * @date: 2016年4月11日 下午7:08:18
  * @package_name: day07.sample
 */
public class WordcountCombiner extends Reducer<Text, IntWritable, Text, IntWritable>{

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

		int count=0;
		for(IntWritable v: values){
			
			count += v.get();
		}
		context.write(key, new IntWritable(count));
	}
}

```



# 使用reduce代替combiner
如果combiner和reducer的业务逻辑是一样的，那么在启动程序中，将combiner的设置类改成reducer
```
/*job.setCombinerClass(WordcountCombiner.class);*/
        job.setCombinerClass(WordcountReducer.class);
*/
```


* combiner是MR程序中Mapper和Reducer之外的一种组件
* combiner组件的父类就是Reducer
* combiner和reducer的区别在于运行的位置：
	* Combiner是在每一个maptask所在的节点运行
	* Reducer是接收全局所有Mapper的输出结果；
* combiner的意义就是对每一个maptask的输出进行局部汇总，以减小网络传输量

具体实现步骤：
1、自定义一个combiner继承Reducer，重写reduce方法
2、在job中设置：  job.setCombinerClass(CustomCombiner.class)
* combiner能够应用的前提是不能影响最终的业务逻辑,而且，combiner的输出kv应该跟reducer的输入kv类型要对应起来

Combiner的使用要非常谨慎,因为combiner在mapreduce过程中可能调用也肯能不调用，可能调一次也可能调多次,所以：combiner使用的原则是：有或没有都不能影响业务逻辑








