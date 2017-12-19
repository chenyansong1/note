网页刷新：command+r 
打开一个新的标签页：command+t

command VirtualBox 更改主机和虚拟机的切换


剪切：
首先选中文件，按Command+C复制文件；
然后按Command＋Option＋V；就可以把你的文件剪走了！


使用「终端」应用程序对截图进行自定义

关闭「预览」程序里的「复原修改」功能

```
defaults write com.apple.Preview NSQuitAlwaysKeepsWindows -bool FALSE
```

改变截图文件后缀格式

```
defaults write com.apple.screencapture type [type] && killall SystemUIServer
```

将 type 替换成你要的图片格式即可，注意：格式只能用三位字母代表，比如 png、jpg 等。


改变截图保存位置

```
defaults write com.apple.screencapture location [path] && killall SystemUIServer
```

使用此命令可修改截图默认的保存目录（默认是桌面），比如将 path 修改为：/Users/dann/Documents/Screenshots，这样截图就能自动保存到文稿目录下的 Screenshots 文件夹内，如果你的网络云盘同步速度不错，也可以将自动保存目录设定为 Dropbox 的同步目录。

如果你懒得输入长长的目录路径，也可以将打开 Finder 里的目录窗口直接拖入「终端」，即可自动生成目录路径。

去除窗口截图四周的阴影

这是许多朋友都在问我的问题，使用以下命令可以立即去除：
```
defaults write com.apple.screencapture disable-shadow -bool true && killall SystemUIServer
```
还原成带阴影的窗口截图执行以下命令：
```
defaults write com.apple.screencapture disable-shadow -bool false && killall SystemUIServer
```
改变截图默认文件名命名规则
```
defaults write com.apple.screencapture name [file name] && killall SystemUIServer
```
如果你不喜欢「屏幕快照 + 时间」的截图命名方式，你可以自行定义文件名，将 file name 替换即可。


# Command快捷键

Command是Mac里最重要的修饰键，在大多数情况下相当于Windows下的Ctrl。所以以下最基本操作很好理解： 

* Command-Z 撤销　
* Command-X 剪切　　
* Command-C 拷贝（Copy）　　
* Command-V 粘贴　　
* Command-A 全选（All）　　
* Command-S 保存（Save)　　
* Command-F 查找（Find),查找下一个使用**回车键**，如果想要查询上一个，使用**shift+回撤键**


# Command-Shift截图

* Command-Shift-4 截取所选屏幕区域到一个文件　　
* Command-Shift-3 截取全部屏幕到文件　　
* Command-Shift-Control-3 截取全部屏幕到剪贴板　　
* Command-Shift-4 截取所选屏幕区域到一个文件，或按空格键仅捕捉一个窗口　　
* Command-Shift-Control-4 截取所选屏幕区域到剪贴板，或按空格键仅捕捉一个窗

修改截图的位置：

```
1.打开终端
2.defaults write com.apple.screencapture location ~/Documents/Screenshots
3.killall SystemUIServer

其中 ~/Documents/Screenshots就是修改后的截图保存位置
```


# 在Finder中

* Command-Option-V 剪切文件　　
* Command-Shift-N 新建文件夹（New）　　
* Command-Shift-G 调出窗口，可输入绝对路径直达文件夹（Go）　　
* return (选中文件或者文件夹之后，可以对这个文件或者文件夹进行重命名)这个其实不算快捷键，点击文件，按下可重命名文件　　
* Command-O 打开所选项。在Mac里打开文件不像Windows里直接按Enter　　
* Command-Option-V 作用相当于Windows里的文件剪切。在其它位置上对文件复制（Command-C），在目的位置按下这个快捷键，文件将被剪切到此位置　　
* Command-上箭头 打开包含当前文件夹的文件夹，相当于Windows里的“向上”,返回父层级的目录
* Command-Delete 将文件移至废纸篓　　
* Command-Shift-Delete 清倒废纸篓　　
* 空格键 快速查看选中的文件，也就是预览功能


# 在浏览器中
　
* Command-L 光标直接跳至地址栏　　
* Control-Tab 转向下一个标签页　　
* Control-Shift-Tab 转向上一个标签页　　
* Command-加号或等号 放大页面　　
* Command-减号 缩小页面


# 在应用程序中

