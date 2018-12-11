#-*- coding: UTF-8 -*-
import os,re 


g = os.walk("/home/test/bigdata/zookeeper/")  

for path,dir_list,file_list in g:  
    for dir_name in file_list:
            if(dir_name.endswith(".md")):
                try:
                        #读文件
                        fileRead = open(os.path.join(path, dir_name), 'r')
                        lines = fileRead.readlines()
                        fileRead.close()

                        #修改文件
                        f = open(os.path.join(path, dir_name), 'w')
                        for line in lines:
                                if line.startswith("xxxxxxxxx"):
                                #if line.startswith("!["):
                                        m = re.match(r'.+?\/(im.+png\))$', line)
                                        print(os.path.join(path, dir_name))
                                        print(line)
                                        
                                        insert ="![](https://github.com/chenyansong1/note/blob/master/"+m.group(1) 
                                        f.write(insert)
                                else:
                                        f.write(line)

                finally:
                        #关闭文件
                        if f:
                                f.close()
