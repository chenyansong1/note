# 1.FilterList

这是一个过滤器列表，可以指定：
* FilterList.Operator.MUST_PASS_ALL（列表内的过滤器要全部满足，相当于与）
* FilterList.Operator.MUST_PASS_ONE（列表的过滤器只要满足一项就行了，相当于或，这是默认）
* list里面可以包含list。

```
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row9".getBytes());  

#设置两个简单的过滤器
FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);  
SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.GREATER_OR_EQUAL, "name7".getBytes());  
  
SingleColumnValueFilter scvf2 = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.LESS_OR_EQUAL, "name9".getBytes());  
  
list.addFilter(scvf2);  
list.addFilter(scvf);  
  
scan.setFilter(list);

#CompareOp是一个枚举类型，有等于，不等于，大于，大于或等于，小于，小于或等于，no operation。

```

# 2.SingleColumnValueFilter值的过滤器

用于测试列值，有CompareOp的枚举类型，可以测试等于，不等于或范围。不用FilterList的话scan设置的Filter就是最后设置的那个。

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row9".getBytes());  
  
SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.GREATER, "name2".getBytes());  
  
scan.setFilter(scvf);

```

# 3. RegexStringComparator

支持值比较的正则表达式

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row9".getBytes());  
  
RegexStringComparator filter = new RegexStringComparator("name.");//所有值为name开头的  
SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.EQUAL, filter);  
  
scan.setFilter(scvf);  
```

# 4.SubstringComparator

用于检测列值是不是包含一段字符串
```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row9".getBytes());  
  
SubstringComparator sc = new SubstringComparator("NAme");//大小写不敏感  
SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.EQUAL, sc);  

scan.setFilter(scvf); 
```

# 5.BinaryComparator
这是一个二进制的值的比较器
```
scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row9".getBytes());  
  
BinaryComparator bc = new BinaryComparator("name6".getBytes());  
System.out.println(bc.compareTo("name7".getBytes()));  
SingleColumnValueFilter scvf = new SingleColumnValueFilter(  
        "f".getBytes(),"name".getBytes(), CompareOp.GREATER, bc);  

scan.setFilter(scvf);
```



# 6.FamilyFilter列族的过滤器
 过滤列族的。

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row99".getBytes());      
FamilyFilter ff =new FamilyFilter(CompareOp.EQUAL,new BinaryComparator("f".getBytes()));  
scan.setFilter(ff); 
 
```


# 7.QuallifierFilter   列名的过滤器
过滤列名的。

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row99".getBytes());  
  
QualifierFilter qf = new QualifierFilter(CompareOp.EQUAL, new BinaryComparator("age".getBytes()));//写列名  
  
scan.setFilter(qf);  

```


# 8.ColumnPrefixFilter 列名前缀过滤

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row99".getBytes());  
  
ColumnPrefixFilter cpf = new ColumnPrefixFilter("n".getBytes());//写列名前缀  

scan.setFilter(cpf);  

```



# 9.MultipleColumnPrefixFilter

可用包含多个列名前缀

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row99".getBytes());  
  
MultipleColumnPrefixFilter cpf = new MultipleColumnPrefixFilter(new byte[][]{"a".getBytes(),"n".getBytes()});  

scan.setFilter(cpf);  
```


# 10.ColumnRangeFilter
     列名范围的过滤器

```
Scan scan = new Scan();  
        scan.setStartRow("row0".getBytes());  
        scan.setStopRow("row6".getBytes());  
          
    ColumnRangeFilter cpf = new ColumnRangeFilter("age".getBytes(),true,"ass".getBytes(),true);//minColumn,是否包含,maxColumn,是否包含  
          
        scan.setFilter(cpf);  
```


# 11.RowFilter 过滤行的，但是一般使用scan的startRow/stopRow

```
Scan scan = new Scan();  
//      scan.setStartRow("row0".getBytes());  
//      scan.setStopRow("row6".getBytes());  
          
        FilterList list = new FilterList();  
        RowFilter cpf = new RowFilter(CompareOp.GREATER, new BinaryComparator("row1".getBytes()));  
        RowFilter cpf2 = new RowFilter(CompareOp.LESS, new BinaryComparator("row6".getBytes()));  
          
        list.addFilter(cpf);  
        list.addFilter(cpf2);  
        scan.setFilter(list);  

```


# 12. FirstKeyOnlyFilter
    返回每一行的第一个列数据

```
Scan scan = new Scan();  
        scan.setStartRow("row0".getBytes());  
        scan.setStopRow("row6".getBytes());  
          
        FirstKeyOnlyFilter list = new FirstKeyOnlyFilter();  
          
        scan.setFilter(list);  
```



# 13.ColumnCountGetFilter
    返回一行的前几列

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row6".getBytes());  
  
ColumnCountGetFilter filter =  new ColumnCountGetFilter(2);  
scan.setFilter(filter); 
```




# 14.KeyOnlyFilter
    列值全部重写为空。
```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row6".getBytes());  
  
Filter filter = new KeyOnlyFilter();  
scan.setFilter(filter);  
```


# 15.PageFilter
限制返回的行数

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row6".getBytes());  
  
Filter filter = new PageFilter(10);  
scan.setFilter(filter);

```


# 16.TimestampsFilter
    只返回指定时间戳的行

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row6".getBytes());  
  
List<Long> list = new ArrayList<Long>();  
long time = 1499137110890l;  
list.add(time);  
Filter filter = new TimestampsFilter(list);  
scan.setFilter(filter); 
```


# 17.ValueFilter   只返回满足条件的列

```
Scan scan = new Scan();  
scan.setStartRow("row0".getBytes());  
scan.setStopRow("row6".getBytes());  
  
Filter filter = new ValueFilter(CompareOp.EQUAL, new BinaryComparator("age2".getBytes()));  
scan.setFilter(filter);

```


# 18.prefixFilter
行的前缀过滤

```
Scan scan = new Scan();  
//      scan.setStartRow("row0".getBytes());  
//      scan.setStopRow("row6".getBytes());  
          
        Filter filter = new PrefixFilter("row3".getBytes());  
        scan.setFilter(filter);  

```


参考：

http://blog.csdn.net/u014034934/article/details/74282555

