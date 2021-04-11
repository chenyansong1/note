[toc]



# 变量

```go
package main

import "fmt"

func main() {
	//变量定义：var
	//常量定义：const

	//1.先定义变量，再赋值，var var_name var_type
	var name string
	name = "zhangsan"

	//2.定义时赋值
	var age int = 20
	var age2  = 20 //如果是已经赋值了，不用指定类型的，可以自动推导的，上面的int是灰色的

	fmt.Println(name)//goland会自动导入程序中使用的包
	fmt.Print(age2)
	fmt.Printf("name is :%s, age is %d\n", name, age)

	//3.定义直接赋值，使用自动推导（最常用的）
	address := "bejing"
	fmt.Println(address)

	//4.平行赋值
	i, j :=10,20
	fmt.Println(i, j)
	i,j = j,i  // 平行赋值

}
```



# 基础数据类型

```go
int 
int8
int16
int32
int64

uint8,...uint64

float32, float64

boolean (true/false)
```



# 自增语法

```go
//go语言只有：i++, i--两个，同时要求自增的语法必须单独一行


	index := 20
	index++
	fmt.Println(index)
	fmt.Println("index:", index++)//error,不允许和其他代码放在一起，必须单独起一行
```



# 指针

```go
package main

import "fmt"

func main() {

	//go语言的指针
	// 结构体成员调用： c语言：ptr->name; go : ptr.name
	/* go语言使用指针时，会使用内部的垃圾回收机制gc
		开发人员不需要手动释放内存
		c语言不允许返回栈上指针，但是go语言可以返回栈上指针，程序会在编译的时候就确定这个变量的分配位置
		编译的时候，如果发现有必须的话，就将变量分配到堆上，如果是临时使用，那么会被分配到栈上
	*/

	//1.
	name := "lily"
	ptr := &name

	fmt.Println("name:", *ptr) //lily
	fmt.Println("name ptr:", ptr) //地址 0xc000096220

	//2.使用new关键字定义指针
	namePtr := new(string)
	*namePtr = "wangwu"
	fmt.Println("name2:", *namePtr)


	//3.返回栈上指针
	res := testPtr()//编译器在编译程序时，会自动判断这段代码，将city变量分配在堆上（内存逃逸）
	//即使只有一行代码的代码块，也必须使用{}
	if res == nil {//空指针
		fmt.Println("res is nil")
	}
	fmt.Println("res city:", *res)



}

// 函数的嗯返回值写在参数列表的后面
func testPtr() *string{
	city := "shenzheng"
	//定义一个指针
	ptr := &city

	//将函数内部即栈上的指针返回
	return ptr
}


/*
空指针
c-language: null
c++: nullptr
go:nil

if res == nil {
	//...
}
*/
```





# go不支持的语法

```go
1. 自增，自减： --i, ++i
2. 不支持地址加减
3. 不支持三目运算
4. 只有false才能代表逻辑假，数值0和nil不能


	if 0 { // error
		
	}

	if nil { // error
		
	}

	if false { // ok
		
	}

```



# string

```go
package main

import "fmt"

func main()  {

	// 1.定义字符串
	name := "zhangsan"
	fmt.Println(name)

	// 2.print multi line
	// need to new line, use ` ,在c 中是使用 \
	usage := `./a.out <option>
				-h help
				-a xxx`
	fmt.Println(usage)


	//3. length of str
	// go 没有.length()函数，可以使用自由函数len()
	ll := len(name)
	fmt.Println("len is :", ll)

	for i:=0; i<len(name); i++ {
		fmt.Printf("i:%d,v:%c\n", i, name[i])

	}

	firstStr, secondStr := "hell", "world"
	fmt.Println("contract=", firstStr+secondStr)//hellworld

	//如果是const不用自动推导，并且const不能修改
	const address = "beijing lu"
	address = "shanghai"//error

}

