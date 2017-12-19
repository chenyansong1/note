# 1.增加映射
API允许你向index添加文档类型（type），或者向文档类型（type）中添加字段（field）

```
#向索引sicisland中**添加**一个type=log,其中包含字段message，类型为string
PUT http://localhost:9200/secisland
{
	"mappings":{
		"log":{
			"properties":{
				"message":{"type":"string"}
			}
		}
	}
}


#向索引（secisland）的user（type）中**添加**一个字段（field）为name
PUT http://localhost:9200/secisland/_mapping/user
{
	"properties":{
		"name":{"type":"string"}
	}
}

```



