jar下的文件不能读取的解决方式

````

java.io.FileNotFoundException: File 'file:/usr/local/workspace/ReportServer/lib/ReportServer.jar!/esquery/syslog/syslog-all-statistics.json' does not exist
````



```
#建议将文件放到class目录下，然后通过下面的方式进行读取：

// 读取模板文件
InputStream is = ESHttpSearchUtils.class.getResourceAsStream(templateFileName);// 读取class路径下的文件
String jsonTempleteCoentent = IOUtils.toString(is);
```

