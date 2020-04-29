# 前言

Apache HBase 是目前大数据系统中应用最为广泛的分布式数据库之一。我们经常面临向 HBase 中导入大量数据的情景，通常会选择使用标准的客户端 API 对 HBase 进行直接的操作，或者在MapReduce作业中使用 TableOutputFormat 作为输出。实际上，借助 HBase 的 Bulk Load 特性可以更加便捷、快速地向HBase数据库中导入数据。

MapReduce 在写入 HBase 时常采用 TableOutputFormat 方式，直接写入 HBase，但该方式在大量数据写入时效率比较低下（频繁进行 flush、split、compat等I/O操作），并对 HBase 节点稳定性造成影响（ RegionServer 无响应）。

HBase的数据实际上是以特定格式存储在 HDFS 上的，因而 Bulk Load 就是先将数据按照HBase的内部数据格式生成持久化的 HFile 文件，然后复制到合适的位置并通知 RegionServer ，即完成巨量数据的入库。在生成 HFile 时无需占用 Region 资源，降低了 HBase 节点的写入压力，在大量数据写入时能极大地提高写入效率。


# Bulk Load 简介

使用 Bulk Load 特性将数据导入 HBase 通常需要分为三个阶段：

## 从数据源中提取数据

通常需要导入的外部数据都是存储在其它的关系型数据库或一些文本文件中，我们需要将数据提取出来并放置于 HDFS 中。借助 Sqoop 这一工具可以解决大多数关系型数据库向 HDFS 迁移数据的问题。

## 通过 MapReduce 任务生成 HFile

在进行数据导入时，需要对数据进行预处理，如过滤无效数据、数据格式转换等。通常按照不同的导入要求，需要编写不同的 Mapper；Reducer 由 HBase 负责处理。为了按照 HBase 内部存储格式生成数据，一个重要的类是 HFileOutputFormat2(HBase 1.0.0以前版本使用 HFileOutputFormat)。为了更有效地导入数据， 每一个输出的 HFile 要恰好适应一个 Region。为了确保这一点， 需要使用 TotalOrderPartitioner 类将 map 的输出切分为 key 互不相交的部分。HFileOutputFormat2 类中的 configureIncrementalLoad() 方法会依据当前表中的 Region 边界自动设置 TotalOrderPartitioner。

## 完成数据导入

一旦数据准备好，就可以使用 completebulkload 工具将生成的 HFile 导入HBase 集群中。completebulkload 是一个命令行工具，对生成的 HFile 文件迭代进行处理，对每一个 HFile， 确定所属的 region， 然后联系对应的 RegionServer， 将数据移动至相应的存储路径。

如果在准备数据过程中，或者在使用 completebulkload 导入数据过程中， region 的边界发生了改变（split）， completebulkload 工具会按照新的边界自动切分数据文件。这个过程可能会对性能造成影响。

除了使用 completebulkload 工具外，也可以在程序中完成, LoadIncrementalHFiles 类提供了相应的方法。


# Bulk Load实例

这里给出一个简单的例子，旨在说明如何使用 MapReduce 和 Bulk Load 将数据导入到HBase中。这里不介绍如何将数据迁移至 HDFS 中，重点关注 HFile 的生成及载入。

## App.java

创建 MapReduce 作业
```
private static Job createCommitableJob(String tableNameStr, String inputPathStr, String outputPathStr) {
    Configuration conf = HBaseConfiguration.create(new Configuration());
    JobClient client = new JobClient(conf);

    Path inputPath = new Path(inputPathStr);
    Path outputPath = new Path(outputPathStr);

    Job job = Job.getInstance(conf, "load_data_to_" + tableNameStr);
    job.setJarByClass(App.class);

    FileInputFormat.setInputPaths(job, inputPath);
    job.setInputFormatClass(TextInputFormat.class);

    //set mapper class according to job type.
    switch (tableNameStr) {
        case RECORD:
            job.setMapperClass(RecordMapper.class);
            break;
        case XXX:
            job.setMapperClass(XXXMapper.class);
            break;
        case XXXX:
            job.setMapperClass(XXXXMapper.class);
            break;
        default:
            return null;
    }
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(Put.class);
    job.setReducerClass(PutSortReducer.class);

    FileSystem hdfs = FileSystem.newInstance(conf);
    if (null != hdfs) {
        if (hdfs.exists(outputPath)) {
            hdfs.delete(outputPath, true);
        }
    }
    FileOutputFormat.setOutputPath(job, outputPath);

    //for hbase version 1.0.0+
    Connection connection = ConnectionFactory.createConnection(conf);
    TableName tableName = TableName.valueOf(tableNameStr);
    Table table = connection.getTable(tableName);
    HFileOutputFormat2.configureIncrementalLoad(job, table,connection.getRegionLocator(tableName));

    //for hbase 0.96
    /*HTable table = new HTable(conf, tableNameStr);
    HFileOutputFormat.configureIncrementalLoad(job, table);
    TableMapReduceUtil.addDependencyJars(job);
    TableMapReduceUtil.addDependencyJars(job.getConfiguration(), com.google.common.base.Function.class);*/

    return job;
}
```

运行 MapReduce 作业

