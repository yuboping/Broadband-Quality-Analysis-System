document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    loadDatas();
    
    // ------------------------------------------------------------------
    function loadDatas() {
    	var isThreshold = false;
    	var dataUserOnlineMapInfo;
    	$.ajax({
    		async: false,
            url : project.ctxPath + "/data/oltUserOnline/Info.do",
            data : {
            	oltip : oltip,
            	time : time
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                dataUserOnlineMapInfo = data;
                var dataThreshold = data.threshold;
                if (dataThreshold != null && dataThreshold.length !== 0) {
                	isThreshold = true;
                }
            }
        });
    	makeTabDetail(dataUserOnlineMapInfo.chartDatasList);
        loadUserOnline(dataUserOnlineMapInfo);
        if(dataUserOnlineMapInfo.threshold == null){
        	$(".threshold").hide();
        }else{
        	loadThreshold(dataUserOnlineMapInfo);
        }
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
                detail.push("        <div class='span50 '>");
                detail.push("            <div class='content_lable_300 fl'>" + item.name + "：</div>");
                detail.push("            <div class='content_in fl'>" + item.value + "</div>");
                detail.push("        </div>")
            });
            detail.push("    </div>");
            detail.push("</div>");
        });
        $("#tab-detail").append(detail.join(""));
    }

    function loadUserOnline(dataUserOnlineMapInfo) {// 加载在线用户数
    	var userOnline = echarts.init(document.getElementById('userOnline'));
        if (dataUserOnlineMapInfo != null && dataUserOnlineMapInfo.length !== 0) {
            userOnline.setOption(chartshow.alarmThresholdForecastLine(dataUserOnlineMapInfo.originalDataList,dataUserOnlineMapInfo.forecastDataList));
        }else{
        	userOnline.showLoading(chartshow.showNoDataLoading());
        }
    }
    
    function loadThreshold(dataUserOnlineMapInfo) {// 加载界值分析数据
    	if(dataUserOnlineMapInfo.threshold.allErrors.mapee == null){
    		$("#userOnlineMapeeMain").hide();
    	}else{
    		var finitymapee;
    		if(dataUserOnlineMapInfo.threshold.finity.mapee == null){
    			finitymapee = dataUserOnlineMapInfo.propertiesThresholdMap.mapee
    		}else{
    			finitymapee = dataUserOnlineMapInfo.threshold.finity.mapee;
    		}
    		loadThresholdFun("userOnlineMapee","mapee",dataUserOnlineMapInfo.functionShownameMap.mapee,dataUserOnlineMapInfo.forecastDataList,finitymapee,"#008000");
    	}
    	if(dataUserOnlineMapInfo.threshold.allErrors.mae == null){
    		$("#userOnlineMaeMain").hide();
    	}else{
    		var finitymae;
    		if(dataUserOnlineMapInfo.threshold.finity.mae == null){
    			finitymae = dataUserOnlineMapInfo.propertiesThresholdMap.mae
    		}else{
    			finitymae = dataUserOnlineMapInfo.threshold.finity.mae;
    		}
    		loadThresholdFun("userOnlineMae","mae",dataUserOnlineMapInfo.functionShownameMap.mae,dataUserOnlineMapInfo.forecastDataList,finitymae,"#FFFF00");
    	}
    	if(dataUserOnlineMapInfo.threshold.allErrors.smape == null){
    		$("#userOnlineSmapeMain").hide();
    	}else{
    		var finitysmape;
    		if(dataUserOnlineMapInfo.threshold.finity.smape == null){
    			finitysmape = dataUserOnlineMapInfo.propertiesThresholdMap.smape
    		}else{
    			finitysmape = dataUserOnlineMapInfo.threshold.finity.smape;
    		}
    		loadThresholdFun("userOnlineSmape","smape",dataUserOnlineMapInfo.functionShownameMap.smape,dataUserOnlineMapInfo.forecastDataList,finitysmape,"#B22222");
    	}
    	if(dataUserOnlineMapInfo.threshold.allErrors.mape == null){
    		$("#userOnlineMapeMain").hide();
    	}else{
    		var finitymape;
    		if(dataUserOnlineMapInfo.threshold.finity.mape == null){
    			finitymape = dataUserOnlineMapInfo.propertiesThresholdMap.mape
    		}else{
    			finitymape = dataUserOnlineMapInfo.threshold.finity.mape;
    		}
    		loadThresholdFun("userOnlineMape","mape",dataUserOnlineMapInfo.functionShownameMap.mape,dataUserOnlineMapInfo.forecastDataList,finitymape,"#40E0D0");
    	}
    	if(dataUserOnlineMapInfo.threshold.allErrors.mase == null){
    		$("#userOnlineMaseMain").hide();
    	}else{
    		var finitymase;
    		if(dataUserOnlineMapInfo.threshold.finity.mase == null){
    			finitymase = dataUserOnlineMapInfo.propertiesThresholdMap.mase
    		}else{
    			finitymase = dataUserOnlineMapInfo.threshold.finity.mase;
    		}
    		loadThresholdFun("userOnlineMase","mase",dataUserOnlineMapInfo.functionShownameMap.mase,dataUserOnlineMapInfo.forecastDataList,finitymase,"#FFA07A");
    	}
    }
    
    function loadThresholdFun(id,name,showname,forecastDataList,finityData,colour) {
    	var thresholdFun = echarts.init(document.getElementById(id));
        if (forecastDataList != null && forecastDataList.length !== 0) {
        	finityData = Math.round(finityData*100)/100;
        	thresholdFun.setOption(chartshow.alarmThresholdFunLine(forecastDataList,finityData,name,showname,colour));
        }else{
        	thresholdFun.showLoading(chartshow.showNoDataLoading());
        }
    }
});
