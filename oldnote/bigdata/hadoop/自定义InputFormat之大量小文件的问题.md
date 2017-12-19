---
title: 自定义InputFormat之大量小文件的问题
categories: hadoop   
toc: true  
tag: [hadoop,mapreduce]
---


默认情况下，TextInputFormat对任务的切片机制是按文件规划切片，不管文件多小，都会是一个单独的切片，都会交给一个maptask，这样，如果有大量小文件，就会产生大量的maptask，处理效率极其低下

<!--more-->

# 分析
小文件的优化无非以下几种方式：
1、在数据采集的时候，就将小文件或小批数据合成大文件再上传HDFS
2、在业务处理之前，在HDFS上使用mapreduce程序对小文件进行合并
3、在mapreduce处理时，可采用combineInputFormat提高效率


# 采用combineInputFormat

```
/*如果不设置InputFormat，它默认用的是TextInputformat.class
   * 只是将多个小文件放在一个切片中，即一个maptask中
  */
  job.setInputFormatClass(CombineTextInputFormat.class);
  CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
  CombineTextInputFormat.setMinInputSplitSize(job, 2097152);

```


# 自定义InputFormat

实现方式:
1.自定义一个InputFormat
2.改写RecordReader，实现一次读取一个完整文件封装为KV
3.在输出时使用SequenceFileOutPutFormat输出合并文件
 


代码如下：
自定义InputFromat

```
package cn.itcast.bigdata.combinefile;
 
import java.io.IOException;
 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
 
public class WholeFileInputFormat extends FileInputFormat<NullWritable, BytesWritable>{
 
@Override
protected boolean isSplitable(JobContext context, Path file) {
  return false;
}
 
@Override
public RecordReader<NullWritable, BytesWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException,InterruptedException {
  WholeFileRecordReader reader = new WholeFileRecordReader();
  reader.initialize(split, context);
  return reader;
}
}
```

自定义RecordReader

```
package cn.itcast.bigdata.combinefile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * 
 * RecordReader的核心工作逻辑：
 * 通过nextKeyValue()方法去读取数据构造将返回的key   value
 * 通过getCurrentKey 和 getCurrentValue来返回上面构造好的key和value
 */
class WholeFileRecordReader extends RecordReader<NullWritable, BytesWritable> {
	private FileSplit fileSplit;
	private Configuration conf;
	private BytesWritable value = new BytesWritable();
	private boolean processed = false;

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		this.fileSplit = (FileSplit) split;
		this.conf = context.getConfiguration();
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (!processed) {
			byte[] contents = new byte[(int) fileSplit.getLength()];
			Path file = fileSplit.getPath();
			FileSystem fs = file.getFileSystem(conf);
			FSDataInputStream in = null;
			try {
				in = fs.open(file);
				IOUtils.readFully(in, contents, 0, contents.length);
				value.set(contents, 0, contents.length);
			} finally {
				IOUtils.closeStream(in);
			}
			processed = true;//设置为true，那么将只会读取一次，也就是只是返回一个keyValue，一个文件读取完毕
			
			return true;
		}
		return false;
	}

	
	
	
	@Override
	public NullWritable getCurrentKey() throws IOException,
			InterruptedException {
		return NullWritable.get();
	}

	@Override
	public BytesWritable getCurrentValue() throws IOException,
			InterruptedException {
		return value;
	}

	/**
	 * 返回当前进度
	 */
	@Override
	public float getProgress() throws IOException {
		//框架会调用该方法，返回读取的进度，因为只是读取一次（读完整个文件），所以是要么读完了，要么没有读完
		return processed ? 1.0f : 0.0f;
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}
}
```


定义mapreduce处理流程

```
package cn.itcast.bigdata.combinefile;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SmallFilesToSequenceFileConverter extends Configured implements Tool {
	static class SequenceFileMapper extends	Mapper<NullWritable, BytesWritable, Text, BytesWritable> {
		private Text filenameKey;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			InputSplit split = context.getInputSplit();
			Path path = ((FileSplit) split).getPath();
			//拿到文件的路径，作为key
			filenameKey = new Text(path.toString());
		}

		@Override
		protected void map(NullWritable key, BytesWritable value, Context context) throws IOException, InterruptedException {
			//以文件路径作为key，以RecordReader.nextKeyValue()读取的一行（其实是整个文件）作为value
			context.write(filenameKey, value);
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		/*System.setProperty("HADOOP_USER_NAME", "hadoop");*/
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: combinefiles <in> <out>");
			System.exit(2);
		}
		
		Job job = Job.getInstance(conf,"combine small files to sequencefile");
		job.setJarByClass(SmallFilesToSequenceFileConverter.class);
		
		//默认是 TextInputFormat
		job.setInputFormatClass(WholeFileInputFormat.class);
		
		/**
		 * 这里没有指定reduce，所以会调用默认的reduce，但是如果输出的格式仍然是文本的话，那么value.toString()就会
		 * 是一个对象的hash地址，所以这里指定输出value的格式为字节序列：SequenceFileOutputFormat
		 * 这样输出的文件中的格式：文件名（key)	字节序列（value）
		 * 问题：
		 * 1.将所以的小文件最后都以 " 文件名（key)	字节序列（value） "的方式输出到了一个文件中，那么最后这个文件将非常的大
		 * 2.最后输出的文件是字节文件，那么我们再不能使用默认的InputFormat的方式（TextInputformat）来读取文件了
		 */
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BytesWritable.class);
		
		job.setMapperClass(SequenceFileMapper.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		args=new String[]{"c:/wordcount/smallinput","c:/wordcount/smallout"};
		int exitCode = ToolRunner.run(new SmallFilesToSequenceFileConverter(), args);
		System.exit(exitCode);
		
	}
}

```






