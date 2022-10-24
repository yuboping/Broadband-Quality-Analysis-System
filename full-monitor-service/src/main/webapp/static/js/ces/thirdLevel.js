$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadNewStopNum(cityCode);
    }
    function loadNewStopNum(cityCode) {// 新增停机用户数趋势
        var stopTrend = echarts.init(document.getElementById('stopTrend'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/newStop.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    stopTrend.setOption(chartshow.multipleVerticalBar(data));
                }
            }
        });
    }
});