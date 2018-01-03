


# 通用参数

* pretty 参数：当你在任何请求中添加了参数“?pretty=true”,请求的返回值是经过格式化的json数据，这样更加便于阅读

* human 参数：对于统计数据，会输出便于人类阅读的数据，如：3600s,会输出1h，当?human=false的时候，只输出计算机数据，当?human=true的时候，输出更适合人类阅读的数据，**但这更消耗资源**，默认情况下是false

* 日期表达式：大多数参数接收格式化日期表达式，如查询范围gt(大于）和lt(小于）或者在日期中使用from  to 来表达时间范围，表达式设定的**日期为now或者日期字符串||**
 * +1h  增加1小时
 * -1D  减少一小时
 * /D   上一个小时
 
支持的时间单位为：y(年），M（月），w(周），d(日），h(小时），m（分钟），s(秒），例如：

```
now+1h  #当前时间为1小时，以毫秒为单位
now+1h+1m   #当前时间加1小时和1分钟，以毫秒为单位
now+1h/d    #当前时间加1小时，四舍五入到最近一天
2015-01-01||+1M/d   #2015-01-01j加一个月，向下舍入到最近一天
```


* 响应过滤(filter_path):所有的返回值可以通过filter\_path来减少返回值的内容，多个值可以用逗号隔开

![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/filter_path.png)

他也支持通配符*匹配任何字段的名称，例如：


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/filter_path2.png)



注意，有时直接返回Elasticsearch的某个字段的原始值，如\_source字段，如果你想过滤\_source字段，可以结合\_source字段和filter\_path参数,如下：filter_path=hits.hits.source可以拿到所有的source字段，但是如果我们只是需要仅有的几个字段，那么我们可以使用\_source=title，这样就只是返回source中的title字段了

![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/filter_path3.png)


* flat\_settings:是设置扁平化，为true的时候返回的内容更加紧凑，false的时候返回的值更加的容易阅读：

true的时候

![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/flat_settings.png)


false 的时候


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/flat_settings2.png)




