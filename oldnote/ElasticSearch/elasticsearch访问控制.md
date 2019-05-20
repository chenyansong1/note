elasticsearch访问控制



https://www.cnblogs.com/shifu204/p/6376683.html

https://blog.csdn.net/eff666/article/details/52916355

https://www.cnblogs.com/marility/p/9392645.html

https://www.felayman.com/articles/2017/11/21/1511258584025.html#b3_solo_h2_8



https://blog.csdn.net/zmx729618/article/details/81095832

官网：https://docs.search-guard.com/latest/search-guard-versions





升级问题：https://zshell.cc/2018/03/24/elasticsearch--elasticsearch6.x%E5%8D%87%E7%BA%A7%E8%B0%83%E7%A0%94%E6%8A%A5%E5%91%8A/





提供统一的调用接口，不能随便调用ES的查询



search guard 安装

```
#需要根据ES的版本去这个页面看对应的guard和ssl的版本：https://docs.search-guard.com/v5/search-guard-versions

bin/plugin install -b com.floragunn/search-guard-2/2.4.3.12

bin/plugin install -b com.floragunn/search-guard-ssl/2.4.3.19

```





# 搭建search-guard的blog



https://www.cnblogs.com/shifu204/p/6376683.html

https://blog.csdn.net/zmx729618/article/details/81095832



官网：https://docs.search-guard.com/v5/json-web-token

解决版本的选择问题：https://docs.search-guard.com/v5/search-guard-versions







# 添加了权限控制之后的访问问题

<https://search-guard.com/searchguard-elasicsearch-transport-clients/>



java客户端访问：https://search-guard.com/searchguard-elasicsearch-transport-clients/



https://www.techcoil.com/blog/how-to-send-an-http-request-to-a-http-basic-authentication-endpoint-in-java-without-using-any-external-libraries/





1. http访问
2. java客户端访问



访问的接口，测试问题



# 现有涉及到ES的接口修改

1. 现在只是针对海大的版本做修改，其他的项目先不要动
2. java客户端引入证书
3. http提供密码的形式