* Command-Option-esc 打开强制退出窗口　　
* Command-H 隐藏（Hide）当前正在运行的应用程序窗口　　
* Command-Option-H 隐藏（Hide）其他应用程序窗口　　
* Command-Q 退出（Quit）最前面的应用程序　　
* Command-Shift-Z 重做，也就是撤销的逆向操作　　
* Command-Tab 在打开的应用程序列表中转到下一个最近使用的应用程序，相当于Windows中（Alt+Tab）　　
* Command-Option-esc 打开“强制退出”窗口，如果有应用程序无响应，可在窗口列表中选择强制退出
* control+上箭头 显示所有打开的应用程序窗口


# 文本处理
* Command-右箭头 将光标移至当前行的行尾　
* 在**终端**中，需要快速的移动光标，那么可以使用：**option+箭头**　
* Command-B 切换所选文字粗体（Bold）显示　　
* fn-Delete 相当于PC全尺寸键盘上的Delete，也就是向后删除
* Command-Delete 向前删除到行首　
* fn-上箭头 向上滚动一页（Page Up）　　
* fn-下箭头 向下滚动一页（Page Down）　　
* fn-左箭头 滚动至文稿开头（Home）　　
* fn-右箭头 滚动至文稿末尾（End）　　
* Command-右箭头 将光标移至当前行的行尾　　
* Command-左箭头 将光标移至当前行的行首　　
* Command-下箭头 将光标移至文稿末尾　　
* Command-上箭头 将光标移至文稿开头　　
* Option-右箭头 将光标移至下一个单词的末尾　　
* Option-左箭头 将光标移至上一个单词的开头　　
* Control-A 移至行或段落的开头


* Command-X	剪切所选项并拷贝到剪贴板。
* Command-C	将所选项拷贝到剪贴板。这同样适用于 Finder 中的文件。
* Command-V	将剪贴板的内容粘贴到当前文稿或应用中。这同样适用于 Finder 中的文件。
* Command-Z	撤销前一个命令。随后您可以按 Command-Shift-Z 来重做，从而反向执行撤销命令。在某些应用中，您可以撤销和重做多个命令。
* Command-A	全选各项。
* Command-F	查找文稿中的项目或打开“查找”窗口。
* Command-G	再次查找：查找之前所找到项目出现的下一个位置。要查找出现的上一个位置，请按 
* dCommand-Shift-G。（但是正真有效的快捷键时回车键（向下），shift+回车键是查找上一个）
* Command-H	隐藏最前面的应用的窗口。要查看最前面的应用但隐藏所有其他应用，请按 Command-Option-H。
* Command-M	将最前面的窗口最小化至 Dock。要最小化最前面的应用的所有窗口，请按 Command-Option-M。
* Command-N	新建：打开一个新文稿或新窗口。
* Command-O	打开所选项，或打开一个对话框以选择要打开的文件。
* Command-P	打印当前文稿。
* Command-S	存储当前文稿。
* Command-W	关闭最前面的窗口。要关闭应用的所有窗口，请按 Command-Option-W。
* Command-Q	退出应用。
* Option-Command-Esc	强制退出：选择要强制退出的应用。或者，按住 Command-Shift-Option-Esc 3 秒钟来仅强制最前面的应用退出。
* Command–空格键	Spotlight：显示或隐藏 Spotlight 搜索栏。要从 Finder 窗口执行 Spotlight 搜索，请按 Command–Option–空格键。如果您使用多个输入源以便用不同的语言键入内容，这些快捷键会更改输入源而非显示 Spotlight。
* 空格键	快速查看：使用快速查看来预览所选项。
* Command-Tab	切换应用：在打开的应用中切换到下一个最近使用的应用。
* Shift-Command-波浪号 (~)	切换窗口：切换到最前端应用中下一个最近使用的窗口。
* Shift-Command-3	屏幕快照：拍摄整个屏幕的屏幕快照。了解更多屏幕快照快捷键。
* Command-逗号 (,)	偏好设置：打开最前面的应用的偏好设置。

# 文稿快捷键（来自官网）

Command-B	以粗体显示所选文本，或者打开或关闭粗体显示功能。 
Command-I	以斜体显示所选文本，或者打开或关闭斜体显示功能。
Command-U	对所选文本加下划线，或者打开或关闭加下划线功能。
Command-T	显示或隐藏“字体”窗口.

