Hbase的表会被划分为1....n个Region,被托管在RegionServer中。Region二个重要的属性：Startkey与EndKey表示这个Region维护的rowkey的范围，当我们要读写数据时，如果rowkey落在某个start-end key范围内，那么就会定位到目标region并且读写到相关的数据。

默认情况下，当我们通过hbaseAdmin指定TableDescriptor来创建一张表时，只有一个region正处于混沌时期，start-end key无边界，可谓海纳百川。所有的rowkey都写入到这个region里，然后数据越来越多，region的size越来越大时，大到一定的阀值，hbase就会将region一分为二，成为2个region，这个过程称为分裂（region-split）。

如果我们就这样默认建表，表里不断的put数据，更严重的是我们的rowkey还是顺序增大的，是比较可怕的。存在的缺点比较明显：首先是热点写，我们总是向最大的start key所在的region写数据，因为我们的rowkey总是会比之前的大，并且hbase的是按升序方式排序的。所以写操作总是被定位到无上界的那个region中；其次，由于热点，我们总是往最大的start key的region写记录，之前分裂出来的region不会被写数据，有点打入冷宫的感觉，他们都处于半满状态，这样的分布也是不利的。

如果在写比较频繁的场景下，数据增长太快，split的次数也会增多，由于split是比较耗费资源的，所以我们并不希望这种事情经常发生。

在集群中为了得到更好的并行性，我们希望有好的load blance，让每个节点提供的请求都是均衡的，我们也不希望，region不要经常split，因为split会使server有一段时间的停顿，如何能做到呢？

**随机散列与预分区二者结合起来，是比较完美的**。预分区一开始就预建好了一部分region，这些region都维护着自己的start-end keys，在配合上随机散列，写数据能均衡的命中这些预建的region，就能解决上面的那些缺点，大大提供性能。

# 1.解决思路

提供两种思路：hash与partition。


## 1.1.hash方案
 
hash就是rowkey前面由一串随机字符串组成，随机字符串生成方式可以由SHA或者MD5方式生成，只要region所管理的start-end keys范围比较随机，那么就可以解决写热点问题。例如：

```

long currentId = 1L;  
byte [] rowkey = Bytes.add(MD5Hash.getMD5AsHex(Bytes.toBytes(currentId)).substring(0, 8).getBytes(),Bytes.toBytes(currentId));  

```

假如rowkey原本是自增长的long型，可以将rowkey转为hash再转为bytes，加上本身id转为bytes，这样就生成随便的rowkey。那么对于这种方式的rowkey设计，如何去进行预分区呢？

* 取样，先随机生成一定数量的rowkey，将取样数据按升序排序放到一个集合里。
* 根据预分区的region个数，对整个集合平均分割，即是相关的splitkeys。
* HBaseAdmin.createTable(HTableDescriptor tableDescriptor,byte[][] splitkeys)可以指定预分区的splitkey，即指定region间的rowkey临界值。


创建split计算器，用于从抽样数据生成一个比较合适的splitkeys

```
public class HashChoreWoker implements SplitKeysCalculator{  
    //随机取机数目  
    private int baseRecord;  
    //rowkey生成器  
    private RowKeyGenerator rkGen;  
    //取样时，由取样数目及region数相除所得的数量.  
    private int splitKeysBase;  
    //splitkeys个数  
    private int splitKeysNumber;  
    //由抽样计算出来的splitkeys结果  
    private byte[][] splitKeys;  
  
    public HashChoreWoker(int baseRecord, int prepareRegions) {  
        this.baseRecord = baseRecord;  
        //实例化rowkey生成器  
        rkGen = new HashRowKeyGenerator();  
        splitKeysNumber = prepareRegions - 1;  
        splitKeysBase = baseRecord / prepareRegions;  
    }  
  
    public byte[][] calcSplitKeys() {  
        splitKeys = new byte[splitKeysNumber][];  
        //使用treeset保存抽样数据，已排序过  
        TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);  
        for (int i = 0; i < baseRecord; i++) {  
            rows.add(rkGen.nextId());  
        }  
        int pointer = 0;  
        Iterator<byte[]> rowKeyIter = rows.iterator();  
        int index = 0;  
        while (rowKeyIter.hasNext()) {  
            byte[] tempRow = rowKeyIter.next();  
            rowKeyIter.remove();  
            if ((pointer != 0) && (pointer % splitKeysBase == 0)) {  
                if (index < splitKeysNumber) {  
                    splitKeys[index] = tempRow;  
                    index ++;  
                }  
            }  
            pointer ++;  
        }  
        rows.clear();  
        rows = null;  
        return splitKeys;  
    }  
}  
```


 KeyGenerator及实现