```



# 数组

* 定长数组

  ```go
  package main
  
  import "fmt"
  
  func main()  {
  
  	//1. 定义数组
  	// c 的定义 int num[10] = {1,3,4}
  	// go的定义：nums := [10]int{1,2,3}  //常用的方式
  
  	nums := [10]int{1,23}
  
  	// 2.遍历
  	// 遍历方式1
  	for i:=0; i<len(nums); i++ {
  		nums[i] = i
  		fmt.Println(nums[i])
  	}
  
  	// 遍历方式2, for range ===> python 支持
  	for idx,value := range nums {//value是nums[0]的副本
  		// value = 111 ----> nums[0] = 1
  		fmt.Println("idx", idx, ",value:", value)
  	}
  
  	// go中如果想要忽略一个值，可以使用_
  	for _, value := range nums {
  		fmt.Println("value:", value)
  	}
  
  	for _, _ := range nums { //error 如果两个都忽略，那么就不能使用 :=
  
  	}
  	for _, _ = range nums {//ok
  
  	}
  
  }
  ```

  



* 不定长数组(切片， slice)

  slice，他的底层也是数组，可以动态改变长度

  ```go
  package main
  
  import (
  	"fmt"
  )
  
  func main()  {
  
  	// 定义一个切片，包含多个地名
  	//names := [10]string{"beijing", "shanghai", "guangzhou"}
  	names := []string{"beijing", "shanghai", "guangzhou"}
  
  	for i, v := range names {
  		fmt.Println("i:", i, "v:", v)
  	}
  	/*
  	i: 0 v: beijing
  	i: 1 v: shanghai
  	i: 2 v: guangzhou
  	*/
  
  
  	// 2.追加数据
  	name2 := append(names, "wuhan")
  
  	fmt.Println("names:", names)
  	fmt.Println("name2:", name2)
  	/*
  	names: [beijing shanghai guangzhou]
  	name2: [beijing shanghai guangzhou wuhan]
  	*/
  
  	// 3.对于一个slice，不仅有len(), 还有容量cap()
  	fmt.Println("befor append,len=", len(names), "cap:", cap(names))
  	names = append(names, "tianjin")
  	fmt.Println("after append,len=", len(names), "cap:", cap(names))
  	/*
  	befor append,len= 3 cap: 3
  	after append,len= 4 cap: 6  //如果cap不够，那么会一个分配原来的两倍的cap
  	 */
  	names = append(names, "tianjin2")
  	fmt.Println("after append,len=", len(names), "cap:", cap(names))
  	/*
  	befor append,len= 3 cap: 3
  	after append,len= 4 cap: 6
  	after append,len= 5 cap: 6  //此时cap还是6，如果超过6个，那么cap变成12，1k之后，比率不再是2，而是1.x
  	 */
  
  }
  
  ```

  

* 切片截取

  ```go 
  package main
  
  import "fmt"
  
  func main()  {
  
  	// 定义一个切片，包含多个地名
  	names := [5]string{"beijing", "shanghai", "guangzhou", "wuhan", "xian"}
  
  	// copy 数组的部分元素
  	name1 := [3]string{}
  	name1[0] = names[0]
  	name1[1] = names[1]
  	name1[2] = names[2]
  
  
  	// 切片可以基于一个数组，灵活的创建新的数组
  	name2 := names[0:3] // [0,3) 得到的是一个切片
  	fmt.Println("name2:", name2)
  	/*
  	name2: [beijing shanghai guangzhou]
  	*/
  
  	// modify name2 element
  	name2[0] = "hello"
  	fmt.Println("names:", names)
  	fmt.Println("name2", name2, "cap:", cap(name2))
  	/*
  	names: [hello shanghai guangzhou wuhan xian]
  	name2 [hello shanghai guangzhou] cap: 5
  	*/
  
  	// 如果从0元素开始
  	name3 := names[:3]
  	fmt.Println("name3:", name3)
  
  	// 如果截取到末尾
  	name4 := names[3:]
  	fmt.Println("name4:", name4)
  
  	// 如果是全部
  	name5 := names[:]
  	fmt.Println("name5:", name5)
  
  	// 也可以基于一个字符串进行切片截取，取字符串的子串
  	sub1 := "helloworld"[5:7]
  	fmt.Println("sub1:", sub1)//sub1: wo
  
  	// 可以在创建空切片的时候，明确指定切片的容量
  	str2 := make([]string, 10, 20) // len, cap
  	fmt.Println("str2:", len(str2), "cap:", cap(str2))//str2: 10 cap: 20
  
  	// 一般常用的方式,创建空切片，明确指定容量，这样可以提供运行效率
  	str3 := make([]string, 0, 20)//cap这个参数并不是必须的，如果没有，默认与length相同
  	fmt.Println(str3)
  
  
  
  	// 如果想要使切片完全独立于原始数组（深拷贝），可以使用copy()函数来完成
  	namesCopy := make([]string , len(names))
  	copy(namesCopy, names[:]) //copy的参数都是切片
  
  }
  ```

  

# 字典map

```go
package main

