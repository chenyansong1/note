# 逻辑视图

|Row Key	|	Time Stamp	|	ColumnFamily contents |	ColumnFamily anchor	|	ColumnFamily people|
|-------|---------------|--------------------------|---------------------|---------------------|
|"com.cnn.www"| t9		|				&emsp;		   | anchor:cnnsi.com = "CNN"|			&emsp;		|
|"com.cnn.www"|		t8	|				&emsp;		  |anchor:my.look.ca = "CNN.com"|	&emsp;				|
|"com.cnn.www"|		t6	|contents:html = "<html>…​"  |			&emsp;			  |		&emsp;			|
|"com.cnn.www"|		t5	|contents:html = "<html>…​" |			&emsp;			  |		&emsp;			|
|"com.cnn.www"|		t3	|contents:html = "<html>…​" |			&emsp;			  |		&emsp;			|
|"com.example.www"|		t5	|contents:html = "<html>…​" |people:author="John Doe"	  |		&emsp;			|


另外一种表现形式为：

![](/images/bigdata/hbase/data_model.jpg)


# 物理视图

```
{
  "com.cnn.www": {#一列（相同的行健）都存放在一起
    contents: {#一个列族都存放在一起（说明Hbase是面向列存储的）
      t6: contents:html: "<html>..."
      t5: contents:html: "<html>..."
      t3: contents:html: "<html>..."
    }
    anchor: {
      t9: anchor:cnnsi.com = "CNN"
      t8: anchor:my.look.ca = "CNN.com"
    }
    people: {}#一行中空列是不会存储的
  }
  "com.example.www": {
    contents: {
      t5: contents:html: "<html>..."
    }
    anchor: {}
    people: {
      t5: people:author: "John Doe"
    }
  }
}
```


通过物理视图和逻辑视图结合，可以知道：
* Hbase中相同的row key作为一行
* Hbase是面向列存储的
* Hbase中列族是固定的，但是列是可以动态添加的
* 查询的时候Hbase，默认返回最新的列（如：上面的"com.cnn.www"行，对于contents:html列，返回时间戳为t6的内容







