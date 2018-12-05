#-*- coding: UTF-8 -*-
import os 


g = os.walk("./")  

for path,dir_list,file_list in g:  
    for dir_name in file_list:
    	    if(dir_name.endswith(".md")):
            	print(os.path.join(path, dir_name) )
#print('中国')
