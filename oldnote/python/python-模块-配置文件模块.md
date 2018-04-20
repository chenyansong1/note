配置文件模块

test.conf

```
[a]
a_key1 = 20
a_key2 = 10
[b]
b_key1 = 121
b_key2 = b_value2
b_key3 = $r
b_key4 = 127.0.0.1

```

读取指定字段

```
cf = ConfigParser.ConfigParser()
cf.read("test.conf")  # 读取配置文件内容
secs = cf.sections()    # 对内容进行划分，得到所有的章节名
print 'sections:', secs     # 以列表形式打印章节名
opts = cf.options('a')
print 'options:', opts  # 以列表形式打印a章节里面的Key
kvs = cf.items('a')
print 'sec_a:', kvs     # 以列表形式打印a章节的(key, value)
str_val = cf.get('a', 'a_key1')     # 返回a章节里面key为a_key1的值，返回为string类型
int_val = cf.getint('a', 'a_key2')  # 返回_a章节里面key为a_key2的值，返回为int类型
print "value for a's a_key1:", str_val
print "value for a's a_key2:", int_val
cf.set("b", "b_key3", "new-$r")     # 章节a里面添加一个key为b_key3，值为new-$r，如果key存在就更新key的值
cf.set("b", "b_newkey", "new-value")    # 章节b里面添加一个key为b_newkey，值为new-value，key存在就更新key的值
cf.add_section('a_new_section')     # 新建一个章节a_new_section
cf.set('a_new_section', 'new_key', 'new_value')     # 章节a_new_section里面新建一个key为new_key，值为new_value
cf.write(open("test.conf", "w"))    # 把修改写入到文件test.conf中


```

config_file

```
[es]
es_url=http://172.16.14.38:9200

```

读取配置文件

```
    #读取ES的url配置
    cf = ConfigParser.ConfigParser()
    cf.read(config_file)
    es_url = cf.get('es', 'es_url')
```

https://www.jianshu.com/p/4202a2051668