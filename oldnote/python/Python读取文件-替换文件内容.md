```
#!/usr/local/bin/python
#coding:gbk
import re

old_file='/tmp/test'
fopen=open(old_file,'r')

w_str=""
for line in fopen:
        if re.search('hello',line):
                line=re.sub('hello','world',line)
                w_str+=line
        else:
                w_str+=line
print w_str
wopen=open(old_file,'w')
wopen.write(w_str)
fopen.close()
wopen.close()

```

