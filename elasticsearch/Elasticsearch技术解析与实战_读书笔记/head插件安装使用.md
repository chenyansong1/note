# 安装


```
cd ${ES_HOME}/bin/
[hadoop@hdp-node-01 bin]$ ./plugin install mobz/elasticsearch-head
//.....
Installed head into /home/hadoop/app/elasticsearch-2.4.3/plugins/head



#注意这个是需要外网的：https://github.com/mobz/elasticsearch-head/archive/master.zip
Trying https://github.com/mobz/elasticsearch-head/archive/master.zip ...
Downloading .....................................DONE
Verifying https://github.com/mobz/elasticsearch-head/archive/master.zip checksums if available ...
NOTE: Unable to verify checksum for downloaded plugin (unable to find .sha1 or .md5 file to verify)
Installed head into /home/hadoop/elasticsearch-2.4.3/plugins/head
```

下面是访问head的地址：

```
http://192.168.153.202:9200/_plugin/head/

```

> 在请求的URL中使用127.0.0.1或者具体的IP，使用localhost没用




# head插件操作

## 创建索引

```
#在请求的URL中输入
    http://127.0.0.1:9200/secisland?pretty

#在请求的方法中选择PUT
```

创建成功了之后，出现如下的反馈


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/head_1.png)


* 查看所有的索引

```
http://192.168.0.121:9200/_cat/indices?v/
```


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/head_2.png)

表示已经建成了一个索引secisland,主分片是5个，健康度是黄色，状态是活动，文档数为0，同时也可以在head中进行查看


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/head_3.png)


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/head_4.png)

## 插入数据

![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/head_5.png)

## 修改文档


```
请求：POST http://127.0.0.1:9200/secisland/secilog/1/_update
参数：
{
    "doc":{
        "computer":"secisland",
        "messag":"secisland is an security computer, It provides log ..."
    }
}

返回值：
{
    //....
}

```

## 查询文档

![](/Users/chenyansong/Desktop/2.png)

```
请求：GET http://127.0.0.1:9200/secisland/secilog/1
返回值：
{
//....
}

返回值：
{
//...
}
```



## 删除文档


![](/Users/chenyansong/Desktop/2.png)

```
请求：DELETE http://127.0.0.1:9200/secisland/secilog/1
返回值：
{
//....
}

返回值：
{
//...
}
```


## 删除库

```
请求：DELETE http://127.0.0.1:9200/secisland
返回值：
{
//...
}
```


https://www.elastic.co/guide/cn/elasticsearch/guide/current/dynamic-mapping.html



postman 发送HTTP的客户端
