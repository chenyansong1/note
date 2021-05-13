

Pycharm无法debug（分步调试），无法命中断点直接运行到结束的几种原因的总结！

情况一.可能是因为File -> Settings ->Build, Execution, Deployment -> Python Debugger 中选项PyQt compatible 选择了Auto，去掉勾就好了。

情况二、实际上你不小心选中了Mute Breakpoints,使得断点不起作用了。若要断点起作用只要取消选中即可。如下图所示：

情况三、如果上面两个情况都没有用的话建议你删除idea文件，重新启动pycharm.希望我的总结对您有用！（实验有用）



参考：https://blog.csdn.net/weixin_44313986/article/details/106330287