---
title:  python数据类型之文件
categories: python   
toc: true  
tags: [python]
---



# 1.常见的文件运算


|表达式|含义|
|-|-|
|output = open(r'C:\spam', 'w')	|创建输出文件（w表示写入）              |
|input = open('data', 'r')	|创建输入文件（r表示读）                    |
|input = open('data')	|同上（默认是r)                                 |
|aString = input.read()	|把整个文件读进单一字符串                       |
|aString = input.read(N)	|读取之后的N个字节到一个字符串              |
|aString = input.readline()	|读取下一行（包含文末标识符）到一个字符串   |
|aList = input.readlines()	|读取整个文件到字符串列表                   |
|output.write(aString)	|写入字节字符串到文件                           |
|output.writelines(aList)	|将列表中所有字符串写入到文件               |
|output.close()	|手动关闭                                               |
|output.flush()	|将输出缓冲刷到磁盘，但是不关闭                         |
|anyFile.seek(N)	|修改文件位置到偏移量N处以便进行下一个操作          |
|for line in open('data'): use line	|文件迭代器一行一行的读取           |
|open('f.txt', encoding='latin-1')	                |                   |
|open('f.bin', 'rb')		||




# 2.打开文件的模式
open(file_name, 处理模式）

* r 表示输入打开文件（默认）
* w 表示输出打开文件
* a 表示在文件尾部追加内容而打开文件
* ｂ表示进行二进制数据处理


# 3.文件写入
```
>> myfile = open('myfile.txt', 'w')
>> myfile.write('hello text fle\n')                 #写入方法不会为我们添加终止符，我们必须手动添加行终止符（否则，下次写入时会简单地延长文件的当前行）
16                                                            #表示写入了16个字符
>> myfile.write('goodbye text fle\n')
18
>> myfile.close()

```


# 4.文件读取
```
>> myfile = open('myfile.txt')            #默认的模式r，读取
>>myfile.readline()
'hello text fle\n'
>>myfile.readline()
'goodbye text fle\n'
>>myfile.readline()
''                                                #已经到了文件的末尾


#另一种读取方式
>>for line in open(myfile):                        #open临时创建的文件对象将自动在每次循环迭代的时候读入并返回一行
..... print(line,end='')
```

# 5.在文件中存储并解析python对象
```
#步骤1、创建对象
>>x, y, z = 43, 44, 45
>> s ='spam'
>>D = {'a':1,'b':2}
>>L = [1,2,3]

#步骤2：将创建的对象写入文件
>>F = open('datafile.txt', 'w')
>>F.write(s+ '\n')
>>F.write('%s,%s,%s\n' % (x,y,z))
>>F.write(str(L) + '$' + str(D) + '\n')
>>F.close()

#步骤3：提取文件字符，并转成对象（还原）
>>F = open('datafile.txt')
>>line = F.readline()
>>line
'spam\n'
>>line.rstrip()
'spam'                                        #还原字符串

>>line = F.readline()
>>line
'43,44,45\n'
>>parts = line.split(',')
>>parts
['43', '44', '45\n']
>>numbers = [int(p) for p in parts]
[43, 44, 45]                              #还原int列表


>>line = F.readline()
>>line
“[1,2,3]${'a':1, 'b':2}\n”
>>parts = line.split('$')
>>parts
['[1,2,3]' , "{'a':1, 'b':2}"]
>>objects = [eval(p) for p in parts]                     #eval能够将字符串当做可执行程序代码
[[1,2,3], {'a':1, 'b':2}]

```

# 6.使用pickle存取python的原生对象
```
>>D = {'a':1, 'b':2}
>>F = open('datafile.pk1', 'wb')
>>import pickle
>>pickle.dump(D,F)        #将D写入到文件对象F
>>F.close()


>>F = open('datafile.pk1', 'rb')
>>E = pickle.load(F)
>>E
{'a':1, 'b':2}

```

# 7.打包二进制数据的存储与解析
struct模块能够构造并解析打包的二进制数据，从某种意义上来说，他是另一个数据转换工具，他能够把文件中的字符串解读为二进制数
```
>>F = open('data.bin', 'wb')
>>import struct
>>data = struct.pack('>i4sh', 7, 'spam', 8)                  #格式化的字符串是：一个4字节的整数，一个包含4个字符的字符串，一个2为整数
>>data
b'\xoo\xoo\xoo\xo7spam\xoo\xo8'
>>F.write(data)
>>F.close()


#读取
>>F = open('data.bin', 'rb')
>>data = F.read()
>>data
b'\xoo\xoo\xoo\xo7spam\xoo\xo8'
>>values = struct.uppack('>i4sh', data)                    #还原数据
>>values
(7,'spam', 8 )

```



# 8.其他文件工具
* 标准流
在sys模块中预先打开的文件对象，例如：sys.stdout
* os 模块中的描述文件
* socket 、pipes 和FIFO文件
* 通过键来存取的文件
* shell命令流





