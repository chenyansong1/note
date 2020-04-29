---
title: spark core之spark-submit说明
categories: spark   
toc: true  
tag: [spark]
---



将我们的spark工程打包好之后，就可以使用spark-submit脚本提交工程中的spark应用了spark-submit脚本会设置好spark的classpath环境变量（用于类加载）和相关的依赖，而且还可以支持多种不同的集群管理器和不同的部署模式

<!--more-->

# spark-submit脚本参数说明

一般会将执行spark-submit脚本的命令，放置在一个自定义的shell脚本里面，所以说这是比较灵活的一种做法,推荐使用

wordcount.sh
```
/usr/local/spark/bin/spark-submit \
--class org.leo.spark.study.WordCount \
--master spark://192.168.0.101:7077 \
--deploy-mode client \
--conf <key>=<value> \
/usr/local/spark-study/spark-study.jar \
${1}

```

以下是上面的spark-submit参数说明

--class: spark应用程序对应的主类，也就是spark应用运行的主入口，通常是一个包含了main方法的java类或scala类，需要包含全限定包名，比如org.leo.spark.study.WordCount
--master: spark集群管理器的master URL，standalone模式下，就是ip地址+端口号，比如spark://192.168.0.101:7077，standalone默认端口号就是7077
--deploy-mode: 部署模式，决定了将driver进程在worker节点上启动，还是在当前本地机器上启动；默认是client模式，就是在当前本地机器上启动driver进程，如果是cluster，那么就会在worker上启动
--conf: 配置所有spark支持的配置属性，使用key=value的格式；如果value中包含了空格，那么需要将key=value包裹的双引号中
application-jar: 打包好的spark工程jar包，在当前机器上的全路径名
application-arguments: 传递给主类的main方法的参数; 在shell中用${1}这种格式获取传递给shell的参数；然后在比如java中，可以通过main方法的args[0]等参数获取


# spark-submit给main类传递参数
下面是伪代码:

```
main(String[] args){
	val conf = new SparkConf().setAppName("WordCount")
	val sc = new SparkContext(conf)

	val file = _
	if(args!=null && args.length>0){
		println("=====接收到了参数:"+args(0))
		file = args(0)
	}else{
		file="hdfs://hadoop-node1:9000/text/hello.txt"
	}

	val rdd = sc.textFile(file)
	
	//....
	

	sc.close
	
}

```

spark-submit提交脚本

wordcount.sh

```
/usr/local/spark/bin/spark-submit \
--class org.leo.spark.study.WordCount \
--master spark://192.168.0.101:7077 \
--deploy-mode client \
--conf <key>=<value> \
/usr/local/spark-study/spark-study.jar \
${1}

```


执行脚本
```
wordcount.sh hdfs://hadoop-node1:9000/test/hello.txt
```


# spark-submit多个示例,及常用参数详解

```
# 使用local本地模式，以及8个线程运行
# --class 指定要执行的main类
# --master 指定集群模式，local，本地模式，local[8]，进程中用几个线程来模拟集群的执行
./bin/spark-submit \
  --class org.leo.spark.study.WordCount \
  --master local[8] \
  /usr/local/spark-study.jar \
```

```
# 使用standalone client模式运行
# executor-memory，指定每个executor的内存量，这里每个executor内存是2G
# total-executor-cores，指定所有executor的总cpu core数量，这里所有executor的总cpu core数量是100个
./bin/spark-submit \
  --class org.leo.spark.study.WordCount \
  --master spark://192.168.0.101:7077 \
  --executor-memory 2G \
  --total-executor-cores 100 \
  /usr/local/spark-study.jar \

```

```
# 使用standalone cluster模式运行
# supervise参数，指定了spark监控driver节点，如果driver挂掉，自动重启driver
./bin/spark-submit \
  --class org.leo.spark.study.WordCount \
  --master spark://192.168.0.101:7077 \
  --deploy-mode cluster \
  --supervise \
  --executor-memory 2G \
  --total-executor-cores 100 \
  /usr/local/spark-study.jar \
```

```
# 使用yarn-cluster模式运行
# num-executors，指定总共使用多少个executor运行spark应用
./bin/spark-submit \
  --class org.leo.spark.study.WordCount \
  --master yarn-cluster \  
  --executor-memory 20G \
  --num-executors 50 \
  /usr/local/spark-study.jar \

```

```
# 使用standalone client模式，运行一个python应用
./bin/spark-submit \
  --master spark://192.168.0.101:7077 \
  /usr/local/python-spark-wordcount.py \

--class
application jar
--master
--num-executors
--executor-cores 
--total-executor-cores
--executor-memory
--driver-memory 
--supervise


在实际生产环境中的配置如下:
./bin/spark-submit \
  --class org.leo.spark.study.WordCount \
  --master yarn-cluster \
  --num-executors 100 \
  --executor-cores 2 \
  --executor-memory 6G \
  --driver-memory  1G \
  /usr/local/spark-study.jar \

```

# sparkConf,spark-submit以及spark-defaultconf优先级


默认的配置属性

