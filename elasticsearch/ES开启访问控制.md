ES的





两种方式：

1. 在ES的前面加上Nginx作为代理，通过在Nginx中添加白名单，这样达到访问控制的目的（此时需要将ES自身的9200端口对外隐藏，通过IPtables停掉）
2. 使用ES自带的search garud