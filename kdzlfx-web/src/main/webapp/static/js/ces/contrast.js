$(document).ready(function() {
    $('#datetimepicker').datetimepicker({
        language : "zh-CN",
        format : 'yyyy-mm',
        autoclose : 1,
        startView : 3,
        minView : 3,
    }).val(getLastMonth());
    loadDatas();
    $("#datetimepicker").change(function() {
        loadDatas();
    });

    // ---------------------------------------------------------------------------
    function loadDatas() {
        var month = $('#datetimepicker').val();
        month = month.replace("-", "");
        loadAllkindsNum(month);
    }
    function loadAllkindsNum(month) {// 加载区域分布、区域发展趋势
        $.get(project.staticPath + '/js/map/province/' + province + '.json', function(chinaJson) {
            echarts.registerMap('province', chinaJson);
            var regNum_map = echarts.init(document.getElementById('regNum_map'));
            var regNum_bar = echarts.init(document.getElementById('regNum_bar'));
            var devTrend = echarts.init(document.getElementById('devTrend'));
            var increase = echarts.init(document.getElementById('increase'));
            var activity = echarts.init(document.getElementById('activity'));
            $.ajax({
                url : project.ctxPath + "/data/areaContrast/allkindsNum.do",
                data : {
                    month : month
                },
                success : function(retdata) {
                    var data = JSON.parse(retdata);
                    if (data != null && data.length == 7) {
                        regNum_map.setOption(chartshow.provinceMap(data[0]));
                        regNum_bar.setOption(chartshow.horizontalBar([ data[0] ]));
                        devTrend.setOption(chartshow.multipleVerticalBar(data));
                        increase.setOption(chartshow.upDownBar([ data[1], data[2], data[3] ]));
                        var option = chartshow.verticalBar([ data[4], data[5], data[6] ]);
                        option.xAxis.show = true;
                        activity.setOption(option);
                    }
                }
            });
        });
    }
    function getLastMonth() {// 获取上个月日期
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth();
        if (month == 0) {
            year = year - 1;
            month = 12;
        }
        if (month < 10) {
            return year + "-0" + month;
        } else {
            return year + "-" + month;
        }
    }
});