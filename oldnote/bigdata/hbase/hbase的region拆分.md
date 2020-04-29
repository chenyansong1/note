hbase的region拆分

通过scan表hbase:meta，观察表user的元数据

```
scan 'hbase:mate'

```


以下是user表的元数据信息，
```
#拆分前的region
 user,,1497946582548.40404b875d9d5c column=info:seqnumDuringOpen, timestamp=1498123699200, value=\x00\x00\x00\x00\x00\x03}\x13            
 1d9f885de946a27326.                                                                                                                      
 user,,1497946582548.40404b875d9d5c column=info:server, timestamp=1498123699200, value=hdp-node-01:16020                                  
 1d9f885de946a27326.                                                                                                                      
 user,,1497946582548.40404b875d9d5c column=info:serverstartcode, timestamp=1498123699200, value=1498123658115                             
 1d9f885de946a27326.                                                                                                                      
 user,,1497946582548.40404b875d9d5c column=info:splitA, timestamp=1498123704039, value={ENCODED => 2400f4b98150859d9576a74020fb4ac0, NAME 
 1d9f885de946a27326.                => 'user,,1498123700123.2400f4b98150859d9576a74020fb4ac0.', STARTKEY => '', ENDKEY => 'rowkey_new:5520
                                    0'}                                                                                                   
 user,,1497946582548.40404b875d9d5c column=info:splitB, timestamp=1498123704039, value={ENCODED => 67e71fce39bf9e210fcc3c37b952963c, NAME 
 1d9f885de946a27326.                => 'user,rowkey_new:55200,1498123700123.67e71fce39bf9e210fcc3c37b952963c.', STARTKEY => 'rowkey_new:55
                                    200', ENDKEY => ''}  

#从拆分前可以看到splitA和splitB这两个拆分动作


#拆分后的region1	（起始rowkey=0,endkey="rowkey_new:55200"	)				
 user,,1498123700123.2400f4b9815085 column=info:regioninfo, timestamp=1498123704039, value={ENCODED => 2400f4b98150859d9576a74020fb4ac0, N
 9d9576a74020fb4ac0.                AME => 'user,,1498123700123.2400f4b98150859d9576a74020fb4ac0.', STARTKEY => '', ENDKEY => 'rowkey_new:
                                    55200'}                                                                                               
 user,,1498123700123.2400f4b9815085 column=info:seqnumDuringOpen, timestamp=1498123704671, value=\x00\x00\x00\x00\x00\x03}\x11            
 9d9576a74020fb4ac0.                                                                                                                      
 user,,1498123700123.2400f4b9815085 column=info:server, timestamp=1498123704671, value=hdp-node-01:16020                                  
 9d9576a74020fb4ac0.                                                                                                                      
 user,,1498123700123.2400f4b9815085 column=info:serverstartcode, timestamp=1498123704671, value=1498123658115                             
 9d9576a74020fb4ac0.   


 
#拆分后的region2	（起始rowkey="rowkey_new:55200",endkey='' )
 user,rowkey_new:55200,149812370012 column=info:regioninfo, timestamp=1498123704039, value={ENCODED => 67e71fce39bf9e210fcc3c37b952963c, N
 3.67e71fce39bf9e210fcc3c37b952963c AME => 'user,rowkey_new:55200,1498123700123.67e71fce39bf9e210fcc3c37b952963c.', STARTKEY => 'rowkey_ne
 .                                  w:55200', ENDKEY => ''}                                                                               
 user,rowkey_new:55200,149812370012 column=info:seqnumDuringOpen, timestamp=1498123704658, value=\x00\x00\x00\x00\x00\x03}\x12            
 3.67e71fce39bf9e210fcc3c37b952963c                                                                                                       
 .                                                                                                                                        
 user,rowkey_new:55200,149812370012 column=info:server, timestamp=1498123704658, value=hdp-node-01:16020                                  
 3.67e71fce39bf9e210fcc3c37b952963c                                                                                                       
 .                                                                                                                                        
 user,rowkey_new:55200,149812370012 column=info:serverstartcode, timestamp=1498123704658, value=1498123658115                             
 3.67e71fce39bf9e210fcc3c37b952963c  
```