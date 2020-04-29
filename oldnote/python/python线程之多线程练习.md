---
title:  python线程之多线程练习
categories: python   
toc: true  
tags: [python]
---





# 练习
* 使用多线程统计每门课程的平均分
* 前提条件：学生成绩保存在TXT文件中：chinese.txt maths.txt
* 思路：
 1. 将文件中你的每门课程的成绩解析出来
 2. 统计每门课程分数总和及学生数量，并计算平均值

* 应用知识点：
 1. 文件读写
 2. 正则表达式得到学生成绩
 3. 将统计过程抽象成类
 4. 实现多线程统计
以下是多线程和单线程统计过程的区别：

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/thread_practice/1.png)

 
```

#创建分数文件
#!/usr/bin/python
 
import sys
import random
 
'''
#文件模板
open file
40 database
name_1:90
name_2:92
.....
'''
def construct_data(filename):
        f = open(filename,'w')
        for i in range(40):
                data = 'name_'+str(i)+':'+str(random.randint(50,100))+'\n'                    #格式为：name_i:99
                f.write(data)
        f.close()
 
 
def create_file(filenames):
        for filen in filenames:                                    #循环创建多个数据文件
                construct_data(filen)
 
 
if __name__=="__main__":
        print(sys.argv[1:])
        create_file(sys.argv[1:])                    #指定要创建哪些文件

-------------------------------------------------------------
#测试
[root@backup python]# python create_data.py chinese.txt math.txt
['chinese.txt', 'math.txt']
[root@backup python]# cat math.txt
name_0:65
name_1:70
name_2:95
name_3:54
name_4:72
name_5:67
name_6:74
name_7:93
name_8:98
。。。。。。。。。。。。

```


```

#下面的代码是统计每一个文件中的分数平均值
[root@backup python]# cat thread_count.py
#!/usr/bin/python
 
import threading
import re
import sys
 
 
class my_thread(threading.Thread):                                #继承Thread线程
        def __init__(self,filepath):
                threading.Thread.__init__(self)
                self.filepath = filepath
                self.result = 0
                self.sumscore = 0
 
        def run(self):
                f = open(self.filepath)
                iter_f = iter(f)
                num = 0
                for line in iter_f:
                        score = int(re.split(":",line)[1])                                        #用正则取出分数列
                        self.sumscore += score
                        num += 1
                f.close()
                self.result = self.sumscore / num                                    #求平均值
                print("file={0},sum={1},num={2},avg={3}".format(self.filepath,self.sumscore,num,self.result))                    #打印统计结果
 
def count_score(file_list):
        thread_list = []
        for filel in file_list:                                #对每个文件都创建一个线程，在线程中对该文件中的分数做统计
                p = my_thread(filel)
                p.start()
                thread_list.append(p)
 
        for p in thread_list:
                p.join()                                    #等待所有的线程结束
 
 
if __name__ == "__main__":
        print(sys.argv[1:])
        count_score(sys.argv[1:])                #调用统计分数的函数，传过去的是参数list


-----------------------------------------------------
#测试
[root@backup python]# python thread_count.py chinese.txt math.txt
['chinese.txt', 'math.txt']
file=chinese.txt,sum=2892,num=40,avg=72
file=math.txt,sum=2996,num=40,avg=74

```





