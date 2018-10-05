

* 创建临时文件



mktemp /tmp/file.XX : 注意：XX一定是大写的



```

mktemp /tmp/file.XX

chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp file.XX
file.UX
chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp file.XX
file.rE
chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp file.XX
file.H4
chenyansongdeMacBook-Pro:test-d chenyansong$ 
chenyansongdeMacBook-Pro:test-d chenyansong$ ll
total 0
-rw-------  1 chenyansong  staff  0 10  2 09:36 file.H4
-rw-------  1 chenyansong  staff  0 10  2 09:35 file.UX
-rw-------  1 chenyansong  staff  0 10  2 09:35 file.rE
chenyansongdeMacBook-Pro:test-d chenyansong$ 
```



* 创建临时目录

mktemp /tmp/file.XX : 注意：XX一定是大写的



```
mktemp -d /tmp/file.XX

chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp -d file.XX
file.VR
chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp -d file.xx
file.xx
chenyansongdeMacBook-Pro:test-d chenyansong$ ll
total 0
-rw-------  1 chenyansong  staff   0 10  2 09:36 file.H4
-rw-------  1 chenyansong  staff   0 10  2 09:35 file.UX
drwx------  2 chenyansong  staff  68 10  2 09:38 file.VR
-rw-------  1 chenyansong  staff   0 10  2 09:35 file.rE
drwx------  2 chenyansong  staff  68 10  2 09:38 file.xx
chenyansongdeMacBook-Pro:test-d chenyansong$ mktemp -d file.xx
mktemp: mkdtemp failed on file.xx: File exists
chenyansongdeMacBook-Pro:test-d chenyansong$ 
```

