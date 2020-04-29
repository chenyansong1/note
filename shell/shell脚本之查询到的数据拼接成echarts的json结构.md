# 1.查询到的数据结构

```
time	A	B	C	D
20170511	1	2	3	1
20170512	2	6	2	2
20170513	3	3	4	4
20170514	9	1	1	2
20170515	1	2	3	1
```


# 2.执行Python脚本生成echarts的json字符串

genderEcharJsonByQueryData.py

[Python脚本](/shell/genderEcharJsonByQueryData.py)

脚本传递的参数：
* 参数1：数据存放的文件（全路径）
* char的类型（不传是line），你可以传line，bar等



# 3.去页面渲染char

将json字符串贴到echarts的官网，就可以看到效果


[echarts在线编辑页](http://gallery.echartsjs.com/editor.html)
