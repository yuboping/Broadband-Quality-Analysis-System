$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadOffnetRate(cityCode);
        loadPredictionNum(cityCode);
        loadMaintainNum(cityCode)
    }
    function loadOffnetRate(cityCode) {// 加载离网概率
        var predictTrend = echarts.init(document.getElementById('predictTrend'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/firstOffnetRate.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    predictTrend.setOption(chartshow.multipleLine([ data[0] ], null, '%'));
                }
            }
        });
    }
    function loadPredictionNum(cityCode) {// 加载离网等级趋势
        var levelTrend = echarts.init(document.getElementById('levelTrend'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/firstUserPrediction.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 10) {
                    levelTrend.setOption(chartshow.verticalBar(data));
                }
            }
        });
    }
    function loadMaintainNum(cityCode) {// 加载派单、维系趋势
        var dispatchTrend = echarts.init(document.getElementById('dispatchTrend'));
        var keepTrend = echarts.init(document.getElementById('keepTrend'));
        var rateTrend = echarts.init(document.getElementById('rateTrend'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/firstUserMaintain.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 5) {
                    dispatchTrend.setOption(chartshow.verticalBar([ data[0] ]));
                    keepTrend.setOption(chartshow.multipleVerticalBar([ data[1], data[2] ]));
                    rateTrend.setOption(chartshow.multipleLine([ data[3], data[4] ], null, '%'));
                }
            }
        });
    }
});