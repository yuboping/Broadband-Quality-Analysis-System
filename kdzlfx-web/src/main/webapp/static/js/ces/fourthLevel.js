$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadUserStructure(cityCode);
        loadFourthNotrenewedRate(cityCode);
        loadPredictionNum(cityCode);
        loadMaintainNum(cityCode);
    }
    function loadUserStructure(cityCode) {// 到期用户结构趋势
        var userStructure = echarts.init(document.getElementById('userStructure'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/expiringUser.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 4) {
                    userStructure.setOption(chartshow.verticalBar(data));
                }
            }
        });
    }
    function loadFourthNotrenewedRate(cityCode) {// 到期未续约率
        var predictTrend = echarts.init(document.getElementById('unrenewedRate'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/fourthNotrenewedRate.do",
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
            url : project.ctxPath + "/data/alarm/fourthUserPrediction.do",
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
            url : project.ctxPath + "/data/alarm/fourthUserMaintain.do",
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