```
//interface  
public interface RowKeyGenerator {  
    byte [] nextId();  
}  
//implements  
public class HashRowKeyGenerator implements RowKeyGenerator {  
    private long currentId = 1;  
    private long currentTime = System.currentTimeMillis();  
    private Random random = new Random();  
    public byte[] nextId() {  
        try {  
            currentTime += random.nextInt(1000);  
            byte[] lowT = Bytes.copy(Bytes.toBytes(currentTime), 4, 4);  
            byte[] lowU = Bytes.copy(Bytes.toBytes(currentId), 4, 4);  
            return Bytes.add(MD5Hash.getMD5AsHex(Bytes.add(lowU, lowT)).substring(0, 8).getBytes(),  
                    Bytes.toBytes(currentId));  
        } finally {  
            currentId++;  
        }  
    }  
} 
```

unit test case测试

```
@Test  
public void testHashAndCreateTable() throws Exception{  
    HashChoreWoker worker = new HashChoreWoker(1000000,10);  
    byte [][] splitKeys = worker.calcSplitKeys();  
      
    HBaseAdmin admin = new HBaseAdmin(HBaseConfiguration.create());  
    TableName tableName = TableName.valueOf("hash_split_table");  
      
    if (admin.tableExists(tableName)) {  
        try {  
            admin.disableTable(tableName);  
        } catch (Exception e) {  
        }  
        admin.deleteTable(tableName);  
    }  

    HTableDescriptor tableDesc = new HTableDescriptor(tableName);  
    HColumnDescriptor columnDesc = new HColumnDescriptor(Bytes.toBytes("info"));  
    columnDesc.setMaxVersions(1);  
    tableDesc.addFamily(columnDesc);  

    admin.createTable(tableDesc ,splitKeys);  

    admin.close();  
}  

```

查看建表结果，执行：scan 'hbase:meta'

也可以到hdfs中的：/hbase/data/default/tableName/下查看分区的情况

以上就是按照hash方式，预建好分区，以后再插入数据的时候，也是按照此rowkeyGenerator的方式生成rowkey。


# 1.2.partition的方式

partition顾名思义就是分区式，这种分区有点类似于mapreduce中的partitioner，将区域用长整数作为分区号，每个region管理着相应的区域数据，在rowkey生成时，将ID取模后，然后拼上ID整体作为rowkey，这个比较简单，不需要取样，splitkeys也非常简单，直接是分区号即可。直接上代码：

```
public class PartitionRowKeyManager implements RowKeyGenerator, SplitKeysCalculator {  
  
    public static final int DEFAULT_PARTITION_AMOUNT = 20;  
    private long currentId = 1;  
    private int partition = DEFAULT_PARTITION_AMOUNT;  
    public void setPartition(int partition) {  
        this.partition = partition;  
    }  
  
    public byte[] nextId() {
        try {
            long partitionId = currentId % partition;  
            return Bytes.add(Bytes.toBytes(partitionId),  
                    Bytes.toBytes(currentId));  
        } finally {
            currentId++;  
        }
    }
  
    public byte[][] calcSplitKeys() {
        byte[][] splitKeys = new byte[partition - 1][];  
        for(int i = 1; i < partition ; i ++) {
            splitKeys[i-1] = Bytes.toBytes((long)i);    
        }
        return splitKeys;  
    }
}
```

 calcSplitKeys方法比较单纯，splitkey就是partition的编号，测试类如下：

