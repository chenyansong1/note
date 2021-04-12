[toc]

# 封装

go 语言支持类的操作，但是没有class关键字，使用struct来模拟类

```go
package main

import "fmt"

//Persion 类， 绑定方法：Eat， Run, Laugh 成员
// public , private

type Persion struct {
	name string
	age int
	gender string
	score float64
}

//在类的外面绑定方法
func (p Persion) Eat(){//p表示类的成员变量
	fmt.Println("Persion is eating")
	fmt.Println(p.name + " is  eating")
}

//指针会改变成员变量p的值
func (p *Persion) Eat2(){//p表示类的成员变量
	fmt.Println("Persion is eating")
	p.name = "zhangsan"
	fmt.Println(p.name + " is  eating")
}

func main() {
	lily := Persion{
		name:   "lily",
		age:    10,
		gender: "man",
		score:  90,
	}

	fmt.Println("before modity ===" , lily)

	lily.Eat()//Persion is eating
	fmt.Println("after eat modity ===" , lily)


	lily.Eat2()

	fmt.Println("after eat2 modity ===" , lily)

}

```

