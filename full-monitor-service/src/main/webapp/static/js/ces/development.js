$(document).ready(function() {
    loadDatas();
    $("#query").click(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        var queryDate = $('#queryDate').val();
        loadAllkindsNum(cityCode, queryDate);
    }
    function loadAllkindsNum(cityCode, queryDate) {// 加载区域分布、区域发展趋势
        var devTrend = echarts.init(document.getElementById('devTrend'));
        var increase = echarts.init(document.getElementById('increase'));
        var activity = echarts.init(document.getElementById('activity'));
        $.ajax({
            url : project.ctxPath + "/data/areaDevelopment/allkindsNum.do",
            data : {
                cityCode : cityCode,
                queryDate : queryDate
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 7) {
                    devTrend.setOption(chartshow.multipleVerticalBar(data));
                    increase.setOption(chartshow.upDownBar([ data[1], data[2], data[3] ]));
                    var option = chartshow.verticalBar([ data[4], data[5], data[6] ]);
                    option.xAxis.show = true;
                    activity.setOption(option);
                }
            }
        });
    }
});