```
@Test  
public void testPartitionAndCreateTable() throws Exception{  
      
    PartitionRowKeyManager rkManager = new PartitionRowKeyManager();  
    //只预建10个分区  
    rkManager.setPartition(10);  
      
    byte [][] splitKeys = rkManager.calcSplitKeys();  
      
    HBaseAdmin admin = new HBaseAdmin(HBaseConfiguration.create());  
    TableName tableName = TableName.valueOf("partition_split_table");  
      
    if (admin.tableExists(tableName)) {  
        try {  
            admin.disableTable(tableName);  

        } catch (Exception e) {  
        }  
        admin.deleteTable(tableName);  
    }  

    HTableDescriptor tableDesc = new HTableDescriptor(tableName);  
    HColumnDescriptor columnDesc = new HColumnDescriptor(Bytes.toBytes("info"));  
    columnDesc.setMaxVersions(1);  
    tableDesc.addFamily(columnDesc);  

    admin.createTable(tableDesc ,splitKeys);  

    admin.close();  
} 

```

同样我们可以看看meta表和hdfs的目录结果，其实和hash类似，region都会分好区。

通过partition实现的loadblance写的话，当然生成rowkey方式也要结合当前的region数目取模而求得，大家同样也可以做些实验，看看数据插入后的分布。
在这里也顺提一下，如果是顺序的增长型原id,可以将id保存到一个数据库，传统的也好,redis的也好，每次取的时候，将数值设大1000左右，以后id可以在内存内增长，当内存数量已经超过1000的话，再去load下一个，有点类似于oracle中的sqeuence.

随机分布加预分区也不是一劳永逸的。因为数据是不断地增长的，随着时间不断地推移，已经分好的区域，或许已经装不住更多的数据，当然就要进一步进行split了，同样也会出现性能损耗问题，所以我们还是要规划好数据增长速率，观察好数据定期维护，按需分析是否要进一步分行手工将分区再分好，也或者是更严重的是新建表，做好更大的预分区然后进行数据迁移。如果数据装不住了，对于partition方式预分区的话，如果让它自然分裂的话，情况分严重一点。因为分裂出来的分区号会是一样的，所以计算到partitionId的话，其实还是回到了顺序写年代，会有部分热点写问题出现，如果使用partition方式生成主键的话，数据增长后就要不断地调整分区了，比如增多预分区，或者加入子分区号的处理.(我们的分区号为long型，可以将它作为多级partition)

以上基本已经讲完了防止热点写使用的方法和防止频繁split而采取的预分区。但rowkey设计，远远也不止这些，比如rowkey长度，然后它的长度最大可以为char的MAXVALUE,但是看过之前我写KeyValue的分析知道，我们的数据都是以KeyValue方式存储在MemStore或者HFile中的，每个KeyValue都会存储rowKey的信息，如果rowkey太大的话，比如是128个字节，一行10个字段的表，100万行记录，光rowkey就占了1.2G+所以长度还是不要过长，另外设计，还是按需求来吧。



# hash随机rowkey的详细代码如下