Command-D	从“打开”对话框或“存储”对话框中选择“桌面”文件夹。
Control-Command-D	显示或隐藏所选字词的定义。
Shift-Command-冒号 (:)	显示“拼写和语法”窗口。
Command-分号 (;)	查找文稿中拼写错误的字词。
Option-Delete	删除插入点左边的字词。
Control-H	删除插入点左边的字符。也可以使用 Delete 键。
Control-D	删除插入点右边的字符。也可以使用 Fn-Delete。
Fn-Delete	在没有向前删除   键的键盘上向前删除。也可以使用 Control-D。
Control-K	删除插入点与行或段落末尾处之间的文本。
Command-Delete	在包含“删除”或“不存储”按钮的对话框中选择“删除”或“不存储”。
Fn–上箭头	向上翻页：向上滚动一页。 
Fn–下箭头	向下翻页：向下滚动一页。
Fn–左箭头	开头：滚动到文稿开头。
Fn–右箭头	结尾：滚动到文稿末尾。
Command–上箭头	将插入点移至文稿开头。
Command–下箭头	将插入点移至文稿末尾。
Command–左箭头	将插入点移至当前行的行首。
Command–右箭头	将插入点移至当前行的行尾。
Option–左箭头	将插入点移至上一字词的词首。
Option–右箭头	将插入点移至下一字词的词尾。
Shift–Command–上箭头	选中插入点与文稿开头之间的文本。
Shift–Command–下箭头	选中插入点与文稿末尾之间的文本。
Shift–Command–左箭头	选中插入点与当前行行首之间的文本。
Shift–Command–右箭头	选中插入点与当前行行尾之间的文本。
Shift–上箭头	将文本选择范围扩展到上一行相同水平位置的最近字符处。
Shift–下箭头	将文本选择范围扩展到下一行相同水平位置的最近字符处。
Shift–左箭头	将文本选择范围向左扩展一个字符。
Shift–右箭头	将文本选择范围向右扩展一个字符。
Option–Shift–上箭头	将文本选择范围扩展到当前段落的段首，再按一次则扩展到下一段落的段首。
Option–Shift–下箭头	将文本选择范围扩展到当前段落的段尾，再按一次则扩展到下一段落的段尾。
Option–Shift–左箭头	将文本选择范围扩展到当前字词的词首，再按一次则扩展到后一字词的词首。
Option–Shift–右箭头	将文本选择范围扩展到当前字词的词尾，再按一次则扩展到后一字词的词尾。
Control-A	移至行或段落的开头。
Control-E	移至行或段落的末尾。
Control-F	向前移动一个字符。
Control-B	向后移动一个字符。
Control-L	将光标或所选内容置于可见区域中央。
Control-P	上移一行。
Control-N	下移一行。
Control-O	在插入点后插入一行。
Control-T	将插入点后面的字符与插入点前面的字符交换。
Command–左花括号 ({)	左对齐。
Command–右花括号 (})	右对齐。
Shift–Command–竖线 (|)	居中对齐。
Option-Command-F	前往搜索栏。 
Option-Command-T	显示或隐藏应用中的工具栏。
Option-Command-C	拷贝样式：将所选项的格式设置拷贝到剪贴板。
Option-Command-V	粘贴样式：将拷贝的样式应用到所选项。
Option-Shift-Command-V	粘贴并匹配样式：将周围内容的样式应用到粘贴在这个内容中的项目。
Option-Command-I	显示或隐藏检查器窗口。
Shift-Command-P	页面设置：显示用于选择文稿设置的窗口。
Shift-Command-S	显示“存储为”对话框或复制当前文稿。
Shift–Command–
减号 (-)    	缩小所选项。
Shift–Command–
加号 (+)	放大所选项。Command–等号 (=) 可执行相同的功能。
Shift–Command–
问号 (?)	打开“帮助”菜单。


# Finder 快捷键(来自官网)

