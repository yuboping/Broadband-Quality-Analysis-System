$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadAAANum(cityCode);
        loadCrmNum(cityCode);
        loadFaultNum(cityCode);
        loadLineNum(cityCode);
        loadBehaviourNum(cityCode);
        loadComplaintNum(cityCode);
    }
    function loadAAANum(cityCode) {// 加载AAA监控数据
        var aaaTrend = echarts.init(document.getElementById('aaaTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/aaaMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 4) {
                    aaaTrend.setOption(chartshow.multipleLine(data));
                }
            }
        });
    }
    function loadCrmNum(cityCode) {// 加载CRM数据
        var crmTrend = echarts.init(document.getElementById('crmTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/crmMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    var option = chartshow.verticalBar(data, [ "#00b050" ])
                    option.legend.show = false;
                    crmTrend.setOption(option);
                }
            }
        });
    }
    function loadFaultNum(cityCode) {// 加载报障数据
        var faultTrend = echarts.init(document.getElementById('faultTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/faultMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 2) {
                    faultTrend.setOption(chartshow.multipleLine(data));
                }
            }
        });
    }
    function loadLineNum(cityCode) {// 加载线路数据
        var lineTrend = echarts.init(document.getElementById('lineTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/lineMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    var option = chartshow.verticalBar(data, [ "#ffc000" ])
                    option.legend.show = false;
                    lineTrend.setOption(option);
                }
            }
        });
    }
    function loadBehaviourNum(cityCode) {// 加载行为数据
        var behaviourTrend = echarts.init(document.getElementById('behaviourTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/behaviourMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    var option = chartshow.verticalBar(data, [ "#c00000" ])
                    option.legend.show = false;
                    behaviourTrend.setOption(option);
                }
            }
        });
    }
    function loadComplaintNum(cityCode) {// 加载投诉数据
        var complaintTrend = echarts.init(document.getElementById('complaintTrend'));
        $.ajax({
            url : project.ctxPath + "/data/monitor/complaintMon.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    var option = chartshow.verticalBar(data, [ "#0070c0" ]);
                    option.legend.show = false;
                    complaintTrend.setOption(option);
                }
            }
        });
    }
});