```
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.hbase.HBaseConfiguration;  
import org.apache.hadoop.hbase.HColumnDescriptor;  
import org.apache.hadoop.hbase.HTableDescriptor;  
import org.apache.hadoop.hbase.KeyValue;  
import org.apache.hadoop.hbase.MasterNotRunningException;  
import org.apache.hadoop.hbase.TableName;  
import org.apache.hadoop.hbase.ZooKeeperConnectionException;  
import org.apache.hadoop.hbase.client.Get;  
import org.apache.hadoop.hbase.client.HBaseAdmin;  
import org.apache.hadoop.hbase.client.HTable;  
import org.apache.hadoop.hbase.client.HTablePool;  
import org.apache.hadoop.hbase.client.Put;  
import org.apache.hadoop.hbase.client.Result;  
import org.apache.hadoop.hbase.client.ResultScanner;  
import org.apache.hadoop.hbase.client.Scan;  
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;  
import org.apache.hadoop.hbase.filter.Filter;  
import org.apache.hadoop.hbase.filter.FilterList;  
import org.apache.hadoop.hbase.filter.PrefixFilter;  
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;  
import org.apache.hadoop.hbase.util.Bytes;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
  
import com.kktest.hbase.HashChoreWoker;  
import com.kktest.hbase.HashRowKeyGenerator;  
import com.kktest.hbase.RowKeyGenerator;  
import com.kktest.hbase.BitUtils;  
  
/** 
 * hbase 客户端 
 *  
 * @author kuang hj 
 *  
 */  
@SuppressWarnings("all")  
public class HBaseClient {  
  
    private static Logger logger = LoggerFactory.getLogger(HBaseClient.class);  
    private static Configuration config;  
    static {  
        config = HBaseConfiguration.create();  
        config.set("hbase.zookeeper.quorum",  
                "192.168.1.100:2181,192.168.1.101:2181,192.168.1.103:2181");  
    }  
  
    /** 
     * 根据随机散列（hash）创建分区表 
     *  
     * @throws Exception 
     *             hash_split_table 
     */  
    public static void testHashAndCreateTable(String tableNameTmp,  
            String columnFamily) throws Exception {<p>        // 取随机散列 10 代表 10个分区  
        HashChoreWoker worker = new HashChoreWoker(1000000, 10);  
        byte[][] splitKeys = worker.calcSplitKeys();  
  
        HBaseAdmin admin = new HBaseAdmin(config);  
        TableName tableName = TableName.valueOf(tableNameTmp);  
  
        if (admin.tableExists(tableName)) {  
            try {  
                admin.disableTable(tableName);  
            } catch (Exception e) {  
            }  
            admin.deleteTable(tableName);  
        }  
  
        HTableDescriptor tableDesc = new HTableDescriptor(tableName);  
        HColumnDescriptor columnDesc = new HColumnDescriptor(  
                Bytes.toBytes(columnFamily));  
        columnDesc.setMaxVersions(1);  
        tableDesc.addFamily(columnDesc);  
  
        admin.createTable(tableDesc, splitKeys);  
  
        admin.close();  
    }  
  
    /** 
     * @Title: queryData 
     * @Description: 从HBase查询出数据 
     * @author kuang hj 
     * @param tableName 
     *            表名 
     * @param rowkey 
     *            rowkey 
     * @return 返回用户信息的list 
     * @throws Exception 
     */  
    @SuppressWarnings("all")  
    public static ArrayList<String> queryData(String tableName, String rowkey)  
            throws Exception {  
        ArrayList<String> list = new ArrayList<String>();  
        logger.info("开始时间");  
        HTable table = new HTable(config, tableName);  
  
        Get get = new Get(rowkey.getBytes()); // 根据主键查询  
        Result r = table.get(get);  
        logger.info("结束时间");  
        KeyValue[] kv = r.raw();  
        for (int i = 0; i < kv.length; i++) {  
            // 循环每一列  
            String key = kv[i].getKeyString();  
              
            String value = kv[i].getValueArray().toString();  
              
            // 将查询到的结果写入List中  
            list.add(key + ":"+ value);  
              
        }// end of 遍历每一列  
          
        return list;  
    }  
  
    /** 
     * 增加表数据 
     *  
     * @param tableName 
     * @param rowkey 
     */  
    public static void insertData(String tableName, String rowkey) {  
        HTable table = null;  
        try {  
            table = new HTable(config, tableName);  
            // 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值  
            for (int i = 1; i < 100; i++) {  
                byte[] result = getNumRowkey(rowkey,i);  
                Put put = new Put(result);  
                // 本行数据的第一列  
                put.add(rowkey.getBytes(), "name".getBytes(),  
                        ("aaa" + i).getBytes());  
                // 本行数据的第三列  
                put.add(rowkey.getBytes(), "age".getBytes(),  
                        ("bbb" + i).getBytes());  
                // 本行数据的第三列  
                put.add(rowkey.getBytes(), "address".getBytes(),  
                        ("ccc" + i).getBytes());  
  
                table.put(put);  
            }  
  
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }  
    }  
  
    private static byte[] getNewRowkey(String rowkey) {  
        byte[] result = null;  
  
        RowKeyGenerator rkGen = new HashRowKeyGenerator();  
        byte[] splitKeys = rkGen.nextId();  
  
        byte[] rowkeytmp = rowkey.getBytes();  
  
        result = new byte[splitKeys.length + rowkeytmp.length];  
        System.arraycopy(splitKeys, 0, result, 0, splitKeys.length);  
        System.arraycopy(rowkeytmp, 0, result, splitKeys.length,  
                rowkeytmp.length);  
  
        return result;  
    }  
      
    public static void main(String[] args) {  
        RowKeyGenerator rkGen = new HashRowKeyGenerator();  
        byte[] splitKeys = rkGen.nextId();  
        System.out.println(splitKeys);      
    }  
  
    private static byte[] getNumRowkey(String rowkey, int i) {  
        byte[] result = null;  
  
        RowKeyGenerator rkGen = new HashRowKeyGenerator();  
        byte[] splitKeys = rkGen.nextId();  
  
        byte[] rowkeytmp = rowkey.getBytes();  
  
        byte[] intVal = BitUtils.getByteByInt(i);  
        result = new byte[splitKeys.length + rowkeytmp.length + intVal.length];  
        System.arraycopy(splitKeys, 0, result, 0, splitKeys.length);  
        System.arraycopy(rowkeytmp, 0, result, splitKeys.length,  
                rowkeytmp.length);  
        System.arraycopy(intVal, 0, result, splitKeys.length+rowkeytmp.length,  
                intVal.length);  
  
        return result;  
    }  
      
      
  
    /** 
     * 删除表 
     *  
     * @param tableName 
     */  
    public static void dropTable(String tableName) {  
        try {  
            HBaseAdmin admin = new HBaseAdmin(config);  
            admin.disableTable(tableName);  
            admin.deleteTable(tableName);  
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();  
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 查询所有 
     *  
     * @param tableName 
     */  
    public static void QueryAll(String tableName) {  
        HTable table  = null;  
        try {  
            table  = new HTable(config, tableName);  
            ResultScanner rs = table.getScanner(new Scan());  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 查询所有 
     *  
     * @param tableName 
     */  
    public static void QueryByCondition1(String tableName) {  
  
        HTable table = null;  
        try {  
            table  = new HTable(config, tableName);  
            Get scan = new Get("abcdef".getBytes());// 根据rowkey查询  
            Result r = table.get(scan);  
            System.out.println("获得到rowkey:" + new String(r.getRow()));  
            for (KeyValue keyValue : r.raw()) {  
                System.out.println("列：" + new String(keyValue.getFamily())  
                        + "====值:" + new String(keyValue.getValue()));  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     *  根据rowkwy前坠查询  
     * @param tableName 
     * @param rowkey 
     */  
    public static void queryByRowKey(String tableName,String rowkey)  
    {  
        try {  
            HTable table = new HTable(config, tableName);  
            Scan scan = new Scan();  
            scan.setFilter(new PrefixFilter(rowkey.getBytes()));  
            ResultScanner rs = table.getScanner(scan);  
            KeyValue[] kvs = null;  
            for (Result tmp : rs)  
            {  
                kvs = tmp.raw();  
                for (KeyValue kv : kvs)  
                {  
                    System.out.print(kv.getRow()+" ");  
                    System.out.print(kv.getFamily()+" :");  
                    System.out.print(kv.getQualifier()+" ");  
                    System.out.print(kv.getTimestamp()+" ");  
                    System.out.println(kv.getValue());  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
    }  
    /** 
     * 查询所有 
     *  
     * @param tableName 
     */  
    public static void QueryByCondition2(String tableName) {  
  
        try {  
            HTable table = new HTable(config, tableName);  
            // 当列column1的值为aaa时进行查询  
            Filter filter = new SingleColumnValueFilter(  
                    Bytes.toBytes("column1"), null, CompareOp.EQUAL,  
                    Bytes.toBytes("aaa"));   
            Scan s = new Scan();  
            s.setFilter(filter);  
            ResultScanner rs = table.getScanner(s);  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
  
    /** 
     * 查询所有 
     *  
     * @param tableName 
     */  
    public static void QueryByCondition3(String tableName) {  
  
        try {  
              
            HTable table = new HTable(config, tableName);  
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new SingleColumnValueFilter(  
                    Bytes.toBytes("column1"), null, CompareOp.EQUAL,  
                    Bytes.toBytes("aaa"));  
            filters.add(filter1);  
  
            Filter filter2 = new SingleColumnValueFilter(  
                    Bytes.toBytes("column2"), null, CompareOp.EQUAL,  
                    Bytes.toBytes("bbb"));  
            filters.add(filter2);  
  
            Filter filter3 = new SingleColumnValueFilter(  
                    Bytes.toBytes("column3"), null, CompareOp.EQUAL,  
                    Bytes.toBytes("ccc"));  
            filters.add(filter3);  
  
            FilterList filterList1 = new FilterList(filters);  
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("获得到rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("列：" + new String(keyValue.getFamily())  
                            + "====值:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
}
```




