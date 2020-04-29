# 判断JS数据类型

说到数据类型，我们先说一下JavaScript 中常见的几种数据类型：

基本类型：string,number,boolean

特殊类型：undefined,null

引用类型：Object,Function,Function,Array,RegExp,Date,...

很多时候我们都需要通过判断变量的数据类型来进行下一步操作，下面我们介绍常用的4种方法：

1、typeof

typeof 返回一个表示数据类型的字符串，返回结果包括：number、boolean、string、object、undefined、function等6种数据类型。

```
typeof ''; // string 有效
typeof 1; // number 有效
typeof true; //boolean 有效
typeof undefined; //undefined 有效
typeof null; //object 无效
typeof [] ; //object 无效
typeof new Function(); // function 有效
typeof new Date(); //object 无效
typeof new RegExp(); //object 无效

#例如我们要判断一个变量的类型，可以使用下面的方式进行：

```

其他参见： http://www.cnblogs.com/onepixel/p/5126046.html



```
typeof 111
"number"

typeof "111"
"string"		#字符串的数值也是string类型

typeof "true"
"string"		#字符串的布尔值也是string类型

typeof true
"boolean"

#判断一个变量是数字
if(typeof "true" == "string"){
	alert("number");
}

```

# 2.判断一个字符串是否为数字

下面给出了2种方式：

```
//判断是否是正整数
function IsNum(s)
{
    if(s!=null){
        var r,re;
        re = /\d*/i; //\d表示数字,*表示匹配多个数字
        r = s.match(re);
        return (r==s)?true:false;
    }
    return false;
}

//判断是否为数字
function IsNum(s)
{
    if (s!=null && s!="")
    {
        return !isNaN(s);
    }
    return false;
}
```



参考：
http://www.cnblogs.com/onepixel/p/5140944.html

http://www.cnblogs.com/onepixel/p/5126046.html

