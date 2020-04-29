# Documents, Fields, and Schema Design概念

solr的数据的基本组成单位是doc

# Solr's Schema File

这里有两个schema文件
* schema.xml	老的schema文件
* managed-schema 新schema文件
* 以上两种schema文件的结构不会改变，但是和文件交互的方式会有所不同
如果你使用的是solrCloud模式的话，那么这些的schema文件在本地是看不到的，在zookeeper上或者在webUI上是可以看的

如果你使用的是managed schema，只能通过 Schema API，来进行修改schema，但是如果你使用的是schem.xml那么可以通过手动编辑的方式来修改schema文件，如果你使用的是schema.xml的方式，还有一种方式就是通过zookeeper的 upconfig and downconfig commands来拷贝和上传你的本地文件

# Solr Field Types






