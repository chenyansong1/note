---
title: 直角坐标系之axis(xAsix/yAxis)
categories: echarts   
toc: true  
tag: [echarts]
---


下面是对axis(轴)的一些常用选项的介绍
<!--more-->

---

axis.type | string  :坐标轴类型
[ default: 'value' ]

可选：  
* 'value' 数值轴，适用于连续数据。
* 'category' 类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据。
* 'time' 时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同，例如会根据跨度的范围来决定使用月，星期，日还是小时范围的刻度。
* 'log' 对数轴。适用于对数数据。

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_type.png)

---


axis.position | string :x/y轴的位置
默认 grid 中的第一个 y 轴在 grid 的左侧（'left'），第二个 y 轴视第一个 y 轴的位置放在另一侧。

可选：
* 'left'
* 'right'

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_position.png)


---
axis.name | string  : 坐标轴名称。

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_name.png)

---


axis.nameLocation | string : 坐标轴名称显示位置
[ default: 'end' ]

可选：
* 'start'
* 'middle'
* 'end'

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_nameLocation.png)
---


axis.nameTextStyle | Object : 坐标轴名称的文字样式

nameTextStyle:{
	color:...,//坐标轴名称的颜色
	fontStyle:..,//坐标轴名称的文字字体风格
	//....
}



---


axis.nameGap | number : 坐标轴名称与轴线之间的距离
[ default: 15 ]

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_nameGap.png)



---



axis.boundaryGap | boolean, Array : 坐标轴两边留白策略，类目轴和非类目轴的设置和表现不一样

类目轴中 boundaryGap 可以配置为 true 和 false。默认为 true，这时候刻度只是作为分隔线，标签和数据点都会在两个刻度之间的带(band)中间。
非类目轴，包括时间，数值，对数轴，boundaryGap是一个两个值的数组，分别表示数据最小值和最大值的延伸范围，可以直接设置数值或者相对的百分比，在设置 min 和 max 后无效。 示例：

**类目轴**
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_boundaryGap.png)

对于boundaryGap，对于柱形图(类目轴)是比较有用的，柱形图和y轴之间留出空白，这样会比较的好看。

**非类目轴**
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_boundaryGap_2.png)


---

axis.min | number, string  : 坐标轴刻度最小值
[ default: 'auto' ]

可以设置成特殊值 'dataMin'，此时取数据在该轴上的最小值作为最小刻度。
不设置时会自动计算最小值保证坐标轴刻度的均匀分布。
在类目轴中，也可以设置为类目的序数（如类目轴 data: ['类A', '类B', '类C'] 中，序数 2 表示 '类C'。也可以设置为负数，如 -3）。


![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_min.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_min_2.png)


---

axis.max | number, string : [ default: 'auto' ]
坐标轴刻度最大值。
可以设置成特殊值 'dataMax'，此时取数据在该轴上的最大值作为最大刻度。
不设置时会自动计算最大值保证坐标轴刻度的均匀分布。
在类目轴中，也可以设置为类目的序数（如类目轴 data: ['类A', '类B', '类C'] 中，序数 2 表示 '类C'。也可以设置为负数，如 -3）。


---

axis.scale | boolean  
[ default: false ]

只在数值轴中（type: 'value'）有效。
是否是脱离 0 值比例。设置成 true 后坐标刻度不会强制包含零刻度。在双数值轴的散点图中比较有用。
在设置 min 和 max 之后该配置项无效。

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_scale.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_scale_2.png)


---

axis.splitNumber | number : 坐标轴的分割段数
[ default: 5 ]

需要注意的是这个分割段数只是个预估值，最后实际显示的段数会在这个基础上根据分割后坐标轴刻度显示的易读程度作调整。
在类目轴中无效。


![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_splitNumber.png)


---


aAxis.data[i] Object
类目数据，在类目轴（type: 'category'）中有效。
示例：
```
// 所有类目名称列表
data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
// 每一项也可以是具体的配置项，此时取配置项中的 `value` 为类目名
data: [{
    value: '周一',
    // 突出周一
    textStyle: {
        fontSize: 20,
        color: 'red'
    }
}, '周二', '周三', '周四', '周五', '周六', '周日']
```

yAxis.data[i].value | string : 单个类目名称

yAxis.data[i].textStyle | Object : 类目标签的文字样式


![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_data.png)


![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_data_2.png)



---


axis.axisLine | Object : 坐标轴轴线相关设置。

axis.axisLine.show | boolean : 是否显示坐标轴轴线
[ default: true ]
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_axixLine.png)


axis.axisLine.onZero | boolean : 
[ default: true ]
X 轴或者 Y 轴的轴线是否在另一个轴的 0 刻度上，只有在另一个轴为数值轴且包含 0 刻度时有效。
![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_axixLine_zero.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_axixLine_zero_2.png)


axis.axisLine.lineStyle | Object

![](http://ols7leonh.bkt.clouddn.com//assert/img/echarts/axis/axis_axixLine_style.png)


---


axis.Tick


axis.Tick.interval

---


axisLable

---


axisLable.interval

---


axisLable.rotate

---


axisLable.margn


---


axisLable.formatter/textStyle

---




splitLine

---



splitArea



---