spark-submit脚本会自动加载conf/spark-defaults.conf文件中的配置属性，并传递给我们的spark应用程序
加载默认的配置属性，一大好处就在于，我们不需要在spark-submit脚本中设置所有的属性
比如说，默认属性中有一个spark.master属性，所以我们的spark-submit脚本中，就不一定要显式地设置--master，默认就是local
```
SparkConf.getOrElse("spark.master", "local")

spark配置的优先级如下: SparkConf、spark-submit、spark-defaults.conf

spark.default.parallelism

SparkConf.set("spark.default.parallelism", "100")
spark-submit: --conf spark.default.parallelism=50
spark-defaults.conf: spark.default.parallelism 10
```

如果想要了解更多关于配置属性的信息，可以在spark-submit脚本中，使用--verbose，打印详细的调试信息

使用spark-submit设置属性

虽然说SparkConf设置属性的优先级是最高的，但是有的时候咱们可能不希望在代码中硬编码一些配置属性，否则每次修改了参数以后,还得去代码里修改，然后得重新打包应用程序，再部署到生产机器上去，非常得麻烦

对于上述的情况，我们可以在代码中仅仅创建一个空的SparkConf对象，比如: val sc = new SparkContext(new SparkConf())

然后可以在spark-submit脚本中，配置各种属性的值，比如

./bin/spark-submit \
  --name "My app" \
  --master local[4] \
  --conf spark.shuffle.spill=false \
  --conf "spark.executor.extraJavaOptions=-XX:+PrintGCDetails -XX:+PrintGCTimeStamps" \
  myApp.jar

这里的spark.shuffle.spill属性，我们本来如果是在代码中，SparkConf.set("spark.shuffle.spill", "false")来配置的
此时在spark-submit中配置了，不需要更改代码，就可以更改属性，非常得方便，
尤其是对于spark程序的调优，格外方便，因为调优说白了，就是不断地调整各种各样的参数，然后反复跑反复试的过程




spark的属性配置方式

spark-shell和spark-submit两个工具，都支持两种加载配置的方式
一种是基于命令行参数，比如上面的--master，spark-submit可以通过--conf参数，接收所有spark属性
另一种是从conf/spark-defaults.conf文件中加载，其中每一行都包括了一个key和value
比如spark.executor.memory 4g

所有在SparkConf、spark-submit和spark-defaults.conf中配置的属性，在运行的时候，都会被综合使用
直接通过SparkConf设置的属性，优先级是最高的，会覆盖其余两种方式设置的属性
其次是spark-submit脚本中通过--conf设置的属性
最后是spark-defaults.conf中设置的属性

通常来说，如果你要对所有的spark作业都生效的配置，放在spark-defaults.conf文件中，只要将spark-defaults.conf.template拷贝成那个文，然后在其中编辑即可
然后呢，对于某个spark作业比较特殊的配置，推荐放在spark-submit脚本中，用--conf配置，比较灵活
SparkConf配置属性，有什么用呢？也有用，在eclipse中用local模式执行运行的时候，那你就只能在SparkConf中设置属性了

这里还有一种特例，就是说，在新的spark版本中，可能会将一些属性的名称改变，那些旧的属性名称就变成过期的了
此时旧的属性名称还是会被接受的，但是新的属性名称会覆盖掉旧的属性名称，并且优先级是比旧属性名称更高的

举例来说
shuffle reduce read操作的内存缓冲块儿
spark 1.3.0: spark.reducer.maxMbInFlight
spark 1.5.0: spark.reducer.maxSizeInFlight


# spark-submit配置第三方依赖

使用spark-submit脚本提交spark application时，application jar，还有我们使用--jars命令绑定的其他jar，都会自动被发送到集群上去
**--jar**
spark支持以下几种URL来指定关联的其他jar
```
file: 是由driver的http文件服务提供支持的，所有的executor都会通过driver的HTTP服务来拉取文件
hdfs:，http:，https:，ftp:，这种文件，就是直接根据URI，从指定的地方去拉取，比如hdfs、或者http链接、或者ftp服务器
local: 这种格式的文件必须在每个worker节点上都要存在，所以不需要通过网络io去拉取文件，这对于特别大的文件或者jar包特别适用，可以提升作业的执行性能

--jars，比如，mysql驱动包，或者是其他的一些包
```

**文件清理**

文件和jar都会被拷贝到每个executor的工作目录中，这就会占用很大一片磁盘空间，因此需要在之后清理掉这些文件,在yarn上运行spark作业时，依赖文件的清理都是自动进行的,适用standalone模式，需要配置spark.worker.cleanup.appDataTtl属性，来开启自动清理依赖文件和jar包,在spark-env.sh中如下配置:

```
SPARK_WORKER_OPTS				worker的额外参数，使用"-Dx=y"设置各个参数

参数名											默认值						含义
spark.worker.cleanup.enabled					false						是否启动自动清理worker工作目录，默认是false
spark.worker.cleanup.interval					1800						单位秒，自动清理的时间间隔，默认是30分钟
spark.worker.cleanup.appDataTtl					7 * 24 * 3600				默认将一个spark作业的文件在worker工作目录保留多少时间，默认是7天

```

**--file**

用户还可以通过在spark-submit中，使用--packages，绑定一些maven的依赖包,此外，还可以通过--repositories来绑定过一些额外的仓库,但是说实话，这两种情况还的确不太常见

--files，比如，最典型的就是hive-site.xml配置文件










