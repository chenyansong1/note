---
title: shell之字符串操作
categories: shell   
toc: true  
tags: [shell]
---



[TOC]



# 1.判断读取字符串值

```
${var}	    #变量var的值, 与$var相同 
${var-DEFAULT}	    #如果var没有被声明, 那么就以$DEFAULT作为其值 * 
${var:-DEFAULT}    #如果var没有被声明, 或者其值为空, 那么就以$DEFAULT作为其值 * 
${var=DEFAULT}    #如果var没有被声明, 那么就以$DEFAULT作为其值 * 
${var:=DEFAULT}    #如果var没有被声明, 或者其值为空, 那么就以$DEFAULT作为其值,并且将值赋值给var变量 * 
${var+OTHER}    #如果var声明了, 那么其值就是$OTHER, 否则就为null字符串 
${var:+OTHER}    #如果var被设置了, 那么其值就是$OTHER, 否则就为null字符串 
${var?ERR_MSG}    #如果var没被声明, 那么就打印$ERR_MSG * 
${var:?ERR_MSG}    #如果var没被设置, 那么就打印$ERR_MSG * 
${!varprefix*}    #匹配之前所有以varprefix开头进行声明的变量 
${!varprefix@}    #匹配之前所有以varprefix开头进行声明的变量 
```

# 2.字符串操作（长度，读取，替换）

```
${    #string}    #$string的长度 
${string:position}    # 在$ string中,  从位置$ position开始提取子串 
${string:position:length}    
${string#substring} 	# 从变量$string的开头, 删除最短匹配$substring的子串 
${string##substring}    #从变量$string的开头, 删除最长匹配$substring的子串 
${string%substring}    #从变量$string的结尾, 删除最短匹配$substring的子串 
${string%%substring}    #从变量$string的结尾, 删除最长匹配$substring的子串 
${string/substring/replacement}    #使用$replacement, 来代替第一个匹配的$substring 
${string//substring/replacement}    #使用$replacement, 代替所有匹配的$substring 
${string/    #substring/replacement}    #如果$string的前缀匹配$substring, 那么就用$replacement来代替匹配到的$substring 
${string/%substring/replacement}    #如果$string的后缀匹配$substring, 那么就用$replacement来代替匹配到的$substring 

```

* 路径截取后面

```
chenyansongdeMacBook-Pro:shell chenyansong$ echo $file
/usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file#/}
usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file##/}
usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file##*/}
src
chenyansongdeMacBook-Pro:shell chenyansong$ 

```



* 路径截取前面

```
chenyansongdeMacBook-Pro:shell chenyansong$ echo $file
/usr/local/src
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file%/*}
/usr/local
chenyansongdeMacBook-Pro:shell chenyansong$ echo ${file%%/*}

chenyansongdeMacBook-Pro:shell chenyansong$ 

```



* 字符串转数组

  ```shell
  [root@host ~]# str="ONE,TWO,THREE,FOUR"
  [root@host ~]# arr=(`echo $str | tr ',' ' '`) 
  [root@host ~]# echo ${arr[@]}
  ONE TWO THREE FOUR
  
  #循环遍历
  for service_name in "${arr[@]}";do
      #echo $service_name"############"
      systemctl enable $service_name
  done
  ```

  

* 字符串分割为字符数组，然后遍历

```shell
% awk -F\| '{
  for (i = 0; ++i <= NF;)
    print i, $i
  }' <<<'12|23|11'
1 12
2 23
3 11

#Or, using split:

% awk '{
  n = split($0, t, "|")
  for (i = 0; ++i <= n;)
    print i, t[i]
  }' <<<'12|23|11'
1 12
2 23
3 11
```



或者使用shell的方式分割

```shell
shell编程中，经常需要将由特定分割符分割的字符串分割成数组，多数情况下我们首先会想到使用awk
但是实际上用shell自带的分割数组功能会更方便。假如
a="one,two,three,four"
要将$a分割开，可以这样：


OLD_IFS="$IFS" 
IFS="," 
arr=($a) 
IFS="$OLD_IFS" 
for s in ${arr[@]} 
do 
    echo "$s" 
done


or 下标访问
		component_name_list=“kafka#jdk#zookeeper”
		OLD_IFS="$IFS" 
		IFS="#" 
		arr=($component_name_list) 
		IFS="$OLD_IFS" 
		
		
		for((i=0;i<${#arr[*]};i++));do
			case ${arr[i]} in
			"jdk")
				echo "$i jdk installing...";;
				scp $component_dir/
			"scala")
				echo "scala installing...";;
			*)
				echo "$component_name[i] is unknown component"
			esac
		done


```



* 字符串正则提取

```shell
[root@node-test ~]# echo here365test | sed 's/.*ere\([0-9]*\).*/\1/g'
365
[root@node-test ~]# echo zookeeper1 | sed 's/zookeeper\([0-9]*\)/\1/g'
1
[root@node-test ~]# 
```



* 字符串转数组

  ```shell
  index_array=`curl es:9200/_cat/indices|grep -v close|grep open|uniq |sort -n|awk -F " " '{print $3}'`
  index_array=($index_array)
  ```

  