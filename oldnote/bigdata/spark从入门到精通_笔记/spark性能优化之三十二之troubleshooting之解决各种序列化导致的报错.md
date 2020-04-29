---
title: spark性能优化之三十二之troubleshooting之解决各种序列化导致的报错
categories: spark  
tags: [spark]
---


你会看到什么样的序列化导致的报错？

<!--more-->

用client模式去提交spark作业，观察本地打印出来的log。如果出现了类似于Serializable、Serialize等等字眼，报错的log，那么恭喜大家，就碰到了序列化问题导致的报错。

虽然是报错，但是序列化报错，应该是属于比较简单的了，很好处理。

序列化报错要注意的三个点：

1、你的算子函数里面，如果使用到了外部的自定义类型的变量，那么此时，就要求你的自定义类型，**必须是可序列化的**

```

final Teacher teacher = new Teacher("leo");

studentsRDD.foreach(new VoidFunction() {
 
public void call(Row row) throws Exception {
  String teacherName = teacher.getName();
  ....  
}

});

public class Teacher implements Serializable {
  
}
```

2、如果要将自定义的类型，**作为RDD的元素类型**，那么自定义的类型也必须是可以序列化的
```
JavaPairRDD<Integer, Teacher> teacherRDD
JavaPairRDD<Integer, Student> studentRDD
studentRDD.join(teacherRDD)

public class Teacher implements Serializable {
  
}

public class Student implements Serializable {
  
}
```

3、不能在上述两种情况下，去使用一些第三方的，不支持序列化的类型
```
Connection conn = 

studentsRDD.foreach(new VoidFunction() {
 
public void call(Row row) throws Exception {
  conn.....
}

});

//Connection是不支持序列化的
```






