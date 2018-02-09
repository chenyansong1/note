function test(){
}

$(function(){

    var myChart4 = echarts.init(document.getElementById('cys'),'macarons');

    myChart4.setOption({
        title: {
            text: '折线图堆叠'
        },
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            data:['sys','gen','event']
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: $xAxis$
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                name:'sys',
                type:'line',
                smooth: true,
                data:$sys$
            },
            {
                name:'gen',
                type:'line',
                smooth: true,
                data:$gen$
            },
            {
                name:'event',
                type:'line',
                smooth: true,
                data:$event$
            }
        ]
    })

});


