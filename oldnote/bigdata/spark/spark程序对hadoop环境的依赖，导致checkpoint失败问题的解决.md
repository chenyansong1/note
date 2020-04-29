虽然没有使用Hadoop，但是在windows下运行Spark程序报如下错误：

```
INFO : org.apache.spark.SparkContext - Running Spark version 1.3.1  
WARN : org.apache.hadoop.util.NativeCodeLoader - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable  
ERROR: org.apache.hadoop.util.Shell - Failed to locate the winutils binary in the hadoop binary path  
java.io.IOException: Could not locate executable null\bin\winutils.exe in the Hadoop binaries.  
    at org.apache.hadoop.util.Shell.getQualifiedBinPath(Shell.java:355)  
    at org.apache.hadoop.util.Shell.getWinUtilsPath(Shell.java:370)  
    at org.apache.hadoop.util.Shell.<clinit>(Shell.java:363)  
    at org.apache.hadoop.util.StringUtils.<clinit>(StringUtils.java:79)  
    at org.apache.hadoop.security.Groups.parseStaticMapping(Groups.java:104)  
    at org.apache.hadoop.security.Groups.<init>(Groups.java:86)  
    at org.apache.hadoop.security.Groups.<init>(Groups.java:66)  
    at org.apache.hadoop.security.Groups.getUserToGroupsMappingService(Groups.java:280)  
    at org.apache.hadoop.security.UserGroupInformation.initialize(UserGroupInformation.java:271)  
    at org.apache.hadoop.security.UserGroupInformation.ensureInitialized(UserGroupInformation.java:248)  
    at org.apache.hadoop.security.UserGroupInformation.loginUserFromSubject(UserGroupInformation.java:763)  
    at org.apache.hadoop.security.UserGroupInformation.getLoginUser(UserGroupInformation.java:748)  
    at org.apache.hadoop.security.UserGroupInformation.getCurrentUser(UserGroupInformation.java:621)  
    at org.apache.spark.util.Utils$$anonfun$getCurrentUserName$1.apply(Utils.scala:2001)  
    at org.apache.spark.util.Utils$$anonfun$getCurrentUserName$1.apply(Utils.scala:2001)  
    at scala.Option.getOrElse(Option.scala:120)  
    at org.apache.spark.util.Utils$.getCurrentUserName(Utils.scala:2001)  
    at org.apache.spark.SecurityManager.<init>(SecurityManager.scala:207)  
    at org.apache.spark.SparkEnv$.create(SparkEnv.scala:218)  
    at org.apache.spark.SparkEnv$.createDriverEnv(SparkEnv.scala:163)  
    at org.apache.spark.SparkContext.createSparkEnv(SparkContext.scala:269)  
    at org.apache.spark.SparkContext.<init>(SparkContext.scala:272)  
    at org.apache.spark.streaming.StreamingContext$.createNewSparkContext(StreamingContext.scala:643)  
    at org.apache.spark.streaming.StreamingContext.<init>(StreamingContext.scala:75)  
    at org.apache.spark.streaming.api.java.JavaStreamingContext.<init>(JavaStreamingContext.scala:132)  
    at com.jd.security.spark.streaming.KafkaDirectConsumer.main(KafkaDirectConsumer.java:122)  
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)  
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)  
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)  
    at java.lang.reflect.Method.invoke(Method.java:606)  
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:140)  

```

在我的程序代码中使用如下语句进行checkpoint:

```
jssc.checkpoint("D:/debug/checkpoint" );  
```

虽然checkpoint的目录是pc的本地目录，但是在执行checkpoint的时候同时会报如下错误：

```
Exception in thread "pool-7-thread-1" java.lang.NullPointerException  
    at java.lang.ProcessBuilder.start(ProcessBuilder.java:1010)  
    at org.apache.hadoop.util.Shell.runCommand(Shell.java:482)  
    at org.apache.hadoop.util.Shell.run(Shell.java:455)  
    at org.apache.hadoop.util.Shell$ShellCommandExecutor.execute(Shell.java:715)  
    at org.apache.hadoop.util.Shell.execCommand(Shell.java:808)  
    at org.apache.hadoop.util.Shell.execCommand(Shell.java:791)  
    at org.apache.hadoop.fs.RawLocalFileSystem.setPermission(RawLocalFileSystem.java:656)  
    at org.apache.hadoop.fs.FilterFileSystem.setPermission(FilterFileSystem.java:490)  
    at org.apache.hadoop.fs.ChecksumFileSystem.create(ChecksumFileSystem.java:462)  
    at org.apache.hadoop.fs.ChecksumFileSystem.create(ChecksumFileSystem.java:428)  
    at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:908)  
    at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:889)  
    at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:786)  
    at org.apache.hadoop.fs.FileSystem.create(FileSystem.java:775)  
    at org.apache.spark.streaming.CheckpointWriter$CheckpointWriteHandler.run(Checkpoint.scala:141)  
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)  
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)  
    at java.lang.Thread.run(Thread.java:744)  
```

问题解决方法：
在spark程序开始执行的时候，设置hadoop.home.dir程序环境变量，也就是添加如下语句：

System.setProperty("hadoop.home.dir", "D:\\download\\hadoop-common-2.2.0-bin-master");  

其中，D:\\download\\hadoop-common-2.2.0-bin-master是从 [hadoop-common-2.2.0-bin-master](https://github.com/srccodes/hadoop-common-2.2.0-bin/archive/master.zip)下载的zip包解压后的路径

问题原因：虽然checkpoint到本地目录，但是在spark的底层调用里面还是用到了hadoop的api，hadoop的api里面用到了hadoop.home.dir环境变量，程序又没有指定，所以报错了。


这种解决方法是这个问题的简易解决方法，另外一种方法是安装windows hadoop环境，这种方法比较繁琐，感兴趣的同学可以试试

转自：http://blog.csdn.net/u012684933/article/details/46124957