[TOC]

# typedef

使某一种类型创建自己的名字，如果你定义的是一种数据类型可以使用typedef,但是如果只是定义的常量，可以使用define

```c
typedef char BYTE;//定义了一个新的数据类型，名字叫BYTE，类型为char

//define只是在预编译的时候做语法替换，将BYTE1出现的地方替换为char
#define BYTE1 char
#define MAX 10

//error
//typedef 10 AAA;//10不是一种数据类型

struct abc
{
  int a;
  char b;
};

typedef struct abc A;


//简化写法
typedef struct abc 
{
  int a;
  char b;
} A2;



int main(void)
{
  BYTE a;
  a = 10;
  
  printf("%d\n", a);
  
  return 0;
}
```

typedef定义指向函数的指针

```c
char *mystrcat(char *s1, char *s2)
{
  strcat(s1, s2);
  return s1;
}

char *test(char *(*p)(char *, char *), char *s1, char *s2)
{
  return p(s1,s2);
}

//换一种写法
typedef char *(*STRCAT)(char *, char *);
char *test(STRCAT p, char *s1, char *s2)
{
  return p(s1,s2);
}


int main()
{
  char s1[100] = "hello";
  char s2[100] = "world";
  char *s = mystrcat(s1, s2);
  printf("s = %s\n", s);
  
  char *sp = test(mystrcat, s1, s2);
  
  return 0;
}

```

