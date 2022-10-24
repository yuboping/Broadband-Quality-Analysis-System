document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    loadDatas();
    
    // ------------------------------------------------------------------
    function loadDatas() {
        loadOltInfo(oltip);
        loadUserOnline(oltip);
//        loadAuthFailRate(oltip);
    }
    function loadOltInfo(oltip) {// 加载基本信息
        $.ajax({
            url : project.ctxPath + "/data/olt/info.do",
            data : {
            	oltip : oltip
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data == null || data.length == 0) {
                    return;
                }
                makeTabDetail(data);
            }
        });
    }
    function makeTabDetail(data) {
        $("#tab-detail").html("");
        var detail = [];
        data.forEach(function(row, index) {
            if (index == 0) {
                detail.push("<div class='layui-tab-item layui-show'>");
            } else {
                detail.push("<div class='layui-tab-item'>");
            }
            detail.push("    <div class='info_box clearfix'>");
            row.data.forEach(function(item, i) {
                detail.push("        <div class='span12 '>");
                detail.push("            <div class='content_lable fl'>" + item.name + "：</div>");
                detail.push("            <div class='content_in fl'>" + item.value + "</div>");
                detail.push("        </div>")
            });
            detail.push("    </div>");
            detail.push("</div>");
        });
        $("#tab-detail").append(detail.join(""));
    }

    function loadUserOnline(oltip) {// 加载在线用户数
    	var time = new Date(+new Date()+8*3600*1000).toISOString().replace(/T/g,' ').replace(/\.[\d]{3}Z/,'');
        $.ajax({
            url : project.ctxPath + "/data/oltEquipment/userOnline.do",
            data : {
            	oltip : oltip,
            	time : time
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                var userOnline = echarts.init(document.getElementById('userOnline'));
                if (data != null && data.length !== 0) {
                    userOnline.setOption(chartshow.alarmThresholdLine(data));
                    userOnline.on('click', function (param) {  
                        //param.name：X轴的值 
                        //param.data：Y轴的值 
                    	var date = getFormatDate(param.name);
                        window.open(project.ctxPath + "/oltUserOnlineInfo.do?oltip="+oltip+"&time="+date);
                    });
                }else{
                	userOnline.showLoading(chartshow.showNoDataLoading());
                }
            }
        });
    }
    
    function getFormatDate(name) {
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var monthname = name.substring(0, 2);
        if(monthname>month){
        	year = date.getFullYear()-1;
        }
        return year+"-"+name+":00";
    }
    
    function loadAuthFailRate(oltip) {// 加载认证失败率
    	$.ajax({
            url : project.ctxPath + "/data/oltEquipment/authFailRate.do",
            data : {
            	oltip : oltip
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                var authFailRate = echarts.init(document.getElementById('authFailRate'));
                if (data != null && data.length !== 0) {
                	authFailRate.setOption(chartshow.alarmLine(data));
                }else{
                	authFailRate.showLoading(chartshow.showNoDataLoading());
                }
            }
        });
    }
});
