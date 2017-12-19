#增加一个shard的副本

//在192.168.1.194上创建副本
http://192.168.1.194:28111/solr/admin/cores?action=CREATE&name=rcmd_shard1_replica3&collection=rcmd&shard=shard1&collection.configName=rcmd
//在192.168.1.204上创建副本
http://192.168.1.204:28111/solr/admin/cores?action=CREATE&name=rcmd_shard1_replica5&collection=rcmd&shard=shard1&collection.configName=rcmd


# 删除一个副本


http://192.168.1.194:28111/solr/admin/collections?action=DELETEREPLICA&collection=rcmd&shard=shard1&replica=core_node3

> 后面的replica=core_node3就是副本的节点，如replica=core_node2，replica=core_node3，replica=core_node4


同步一下这两个表到hbase，有一个统计需求需要用到
card.cdb_card_convert
card.cdb_card_qiniu_one



1.增量





2.全量

====================================================================================

需要去hbase中去建表

exists 'cdb_card_convert'

create 'cdb_card_convert','c'

exists 'cdb_card_convert'


在indexing下添加一个配置文件

id as rowkey,id,assetId,type,status,convertId,sourceUrl,createTime,finishTime,recallNum,quality,cardPath,convertVideoDuration


====================================================================================


需要去hbase中去建表

exists 'cdb_card_qiniu_one'

create 'cdb_card_qiniu_one','c'

exists 'cdb_card_qiniu_one'

cdb_card_convert
cdb_card_qiniu_one



在indexing下添加一个配置文件

id as rowkey,uploadType,assetId,status,startUploadTime,endUploadTime,startPullTime,endPullTime,fileName,deleteTime,sourceZip,sourceUrl,filesize,pullTime,pullSpeed,quality,img,info,convertId,recallNum





 scan 'cdb_card_convert',{LIMIT=>1}


hive 中建立mapping的映射


disable 'cdb_card_qiniu_one'
drop 'cdb_card_qiniu_one'


create 'cdb_card_qiniu_one',{NAME => 'c',COMPRESSION => 'Snappy', VERSIONS => '1', MIN_VERSIONS => '0'}


disable 'cdb_card_convert'
drop 'cdb_card_convert'

create 'cdb_card_convert',{NAME => 'c',COMPRESSION => 'Snappy', VERSIONS => '1', MIN_VERSIONS => '0'}


#HIVE-----------------------
CREATE EXTERNAL TABLE tb_ext_cdb_card_tag_group_relation
(rowkey string,id bigint,groupId bigint,tagId bigint,isDelete bigint,createTime bigint,updateTime bigint)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
("hbase.columns.mapping" = ":key,c:id,c:groupId,c:tagId,c:isDelete,c:createTime,c:updateTime") TBLPROPERTIES ("hbase.table.name"="cdb_card_tag_group_relation");


#cdb_user_mobile
create 'cdb_creator_task_new',{NAME => 'c',COMPRESSION => 'Snappy', VERSIONS => '1', MIN_VERSIONS => '0'}

CREATE EXTERNAL TABLE tb_ext_cdb_user_mobile
(bid bigint,createTime int,bindWebJifen int,mobile bigint)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
("hbase.columns.mapping" = ":key,c:createTime,c:bindWebJifen,c:mobile") TBLPROPERTIES ("hbase.table.name"="cdb_user_mobile");




create 'cdb_card_tag_group_relation',{NAME => 'c',COMPRESSION => 'Snappy', VERSIONS => '1', MIN_VERSIONS => '0'}



#HIVE-----------------------


CREATE EXTERNAL TABLE tb_ext_cdb_card_convert
(rowkey string,id bigint,assetId bigint,type int,status int,convertId int,sourceUrl string,createTime bigint,finishTime bigint,recallNum int,quality int,cardPath string,convertVideoDuration bigint)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
("hbase.columns.mapping" = ":key,c:id,c:assetId,c:type,c:status,c:convertId,c:sourceUrl,c:createTime,c:finishTime,c:recallNum,c:quality,c:cardPath,c:convertVideoDuration") TBLPROPERTIES ("hbase.table.name"="cdb_card_convert");

drop table tb_ext_cdb_card_convert


id bigint,assetId bigint,type int,status int,convertId int,sourceUrl string,createTime bigint,finishTime bigint,recallNum int,quality int,cardPath string,convertVideoDuration bigint

==============================================================


