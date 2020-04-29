---
title: HDFS的API使用
categories: hadoop
toc: true
tag: [hadoop]
---



# 1.获取api中的客户端对象
在java中操作hdfs，首先要获得一个客户端实例
```
Configuration conf = new Configuration()
FileSystem fs = FileSystem.get(conf)

```

&emsp;而我们的操作目标是HDFS，所以获取到的fs对象应该是DistributedFileSystem的实例；get方法是从何处判断具体实例化那种客户端类呢？ 
从conf中的<font color=red>一个参数 fs.defaultFS的配置值判断</font>；如果我们的代码中没有指定fs.defaultFS，并且工程classpath下也没有给定相应的配置，conf中的默认值就来自于hadoop的jar包中的core-default.xml，默认值为： file:///，则获取的将不是一个DistributedFileSystem的实例，而是一个本地文件系统的客户端对象


<!--more-->

# 2.HDFS客户端增删改查

```
package cn.itcast.bigdata.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.junit.Before;
import org.junit.Test;

public class HdfsClient {

	FileSystem fs = null;

	@Before
	public void init() throws Exception {

		// 构造一个配置参数对象，设置一个参数：我们要访问的hdfs的URI
		// 从而FileSystem.get()方法就知道应该是去构造一个访问hdfs文件系统的客户端，以及hdfs的访问地址
		// new Configuration();的时候，它就会去加载jar包中的hdfs-default.xml
		// 然后再加载classpath下的hdfs-site.xml
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://hdp-node-01:9000");
		/**
		 * 参数优先级： 1、客户端代码中设置的值 2、classpath下的用户自定义配置文件 3、然后是服务器的默认配置
		 */
		conf.set("dfs.replication", "2");
		conf.set("dfs.block.size","64m");

		// 获取一个hdfs的访问客户端，根据参数，这个实例应该是DistributedFileSystem的实例
//		 fs = FileSystem.get(conf);

		// 如果这样去获取，那conf里面就可以不要配"fs.defaultFS"参数，而且，这个客户端的身份标识已经是root用户
		fs = FileSystem.get(new URI("hdfs://hdp-node-01:9000"), conf, "root");
		
		// 获取文件系统相关信息
		DatanodeInfo[] dataNodeStats = ((DistributedFileSystem) fs).getDataNodeStats();
		for(DatanodeInfo dinfo: dataNodeStats){
			System.out.println(dinfo.getHostName());
		}

	}

	/**
	 * 往hdfs上传文件
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddFileToHdfs() throws Exception {

		// 要上传的文件所在的本地路径
		Path src = new Path("g:/apache-flume-1.6.0-bin.tar.gz");
		// 要上传到hdfs的目标路径
		Path dst = new Path("/");
		fs.copyFromLocalFile(src, dst);

		fs.close();
	}

	/**
	 * 从hdfs中复制文件到本地文件系统
	 */
	@Test
	public void testDownloadFileToLocal() throws IllegalArgumentException, IOException {

//		fs.copyToLocalFile(new Path("/apache-flume-1.6.0-bin.tar.gz"), new Path("d:/"));
		fs.copyToLocalFile(false,new Path("/apache-flume-1.6.0-bin.tar.gz"), new Path("d:/"),true);
		fs.close();

	}

	/**
	 * 目录操作
	 */
	@Test
	public void testMkdirAndDeleteAndRename() throws IllegalArgumentException, IOException {

		// 创建目录
		fs.mkdirs(new Path("/a1/b1/c1"));

		// 删除文件夹 ，如果是非空文件夹，参数2必须给值true
		fs.delete(new Path("/aaa"), true);

		// 重命名文件或文件夹
		fs.rename(new Path("/a1"), new Path("/a2"));

	}

	/**
	 * 显示所有的文件
	 */
	@Test
	public void testListFiles() throws FileNotFoundException, IllegalArgumentException, IOException {

		// 思考：为什么返回迭代器，而不是List之类的容器， 如果文件特大， 那不就崩啦！ 迭代器是每迭代一次都向服务器取一次
		RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);//true表示递归调用

		while (listFiles.hasNext()) {

			LocatedFileStatus fileStatus = listFiles.next();

			System.out.println(fileStatus.getPath().getName());//文件名
			System.out.println(fileStatus.getBlockSize());//block块的大小
			System.out.println(fileStatus.getPermission());//文件的权限
			System.out.println(fileStatus.getLen());//字节数
			
			BlockLocation[] blockLocations = fileStatus.getBlockLocations();//获取block块
			for (BlockLocation bl : blockLocations) {
				System.out.println("block-length:" + bl.getLength() + "--" + "block-offset:" + bl.getOffset());
				String[] hosts = bl.getHosts();	//主机名
				for (String host : hosts) {
					System.out.println(host);
				}
			}

			System.out.println("--------------为angelababy打印的分割线--------------");

		}

	}

	/**
	 * 查看一个目录下的文件及文件夹信息（只是一级）
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	@Test
	public void testListAll() throws FileNotFoundException, IllegalArgumentException, IOException {

		FileStatus[] listStatus = fs.listStatus(new Path("/"));

		String flag = "d-- ";
		for (FileStatus fstatus : listStatus) {

			if (fstatus.isFile()){
				flag = "f-- ";
			}else{
				flag = "d-- ";
			}

			System.out.println(flag + fstatus.getPath().getName());
			System.out.println(fstatus.getPermission());

		}
	}
}

```


