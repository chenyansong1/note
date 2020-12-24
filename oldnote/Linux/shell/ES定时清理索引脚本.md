
````shell
#!/bin/bash

############delete 3 month before#######################
tables=(event syslog)
j=1
while [ "20180101" \< "`date +"%Y%m%d" -d "-2 month -${j} day"`" ]
do
  dd=`date +"%Y%m%d" -d "-2 month -${j} day"`
  for item in ${tables[*]}
  do
    index_cn=`curl -XHEAD -i http://es:9200/${item}_${dd}|grep 200|wc -l`
    [ $index_cn -eq 1 ] && curl -XDELETE es:9200/${item}_${dd}
    echo ${item}_${dd}
  done

  j=$((j+1))
done

exit 0
````

