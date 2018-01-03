es对外提供API是以http协议的方式，通过json格式以REST约定对外提供

http相关的配置在elasticsearch.yml中，所有与http配置相关的内容都是静态的，也就是需要重启后才能生效，http对外接口模块是可以禁用的，只需要http.enabled: false，**es集群中的通信是通过内部接口实现的，而不是http协议，所以在集群中不需要所有节点都开启http协议，正常情况下，只需要在一个节点开启http协议即可**



**REST介绍**

REST约定用HTTP的请求头POT,GET,PUT,DELETE正好可以对应CRUD（Create，Read，Update，Delete）四种数据操作，REST请求头说明见表：


![](/Users/chenyansong/Documents/note/elasticsearch/Elasticsearch技术解析与实战_读书笔记/images/rest_crud.jpg)


这里需要理解一下这种请求的形式：

* 首先，不论是PUT,还是GET，还是其他的请求头，这只是HTTP的请求头，他们放在**请求的head中**，真正请求的URL是一个HTTP的地址，我们可以把请求的地址当做一个连接，这样这个链接就可以分解为ip:port/action/method，知道ssh的就能够理解，每一个请求就是请求对应action的method

