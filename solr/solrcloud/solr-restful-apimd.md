# del

删除所有数据：

```
http://localhost:8983/solr/collection1/update?stream.body=<delete><query>*:*</query></delete>&commit=true
```

删除指定数据

```
http://localhost:8983/solr/collection1/update?stream.body=<delete><query>title:abc</query></delete>&commit=true
```

多条件删除

```
http://localhost:8983/solr/collection1/update?stream.body=<delete><query>title:abc AND name:zhang</query></delete>&commit=true
```

# select

查询数据
查询数据都是通过HTTP的GET请求获取的，搜索关键字用参数q指定，另外还可以指定很多可选的参数来控制信息的返回，例如：用fl指定返回的字段，比如f1=name，那么返回的数据就只包括name字段的内容

```
http://localhost:8983/solr/collection1/select?q=solr&fl=name&wt=json&indent=true
```

# 排序
Solr提供排序的功能，通过参数sort来指定，它支持正序、倒序，或者多个字段排序

q=video&sort=price desc
q=video&sort=price asc
q=video&sort=inStock asc, price desc
默认条件下，Solr根据socre 倒序排列，socre是一条搜索记录根据相关度计算出来的一个分数。


# 高亮

网页搜索中，为了突出搜索结果，可能会对匹配的关键字高亮出来，Solr提供了很好的支持，只要指定参数：

* hl=true #开启高亮功能
* hl.fl=name #指定需要高亮的字段

```
http://localhost:8983/solr/collection1/select?q=Search&wt=json&indent=true&hl=true&hl.fl=features
```

返回的内容中包含：

```
"highlighting":{
       "SOLR1000":{
           "features":["Advanced Full-Text <em>Search</em> Capabilities using Lucene"]
       }
}

```

# 文本分析
文本字段通过把文本分割成单词以及运用各种转换方法（如：小写转换、复数移除、词干提取）后被索引，schema.xml文件中定义了字段在索引中，这些字段将作用于其中.
默认情况下搜索”power-shot”是不能匹配”powershot”的，通过修改schema.xml文件(solr/example/solr/collection1/conf目录)，把features和text字段替换成”text_en_splitting”类型，就能索引到了。

```
<field name="features" type="text_en_splitting" indexed="true" stored="true" multiValued="true"/>
...
<field name="text" type="text_en_splitting" indexed="true" stored="false" multiValued="true"/>
```

修改完后重启solr，然后重新导入文档
