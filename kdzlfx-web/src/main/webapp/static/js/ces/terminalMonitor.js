document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    var cityData;
    initData();
    // -------------------------------------
    function initData() {
        $("#tab-title").html("");
        var title = [];
        title.push("<li value = '1' class='layui-this'><p><b>地市质差</b></p></li>");
        title.push("<li value = '2' ><p><b>重点用户质差</b></p></li>");
        title.push("<li value = '3' ><p><b>厂家质差</b></p></li>");
        $("#tab-title").append(title.join(""));
        $("ul#tab-title li").bind("click",function(){
            loadMoinitorData(this.value);
        });
        // 加载第一个li数据
        loadMoinitorData($("#tab-title").children("li").first().attr("value"));
    }
    
    function loadMoinitorData(type) {
        $("#terminal_type").val(type);
        // 传入后台加载数据
        $.ajax({
            url : project.ctxPath + "/data/terminalMonitorInfo.do",
            data : {
                type : type
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                initBarData(data[0],"authFailSort",'#5b9bd5');
                initBarData(data[1],"shotOftenUpDownSort",'#fb883c');
                initBarData(data[2],"oftenDownLineSort",'#c5c5c5');
            }
        });
    }
    
    function initBarData(data, divId, color) {
        var opts;
        if (data.data != null && data.data.length > 0) {
            //设置样式 16 * 32 
            var dataLength = data.data.length;
            var heigtht = dataLength * 32;
            if(dataLength < 10 ) {
                heigtht = heigtht + 70;
            }
            $("#"+divId).css("height",heigtht+"px");
            opts = {
                    height: heigtht
                }
        }
        echarts.dispose(document.getElementById(divId));
        var data_bar = echarts.init(document.getElementById(divId));
        var color=[color];
        var bardata=[data];
        data_bar.setOption(chartshow.horizontalBar(bardata,color), true);
    }
    
});
