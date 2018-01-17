---
title: es-java-API
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---


# 1.CRUD

```
public class EmployeeSearchApp {

	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws Exception {
		Settings settings = Settings.builder()
				.put("cluster.name", "elasticsearch")
				.build();
		
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300)); 
		
//		prepareData(client);
		executeSearch(client);
		
		client.close();
	
	}

	/**
	 * 执行搜索操作
	 * @param client
	 */
	private static void executeSearch(TransportClient client) {
		SearchResponse response = client.prepareSearch("company")
				.setTypes("employee")
				.setQuery(QueryBuilders.matchQuery("position", "technique"))
				.setPostFilter(QueryBuilders.rangeQuery("age").from(30).to(40))
				.setFrom(0).setSize(1)
				.get();
		
		SearchHit[] searchHits = response.getHits().getHits();
		for(int i = 0; i < searchHits.length; i++) {
			System.out.println(searchHits[i].getSourceAsString()); 
		}
	}

	/**
	 * 准备数据
	 * @param client
	 */
	private static void prepareData(TransportClient client) throws Exception {
		client.prepareIndex("company", "employee", "1") 
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
							.field("name", "jack")
							.field("age", 27)
							.field("position", "technique software")
							.field("country", "china")
							.field("join_date", "2017-01-01")
							.field("salary", 10000)
						.endObject())
				.get();
		
		client.prepareIndex("company", "employee", "2") 
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
							.field("name", "marry")
							.field("age", 35)
							.field("position", "technique manager")
							.field("country", "china")
							.field("join_date", "2017-01-01")
							.field("salary", 12000)
						.endObject())
				.get();
		
	}
	
}

	
	/**
	 * 创建员工信息（创建一个document）
	 * @param client
	 */
	private static void createEmployee(TransportClient client) throws Exception {
		IndexResponse response = client.prepareIndex("company", "employee", "1")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
							.field("name", "jack")
							.field("age", 27)
							.field("position", "technique")
							.field("country", "china")
							.field("join_date", "2017-01-01")
							.field("salary", 10000)
						.endObject())
				.get();
		System.out.println(response.getResult()); 
	}
	
	/**
	 * 获取员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void getEmployee(TransportClient client) throws Exception {
		GetResponse response = client.prepareGet("company", "employee", "1").get();
		System.out.println(response.getSourceAsString()); 
	}
	
	/**
	 * 修改员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void updateEmployee(TransportClient client) throws Exception {
		UpdateResponse response = client.prepareUpdate("company", "employee", "1") 
				.setDoc(XContentFactory.jsonBuilder()
							.startObject()
								.field("position", "technique manager")
							.endObject())
				.get();
		System.out.println(response.getResult());  
 	}
	
	/**
	 * 删除 员工信息
	 * @param client
	 * @throws Exception
	 */
	private static void deleteEmployee(TransportClient client) throws Exception {
		DeleteResponse response = client.prepareDelete("company", "employee", "1").get();
		System.out.println(response.getResult());  
	}
```


# 聚合api

需求：

（1）首先按照country国家来进行分组
（2）然后在每个country分组内，再按照入职年限进行分组
（3）最后计算每个分组内的平均薪资


```
GET /company/employee/_search
{
  "size": 0,
  "aggs": {
    "group_by_country": {
      "terms": {
        "field": "country"
      },
      "aggs": {
        "group_by_join_date": {
          "date_histogram": {
            "field": "join_date",
            "interval": "year"
          },
          "aggs": {
            "avg_salary": {
              "avg": {
                "field": "salary"
              }
            }
          }
        }
      }
    }
  }
}



SearchResponse sr = node.client().prepareSearch()
    .addAggregation(
        AggregationBuilders.terms("by_country").field("country")
        .subAggregation(AggregationBuilders.dateHistogram("by_year")
            .field("dateOfBirth")
            .dateHistogramInterval(DateHistogramInterval.YEAR)
            .subAggregation(AggregationBuilders.avg("avg_children").field("children"))
        )
    )
    .execute().actionGet();
    

Map<String, Aggregation> aggrMap = searchResponse.getAggregations().asMap();
		StringTerms groupByCountry = (StringTerms) aggrMap.get("group_by_country");
		Iterator<Bucket> groupByCountryBucketIterator = groupByCountry.getBuckets().iterator();
		
		while(groupByCountryBucketIterator.hasNext()) {
			Bucket groupByCountryBucket = groupByCountryBucketIterator.next();
			
			System.out.println(groupByCountryBucket.getKey() + "\t" + groupByCountryBucket.getDocCount()); 
			
			Histogram groupByJoinDate = (Histogram) groupByCountryBucket.getAggregations().asMap().get("group_by_join_date"); 
			Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByJoinDateBucketIterator = groupByJoinDate.getBuckets().iterator();
			 
			while(groupByJoinDateBucketIterator.hasNext()) {
				org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket groupByJoinDateBucket = groupByJoinDateBucketIterator.next();
				
				System.out.println(groupByJoinDateBucket.getKey() + "\t" + groupByJoinDateBucket.getDocCount()); 
				
				Avg avgSalary = (Avg) groupByJoinDateBucket.getAggregations().asMap().get("avg_salary");
				System.out.println(avgSalary.getValue()); 
			}
		}
		
		client.close();
	}
```