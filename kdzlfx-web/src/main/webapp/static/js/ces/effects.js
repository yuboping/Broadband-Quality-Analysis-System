$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadTrainingEffect(cityCode);
        loadPredictResult(cityCode);
        loadPredictEffect(cityCode);
    }
    function loadTrainingEffect(cityCode) {// 加载训练效果
        var trend = echarts.init(document.getElementById('trainingEffect'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/trainingEffect.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 3) {
                    trend.setOption(chartshow.multipleLine(data, null, "%"));
                }
            }
        });
    }
    function loadPredictResult(cityCode) {// 加载预测结果
        var trend = echarts.init(document.getElementById('predictResult'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/predictResult.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 10) {
                    trend.setOption(chartshow.verticalBar(data));
                }
            }
        });
    }
    function loadPredictEffect(cityCode) {// 加载预测效果
        var trend = echarts.init(document.getElementById('predictEffect'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/predictEffect.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 3) {
                    trend.setOption(chartshow.multipleLine(data, null, "%"));
                }
            }
        });
    }

});