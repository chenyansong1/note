
我们在Hbase shell中查看是"hbase:meta"表的内容如下：
```
hbase(main):024:0> scan 'hbase:meta'
ROW                                 COLUMN+CELL                                                                                           
 hbase:namespace,,1497926055613.d61 column=info:regioninfo, timestamp=1497926056458, value={ENCODED => d610d4453ac45036ad1bce14b2f65ec4, N
 0d4453ac45036ad1bce14b2f65ec4.     AME => 'hbase:namespace,,1497926055613.d610d4453ac45036ad1bce14b2f65ec4.', STARTKEY => '', ENDKEY => '
                                    '}                                                                                                    
 hbase:namespace,,1497926055613.d61 column=info:seqnumDuringOpen, timestamp=1498045358517, value=\x00\x00\x00\x00\x00\x00\x00\x19         
 0d4453ac45036ad1bce14b2f65ec4.                                                                                                           
 hbase:namespace,,1497926055613.d61 column=info:server, timestamp=1498045358517, value=hdp-node-01:16020                                  
 0d4453ac45036ad1bce14b2f65ec4.                                                                                                           
 hbase:namespace,,1497926055613.d61 column=info:serverstartcode, timestamp=1498045358517, value=1498045333398                             
 0d4453ac45036ad1bce14b2f65ec4. 
 
 #user表的结构（有3个列族)
 user,,1497946582548.40404b875d9d5c column=info:regioninfo, timestamp=1497946584316, value={ENCODED => 40404b875d9d5c1d9f885de946a27326, N
 1d9f885de946a27326.                AME => 'user,,1497946582548.40404b875d9d5c1d9f885de946a27326.', STARTKEY => '', ENDKEY => ''}         
 user,,1497946582548.40404b875d9d5c column=info:seqnumDuringOpen, timestamp=1498045358997, value=\x00\x00\x00\x00\x00\x00\x00\x18         
 1d9f885de946a27326.                                                                                                                      
 user,,1497946582548.40404b875d9d5c column=info:server, timestamp=1498045358997, value=hdp-node-03:16020                                  
 1d9f885de946a27326.                                                                                                                      
 user,,1497946582548.40404b875d9d5c column=info:serverstartcode, timestamp=1498045358997, value=1498045339663                             
 1d9f885de946a27326.      
```


The hbase:meta 表的结构如下：

```
Key
Region key of the format ([table],[region start key],[region id])

Values
#region的实例序列化信息
info:regioninfo (serialized HRegionInfo instance for this region)

#包含该region的regionServer的ip:port
info:server (server:port of the RegionServer containing this region)

#包含该region的regionServer启动时的时间
info:serverstartcode (start-time of the RegionServer process containing this region)

```

When a table is in the process of splitting, two other columns will be created, called info:splitA and info:splitB. These columns represent the two daughter regions. The values for these columns are also serialized HRegionInfo instances. After the region has been split, eventually this row will be deleted.