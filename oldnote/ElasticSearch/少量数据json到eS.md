
# 查询ES，写入数据到json文件


```
#!/bin/python
# coding: utf-8


import requests,json,csv,datetime,time,sys,os,commands,logging,uuid

#中文字段编码的问题
reload(sys)
sys.setdefaultencoding('utf8')


# 查询数据到ES
def queryDataFromES():

	url = "http://172.16.14.21:9200/event_20180328/_search"
	params=json.dumps({'query': {'bool': {'must': [{'term': {'event_base_type': 3}}]}}})
    headers = {'content-type': 'application/json', 'Accept-Charset': 'UTF-8'}
    r = requests.post(url, data=params, headers=headers)

    #返回json数据
    json_data = json.loads(r.content)

    return json_data['hits']['hits']



# 写数据到json
def writeData2JsonFile(data):

	# 写入json文件
	f=open("data-event.json", "w")

	for obj in data:
		id = obj['_id']
		data_id = {"index":{"_id":id}}
		row = obj['_source']

		idStr = json.dumps(data_id)
		rowStr = json.dumps(row)

		f.write(idStr)
		f.write('\n')
		f.write(rowStr)
		f.write('\n')#显示写入换行
	f.close()

		# json.dump(row, outfile)
		# json.dump(outfile)
				


# 加载数据到es


if __name__ == "__main__":
	
	# 查询数据到json文件
	data = queryDataFromES()
	
	# 处理数据
	writeData2JsonFile(data)

```


# 加载json数据到ES


新建索引

```
# 新建索引

curl -XPUT 'http://192.168.0.1:9200/event_20180328' -d '{"settings" : { "number_of_shards" : 1 },"event_20180328" : {"event_20180328" : { "properties" : { "event_base_type":{ "type" : "string", "index" : "not_analyzed"  }, "event_sub_type":{ "type" : "string", "index" : "not_analyzed"  },"event_rule_name":{ "type" : "string","index" : "not_analyzed" },"event_rule_level":{"type" : "string","index" : "not_analyzed" }}}}}'

curl -XPUT 'http://192.168.0.1:9200/event_20180328/event_20180328' 
 
curl -XPUT 'http://192.168.0.1:9200/event_20180328' -d '{"settings" : { "number_of_shards" : 1 },"event_20180328" : {"event_20180328" : { "properties" : { "event_base_type":{ "type" : "string", "index" : "not_analyzed"  }, "event_sub_type":{ "type" : "string", "index" : "not_analyzed"  },"event_rule_name":{ "type" : "string","index" : "not_analyzed" },"event_rule_level":{"type" : "string","index" : "not_analyzed" }}}}}'

curl -XPOST localhost:9200/syslog_20180328 -d '{  "settings" : {  "number_of_shards" : 1 } }'

```

导入

```
curl -XPOST 'http://192.168.0.1:9200/event_20180328/event_20180328/_bulk?pretty' --data-binary "@/home/data-event-20180328.json"

```
