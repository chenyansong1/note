Linux之ELF文件初探

转自： https://www.cnblogs.com/TJTO/p/11470294.html

ELF：（Executable and Linkable Format)

可执行和可链接文件

在windows中可执行文件是pe文件格式，Linux中可执行文件是ELF文件，其文件格式是ELF文件格式，在Linux下的ELF文件除了可执行文件（*Excutable File*）,可重定位目标文件（*RellocatableObject File）、共享目标文件（*SharedObject File*）、核心转储文件（*CoreDump File）也都是ELF格式文件。

一个典型的ELF文件大致的结构如下

| 文件头（ELF Header）             |
| -------------------------------- |
| 程序头表（Program Header Table） |
| 代码段（.text）                  |
| 数据段(.data)                    |
| bss段(.bss)                      |
| 段表字符串表(.shstrtab)          |
| 段表(Section Header Table)       |
| 符号表(.symtab)                  |
| 字符串表(.strtab)                |
| 重定位表(.rel.text)              |
| 重定位表(.rel.data)              |









