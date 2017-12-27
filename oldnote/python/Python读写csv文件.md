Python读写csv文件

1. 写入并生成csv文件
代码：

```
# coding: utf-8

import csv

csvfile = file('csv_test.csv', 'wb')
writer = csv.writer(csvfile)
writer.writerow(['姓名', '年龄', '电话'])

data = [
    ('小河', '25', '1234567'),
    ('小芳', '18', '789456')
]
writer.writerows(data)

csvfile.close()
```

wb中的w表示写入模式，b是文件模式
写入一行用writerow
多行用writerows

2. 读取csv文件
代码：

```
# coding: utf-8

import csv

csvfile = file('csv_test.csv', 'rb')
reader = csv.reader(csvfile)

for line in reader:
    print line

csvfile.close()
``` 