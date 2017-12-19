# 映射语句


```
CREATE EXTERNAL TABLE tb_ext_cdb_card_convert
(rowkey string,id bigint,assetId bigint,type int,status int,convertId int,sourceUrl string,createTime bigint,finishTime bigint,recallNum int,quality int,cardPath string,convertVideoDuration bigint)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
("hbase.columns.mapping" = ":key,c:id,c:assetId,c:type,c:status,c:convertId,c:sourceUrl,c:createTime,c:finishTime,c:recallNum,c:quality,c:cardPath,c:convertVideoDuration") TBLPROPERTIES ("hbase.table.name"="cdb_card_convert");
```