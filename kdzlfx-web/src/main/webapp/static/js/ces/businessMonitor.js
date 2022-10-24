document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    var cityData;
    initData();
    // -------------------------------------
    function initData() {
        $("#tab-title").html("");
        var title = [];
        title.push("<li value = '1' class='layui-this'><p><b>流量使用评分</b></p></li>");
        title.push("<li value = '2' ><p><b>家庭终端评分</b></p></li>");
        title.push("<li value = '3' ><p><b>投诉告障评分</b></p></li>");
        title.push("<li value = '4' ><p><b>上网线路评分</b></p></li>");
        title.push("<li value = '5' ><p><b>行为特征评分</b></p></li>");
        $("#tab-title").append(title.join(""));
        $("ul#tab-title li").bind("click",function(){
            loadMoinitorData(this.value);
        });
        // 加载第一个li数据
        loadMoinitorData($("#tab-title").children("li").first().attr("value"));
    }
    
    function loadMoinitorData(type) {
        $("#health_city_code").val(type);
        // 传入后台加载数据
        $.ajax({
            url : project.ctxPath + "/data/areaMonitorInfo.do",
            data : {
                type : type
            },
            success : function(retdata) {
//                document.getElementById('areaHealthAvgSort').innerHTML="";
//                $("#areaHealthAvgSort").empty();
                var data = JSON.parse(retdata);
                var opts;
                if (data.data != null && data.data.length > 0) {
                    cityData = data.data;
                    var cityCode = data.data[data.data.length-1].mark;
                    loadMoinitorUserData(type,cityCode);
                    //设置样式 16 * 32 
                    var heigtht = data.data.length * 32;
                    if(data.data.length < 10 ) {
                        heigtht = heigtht + 70;
                    }
                    $("#areaHealthAvgSort").css("height",heigtht+"px");
                    opts = {
                            height: heigtht
                        }
                } else {
                    loadMoinitorUserData(type,"-1");
                }
                echarts.dispose(document.getElementById('areaHealthAvgSort'));
                var areaHealthAvgSort_bar = echarts.init(document.getElementById('areaHealthAvgSort'));
//                var areaHealthAvgSort_bar = echarts.init(document.getElementById('areaHealthAvgSort'),null,opts);
                var color;
                if(type==1) {
                    color=[ '#5b9bd5'];
                }else if(type==2) {
                    color=[ '#AFEEEE'];
                }else if(type==3) {
                    color=[ '#ADD8E6'];
                }else if(type==4) {
                    color=[ '#B0C4DE'];
                }else {
                    color=[ '#D2B48C'];
                }
                
                var bardata=[data];
                areaHealthAvgSort_bar.setOption(chartshow.horizontalBar(bardata,color), true);
                areaHealthAvgSort_bar.off('click');
                areaHealthAvgSort_bar.on('click', function(params) {
                    loadUserData(type, params.name);
                })
            }
        });
    }
    
    // 加载排名靠后十位用户
    function loadUserData(type, cityName) {
        var cityCode;
        for (var i = 0; i < cityData.length; i++) {  
            if(cityData[i].name==cityName) {
                cityCode = cityData[i].mark;
                break;
            }
        }
        loadMoinitorUserData(type, cityCode);
    }
    function loadMoinitorUserData(type,cityCode) {
        if('-1'==cityCode) {
            var html="";
            html += "<tr>";
            html += "<td colspan=\"3\">无数据</td>";
            html += "</tr>";
            $("#tbody").html(html);
            return;
        }
        
        $.ajax({
            url : project.ctxPath + "/data/userMonitorInfo.do",
            data : {
                type : type,
                cityCode : cityCode
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                var html="";
                if (data.data != null && data.data.length > 0) {
                    for(var i=0; i < data.data.length; i++){
                        html += "<tr>";
                        html += "<td>"+data.data[i].name+"</td>";
                        html += "<td>"+data.data[i].mark+"</td>";
                        html += "<td>"+data.data[i].value+"</td>";
                        html += "</tr>";
                        $("#tbody").html(html);
                   }
                } else {
                    html += "<tr>";
                    html += "<td colspan=\"3\">无数据</td>";
                    html += "</tr>";
                    $("#tbody").html(html);
                }
            }
        });
    }
});