```
HashChoreWoker：  
  
import java.util.Iterator;  
import java.util.TreeSet;  
  
import org.apache.hadoop.hbase.util.Bytes;  
  
/** 
 *  
 * @author kuang hj 
 * 
 */  
public class HashChoreWoker{  
    // 随机取机数目  
    private int baseRecord;  
    // rowkey生成器  
    private RowKeyGenerator rkGen;  
    // 取样时，由取样数目及region数相除所得的数量.  
    private int splitKeysBase;  
    // splitkeys个数  
    private int splitKeysNumber;  
    // 由抽样计算出来的splitkeys结果  
    private byte[][] splitKeys;  
  
    public HashChoreWoker(int baseRecord, int prepareRegions) {  
        this.baseRecord = baseRecord;  
        // 实例化rowkey生成器  
        rkGen = new HashRowKeyGenerator();  
        splitKeysNumber = prepareRegions - 1;  
        splitKeysBase = baseRecord / prepareRegions;  
    }  
  
    public byte[][] calcSplitKeys() {  
        splitKeys = new byte[splitKeysNumber][];  
        // 使用treeset保存抽样数据，已排序过  
        TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);  
        for (int i = 0; i < baseRecord; i++) {  
            rows.add(rkGen.nextId());  
        }  
        int pointer = 0;  
        Iterator<byte[]> rowKeyIter = rows.iterator();  
        int index = 0;  
        while (rowKeyIter.hasNext()) {  
            byte[] tempRow = rowKeyIter.next();  
            rowKeyIter.remove();  
            if ((pointer != 0) && (pointer % splitKeysBase == 0)) {  
                if (index < splitKeysNumber) {  
                    splitKeys[index] = tempRow;  
                    index++;  
                }  
            }  
            pointer++;  
        }  
        rows.clear();  
        rows = null;  
        return splitKeys;  
    }  
} 
```

