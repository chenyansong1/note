---
title: nginx健康检查
categories: nginx   
toc: true  
tags: [nginx,shell]
---



```
[root@lamp01 conf]# cat check_web.sh 
#####################################################################
# File Name: check_web.sh
# Author: chenyansong
# Description: 其实就是通过curl返回的状态,来判断Nginx是否良好
# Created Time: 2016-09-01 19:34:22
#####################################################################
#!/bin/bash

#要检查的Nginx节点
rs_arr=( 
    192.168.0.3
    192.168.0.4
    )

file_location=/var/html/test.html

#通过curl查看网站返回状态是否正常
function web_result {
    rs=`curl -I -s $1|awk 'NR==1{print $2}'`
    return $rs    #返回状态码
}

#拼接table
function new_row {
cat >> $file_location <<eof
   <tr>
    <td bgcolor="$4">$1</td>
    <td bgcolor="$4">$2</td>
    <td bgcolor="$4">$3</td>
   </tr>
eof
}

function auto_html {
    web_result $2
    rs=$?
    if [ $rs -eq 200 ]
    then
         new_row $1 $2 up green
    else
         new_row $1 $2 down red
    fi
}


main(){
while true
do
cat >> $file_location <<eof
 <h4>he Status Of RS :</h4>
 <meta http-equiv="refresh" content="1">
 <table border="1">
  <tr> 
  <th>NO:</th>
  <th>IP:</th>
  <th>Status:</th>
 </tr>
eof

for ((i=0;i<${#rs_arr[*]};i++)); do
    auto_html $i ${rs_arr[$i]}
done
cat >> $file_location <<eof
</table>
eof

sleep 2

> $file_location
done
}
main

```

