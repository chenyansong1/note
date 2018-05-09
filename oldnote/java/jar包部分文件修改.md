用jar工具修改

```

# 解出内部jar包
jar tf 外部jar包文件.jar                    # 列出文件清单
jar xf 外部jar包文件.jar 内部jar包.jar      # 解出jar包中需修改的指定文件

 

# 解出需修改文件
jar tf 内部jar包.jar                       # 列内部jar包的文件清单
jar xf 内部jar包.jar white_list.properties # 解出内部jar包的根路径的指定文件
jar xf 内部jar包.jar conf/hbase.conf       # 解出内部jar包子路径下的指定文件

 

# 编辑配置文件
vim white_list.properties                  # 编辑解出的配置文件
vim conf/hbase.conf                        # 编辑解出的子路径下的文件

 

# 更新配置文件到内部jar包
jar uf 内部jar包.jar white_list.properties # 更新配置文件到内部jar包
jar uf 内部jar包.jar conf/hbase.conf       # 更新子路径下的配置文件到内部jar包

 

# 更新内部jar包到外部jar包文件
jar uf 外部jar包文件.jar 内部jar包.jar     # 更新内部jar包到jar文件

 

# 删除临时文件
rm
-f white_list.properties
rm
-rf conf/hbase.conf
rm
-f 内部jar包.jar

```
