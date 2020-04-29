# 1.solr的提交方式

## 1.1.直接提交（手动提交）

* 软提交 
> 把数据提交到**内存中，可以搜索到**,每次**提交少量数据**时使用这种方式

```
server.commit(true,true,true);
```

* 硬提交
> 把数据提交到**磁盘**中，**可以搜索到**,**批量提交**时使用

```
server.commit()
```

## 1.2.自动提交

* 自动软提交

默认没有开启,需要开启时，可以参考如下：
```
<autoSoftCommit>
	<maxTime>1000</maxTime>
</autoSoftCommit>
```

* 自动硬提交

默认开启了，默认15秒执行一次
```
<autoCommit>
	<maxDocs>1000</maxDocs>
	<maxTime>15000</maxTime>
	<openSearcher>false</openSearcher> 
</autoCommit>
```

注意：   

1.自动提交中，Java代码对应如下：
```
server.add(doc);
```
**只需要将doc加入到server即可,不需要我们手动在代码中commit操作，后面的过程就是根据自动提交的配置文件进行处理**

2.虽然软提交是把数据提交到内存中，但是solr停止，数据并不会丢失，因为solr在没提交一条数据都会记录日志(tlog)，solr后期会从日志中回放数据。

# 2.Commit和SoftCommit在性能上的区别

* Commit，硬提交，Solr和Lucene原本存在的commit方式，负责把索引内容刷入磁盘。需要重新打开searcher，Solr/Lucene才会对这部分内容可见可查，但是这样比较费性能。
* SoftCommit，软提交，这是Solr新增的commit方式，Lucene没有。软提交负责将索引内容在内存中生成segment，并使得索引内容对Solr可见可查，该提交方式是Commit的改善方式，保证了Solr的实时性同时又兼顾了性能。在进行softcommit过程中需要进行预热(即**将现在状态的searcher复制到新的searcher中，保证了旧的softcommit数据不丢失)，虽然没有重新打开searcher那么费性能，但是预热频率过快还是会影响solr的性能。**


# 3.自动软提交和自动硬提交的建议

如果想要实现近实时的搜索（NRT),可以在开启自动硬提交的同时，开启自动软提交

1.自动硬提交一般每10min提交一次         
2.自动软提交每1s(如果没有高的实时性，可以放宽，5s或者更长)提交一次       
3.配置如下         

```
<updateHandler class="solr.DirectUpdateHandler2">
    <updateLog>
        <str name="dir">${solr.ulog.dir:}</str>
        <int name="numVersionBuckets">${solr.ulog.numVersionBuckets:65536}</int>
    </updateLog>
    <autoCommit>
        <maxDocs>25000</maxDocs>
        <maxTime>60000</maxTime>
        <openSearcher>false</openSearcher>
    </autoCommit>
    <autoSoftCommit>
	    <maxDocs>1000</maxDocs>
        <maxTime>6000</maxTime>
    </autoSoftCommit>
</updateHandler>

```

# 4.实验验证自动软提交和自动硬提交的区别

下面首先给出结论：
* 达到自动软提交的条件（最大文档数maxDocs或者最大时间maxTime），会执行自动软提交，然后观察tlog日志和index数据，发现只有tlog数据在变化
* 达到自动硬提交的条件（最大文档数maxDocs或者最大时间maxTime），会执行自动硬提交，然后观察tlog日志和index数据，发现tlog文件被合并了，然后是index数据有变化

下面开始试验

## 4.1.第一步

修改solr的配置文件，参见 [修改配置文件，并生效](https://github.com/belongtocys/notebook/blob/master/solr/solrcloud/%E4%BF%AE%E6%94%B9%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%EF%BC%8C%E5%B9%B6%E7%94%9F%E6%95%88.md)

* 将autoCommit的最大文档数（maxDocs）改成3
* 将autoSoftCommit的最大文档数（maxDocs）改成1000，最大提交时间maxTime改成6s

![](/images/solr/solrcloud/auto_commit_config.jpg)

## 4.2.第二步

编写一个客户端程序，进行添加文档测试

注意：因为是自动提交，所以这里没有进行commit的操作，只是将文档加入到了server中，然后提交的工作是由上面的配置文件进行处理的

![](/images/solr/solrcloud/auto_commit_program.jpg)

在程序中我们插入了2条记录，此时只是自动软提交，但是没有达到自动硬提交的条件

## 4.3.第三步

观察tlog日志和index数据文件

![](/images/solr/solrcloud/auto_commit_tlog.jpg)

![](/images/solr/solrcloud/auto_commit_index.jpg)


## 4.4.第四步

再次提交2条doc文档，观察tlog日志和index数据文件，发现此时是触发了自动硬提交的条件，然后tlog文件和index中的数据都会发生变化，如下图：

![](/images/solr/solrcloud/auto_commit_tlog2.jpg)

![](/images/solr/solrcloud/auto_commit_index2.jpg)


以上四步是自动软提交的结果，在每次提交文档的时候，都能够近实时的索引出近实时的数据（通过web界面可以查询）

## 4.5.第五步

再次修改配置文件，关闭自动软提交
![](/images/solr/solrcloud/auto_commit_config2.jpg)

再次添加2条文档，观察tlog日志和index数据文件，发现只是将数据写入到了tlog中，此时在搜索中实时的搜索，并没有实时的展现出刚刚插入的记录

![](/images/solr/solrcloud/auto_commit_tlog3.jpg)


# softcommit和commit的说明


在solr4.0中增加了软提交，加快了index速度，具体如下：

* softcommit和commit的区别

**A commit operation makes index changes visible to new search requests. A hard commit also calls fsync on the index files to ensure they have been flushed to stable storage and no data loss will result from a power failure**.


A soft commit is much faster since it only makes index changes visible and **does not fsync index files or write a new index descriptor**. If the JVM crashes or there is a loss of power, changes that occurred after the last hard commit will be lost. Search collections that have near-real-time requirements (that want index changes to be quickly visible to searches) will want to soft commit often but hard commit less frequently.

* optimize

An optimize is like a hard commit except that it forces all of the index segments to be merged into a single segment first. Depending on the use cases, this operation should be performed infrequently (like nightly), if at all, since it is very expensive and involves reading and re-writing the entire index. Segments are normally merged over time anyway (as determined by the merge policy), and optimize just forces these merges to occur immediately.



其中软提交是提交数据到内存里面，并没有持久化到磁盘，但是他会把提交的记录写到tlog的日志文件里面

下面就是自动软提交的配置，不需要自己维护提交（默认没有开启）：

注意：如果我们索引了数据并且没有做任何提交，它也会在tlog日志文件中做记录。并且在正常关闭solr 服务的时候，solr会自动执行一次硬提交，在solr 重启的时候会自动加载tlog的日志文件。


