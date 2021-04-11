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
  	for idx,value := range nums {//value是nums[0]的副本，value每次都是重新赋值nums[x]
  		// value = 111 ----> nums[0] = 1
  		fmt.Println("idx", idx, ",value:", value)
  	}
  
  }
  ```

  



* 不定长数组









