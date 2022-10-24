document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    $.fn.countTo = function(options) {  // 设置计数
        options = options || {};
        return $(this).each(function() {
            // 当前元素的选项
            var settings = $.extend({}, $.fn.countTo.defaults, {
                from : $(this).data('from'),
                to : $(this).data('to'),
                speed : $(this).data('speed'),
                refreshInterval : $(this).data('refresh-interval'),
                decimals : $(this).data('decimals')
            }, options);
            // 更新值
            var loops = Math.ceil(settings.speed / settings.refreshInterval), increment = (settings.to - settings.from) / loops;
            // 更改应用和变量
            var self = this, $self = $(this), loopCount = 0, value = settings.from, data = $self.data('countTo') || {};
            $self.data('countTo', data);
            // 如果有间断，找到并清除
            if (data.interval) {
                clearInterval(data.interval);
            }
            data.interval = setInterval(updateTimer, settings.refreshInterval);
            // 初始化起始值
            render(value);
            function updateTimer() {
                value += increment;
                loopCount++;
                render(value);
                if (typeof (settings.onUpdate) == 'function') {
                    settings.onUpdate.call(self, value);
                }
                if (loopCount >= loops) {
                    // 移出间隔
                    $self.removeData('countTo');
                    clearInterval(data.interval);
                    value = settings.to;
                    if (typeof (settings.onComplete) == 'function') {
                        settings.onComplete.call(self, value);
                    }
                }
            }
            function render(value) {
                var formattedValue = settings.formatter.call(self, value, settings);
                $self.html(formattedValue);
            }
        });
    };
    $.fn.countTo.defaults = {
        from : 0, // 数字开始的值
        to : 0, // 数字结束的值
        speed : 1000, // 设置步长的时间
        refreshInterval : 100, // 隔间值
        decimals : 0, // 显示小位数
        formatter : formatter, // 渲染之前格式化
        onUpdate : null, // 每次更新前的回调方法
        onComplete : null
    };
    loadKeyfigures();
    loadUserScatter();
    loadUserChange();
    loadUserStructure();
    loadDoubleCirque();

// ------------------------------------------------------------------
    function loadKeyfigures(){// 加载昨日关键指标
        $.ajax({url:project.ctxPath + "/data/homepage/keyfigures.do",success:function(ret){
            var data = JSON.parse(ret);
            if(data[0]==null||data[0].data.length==0){
                return;
            }
            var color=["#4c84ff","#009fd7","#b165fb","#77c450","#ff9569"];
            data[0].data.forEach(function(row,index) {
                var show=[];
                show.push("<div class='home_list' style='background: " + color[index] + ";'>");
                show.push("    <p>" + row.name + "</p>");
                show.push("    <p><b class='timer' data-to='" + row.value + "' data-speed='1500'>50</b></p>");
                show.push("</div>");
                $("#keyfigures").append(show.join(""));
            });
            $('.timer').each(count);
        }});
    }
    function loadUserScatter(){// 加载用户分布
        $.get(project.staticPath +'/js/map/province/' + province + '.json', function (chinaJson) {
            echarts.registerMap('province', chinaJson);
            var userScatter_map = echarts.init(document.getElementById('userScatter_map'));
            var userScatter_bar = echarts.init(document.getElementById('userScatter_bar'));
            $.ajax({url:project.ctxPath + "/data/homepage/userScatter.do",success:function(ret){
                var data = JSON.parse(ret);
                if(data != null && data.length == 4){
                    userScatter_map.setOption(chartshow.provinceMap(data[0]));
                    var bardata=[data[1],data[2],data[3]];
                    var color=[ '#5b9bd5', '#fb883c', '#c5c5c5' ];
                    userScatter_bar.setOption(chartshow.horizontalBar(bardata,color));
                }
            }});
            $.ajax({url:project.ctxPath + "/data/homepage/userOlt.do",success:function(ret){
                var data = JSON.parse(ret);
                var html="";
                if(data != null){
                	 for(var i=0; i < data.length; i++){
                         html += "<tr>";
                         html += "<td>"
                             +"<a class=\"J_edit pr10\" href=\"#\" name='showOltChart' id=\""+data[i].oltip+"\" >"
                             +data[i].oltip+"</a>"+"</td>";
                         html += "<td>"+data[i].onlinenum+"</td>";
                         html += "</tr>";
                         $("#tbody").html(html);
                	}
                }
                $("[name=showOltChart]").each(function(){
                    $(this).on('click',function(){
                        getOltChart($(this).attr('id'));
                    });
                });
            }});
        });   
    }

    //模态框折线图
    function getOltChart(oltIp) {
        $("#oltLine").empty();
        var color=[ '#5b9bd5', '#fb883c', '#c5c5c5' ];
        $('#oltModal').modal('show');
        $.ajax({url:project.ctxPath + "/data/homepage/userOltByOltip.do", data:{"oltip":oltIp}, success:function (res) {
            var elements = echarts.init(document.getElementById('oltLine'));
            var res = JSON.parse(res);
            console.log("res",res);
            elements.setOption(chartshow.oltLine(res, color));
        }});
    }
    $('#oltModal').on('hide.bs.modal', function(){
        document.getElementById('oltLine').removeAttribute('_echarts_instance_');
    });
    function loadUserChange(){// 加载近30日各用户变化
        var elements=[echarts.init(document.getElementById('userChange_new')),
            echarts.init(document.getElementById('userChange_cancel')),
            echarts.init(document.getElementById('userChange_increase')),
            echarts.init(document.getElementById('userChange_stop'))];
        $.ajax({url:project.ctxPath + "/data/homepage/userChange.do",success:function(ret){
            var data = JSON.parse(ret);
            var color=[['#fcce10','#fffbe8'],['#b5c334','#f8f9eb'],['#b5c334','#f9e9ea'],['#009fd9','#e6f6fc']];
            if(data != null && data.length == 4){
                for(var i=0; i < elements.length; i++){
                    elements[i].setOption(chartshow.singleLine(data[i],color[i]));
                }
            }
        }});
    }
    function loadUserStructure(){// 加载各种用户结构
        var elements=[echarts.init(document.getElementById('userStructure_predict')),//健康度 
            // echarts.init(document.getElementById('userStructure_terminal')),//智能网关
            echarts.init(document.getElementById('userStructure_broadbandtype'))];//用户宽带类型
        $.ajax({url:project.ctxPath + "/data/homepage/userStructure.do",success:function(ret){
            var data = JSON.parse(ret);
            var color = ['#ed7d31', '#ffc000', '#4472c4', '#5b9bd5','#b165fb','#77c450','#ce7e7e','#b2d4ec','#787867','#b5c334'];
            if(data != null && data.length == 2){
                 for(var i=0; i < elements.length; i++){
                     elements[i].setOption(chartshow.singleCirque(data[i],color));
                 }
             }
        }});
    }

    //双个环形图
    function loadDoubleCirque() {
        var elements = echarts.init(document.getElementById('badQualitydoubleCirque'));
        $.ajax({url:project.ctxPath + "/data/homepage/getBadQualityPieData.do",success:function(ret){
                var color = ['#4472c4', '#ed7d31', '#4472c4','#4472c4','#ed7d31','#ed7d31'];
                var data = JSON.parse(ret);
                elements.setOption(chartshow.doubleCirque(data, color));
            }});
    }
 
    function formatter(value, settings) {
        return value.toFixed(settings.decimals);
    }
    function count(options) {
        var $this = $(this);
        options = $.extend({}, options || {}, $this.data('countToOptions') || {});
        $this.countTo(options);
    }
});
