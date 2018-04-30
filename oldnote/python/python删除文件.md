python删除文件


```
def delDir(dir,t=604800):
    #获取文件夹下所有文件和文件夹
    files = os.listdir(dir)
    for file in files:
        filePath = dir + "/" + file
        #判断是否是文件
        if os.path.isfile(filePath):
            #最后一次修改的时间
            last = int(os.stat(filePath).st_mtime)
            #当前时间
            now = int(time.time())
            #删除过期文件
            if (now - last >= t):
                os.remove(filePath)
                #print "##now=" + now + ", ##last=" + last
                print "##now="+str(now)+", ##last="+str(last)
                print(filePath + " was removed!")
        elif os.path.isdir(filePath):
            #如果是文件夹，继续遍历删除
            delDir(filePath,t)
            #如果是空文件夹，删除空文件夹
            if not os.listdir(filePath):
                os.rmdir(filePath)
                print(filePath + " was removed!")
```

