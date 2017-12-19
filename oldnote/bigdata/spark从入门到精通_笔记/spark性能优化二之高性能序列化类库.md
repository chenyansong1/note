---
title: spark性能优化二之高性能序列化类库
categories: spark  
tags: [spark]
---




因为在分布式中存在着网络传输,那么数据就必须进行序列化,而如果在执行序列化操作的时候很慢,或者序列化后的数据还是很大,那么会让分布式应用程序的性能下降很多,所以,进行spark性能优化的第一步,就是进行序列化的性能优化

<!--more-->



spark自身对于序列化的便捷性和性能进行了一个取舍和权衡,默认,spark倾向于序列化的便捷性,使用了java自身提供的序列化机制---基于ObjectInputStream和ObjectOutputStream的序列化机制,因为这种方式是java原生提供的,很方便使用

但是问题是,java序列化的性能并不高,序列化的速度相对较慢,而且序列化以后的数,还是相对来说比较大,还是比较占用内存孔家你的,因此,如果你的spark应用程序对内存很敏感,那么,实际上默认的java序列化的机制并不是最好的选择.


spark提供了两种序列化机制,他默认使用了第一种:
1.java序列化机制,默认情况下,spark使用java自身的ObjectInputStream和ObjectOutputStream机制进行对象的序列化,只要你的类实现了Serializable接口,那么都是可以序列化的,而且java序列化机制是提供了自定义序列化支持的,只要你实现Externalizable接口即可实现自己的更高性能的序列化算法,java序列化机制的速度比较慢,而且序列化后的数据占用的内存空间比较大

2.Kryo序列化机制:spark也支持使用Kryo类库来进行序列化,Kryo序列化机制比java机制更快,而且序列化后的数据占用的空间更小,通常比java序列化的数据占用的空间要小10倍,Kryo序列化机制下之所以不是默认序列化机制的原因是:有些类型虽然实现了Seriralizable接口,但是他也不一定能够进行序列化,因此,如果你要得到最佳的性能,Kryo还要求你在spark应用中,对所有你需要序列化的的类型都进行注册




如何使用Kryo序列化机制
要序列化的类是实现了Serializable接口的
1.首先在SparkConf设置一个参数:
SparkConf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

使用Kryo时,他要求需要序列化的类,是要预先进行注册的,以获得最佳性能---如果不注册的话,那么Kryo必须时刻保存类型的全限定名,反而 占用不少内存,spark默认是对scala中常用的类型自动注册了Kryo的,都在AllScalaRegistry类中


2.注册:比如自己的算子中,使用了外部的自定义类型的对象,那么还是需要将其进行注册:
```
//实际上,下面的写法是错误的,因为counter不是共享的,所以累加器的功能是无法实现的
val count = new Counter()
val numbers = sc.parallelize(Array(1,2,3,4,5))
numbers.foreach(counter.add(_))
```


如果要注册自定义的类型,那么就使用如下的模板代码即可:
```
val conf = new SparkConf().setMaster(...).setAppName(...)
conf.registerKryoClasses(Array(classOf[Counter])) //classOf是拿到某个类的类型(相当于java中的Counter.class)
val sc = new SparkContext(conf)
```




优化Kryo类库的使用
1.优化缓存大小
如果注册的要序列化的自定义的类型,本身特别大,比如包含了超过100个Field,那么就会导致要序列化的对象过大,此时就需要对Kryo本身进行优化,因为Kryo内部的缓存可能不够存放那么大的class对象,此时就需要调用SparkConf.set(),设置spark.kryoserializer.buffer.mb参数的值,将其调大

默认情况下他的值是2,就是说最大能缓存2M的对象,然后进行序列化,可以在必要时将其调大,比如设置为10



2.预先注册自定义类型

虽然不注册自定义类型,Kryo类库也能正常工作,但是那样的话,对于他要序列化的每个对象,都会保存一份他的全限定类名,此时反而会耗费大量内存,因此通常都建议预先注册好要序列化的自定义的类




在什么场景下使用Kryo序列化类库?
首先,这里讨论的都是spark的一些普通的场景,一些特殊的场景,比如RDD的持久化,在后面会讲到

那么这里针对的Kryo序列化类库的使用场景,就是算子函数使用到了外部的大数据(类比较大)的情况,比如,我们在外部定义了一个封装了应用所有配置的对象,比如说自定义了一个MyConfiguration对象,里面包含了100m的数据,然后在算子函数里面使用到了这个外部的大对象,此时如果默认情况下,让spark用java序列化机制来序列化这种外部的大对象,就会导致序列化速度缓慢,并且序列化以后的数据还是比较大,比较占用内存空间,因此在这种情况下,比较适合切换到Kryo类库,来对外部的大对象进行操作,一是序列化的速度会变快,二是会减少序列化后的数据占用的空间


