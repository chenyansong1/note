es的scroll分页

from size 存在深度查询的问题

https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html
https://lxwei.github.io/posts/使用scroll实现Elasticsearch数据遍历和深度分页.html
https://www.elastic.co/guide/cn/elasticsearch/guide/current/scroll.html

https://lxwei.github.io/posts/%E4%BD%BF%E7%94%A8scroll%E5%AE%9E%E7%8E%B0Elasticsearch%E6%95%B0%E6%8D%AE%E9%81%8D%E5%8E%86%E5%92%8C%E6%B7%B1%E5%BA%A6%E5%88%86%E9%A1%B5.html

请求结果超过1w
http://blog.csdn.net/u014431852/article/details/52830938

POST /genlog/genlog/_search?scroll=1m
{
  "query": {
    "range": {
      "firstrecvtime": {
        "gte": 1508309711000,
        "lte": 1508309711000
      }
    }
  }
}

POST  /_search/scroll 
{
    "scroll" : "1m", 
    "scroll_id" : "cXVlcnlUaGVuRmV0Y2g7NTsxMTA3ODpFMmVjUGhQVFNuU2ZZa3hWZFFKZWhROzExMDc5OkUyZWNQaFBUU25TZllreFZkUUplaFE7MTEwODA6RTJlY1BoUFRTblNmWWt4VmRRSmVoUTsxMTA2NzpWeW4xamlXV1NmQ1dJS18wVm9xUUdBOzExMDgxOkUyZWNQaFBUU25TZllreFZkUUplaFE7MDs=" 
}










