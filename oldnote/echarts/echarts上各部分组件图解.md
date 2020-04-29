---
title: echarts上各部分组件图解
categories: echarts   
toc: true  
tag: [echarts]
---
下图标注了一个普通的echarts图表的各个组成部分,如下:
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/all.png)
在学习echarts之前,需要了解echarts的整体结构以及各个部分的组件有大概的了解,下面是对echarts的组件进行图解,说明各名词的含义

<!--more-->

# grid（绘图网格）

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/grid.jpg)


# axis（坐标轴）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/axisDetail.png)

# line（折线图）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/lineTheme.png)

# legend（图例）

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/legend.png)

# title（标题）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/title.png)


# toolbox（工具箱）

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/toolbox.png)


# tooltip（提示）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/tooltip2.jpg)

# dataZoom（数据区域缩放）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/dataZoom.png)

# dataRange（值域）
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/combiner/dataRange.png)




**名词解析**

* chart 	是指一个完整的图表，如折线图，饼图等“基本”图表类型或由基本图表组合而成的“混搭”图表，可能包括坐标轴、图例等
* axis 	直角坐标系中的一个坐标轴，坐标轴可分为类目型、数值型或时间型
* xAxis 	直角坐标系中的横轴，通常并默认为类目型
* yAxis 	直角坐标系中的纵轴，通常并默认为数值型
* grid 	直角坐标系中除坐标轴外的绘图网格，用于定义直角系整体布局
* legend 	图例，表述数据和图形的关联
* 
* dataRange 	值域选择，常用于展现地域数据时选择值域范围
* dataZoom 	数据区域缩放，常用于展现大量数据时选择可视范围
* roamController 	缩放漫游组件，搭配地图使用
* toolbox 	辅助工具箱，辅助功能，如添加标线，框选缩放等
* tooltip 	气泡提示框，常用于展现更详细的数据
* timeline 	时间轴，常用于展现同一系列数据在时间维度上的多份数据
* series 	数据系列，一个图表可能包含多个系列，每一个系列可能包含多个数据 
* line 	折线图，堆积折线图，区域图，堆积区域图。
* bar 	柱形图（纵向），堆积柱形图，条形图（横向），堆积条形图。
* scatter 	散点图，气泡图。散点图至少需要横纵两个数据，更高维度数据加入时可以映射为颜色或大小，当映射到大小时则为气泡图
* k 	K线图，蜡烛图。常用于展现股票交易数据。
* pie 	饼图，圆环图。饼图支持两种（半径、面积）南丁格尔玫瑰图模式
* radar 	雷达图，填充雷达图。高维度数据展现的常用图表。
* chord 	和弦图。常用于展现关系数据，外层为圆环图，可体现数据占比关系，内层为各个扇形间相互连接的弦，可体现关系数据
* force 	力导布局图。常用于展现复杂关系网络聚类布局。
* map 	地图。内置世界地图、中国及中国34个省市自治区地图数据、可通过标准GeoJson扩展地图类型。支持svg扩展类地图应用，如室内地图、运动场、物件构造等。
* gauge 	仪表盘。用于展现关键指标数据，常见于BI类系统。
* funnel 	漏斗图。用于展现数据经过筛选、过滤等流程处理后发生的数据变化，常见于BI类系统。
* evnetRiver 	事件河流图。常用于展示具有时间属性的多个事件，以及事件随时间的演化。
* funnel 	漏斗图。用于展现数据经过筛选、过滤等流程处理后发生的数据变化，常见于BI类系统。
* evnetRiver 	事件河流图。常用于展示具有时间属性的多个事件，以及事件随时间的演化。
* treemap 	矩形式树状结构图，简称：矩形树图。用于展示树形数据结构，优势是能最大限度展示节点的尺寸特征。
* venn 	韦恩图。用于展示集合以及它们的交集。




整理自:
[echarts2](http://echarts.baidu.com/echarts2/doc/example/themeDesigner.html#)
