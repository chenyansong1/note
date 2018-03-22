# netflow 表中的字段

```
{
	"srcip":"172.16.110.49",
	"dstip":"65.55.252.202",
	"srcport":57319,
	"dstport":443,
	"protocol":"tcp",
	"opentime":1521165600000,
	"upbytesize":7296,
	"downbytesize":5463,
}
```



# ES 中的聚合条件

```
# select sum(upbytesize), sum(downbytesize) from netflow group by srcip,dstip ;

{
   "size" : 0,
   "aggs": {
      "src_ip": {
         "terms": {
            "field": "srcip"
         },
         "aggs": {
            "dst_ip": { 
               "terms": {
                  "field": "dstip"
               },
			   "aggs":{
					"upbytesizeSum":{
						"sum":{"field": "upbytesize"}
					},
					"downbytesizeSum":{
						"sum":{"field": "downbytesize"}
					}
			   }
            }
         }
      }
   }
}
```


# 聚合结果
			
```
{
    "took": 114,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits": {
        "total": 4,
        "max_score": 0,
        "hits": []
    },
    "aggregations": {
        "src_ip": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
                {
                    "key": "172.16.1.9",
                    "doc_count": 2,
                    "dst_ip": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "172.11.1.12",
                                "doc_count": 2,
                                "upbytesizeSum": {
                                    "value": 14
                                },
                                "downbytesizeSum": {
                                    "value": 16
                                }
                            }
                        ]
                    }
                },
                {
                    "key": "172.16.110.49",
                    "doc_count": 2,
                    "dst_ip": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "65.55.252.202",
                                "doc_count": 2,
                                "upbytesizeSum": {
                                    "value": 14592
                                },
                                "downbytesizeSum": {
                                    "value": 10926
                                }
                            }
                        ]
                    }
                }
            ]
        }
    }
}
```



# 添加时间戳聚合

```
{
   "size" : 0,
   "aggs": {
		"opentime_agg": {
			"date_histogram": {
				"field": "opentime",
				"interval": "day", 
				"format": "yyyy-MM-dd" 
			},
		
			"aggs": {
			  "src_ip": {
				 "terms": {
					"field": "srcip"
				 },
				 "aggs": {
					"dst_ip": { 
					   "terms": {
						  "field": "dstip"
					   },
					   "aggs":{
							"upbytesizeSum":{
								"sum":{"field": "upbytesize"}
							},
							"downbytesizeSum":{
								"sum":{"field": "downbytesize"}
							}
					   }
					}
				 }
			  }
		   }
      }
   }
}
```

# 添加时间戳后的聚合结果

```
{
    "took": 16,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits": {
        "total": 6,
        "max_score": 0,
        "hits": []
    },
    "aggregations": {
        "opentime_agg": {
            "buckets": [
                {
                    "key_as_string": "2018-03-16",
                    "key": 1521158400000,
                    "doc_count": 4,
                    "src_ip": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "172.16.1.9",
                                "doc_count": 2,
                                "dst_ip": {
                                    "doc_count_error_upper_bound": 0,
                                    "sum_other_doc_count": 0,
                                    "buckets": [
                                        {
                                            "key": "172.11.1.12",
                                            "doc_count": 2,
                                            "upbytesizeSum": {
                                                "value": 14
                                            },
                                            "downbytesizeSum": {
                                                "value": 16
                                            }
                                        }
                                    ]
                                }
                            },
                            {
                                "key": "172.16.110.49",
                                "doc_count": 2,
                                "dst_ip": {
                                    "doc_count_error_upper_bound": 0,
                                    "sum_other_doc_count": 0,
                                    "buckets": [
                                        {
                                            "key": "65.55.252.202",
                                            "doc_count": 2,
                                            "upbytesizeSum": {
                                                "value": 14592
                                            },
                                            "downbytesizeSum": {
                                                "value": 10926
                                            }
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                },
                {
                    "key_as_string": "2018-03-17",
                    "key": 1521244800000,
                    "doc_count": 2,
                    "src_ip": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "172.16.1.9",
                                "doc_count": 2,
                                "dst_ip": {
                                    "doc_count_error_upper_bound": 0,
                                    "sum_other_doc_count": 0,
                                    "buckets": [
                                        {
                                            "key": "172.11.1.12",
                                            "doc_count": 2,
                                            "upbytesizeSum": {
                                                "value": 22
                                            },
                                            "downbytesizeSum": {
                                                "value": 24
                                            }
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                }
            ]
        }
    }
}
```
	