```
//先一些初始化操作，获取作业基本信息，如路径、表名等。
//创建 MapReduce 作业
Job job = createCommitableJob(tableNameStr, inputPathStr, outputPathStr);
if (job == null) {
    LOG.error("Error in create job!");
    return;
}
if (job.waitForCompletion(true)) {
    Counter counter = job.getCounters().findCounter(TaskCounter.MAP_OUTPUT_RECORDS);
    LOG.info("job finished, total " + counter.getValue() + " records!");

    //完成 mapreduce 作业后，使用 bulk load导入数据
    Utils.doBulkLoad(conf, hfilePathStr, tableNameStr);
} else {
    //作业运行失败
    LOG.error("job failed!");
}
```


## RecordMapper.java

定制 Mapper 类，负责对数据进行预处理，如过滤，转换等。
```
public class RecordMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

    private static final byte[] FAMILY_BYTE = Bytes.toBytes(CommonConfig.HBASE_FAMILY);
    private static final byte[] QUALIFER_BYTE = Bytes.toBytes(CommonConfig.HBASE_QUALIFER);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //一些准备工作
        //...
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String record = value.toString();
        //过滤无效数据
        if (isVaild()) {
            //数据处理，如格式转换等操作
            //获取RowKey
            byte[] rkValu = getRowKey(record);
            ImmutableBytesWritable rowKey = new ImmutableBytesWritable(rkValue);
            //创建Put对象
            Put put = new Put(rowKey.copyBytes());

            //获取TimeStamp
            long timestamp = getTimeStamp(record);

            //获取应该插入到HBase中的一个cell
            String cellValue = getCellValue(record);

            //将待插入数据存放至Put对象中
            put.add(FAMILY_BYTE, QUALIFER_BYTE, timestamp, Bytes.toBytes(cellValue));
            context.write(rowKey, put);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        //map完成后的一些清理工作
    }
}
```


## Utils.java

一些辅助方法。这里给出如何在程序中直接使用 Bulk Load，而无需通过命令行工具。
```
/**
 * loading HFile in 'hFilePath' to HBase, target HTable's name is 'tableNameStr'
 * @param  conf         Configuration instance
 * @param  hFilePath
 * @param  tableNameStr
 * @throws Exception
 */
public static void doBulkLoad(Configuration conf, String hFilePath, String tableNameStr) throws Exception{
    //change permission first.
    FileSystem fs = FileSystem.newInstance(conf);
    chmod(new Path(hFilePath), fs);
    //do bulk load.
    HBaseConfiguration.addHbaseResources(conf);
    LoadIncrementalHFiles loadFiles = new LoadIncrementalHFiles(conf);
    Connection connection = ConnectionFactory.createConnection(conf);
    TableName tableName = TableName.valueOf(tableNameStr);
    Table table = connection.getTable(tableName);
    loadFiles.doBulkLoad(new Path(hFilePath), table);
}
``` 

MapReduce 作业生成的文件存放在 HDFS 上时，其权限归运行 MapReduce 作业的用户所有。在使用 Bulk Load 导入数据时， 需要将权限赋给 hbase 用户。简单粗暴的方法就是将文件夹的权限改为“777”， 下面的方法实现了该功能。

```
/**
 * change the permission of a give path to 777, all subdir are changed recursively.
 * @param path
 * @param fs
 * @throws IOException
 */
public static void chmod(Path path, FileSystem fs) throws IOException {
    fs.setPermission(path, FsPermission.createImmutable(FULL_GRANTS));
    if (fs.getFileStatus(path).isFile()) {
        return;
    }
    RemoteIterator<LocatedFileStatus> fileStatuses = fs.listLocatedStatus(path);
    while(fileStatuses.hasNext()) {
        LocatedFileStatus status = fileStatuses.next();
        if (status != null) {
            fs.setPermission(status.getPath(), FsPermission.createImmutable(FULL_GRANTS));
            chmod(status.getPath(), fs);
        }
    }
}
```


# 其他说明

1.在 HFileOutputFormat2.configureIncrementalLoad 方法中，MapReduce 作业的很多配置都自动完成了。从源码中可以看出，该方法中主要完成了以下几点：

* 设置作业输出的 key、value 类为 ImmutableBytesWritable 和 KeyValue
* 设置作业的 OutputFormat 类为 HFileOutputFormat2.class
* 根据作业 Map 的输出设置合适 Reduce 类。Map 输出 key 必须为 ImmutableBytesWritable，Value 类型为 分别为 KeyValue、 Put、和 Text，对应的Reducer 分别为 KeyValueSortReducer.class、PutSortReducer.class 和 TextSortReducer.class。
* 根据当前 region 数量确定 Reduce 的数量
* 调用 configurePartitioner 方法配置 TotalOrderPartitioner


2.Reduce 没有 setNumReduceTasks 是因为，该设置是根据该表当前 region 数量自动配置的。在建表时应当做好 region 的预切分， HFileOutputFormat.configureIncrementalLoad() 方法会根据 region 的数量来决定 reduce 的数量以及每个 reduce 覆盖的 rowkey 范围。否则当单个 reduce 过大时，任务处理不均衡。

3.completebulkload 工具使用方法： hadoop jar hbase-server-VERSION.jar completebulkload [-c /path/to/hbase/config/hbase-site.xml] /path/to/output table


bulk load的局限
因为数据是直接写入到hfile中的，所以不会走WAL


转自：http://blog.jrwang.me/2015/import-data-to-hbase-using-bulk-loding/