CREATE EXTERNAL TABLE tb_ext_cdb_card_qiniu_one
(rowkey string,id bigint,uploadType int,assetId bigint,status int,startUploadTime bigint,endUploadTime bigint,startPullTime bigint,endPullTime bigint,fileName string,deleteTime bigint,sourceZip string,sourceUrl string,filesize bigint,pullTime bigint,pullSpeed double,quality int,img string,info string,convertId bigint,recallNum int)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
("hbase.columns.mapping" = ":key,c:id,c:uploadType,c:assetId,c:status,c:startUploadTime,c:endUploadTime,c:startPullTime,c:endPullTime,c:fileName,c:deleteTime,c:sourceZip,c:sourceUrl,c:filesize,c:pullTime,c:pullSpeed,c:quality,c:img,c:info,c:convertId,c:recallNum") TBLPROPERTIES ("hbase.table.name"="cdb_card_qiniu_one");



rowkey string,id bigint,uploadType int,assetId bigint,status int,startUploadTime bigint,endUploadTime bigint,startPullTime bigint,endPullTime bigint,fileName string,deleteTime bigint,sourceZip string,sourceUrl string,filesize bigint,pullTime bigint,pullSpeed double,quality int,img string,info string,convertId bigint,recallNum int




c:id,c:uploadType,c:assetId,c:status,c:startUploadTime,c:endUploadTime,c:startPullTime,c:endPullTime,c:fileName,c:deleteTime,c:sourceZip,c:sourceUrl,c:filesize,c:pullTime,c:pullSpeed,c:quality,c:img,c:info,c:convertId,c:recallNum

books:
http://www.importnew.com/cat/books




android studio 


为什么在最开始的时候不删除呢？

http://192.168.1.195:28000/solr/user/select?q=bid:64057436&collection=user,user_new


两种方式：
1.将新增和修改都放在一个索引中，此时需要程序中重新：优化索引
2.将分别有3个索引：新增索引，修改索引，和all索引，然后在ext中对搜索的结果集进行处理

iid:就是唯一的主键不同的时候，会不会有什么不一样呢？


"lasttime": 1509207191,

如果请求的是user_new，那么会查询user_new ,user_inc


以user_new为主在111中进行测试吧：
user_new 全量表
user_inc	add表
user_modfy	modify表

当三张表中同时存在一条相同的记录的时候，将事件lasttime时间最新的记录返回

com.aipai.bigdata.solrcloudext.validuser.UserMutilResultComponent

userMutilResultComponent

<searchComponent name="userMutilResultComponent" class="com.aipai.bigdata.solrcloudext.validuser.UserMutilResultComponent">

qt=dismax_user

我将数据放在一个Map中，然后<bid,lsttime>




============================

2017-10-30:上午


select unix_timestamp('2016-01-02');  

select from_unixtime(1451997924);  
  
select from_unixtime(1451997924,'%Y-%d');  
  


quality：

10-360
11-480
15-710
16-1080

过滤：没有结束时间的数据


mysql> select FROM_UNIXTIME(createTime),FROM_UNIXTIME(finishTime),quality from cdb_card_convert where createTime>UNIX_TIMESTAMP('2017-10-02 12:23:00') limit 2;
+---------------------------+---------------------------+---------+
| FROM_UNIXTIME(createTime) | FROM_UNIXTIME(finishTime) | quality |
+---------------------------+---------------------------+---------+
| 2017-10-02 12:23:04       | 2017-10-02 12:36:01       |      10 |
| 2017-10-02 12:23:05       | 2017-10-02 12:37:02       |      10 |
+---------------------------+---------------------------+---------+

createTime,finishTime,quality


1.统计10月2日~10月29日，视频转码环节，各清晰度文件的平均转码时间
2.adw的一个小模块


