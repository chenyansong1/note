---
title: SparkStreaming之与Storm的对比分析
categories: spark  
tags: [spark]
---



sparkStreaming与storm的对比

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/sparkStreaming与storm的对比.png)


sparkStreaming与storm的优劣分析

事实上,spark streaming绝对谈不上比Storm优秀,这两个框架在实时计算领域中,都很优秀,只是擅长的细分场景并不相同

spark streaming仅仅在吞吐量上比storm要优秀,而吞吐量这一点,也是历来挺spark streaming的人着重强调的,但是问题是,并不是所有的实时计算场景下,都那么注重吞吐量,因此,通过吞吐量说spark streaming强于storm并不能说服人

事实上,storm在实时延迟度上,比spark streaming就好多了,前者是纯实时的,但是后者是准实时的,而且,storm的事务机制,健壮性,容错性,动态调整并行度等特性,都要比spark streaming更加优秀

spark streaming有一点是storm绝对比不上的,就是:它位于spark生态技术栈中,因此,spark streaming可以和spark core,spark sql无缝整合,也就意味着,我们可以对实时处理出来的中间数据,立即在程序中无缝进行延时批处理,交互式查询等操作,这个特点大大增强了spark streaming的优势和功能


spark streaming与storm的应用场景
对于storm来说:
1.建议那种需要纯实时,不能忍受1秒以上延时的场景下使用,比如实时金融系统,要求纯实时进行金融交易和分析
2.此外,如果对于实时计算的功能中,要求可靠的事务机制,即数据的处理完全精准,一条也不能少,一条也不能多,那么可以考虑storm
3.如果还需要针对高峰低峰时间段,动态调整实时计算程序的并行度,以最大限度利用集群资源(通常是小型公司,集群资源紧张的情况),也可以考虑storm
4.如果一个大数据应用系统,他就是纯粹的实时计算,不需要在中间执行sql交互式查询,复杂的Transformation算子等,那么用storm是比较好的选择

对于spark streaming来说:
1.如果对上述适用于storm的三点,一条都不满足的实时场景,即:不要求纯实时,不要求强大可靠的事务机制,不要求动态调整并行度,那么可以考虑使用spark streaming
2.考虑使用spark streaming最主要的一个因素,应该是针对整个项目进行宏观的考虑,可能还会牵扯到高延迟批处理,交互式查询等功能,那么就应该首先spark生态,用spark core开发离线批处理,用spark sql开发交互式查询,用spark streaming开发实时计算,三者可以无缝整合,给系统提供非常高的可扩展性
