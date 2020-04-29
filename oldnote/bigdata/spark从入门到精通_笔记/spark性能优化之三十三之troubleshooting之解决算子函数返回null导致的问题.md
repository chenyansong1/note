---
title: spark性能优化之三十三之troubleshooting之解决算子函数返回null导致的问题
categories: spark  
tags: [spark]
---


```
//在算子函数中，返回null

//		return actionRDD.mapToPair(new PairFunction<Row, String, Row>() {
//
//			private static final long serialVersionUID = 1L;
//			
//			@Override
//			public Tuple2<String, Row> call(Row row) throws Exception {
//				return new Tuple2<String, Row>("-999", RowFactory.createRow("-999"));  
//			}
//			
//		});

```

大家可以看到，在有些算子函数里面，是需要我们有一个返回值的。但是，有时候，我们可能对某些值，就是不想有什么返回值。我们如果直接返回NULL的话，那么可以不幸的告诉大家，是不行的，会报错的。

Scala.Math(NULL)，异常

如果碰到你的确是对于某些值，不想要有返回值的话，有一个解决的办法：

1、在返回的时候，返回一些特殊的值，不要返回null，比如“-999”
2、在通过算子获取到了一个RDD之后，可以对这个RDD执行filter操作，进行数据过滤。filter内，可以对数据进行判定，如果是-999，那么就返回false，给过滤掉就可以了。
3、大家不要忘了，之前咱们讲过的那个算子调优里面的coalesce算子，在filter之后，可以使用coalesce算子压缩一下RDD的partition的数量，让各个partition的数据比较紧凑一些。也能提升一些性能。










