$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadFeatureNum(cityCode);
        loadMarkingNum(cityCode);
        loadCorrelationRate(cityCode);
    }
    function loadFeatureNum(cityCode) {// 加载特征表数量
        var trend = echarts.init(document.getElementById('featureNum'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/featureNum.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    trend.setOption(chartshow.multipleLine(data));
                }
            }
        });
    }
    function loadMarkingNum(cityCode) {// 加载特征表标记数量
        var trend = echarts.init(document.getElementById('markingNum'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/markingNum.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 4) {
                    trend.setOption(chartshow.multipleLine(data));
                }
            }
        });
    }
    function loadCorrelationRate(cityCode) {// 加载表关联率
        var trend = echarts.init(document.getElementById('correlationRate'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/correlationRate.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 5) {
                    trend.setOption(chartshow.multipleLine(data, null, "%"));
                }
            }
        });
    }

});