import "fmt"

func main()  {

	// 哈希表，key=>value, 存储的key是经过哈希运算的
	//1. 定义： (studId,studName)
	var stdMap map[int]string// 这里只是定义变量，并没有初始化他

	//2. 分配空间,可以不指定长度，建议指定
	stdMap = make(map[int]string, 10)
	stdMap[0] = "zhangsan"
	stdMap[1] = "lisi"

	for stdId, name := range stdMap {
		fmt.Println(stdId, name)
	}
	/*
	0 zhangsan
	1 lisi
	*/

	// 定义的时候，直接分配空间
	stdMap2 := make(map[int]string, 10)
	fmt.Println(stdMap2)


	// 如何确定一个key是否存在map中
	name9 := stdMap2[111]
	fmt.Println("name9:", name9)
	//name9:
	// 在map中，他认为所有的key都是有效的，他返回这个数字的零值
	// 零值：bool=false ， 数字=0； str=空
	// 无法通过获取value的值，来判断一个key是否存在，因此需要一个机制：能够校验一个key是否存在
	value, ok := stdMap2[222]
	if ok {//通过判断ok的返回值判断value是否存在
		fmt.Println(value)
	} else {
		fmt.Println("key not exist")
	}


	// del key of map
	delete(stdMap2, 1)//ok
	delete(stdMap2, 111)//ok

}

```



# 函数

```go
package main

import "fmt"

func main()  {
	
	v1, s1,_ := func222(10, 20, "hello")

	fmt.Println("v1:", v1, ",s1:", s1)
	
}

//1. 函数的返回值在参数列表之后，如果有多个返回值，需要使用 ()
func func222(a int, b int, c string) (int, string, bool) {
	return a+b, c,true
}

//2. 类型一样，可以一起定义
func test333(a, b int, c string) (res int, str string, b1 bool) {

	// 直接使用返回值的变量名字，参与运算
	res = a+b
	str = c
	b1 = true

	//3. 当返回值有名字的时候，可以直接写return，返回值就可以返回了
	return
	//equal return res, str, b1

}
```



内存逃逸（原本在栈上的变量，最终在堆上了）

```go

func funcPtr() *string {
	adName := "shengzhang"
	namePtr := &adName

	return namePtr
}


func main()  {

	v1, s1,_ := func222(10, 20, "hello")

	fmt.Println("v1:", v1, ",s1:", s1)

	p1 := funcPtr()
	fmt.Println(*p1)

}
```



# import

```go
package add

// 手写字母大写表示public，小写是protected，只有相同包名的文件才能使用(此时不需要使用包名作前缀)
func Add(a, b int) int {

	return a+b
}
```

```go
package sub

// 需要导出的函数，首字母要大写
func Sub(a, b int) int {

	return a-b
}
```

```go
package main

import (
	"11-import/add"
	"11-import/sub"
	"fmt"
)

func main() {

	res := sub.Sub(10, 5)
	res2 := add.Add(1,2)

	fmt.Println(res, res2)

}
```



![](../../images/go/image-20210411160210180.png)

> 同一个目录下不同的文件中不能出现 package xxx , package yyy 多个包名