`status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '七牛存储文件状态：0为未上传完成，1为上传完，2为拉取回来，3为已删除文件',
  `startUploadTime` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '开始上传时间',
  `endUploadTime` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结束上传时间',
  `startPullTime` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '开始拉去时间',
  `endPullTime` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结束拉去时间',
  `fileName` v


  `fileName` v
  select startUploadTime,endUploadTime, startPullTime,endPullTime, fileName from cdb_card_qiniu_one limit 2;



20171017

select FROM_UNIXTIME(createTime),FROM_UNIXTIME(finishTime) ,(createTime-finishTime) as subT
from adw.tb_ext_cdb_card_convert 
where createTime>=1508169600 and createTime<=1508256000 order by subT desc;



1.各个时间段的数据对应的表
2.在转换表中一个视频对应多条记录，然后就是取最大的时间和最小时间的差值，才是两者的时间差

广州市天河区建中路59号西座202房 


1.拉回爱拍的时间，后面的时间都没有，是不是没有对应的记录，拉回爱拍的时间，审核完成的时间点，对应的记录没有
2.3天前是啥意思


new_questions:

统计日期是显示前一天的吗？

0.上传视频总个数；是根据哪张表来的
1.未进审核视频个数
2.审核不通过视频个数


第一张的现实有点复杂，跟海海确认，这些数据从上述三张表中是否可以拿到，对应的字段，

视频ID和用户ID对应的表中是否有？


要跟他对一下第一张表的每一个字段

显示的日期可以去掉吗，因为日期的插件可以回显


总时长是：指视频的播放时长吗？



查询条件：
10.2-10.29 && fishTime>createTime &&
(finishTime-creatTime)/视频总时长

分quality统计



1.mysql那边是定时清理数据的，那么需要将canal中的del删除
2.t7,t8,t9中的数据字段，对应的表

3.将所有的放在一个tab中

4.要确定从qiniu_one表开始一条记录的走向，是一条记录生成多条，还是一条记录到底

5.最后视频表中的记录是怎么的逻辑

6.mysql建表，hive建表



7.到时table的tr的填充可以使用chart填充的方式进行，向一个数组中进行push的方式

数据入库的显示问题

8.可以使用join全局替换的方式进行json的拼接


9.hbase中只需要取前一天的数据，不需要跑所有的全量的数据


videoConversionTable


canal中将del的删除掉



blog的问题：
1.图片动态的问题：是因为图片动态没有入库
2.视频和图片动态加上一个transfer，过滤掉没有审核通过的状态

cdb_blog.status=1 and cdb_blog.share=1   可以加这个判断？


3.第二张表的设计



image.cdb_image_collection 




20171106：周一

1.将user加上一个user_modify,实时的，部署到线上进行测试
	1.添加一个user_modify索引，实时的(配置文件上传，重新加载)
	2.修改user,user_inc索引的配置文件(需要添加上user_modify索引)，然后reload
	3.canal切换
	4.线上测试


提供接口到测试那边进行测试：
1.修改user,user_inc的配置文件
2.添加user_modify的索引（配置文件和创建索引）
3.修改canal的接口
4.在nginx中配置测试接口
5.交给测试进行测


plugin的文档说明：
http://blog.csdn.net/zteny/article/details/51645611
http://blog.csdn.net/yeshenrenjin/article/details/8604372
http://blog.csdn.net/duck_genuine/article/details/6962624
http://blog.csdn.net/hello_ken/article/details/24341735
http://blog.csdn.net/zteny/article/details/51637371

2.视频追踪的，hive作业假设另外两个字段是在另外的两张表中，然后使用视频ID去关联





将del的过滤出来，不去执行


#另外一种创建方式：

http://192.168.1.199:28000/solr/admin/collections?action=CREATE&name=user_modify&numShards=1&collection.configName=user_modify&replicationFactor=3&maxShardsPerNode=1

solr:删除
http://blog.csdn.net/lbf5210/article/details/51207043

只是对搜索的结果进行合并

现在的处理方式是：设置了第三个索引，然后对搜索的结果优先返回user_modify,user_inc,user，但是如果搜到的结果只有user，那么即使在user_inc中有对应的bid，那么还是返回user的搜索结果，举例：

如果用户先开始的nickname是“小明”（此时user索引中的nickname），然后用户修改了nickname，修改为“小红”，此时如果有人搜索“小”，找到的是“小红”，
但是如果搜索的是“小明”，还是会将user索引中的记录显示出来的


假设：视频追踪的，hive作业假设另外两个字段是在另外的两张表中，然后使用视频ID去关联





blog的实时性

	那个blog实时性的问题：你看这样是否可以
	全量一天一次:blog
	增量10分钟执行一次：blog_inc
	查询的重复结果：是blog_inc覆盖blog，因为数据修改不是修改的查询字段，所以不会出现像user那样查询命中的是老数据的情况
	有一个问题是：删除，只能等到全量的执行

	在查询结果中进行z_did的过滤，blog_inc覆盖blog



	需要修改的地方：
	1.solr-config配置文件，其中添加了多个组件过滤的配置
	2.上传ext到扩展中（不用线上和测试使用的是同一个lib），reload，需要重新加载
	3.修改app_blog,app_blog_inc.xml文件，注意zookeeper的指向
	4.配置blog_inc的定时任务


user的实时性
	user中建立一个索引，然后在canal中进行增删改查的逻辑，这样就是一个实时的
	处理逻辑是这样的：
		1.将user的所有的索引数据放在一个索引user中
		2.增删改都放在canal中进行处理
		3.删除的处理逻辑是修改，设置一个标记字段：is_del
		4.查询的时候，过滤is_del=1的记录
		5.索引是实时的

	存在的问题：
		1.如果canal的程序挂了，那么那么整个索引就over了
		2.canal是否可以抗的住增删改

	do:
		1.需要


对应的数据库表是 log.cdb_log_card_auth
字段 
createTime 操作时间
operation 操作方式（1=后台审核通过，2=后台审核不通过，3=后台设置私有，4=后台设置共享，5=后台设置精华，6=后台取消精华，7=后台删除，8=用户删除，9=用户设置私有，10=用户设置共享）



select assetId,min(createTime) from tb_ext_cdb_log_card_auth where operation not in (8,9,10) group by assetId




