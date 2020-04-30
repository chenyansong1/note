[TOC]

------

转自：https://www.jianshu.com/p/014ec71b0215


# 1、前记

Linux学习系列主要侧重数据处理的命令实战学习，包括但不限于`awk,grep,sed`等命令的实战学习。

# 2、文件分割（split）

## 2.1 命令语法



```css
split [--help][--version][-<行数>][-b <字节>][-C <字节>][-l <行数>][要切割的文件][输出文件名]
```

**参数解释**

-  **-a：**指定输出文件名的后缀长度，默认为2个(aa,ab...)；
-  **-d：**指定输出文件名的后缀用数字代替；
-  **-l<行数>：**行数分割模式，指定每多少行切成一个小文件；
-  **-b<字节>：**二进制分割模式，指定每多少字切成一个小文件，支持单位:m,k；
-  **-C<字节>：**文件大小分割模式，与-b参数类似，但切割时尽量维持每行的完整性；
-  **--help：**显示帮助；
-  **--version：**显示版本信息；
-  **[输出文件名]：**设置切割后文件的前置文件名，split会自动在前置文件名后再加上编号。

## 2.2 使用实例

**（1）查看文件总行数**



```css
wc -l seven.sql
```

输出：`3307194 seven.sql`，即约330万行，分割时以30万行为单位。

**（2）分割文件**



```swift
split -l 300000 seven.sql /home/kinson/Desktop/test1/seven_
--解释：
--参数"l"表示按行分割;
--"300000"表示每个文件30w行
--"seven.sql"为将分割文件;
--"/home/kinson/Desktop/test1/seven_"为分割后的文件路径与命名。
```

**（3）分割结果**




![img](https:////upload-images.jianshu.io/upload_images/3471485-2de7b4286e091a15.png?imageMogr2/auto-orient/strip|imageView2/2/w/502/format/webp)

分割结果



# 3、文件合并（cat）

> cat命令的用途是连接文件或标准输入并打印。这个命令常用来显示文件内容，或者将几个文件连接起来显示，或者从标准输入读取内容并显示，它常与重定向符号配合使用。

> **cat主要有如下三大功能：**

- 一次显示整个文件：cat filename；
- 从键盘创建一个文件：cat > filename 只能创建新文件,不能编辑已有文件；
- 将几个文件合并为一个文件：cat file1 file2 > file。

## 3.1 命令语法



```css
cat [-AbeEnstTuv] [--help] [--version] fileName 
```

**参数解释**

-  **-A：**--show-all，等价于 -vET；
-  **-b：** --number-nonblank，对非空输出行编号；
-  **-e：**等价于 -vE；
-  **-E：** --show-ends，在每行结束处显示 $；
-  **-n：**--number，对输出的所有行编号,由1开始对所有输出的行数编号；
-  **-s：**--squeeze-blank，有连续两行以上的空白行，就代换为一行的空白行；
-  **-t：**与 -vT 等价；
-  **-T：**--show-tabs，将跳格字符显示为 ^I；
-  **-v：**--show-nonprinting，使用 ^ 和 M- 引用，除了 LFD 和 TAB 之外。

## 3.2 使用实例

**（1）合并全部已分割文件并检验行数**



```undefined
cat test1/seven_* >newseven
wc -l newseven
```

输出：`3307194 newseven`，与原文件seven.sql行数一致。

**（2）合并部分已分割文件并检验行数**



```undefined
cat test1/seven_aa > seven_part
cat test1/seven_ab >> seven_part
cat test1/seven_ac >> seven_part
wc -l seven_part 
```

`>>`表示追加，输出`900000 seven_part`，即合并了3个文件，每个30万行，所以合并后文件总行数为90万行。