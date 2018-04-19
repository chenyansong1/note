1.对于window的换行引起的问题，遇到时需要去掉文末的换行：


```
x='Hello what is up. ^M\
^M\
What are you doing?'

```

如何去掉

```
>>> mystring = mystring.replace("\r", "").replace("\n", "")
(where "mystring" contain your text)

```


参看：

https://stackoverflow.com/questions/11755208/how-to-remove-m-from-a-text-file-and-replace-it-with-the-next-line







