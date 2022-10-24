document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
	$("#start_time").val(getNowFormatDate());
	$("#end_time").val(getNowFormatDate());
    loadDatas();

    $("#olt_search").click(function() {
        searchBtn();
    });

    $("#start_time").datetimepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        weekStart: 1,
        autoclose: true,
        startView: 2,
        minView: 2,
        forceParse: false,
        endDate:new Date()
    }).on('changeDate', function (e) {
        // var BeginTime = $("#start_time").val();
        $("#end_time").datetimepicker("setStartDate",""+$("#start_time").val());
    });

    $("#end_time").datetimepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        weekStart: 1,
        autoclose: true,
        startView: 2,
        minView: 2,
        forceParse: false,
        endDate:new Date()
    }).on('changeDate', function (e) {
        $("#start_time").datetimepicker("setEndDate",""+$("#end_time").val());
    });

    $("#olt_history_search").click(function () {
        oltHistoryTable();
    });
    
    $("#olt_history_export").click(function () {
        oltHistoryExport();
    });

    // ---------------------------------------------------------------------------
    function loadDatas() {
    	loadOltMapInfo();
    	loadOltMap();
        oltTable();
        oltHistoryTable();
    }

    function loadOltMap(){// 加载OLT地图分布
        $.get(project.staticPath +'/js/map/province/' + province + '.json', function (chinaJson) {
            echarts.registerMap('province', chinaJson);
            var oltinfo_map = echarts.init(document.getElementById('oltinfo_map'));
              $.get(project.staticPath +'/js/map/oltmapinfo/oltmap.json', function (oltmapJson) {
                if(oltmapJson != null){
                	oltinfo_map.setOption(chartshow.oltMap(oltmapJson));
                }
              });
            oltinfo_map.on('click', function (param) {
            	if(param.value != null ){
//            		window.location.href = project.ctxPath + "/oltEquipmentInfo.do?oltip="+param.name;
            		window.open(project.ctxPath + "/oltEquipmentInfo.do?oltip="+param.name);
            	}
              });
        });   
    }
    
    function loadOltMapInfo(){// 加载OLT地图信息
    	$.get(project.staticPath +'/js/map/oltmapinfo/oltmapinfo.json', function (oltmapinfoJson) {
            if(oltmapinfoJson != null){
            	$("#totalNum").html(oltmapinfoJson.totalNum);
            	$("#alarmNumTFH").html(oltmapinfoJson.alarmNumTFH);
            	$("#curAlarmNum").html(oltmapinfoJson.curAlarmNum);
            }
        });
    }

    //查询功能
    function searchBtn() {
        var olt_count = $("#olt_count").val();
        console.log(olt_count);
        if(olt_count != ''){
            window.open(project.ctxPath + "/oltEquipmentInfo.do?oltip="+olt_count);
        }else {
            layer.msg('请输入OLT设备',{
                time:1000
            });
        }
    }

    //olt最近20条告警
    function oltTable(){
    	$.ajax({url:project.ctxPath + "/data/oltList.do",success:function(ret){
            var data = JSON.parse(ret);
            if(data==null||data.length==0){
                return;
            }
            var html="";
            for(var i=0; i < data.length; i++){
                if (data[i].mid_value == null || data[i].mid_value == "null") {
                    data[i].mid_value = "--"
                }
                if (data[i].now_value == null || data[i].now_value == "null") {
                    data[i].now_value = "--"
                }
                if (data[i].down_value == null || data[i].down_value == "null") {
                    data[i].down_value = "--"
                }
                if (data[i].down_per == null || data[i].down_per == "null") {
                    data[i].down_per = "--"
                }
                html += "<tr>";
                html += "<td>"+data[i].alarm_time+"</td>";
                html += "<td>"+data[i].oltip+"</td>";
                html += "<td>"+data[i].mid_value+"</td>";
                html += "<td>"+data[i].now_value+"</td>";
                html += "<td>"+data[i].down_value+"</td>";
                html += "<td>"+data[i].down_per+"</td>";
                html += "</tr>";
                $("#tbody").html(html);
            }
        }});
    }


    function oltHistoryTable(pageNumber) {
        var olt_ip = $("#olt_ip").val();
        var start_time = $("#start_time").val();
        var end_time = $("#end_time").val();
        $("#olt_ip_hidden").val(olt_ip);
        $("#start_time_hidden").val(start_time);
        $("#end_time_hidden").val(end_time);

        if (end_time < start_time){
            layer.msg('请输入正确的开始时间和结束时间',{
                time:1000
            });
            return;
        }

        var data = {
            'olt_ip': olt_ip, 'start_time': start_time,
            'end_time': end_time, 'pageNumber':pageNumber

        };

        $.get(project.ctxPath + "/data/oltHistoryList.do",data,function(res){
            res = JSON.parse(res);
            resetPage(res);
        });
    }

    //拼接tr
    function showTable(data,startnum,endnum){
        var rowdata = "";
        var k = 0;
        for(var i=0;i<=endnum-startnum;i++){
            var pageList = data[i];
            if (pageList.mid_value == null || pageList.mid_value == "null") {
                pageList.mid_value = "--"
            }
            if (pageList.now_value == null || pageList.now_value == "null") {
                pageList.now_value = "--"
            }
            if (pageList.down_value == null || pageList.down_value == "null") {
                pageList.down_value = "--"
            }
            if (pageList.down_per == null || pageList.down_per == "null") {
                pageList.down_per = "--"
            }
            rowdata += "<tr>";
            rowdata += "<td>"+pageList.alarm_time+"</td>";
            rowdata += "<td>"+pageList.oltip+"</td>";
            rowdata += "<td>"+pageList.mid_value+"</td>";
            rowdata += "<td>"+pageList.now_value+"</td>";
            rowdata += "<td>"+pageList.down_value+"</td>";
            rowdata += "<td>"+pageList.down_per+"</td>";
            rowdata += "</tr>";
        }
        var ct = endnum-startnum+1;
        $("#history_tbody").empty().append(rowdata);

    }

    //重置分页(跳转分页)
    function resetPage(res) {
        var totalCount = res.totalCount;
        var pageList = res.pageList;
        var start = res.start;
        var end = res.end;
        var pageNumber = res.pageNumber;
        var totalPages = res.totalPages;
        var pageSize = res.pageSize;
        $("#querynum").text(totalCount);
        layui.use('laypage', function(){
            var laypage = layui.laypage;

            laypage.render({
                elem: "pageinfo", //容器。值支持id名、原生dom对象，jquery对象。【如该容器为】：<div id="page1"></div>
                count: totalCount, //通过后台拿到的总页数
                curr: pageNumber, //当前页
                limit: pageSize, //每页显示的条数
                theme: '#1E9FFF',
                jump: function (obj, first) { //触发分页后的回调
                    showTable(pageList,start,end);
                    $("#currnum").text( start + "-" + end);
                    if(totalCount==0){
                        $("#currnum").empty().text("0 ");
                    }
                    $("#page_curr").val(obj.curr);
                    // resizewh.resizeBodyH($(".home_content"));
                    if (!first) { //点击跳页触发函数自身，并传递当前页：obj.curr
                        oltHistoryTable(obj.curr);
                    }
                }
            });
        });
    }
    
    //导出数据
    function oltHistoryExport() {
    	layer.confirm('是否确认导出该批次数据？', {
            closeBtn:0,
            title: '询问',
            btn: ['确认','取消'] // 按钮
        },function(){
            layer.closeAll();
            var olt_ip = $("#olt_ip_hidden").val();
            var start_time = $("#start_time_hidden").val();
            var end_time = $("#end_time_hidden").val();
            var url = project.ctxPath + "/data/export/oltHistoryList.do?olt_ip="+olt_ip+"&start_time="+start_time+"&end_time="+end_time;
            window.open(url,"_blank");
        });
    }
    
    function getNowFormatDate() {
        var date = new Date();
        var seperator1 = "-";
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var strDate = date.getDate();
        if (month >= 1 && month <= 9) {
            month = "0" + month;
        }
        if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
        }
        var currentdate = year + seperator1 + month + seperator1 + strDate;
        return currentdate;
    }

});