Command-D	复制所选文件。
Command-E	推出所选磁盘或宗卷。
Command-F	在 Finder 窗口中开始 Spotlight 搜索。
Command-I	显示所选文件的“显示简介”窗口。
Shift-Command-C	打开“电脑”窗口。
Shift-Command-D	打开“桌面”文件夹。
Shift-Command-F	打开“我的所有文件”窗口。
Shift-Command-G	打开“前往文件夹”窗口。
Shift-Command-H	打开当前 macOS 用户帐户的个人文件夹。
Shift-Command-I	打开 iCloud Drive。
Shift-Command-K	打开“网络”窗口。
Option-Command-L	打开“下载”文件夹。
Shift-Command-O	打开“文稿”文件夹。
Shift-Command-R	打开“AirDrop”窗口。
Shift-Command-T	将所选的 Finder 项目添加到 Dock（OS X Mountain Lion 或更低版本）
Control-Shift-Command-T	将所选的 Finder 项目添加到 Dock（OS X Mavericks 或更高版本）
Shift-Command-U	打开“实用工具”文件夹。
Option-Command-D	显示或隐藏 Dock。即使您未在 Finder 中，这个快捷键通常也有效。
Control-Command-T	将所选项添加到边栏（OS X Mavericks 或更高版本）。
Option-Command-P	隐藏或显示 Finder 窗口中的路径栏。
Option-Command-S	隐藏或显示 Finder 窗口中的边栏。
Command–斜线 (/)	隐藏或显示 Finder 窗口中的状态栏。
Command-J	显示“显示”选项。
Command-K	打开“连接服务器”窗口。
Command-L	为所选项制作替身。
Command-N	打开一个新的 Finder 窗口。
Shift-Command-N	新建文件夹。
Option-Command-N	新建智能文件夹。
Command-R	显示所选替身的原始文件。
Command-T	在当前 Finder 窗口中有单个标签页开着的状态下显示或隐藏标签页栏。
Shift-Command-T	显示或隐藏 Finder 标签页。
Option-Command-T	在当前 Finder 窗口中有单个标签页开着的状态下显示或隐藏工具栏。
Option-Command-V	移动：将剪贴板中的文件从原始位置移动到当前位置。
Option-Command-Y	显示所选文件的快速查看幻灯片显示。
Command-Y	使用“快速查看”预览所选文件。
Command-1	以图标方式显示 Finder 窗口中的项目。
Command-2	以列表方式显示 Finder 窗口中的项目。
Command-3	以分栏方式显示 Finder 窗口中的项目。 
Command-4	以 Cover Flow 方式显示 Finder 窗口中的项目。
Command–左中括号 ([)	前往上一文件夹。
Command–右中括号 (])	前往下一文件夹。
Command–上箭头	打开包含当前文件夹的文件夹。
Command–Control–上箭头	在新窗口中打开包含当前文件夹的文件夹。
Command–下箭头	打开所选项。
Command–Mission Control	显示桌面。即使您未在 Finder 中，这个快捷键也有效。
Command–调高亮度	开启或关闭目标显示器模式。
Command–调低亮度	当 Mac 连接到多个显示器时打开或关闭显示器镜像功能。
右箭头	打开所选文件夹。这个快捷键仅在列表视图中有效。
左箭头	关闭所选文件夹。这个快捷键仅在列表视图中有效。
Option-连按	在单独的窗口中打开文件夹，并关闭当前窗口。
Command-连按	在单独的标签页或窗口中打开文件夹。
Command-Delete	将所选项移到废纸篓。
Shift-Command-Delete	清倒废纸篓。
Option-Shift-Command-Delete	清倒废纸篓而不显示确认对话框。
Command-Y	使用“快速查看”预览文件。
Option–调高亮度	打开“显示器”偏好设置。这个快捷键可与任一亮度键搭配使用。
Option–Mission Control	打开“Mission Control”偏好设置。
Option–调高音量	打开“声音”偏好设置。这个快捷键可与任一音量键搭配使用。
拖移时按 Command 键	将拖移的项目移到其他宗卷或位置。拖移项目时指针会随之变化。
拖移时按住 Option 键	拷贝拖移的项目。拖移项目时指针会随之变化。
拖移时按住 Option-Command	为拖移的项目制作替身。拖移项目时指针会随之变化。
Option-点按开合三角形	打开所选文件夹内的所有文件夹。这个快捷键仅在列表视图中有效。
Command-点按窗口标题	查看包含当前文件夹的文件夹。
