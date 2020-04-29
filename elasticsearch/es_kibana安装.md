es,kibana的安装
1.es需要jdk，不要讲jdk安装在root家目录下
2.启动es不能使用root用户，需要新建一个用户（elsearch)用来启动es
3.vim es/conf/elasticsearch.yml 

```
#配置网络和端口
 network.host: 192.168.153.202
 http.port: 9200
```


4.vim kibana/conf/kibana.yml

```
# kibana要去连接的es地址和端口
# The Elasticsearch instance to use for all your queries.
 elasticsearch.url: "http://192.168.153.202:9200"
```

elasticsearch 和 kibana 都不能在root用户下直接运行，所以需要我们新建一个用户，此处我们新建的用户为elsearch，
注意有时我们是在root安装的上面连个软件，此时目录中的文件就会存在root的权限，我们需要将文件的权限改为elsearch，
```
chown -R elsearch:elsearch /bigdata_installed/elasticsearch/
chown -R elsearch:elsearch /bigdata_installed/kibana/

```




5. 在 Kibana 目录下运行下面的命令，下载并安装 Sense app：

```
./bin/kibana plugin --install elastic/sense 
```

NOTE：你可以直接从这里 https://download.elastic.co/elastic/sense/sense-latest.tar.gz 下载 Sense 离线安装可以查看这里 install it on an offline machine 。

启动 Kibana.

```
./bin/kibana 
```

参见： https://www.elastic.co/guide/cn/elasticsearch/guide/current/running-elasticsearch.html