# 3.通过流的方式访问hdfs

```
package cn.itcast.bigdata.hdfs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;


/**
 * 用流的方式来操作hdfs上的文件
 * 可以实现读取指定偏移量范围的数据
 * @author
 *
 */
public class HdfsStreamAccess {
	
	FileSystem fs = null;
	Configuration conf = null;
	
	@Before
	public void init() throws Exception{
		
		conf = new Configuration();
		//拿到一个文件系统操作的客户端实例对象
//		fs = FileSystem.get(conf);
		//可以直接传入 uri和用户身份
		fs = FileSystem.get(new URI("hdfs://mini1:9000"),conf,"hadoop");
	}
	

	/**
	 * 通过流的方式上传文件到hdfs
	 * @throws Exception
	 */
	@Test
	public void testUpload() throws Exception {
		
		FSDataOutputStream outputStream = fs.create(new Path("/angelababy.love"), true);
		FileInputStream inputStream = new FileInputStream("c:/angelababy.love");
		
		IOUtils.copy(inputStream, outputStream);
		
	}
	
	
	/**
	 * 通过流的方式获取hdfs上数据
	 * @throws Exception
	 */
	@Test
	public void testDownLoad() throws Exception {
		//先获取一个文件的输入流----针对hdfs上的
		FSDataInputStream inputStream = fs.open(new Path("/angelababy.love"));		
		
   //再构造一个文件的输出流----针对本地的
		FileOutputStream outputStream = new FileOutputStream("d:/angelababy.love");
		
   //再将输入流中数据传输到输出流
		IOUtils.copy(inputStream, outputStream);
		
	}
	
	/**
	 * 获取指定偏移处的数据
          *  hdfs支持随机定位进行文件读取，而且可以方便地读取指定长度
         *用于上层分布式运算框架并发处理数据
	 */
	@Test
	public void testRandomAccess() throws Exception{
		
		FSDataInputStream inputStream = fs.open(new Path("/angelababy.love"));
	
		inputStream.seek(12);//指定偏移
		
		FileOutputStream outputStream = new FileOutputStream("d:/angelababy.love.part2");
		
		IOUtils.copy(inputStream, outputStream);
		
		
	}
	
	
	
	/**
	 * 显示hdfs上文件的内容
	 */
	@Test
	public void testCat() throws IllegalArgumentException, IOException{
		
		FSDataInputStream in = fs.open(new Path("/angelababy.love"));
		
		IOUtils.copy(in, System.out);//打印到控制台
		
//		IOUtils.copyBytes(in, System.out, 1024);
	}
	
}

```

































