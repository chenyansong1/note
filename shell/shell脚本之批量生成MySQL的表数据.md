
shell之批量生成MySQL的表数据

```
#!/bin/bash

echo "start insert into data ..............."
start_time=`date '+%Y-%m-%d %H:%M:%S'`;

MAX_INSERT_ROW_COUNT=$1;
for i in $( seq 1 $MAX_INSERT_ROW_COUNT )
do
        if [ $((i%3)) -eq 0 ];then
                birth=`date '+%Y-%m-%d %H:%M:%S'` 
                mysql -uroot -proot test -e "insert into test_for_sqoop values($i,'zhangsasn_$i',$i,'$birth','school_$i','birth_addr_$i','xingzuo_$i','xuexing_$i')"
        fi

        if [ $((i%3)) -eq 1 ];then
                mysql -uroot -proot test -e "insert into test_for_sqoop(id,name,age) values($i,'zhangsasn_$i',$i)"
        fi

        if [ $((i%3)) -eq 2 ];then
                mysql -uroot -proot test -e "insert into test_for_sqoop(id,name) values($i,'zhangsasn_$i')"
        fi

        sleep 0.05
done

echo "end insert into data ..............."
end_time=`date '+%Y-%m-%d %H:%M:%S'`;

diff=$(($(date +%s -d "${end_time}") - $(date +%s -d "${start_time}"))); 


echo "use time mins "$((diff/60))

exit 0

```