```
HashRowKeyGenerator：  
import org.apache.hadoop.hbase.util.Bytes;  
import org.apache.hadoop.hbase.util.MD5Hash;  
  
import com.kktest.hbase.BitUtils;  
/** 
* 
* 
**/  
public class HashRowKeyGenerator implements RowKeyGenerator {  
    private static long currentId = 1;  
    private static long currentTime = System.currentTimeMillis();  
    //private static Random random = new Random();  
  
    public byte[] nextId()   
    {  
        try {  
            currentTime = getRowKeyResult(Long.MAX_VALUE - currentTime);  
            byte[] lowT = Bytes.copy(Bytes.toBytes(currentTime), 4, 4);  
            byte[] lowU = Bytes.copy(Bytes.toBytes(currentId), 4, 4);  
            byte[] result = Bytes.add(MD5Hash.getMD5AsHex(Bytes.add(lowT, lowU))  
                    .substring(0, 8).getBytes(), Bytes.toBytes(currentId));  
            return result;  
        } finally {  
            currentId++;  
        }  
    }  
      
    /** 
     *  getRowKeyResult 
     * @param tmpData 
     * @return 
     */  
    public static long getRowKeyResult(long tmpData)  
    {  
        String str = String.valueOf(tmpData);  
        StringBuffer sb = new StringBuffer();  
        char[] charStr = str.toCharArray();  
        for (int i = charStr.length -1 ; i > 0; i--)  
        {  
            sb.append(charStr[i]);  
        }  
          
        return Long.parseLong(sb.toString());  
    }  
}  

```



转自：
http://student-lp.iteye.com/blog/2309075

http://blog.csdn.net/kuanghongjiang/article/details/41343789
