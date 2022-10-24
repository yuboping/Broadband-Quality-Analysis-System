$(document).ready(function() {
    loadDatas();
    $("#cityCode").change(function() {
        loadDatas();
    });
    // ---------------------------------------------------------------------------
    function loadDatas() {
        var cityCode = $('#cityCode').val();
        loadNewSelinceNum(cityCode);
    }
    function loadNewSelinceNum(cityCode) {// 新增静默用户数趋势
        var silenceTrend = echarts.init(document.getElementById('silenceTrend'));
        $.ajax({
            url : project.ctxPath + "/data/alarm/newSilence.do",
            data : {
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data != null && data.length == 1) {
                    silenceTrend.setOption(chartshow.multipleVerticalBar(data));
                }
            }
        });
    }
});