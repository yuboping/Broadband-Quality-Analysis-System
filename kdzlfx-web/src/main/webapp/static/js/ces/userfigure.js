document.write("<script language='javascript' src='" + project.ctxPath + "/static/js/tools/chartshow.js'></script>");
$(document).ready(function() {
    loadDatas();
    loadLineTopology();
    $("#account").focus(function() {
        // $("#account").val("");
    });
    $("#query").click(function() {
        loadDatas();
    });
    // ------------------------------------------------------------------
    function loadDatas() {
        var account = $("#account").val();
        loadUserFigureInfo(account);
        loadUserTendency(account);
        loadOptHistory(account);
        loadPeakBandwidth();
        loadInternetFeatures();
        loadUserPreference();
        loadDPIData();
        loadFiveBin();
    }
    function loadUserFigureInfo(account) {// 加载基本信息
        $.ajax({
            url : project.ctxPath + "/data/userFigure/info.do",
            data : {
                account : account
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
        $("#tab-title").html("");
        $("#tab-detail").html("");
        var title = [], detail = [];
        data.forEach(function(row, index) {
            if (index == 0) {
                title.push("<li class='layui-this'><p><b>" + row.title + "</b></p></li>");
                detail.push("<div class='layui-tab-item layui-show'>");
            } else {
                title.push("<li><p><b>" + row.title + "</b></p></li>");
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
        $("#tab-title").append(title.join(""));
        $("#tab-detail").append(detail.join(""));
    }

    function loadUserTendency(account) {// 用户流量趋势，预测离网趋势
        $.ajax({
            url : project.ctxPath + "/data/userFigure/tendency.do",
            data : {
                account : account
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                var offnetTendency = echarts.init(document.getElementById('offnetTendency'));
                var flowTendency = echarts.init(document.getElementById('flowTendency'));
                if (data != null && data.length == 3) {
                    offnetTendency.setOption(chartshow.multipleLine([ data[0] ]));
                    flowTendency.setOption(chartshow.upDownLine([ data[1], data[2] ]));
                }
            }
        });
    }
    function loadOptHistory(account) {// 加载用户操作历史
        $.ajax({
            url : project.ctxPath + "/data/userFigure/history.do",
            data : {
                account : account
            },
            success : function(retdata) {
                var data = JSON.parse(retdata);
                var show = [];
                $("#optHistory").html("");
                if (data != null) {
                    data.forEach(function(row, index) {
                        show.push("<div class='process_list'>");
                        show.push("    <span class='circle '>·</span>");
                        show.push("    <ul class='base_list'>");
                        show.push("        <li>");
                        show.push("            <div class='list_center'>");
                        show.push("                <span class='worker'>" + row.ctime + "</span>")
                        show.push("                <span class='identity'>" + row.operateName + "(" + row.username + ")</span>");
                        show.push("            </div>");
                        show.push("        </li>");
                        show.push("    </ul>");
                        show.push("    <span class='line'></span>")
                        show.push("</div>");
                    });
                    $("#optHistory").append(show.join(""));
                }
            }
        });
    }

    // 峰值带宽折线图
    function loadPeakBandwidth() {
        var peakBandwidth = echarts.init(document.getElementById('peakBandwidth'));

        var timeData = [
            '00:00','01:00','02:00','03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00'
            ,'13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00','23:00','24:00'
        ];

        var option = {
            title: {
                text: '峰值带宽折线图',
                x: 'left'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    animation: false
                }
            },
            legend: {
                data:['上网时长','使用量(M)'],
                x: 'center'
            },
            axisPointer: {
                link: {xAxisIndex: 'all'}
            },
            dataZoom: [
                {
                    show: true,
                    realtime: true,
                    start: 30,
                    end: 70,
                    xAxisIndex: [0, 1]
                },
                {
                    type: 'inside',
                    realtime: true,
                    start: 30,
                    end: 70,
                    xAxisIndex: [0, 1]
                }
            ],
            grid: [{
                left: 50,
                right: 50,
                height: '25%'
            }, {
                left: 50,
                right: 50,
                top: '55%',
                height: '25%'
            }],
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    axisLine: {onZero: true},
                    data: timeData
                },
                {
                    gridIndex: 1,
                    type : 'category',
                    boundaryGap : false,
                    axisLine: {onZero: true},
                    data: timeData,
                    position: 'top'
                }
            ],
            yAxis : [
                {
                    name : '上网时长',
                    type : 'value',
                    max : 500
                },
                {
                    gridIndex: 1,
                    name : '使用量(M)',
                    type : 'value',
                    inverse: true
                }
            ],
            series : [
                {
                    name:'上网时长',
                    type:'line',
                    symbolSize: 8,
                    hoverAnimation: false,
                    data:[
                        10.97,10.97,10.94,10.02,25.95,10.4,10.94,10.94,171.14,10.99,50.98,20.09,11.15,11.04,10.94,100.97,10.94,10.99,60.87,10.96,11.03,21.09,10.97,10.91,10.98
                    ]
                },
                {
                    name:'使用量(M)',
                    type:'line',
                    xAxisIndex: 1,
                    yAxisIndex: 1,
                    symbolSize: 8,
                    hoverAnimation: false,
                    data: [
                        0.002,0.002,0.002,0.002,0.002,0.017,0.005,0.013,0.015,0.009,0.010,0.014,0.017,0.008,0.012,0.007,0.002,0.009,0.016,0.011,0.019,0.010,0.008,0.010,0.005
                    ]
                }
            ]
        };

        peakBandwidth.setOption(option);
    }

    // 用户上网时间段特征
    function loadInternetFeatures() {
        var internetFeatures = echarts.init(document.getElementById('internetFeatures'));

        var hours = ['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00',
            '07:00', '08:00', '09:00','10:00','11:00',
            '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
            '18:00', '19:00', '20:00', '21:00', '22:00', '23:00','24:00'];
        var days = ['星期一', '星期二','星期三','星期四','星期五','星期六','星期日'];

        var data = [
            [0,0,5],[0,1,1],[0,2,0],[0,3,0],[0,4,0],[0,5,0],[0,6,0],[0,7,0],[0,8,0],[0,9,0],[0,10,0],[0,11,2],[0,12,4],[0,13,1],[0,14,1],[0,15,3],[0,16,4],[0,17,6],[0,18,4],[0,19,4],[0,20,3],[0,21,3],[0,22,2],[0,23,5],
            [1,0,7],[1,1,0],[1,2,0],[1,3,0],[1,4,0],[1,5,0],[1,6,0],[1,7,0],[1,8,0],[1,9,0],[1,10,5],[1,11,2],[1,12,2],[1,13,6],[1,14,9],[1,15,8],[1,16,6],[1,17,7],[1,18,8],[1,19,5],[1,20,5],[1,21,5],[1,22,7],[1,23,2],
            [2,0,1],[2,1,1],[2,2,0],[2,3,0],[2,4,0],[2,5,0],[2,6,0],[2,7,0],[2,8,0],[2,9,0],[2,10,3],[2,11,2],[2,12,1],[2,13,7],[2,14,8],[2,15,8],[2,16,6],[2,17,5],[2,18,5],[2,19,5],[2,20,7],[2,21,4],[2,22,2],[2,23,4],
            [3,0,7],[3,1,3],[3,2,0],[3,3,0],[3,4,0],[3,5,0],[3,6,0],[3,7,0],[3,8,1],[3,9,0],[3,10,5],[3,11,4],[3,12,7],[3,13,8],[3,14,9],[3,15,10],[3,16,9],[3,17,5],[3,18,5],[3,19,10],[3,20,6],[3,21,4],[3,22,4],[3,23,1],
            [4,0,1],[4,1,3],[4,2,0],[4,3,0],[4,4,0],[4,5,1],[4,6,0],[4,7,0],[4,8,0],[4,9,2],[4,10,4],[4,11,4],[4,12,2],[4,13,4],[4,14,4],[4,15,10],[4,16,9],[4,17,1],[4,18,8],[4,19,5],[4,20,3],[4,21,7],[4,22,3],[4,23,0],
            [5,0,2],[5,1,1],[5,2,0],[5,3,3],[5,4,0],[5,5,0],[5,6,0],[5,7,0],[5,8,2],[5,9,0],[5,10,4],[5,11,1],[5,12,5],[5,13,10],[5,14,5],[5,15,7],[5,16,10],[5,17,6],[5,18,0],[5,19,5],[5,20,3],[5,21,4],[5,22,2],[5,23,0],
            [6,0,1],[6,1,0],[6,2,0],[6,3,0],[6,4,0],[6,5,0],[6,6,0],[6,7,0],[6,8,0],[6,9,0],[6,10,1],[6,11,0],[6,12,2],[6,13,1],[6,14,3],[6,15,4],[6,16,0],[6,17,0],[6,18,0],[6,19,0],[6,20,1],[6,21,2],[6,22,2],[6,23,6]
        ];

        var option = {
            tooltip: {
                position: 'top',
                formatter: function (params) {
                    return '上网次数:' + params.value[1];
                }
            },
            title: [],
            singleAxis: [],
            series: []
        };

        echarts.util.each(days, function (day, idx) {
            option.title.push({
                textBaseline: 'middle',
                top: (idx + 0.5) * 100 / 7 + '%',
                text: day
            });
            option.singleAxis.push({
                left: 150,
                type: 'category',
                boundaryGap: false,
                data: hours,
                top: (idx * 100 / 7 + 5) + '%',
                height: (100 / 7 - 10) + '%',
                axisLabel: {
                    interval: 2
                }
            });
            option.series.push({
                singleAxisIndex: idx,
                coordinateSystem: 'singleAxis',
                type: 'scatter',
                data: [],
                symbolSize: function (dataItem) {
                    return dataItem[1] * 4;
                }
            });
        });

        echarts.util.each(data, function (dataItem) {
            option.series[dataItem[0]].data.push([dataItem[1], dataItem[2]]);
        });

        internetFeatures.setOption(option);
    }

    // 用户行为喜好
    function loadUserPreference() {
        var userPreference = echarts.init(document.getElementById('userPreference'));

        var option = {
            title: {
                text: '用户行为喜好',
                x: 'left'
            },
            tooltip: {
                trigger: 'item',
                triggerOn: 'mousemove'
            },
            series: [{
                name: '行为喜好',
                type: 'treemap',
                data: [{
                    name: '通讯',
                    value: 10,
                    children: [{
                        name: '通讯',
                        value: 10
                    }]
                }, {
                    name: '财经',
                    value: 10,
                    children: [{
                        name: '财经',
                        value: 10
                    }]
                }, {
                    name: '新闻',
                    value: 30,
                    children: [{
                        name: '新闻',
                        value: 30
                    }]
                }, {
                    name: '体育',
                    value: 10,
                    children: [{
                        name: '体育',
                        value: 10
                    }]
                }, {
                    name: '视频',
                    value: 15,
                    children: [{
                        name: '视频',
                        value: 15
                    }]
                }, {
                    name: '娱乐',
                    value: 20,
                    children: [{
                        name: '娱乐',
                        value: 20
                    }]
                }, {
                    name: '国际',
                    value: 10,
                    children: [{
                        name: '国际',
                        value: 10
                    }]
                }, {
                    name: '美食',
                    value: 15,
                    children: [{
                        name: '美食',
                        value: 15
                    }]
                }, {
                    name: '音乐',
                    value: 20,
                    children: [{
                        name: '音乐',
                        value: 20
                    }]
                }, {
                    name: '旅游',
                    value: 20,
                    children: [{
                        name: '旅游',
                        value: 20
                    }]
                }]
            }]
        };
        userPreference.setOption(option);
    }

    // DPI数据UA终端分析
    function loadDPIData() {
        var DPIData = echarts.init(document.getElementById('DPIData'));

        DPIData.showLoading();


        var data1 = {
            "name": "操作系统",
            "children": [
                {
                    "name": "WINDOWS",
                    "children": [
                        {
                            "name": "Windows XP",
                            "value": 1576
                        },
                        {
                            "name": "Windows Vista",
                            "value": 3322
                        },
                        {
                            "name": "Windows 7",
                            "value": 2209
                        },
                        {
                            "name": "Windows 10",
                            "value": 4907
                        }
                    ]
                },
                {
                    "name": "IOS",
                    "children": [
                        {"name": "ios7", "value": 8833},
                        {"name": "ios8", "value": 1732},
                        {"name": "ios9", "value": 3623},
                        {"name": "ios10", "value": 9887}
                    ]
                },
                {
                    "name": "ANDRIOD",
                    "children": [
                        {"name": "Android 7.0 -Nougat", "value": 4116},
                        {"name":"Android 8.0 -Oreo","value": 3554},
                        {"name":"Android 9.0 -Pie","value": 5687},
                        {"name":"Android 10.0","value": 7988}
                    ]
                }
            ]
        };

        var data2 = {
            "name": "浏览器",
            "children": [
                {
                    "name": "IE",
                    "children": [
                        {"name": "IE 6", "value": 4116},
                        {"name": "IE 7", "value": 4116},
                        {"name": "IE 8", "value": 4116},
                        {"name": "IE 9", "value": 4116}
                    ]
                },
                {
                    "name": "Chrome",
                    "children": [
                        {"name": "Chrome 54.0.2840.59", "value": 2105},
                        {"name": "Chrome 55.0.2883.87", "value": 1316},
                        {"name": "Chrome 62.0.3192.0", "value": 3151},
                        {"name": "Chrome 71.0.3573.0", "value": 3770}
                    ]
                },
                {
                    "name": "火狐",
                    "children": [
                        {"name": "Firefox 64.0.1", "value": 8833},
                        {"name": "Firefox 65.0", "value": 4533},
                        {"name": "Firefox 66.0", "value": 9876},
                        {"name": "Firefox 67.0", "value": 6754}
                    ]
                }
            ]
        };

        DPIData.hideLoading();

        DPIData.setOption(option = {
            tooltip: {
                trigger: 'item',
                triggerOn: 'mousemove'
            },
            title: {
                text: 'DPI数据UA终端分析',
                x: 'left'
            },
            // legend: {
            //     top: '2%',
            //     left: '3%',
            //     orient: 'vertical',
            //     data: [{
            //         name: 'tree1',
            //         icon: 'rectangle'
            //     } ,
            //         {
            //             name: 'tree2',
            //             icon: 'rectangle'
            //         }],
            //     borderColor: '#c23531'
            // },
            series:[
                {
                    type: 'tree',

                    name: 'tree1',

                    data: [data1],

                    top: '5%',
                    left: '10%',
                    bottom: '2%',
                    right: '60%',

                    symbolSize: 7,

                    label: {
                        normal: {
                            position: 'left',
                            verticalAlign: 'middle',
                            align: 'right'
                        }
                    },

                    leaves: {
                        label: {
                            normal: {
                                position: 'right',
                                verticalAlign: 'middle',
                                align: 'left'
                            }
                        }
                    },

                    expandAndCollapse: true,

                    animationDuration: 550,
                    animationDurationUpdate: 750

                },
                {
                    type: 'tree',
                    name: 'tree2',
                    data: [data2],

                    top: '20%',
                    left: '60%',
                    bottom: '22%',
                    right: '18%',

                    symbolSize: 7,

                    label: {
                        normal: {
                            position: 'left',
                            verticalAlign: 'middle',
                            align: 'right'
                        }
                    },

                    leaves: {
                        label: {
                            normal: {
                                position: 'right',
                                verticalAlign: 'middle',
                                align: 'left'
                            }
                        }
                    },

                    expandAndCollapse: true,

                    animationDuration: 550,
                    animationDurationUpdate: 750
                }
            ]
        });

    }

    // 五个饼图
    function loadFiveBin() {
        
        var account = $("#account").val();
        var data;
        if(account=="" || account==null) {
            data = fiveBinDefaultVal();
            createFiveBin(data);
        } else {
            $.ajax({
                url : project.ctxPath + "/data/userFigure/characterHealth.do",
                data : {
                    account : account
                },
                success : function(retdata) {
                    var data = JSON.parse(retdata);
                    var length = data.data.length;
                    var binData;
                    if(length == 0) {
                        binData = fiveBinDefaultVal();
                    } else {
                        binData = fiveBinInitVal(data.data);
                    }
                    createFiveBin(binData);
                }
            });
        }
    }
    
    function fiveBinInitVal(resultData) {
        var data = new Array();
        for(var i=0;i<resultData.length;i++){
            var obj = new Object();
            obj.name = resultData[i].name;
            obj.value = resultData[i].value;
            data.push(obj);
        }
        return data;
    }
    
    function fiveBinDefaultVal() {
        var data = [
            {
                name: '流量使用',
                value: 0
            },{
                name: '上网线路',
                value: 0
            },{
                name: '家庭终端',
                value: 0
            },{
                name: '行为特征',
                value: 0
            },{
                name: '投诉告障',
                value: 0
            }];
        return data;
    }
    
    function createFiveBin(data) {
        echarts.dispose(document.getElementById('fiveBin'));
        var fiveBin = echarts.init(document.getElementById('fiveBin'));
        var titleArr= [], seriesArr=[];
        colors=[['#389af4', '#dfeaff'],['#ff8c37', '#ffdcc3'],['#ffc257', '#ffedcc'], ['#fd6f97', '#fed4e0'],['#a181fc', '#e3d9fe']]
        data.forEach(function(item, index){
            titleArr.push(
                {
                    text:item.name,
                    left: index * 20 + 9.5 +'%',
                    top: '90%',
                    textAlign: 'center',
                    textStyle: {
                        fontWeight: 'normal',
                        fontSize: '16',
                        color: colors[index][0],
                        textAlign: 'center'
                    }
                }
            );
            seriesArr.push(
                {
                    name: item.name,
                    type: 'pie',
                    clockWise: false,
                    radius: [60, 70],
                    itemStyle:  {
                        normal: {
                            color: colors[index][0],
                            shadowColor: colors[index][0],
                            shadowBlur: 0,
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            }
                        }
                    },
                    hoverAnimation: false,
                    center: [index * 20 + 10 +'%', '50%'],
                    data: [{
                        value: item.value,
                        label: {
                            normal: {
                                formatter: function(params){
                                    return params.value+'分';
                                },
                                position: 'center',
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold',
                                    color: colors[index][0]
                                }
                            }
                        }
                    }, {
                        value: 100-item.value,
                        name: 'invisible',
                        itemStyle: {
                            normal: {
                                color: colors[index][1]
                            },
                            emphasis: {
                                color: colors[index][1]
                            }
                        }
                    }]
                }
            )
        });
        var option = {
            backgroundColor: "#fff",
            title:titleArr,
            series: seriesArr
        };
        fiveBin.setOption(option);
    }
    
    
    // 用户线路拓扑图
    function loadLineTopology() {
        var lineTopology = echarts.init(document.getElementById('lineTopology'));
        var imgPath = 'image://' + project.staticPath + '/img/'
        var allData = {
        	    "nodes": [{
        	            "name": "终端",
        	            "value": [100, 150],
        	            symbol: imgPath + 'terminal.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "路由器",
        	            "value": [250, 150],
        	            symbol: imgPath + 'router.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "ONU",
        	            "value": [400, 150],
        	            symbol: imgPath + 'onu.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "分光器",
        	            "value": [550, 150],
        	            symbol: imgPath + 'splitter.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "OLT",
        	            "value": [700, 150],
        	            symbol: imgPath + 'olt.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "BRAS",
        	            "value": [850, 150],
        	            symbol: imgPath + 'bras.png',
        	            "symbolSize": [60, 60]
        	        },
        	        {
        	            "name": "",
        	            "value": [1050, 150],
        	            symbol: imgPath + 'histogram.png',
        	            "symbolSize": [180, 150]
        	        }
        	    ],
        	    
        	    "moveLines": [{
        	            "fromName": "终端",
        	            "toName": "路由器",
        	            "coords": [
        	                [130, 150],
        	                [220, 150]
        	            ]
        	        },{
        	            "fromName": "路由器",
        	            "toName": "ONU",
        	            "coords": [
        	                [280, 150],
        	                [370, 150]
        	            ]
        	        },{
        	            "fromName": "ONU",
        	            "toName": "分光器",
        	            "coords": [
        	                [430, 150],
        	                [520, 150]
        	            ]
        	        },{
        	            "fromName": "分光器",
        	            "toName": "OLT",
        	            "coords": [
        	                [580, 150],
        	                [670, 150]
        	            ]
        	        },{
        	            "fromName": "OLT",
        	            "toName": "BRAS",
        	            "coords": [
        	                [730, 150],
        	                [820, 150]
        	            ]
        	        },{
        	            "fromName": "BRAS",
        	            "toName": "",
        	            "coords": [
        	                [880, 150],
        	                [970, 150]
        	            ]
        	        }
        	    ],
        	};

        	option = {
        	    tooltip: {
        	        trigger: 'item',
        	        show: true,
        	        formatter: function(params, ticket, callback) {
        	            console.log(params)
        	            if (params.componentSubType === 'scatter') {
        	                return params.name
        	            } else if (params.componentSubType === 'lines') {
        	                return '从' + params.data.fromName + '到' + params.data.toName
        	            }
        	        }
        	    },
        	    geo: {
        	    },
        	    series: [{
        	            type: 'scatter',
        	            coordinateSystem: 'geo',
        	            label: {
        	                normal: {
        	                    show: true,
        	                    formatter: '{b}',
        	                    color: '#000',
        	                    position: 'bottom'
        	                },
        	                emphasis: {
        	                    show: true,
        	                    formatter: '{b}',
        	                    position: 'bottom'
        	                }
        	            },
        	            itemStyle: {
        	                normal: {
        	                    color: '#46bee9'
        	                }
        	            },
        	            data: allData.nodes
        	        },
        	        {
        	            name: '线路',
        	            type: 'lines',
        	            coordinateSystem: 'geo',
        	            zlevel: 2,
        	            large: true,
        	            effect: {
        	                show: true,
        	                constantSpeed: 30,
        	                symbol: 'arrow',
        	                symbolSize: 6,
        	                trailLength: 0,
        	            },

        	            lineStyle: {
        	                normal: {
        	                    color: '#0066FF',
        	                    width: 2,
        	                    opacity: 0.6,
        	                    curveness: 0
        	                }
        	            },
        	            data: allData.moveLines
        	        }
        	    ]
        	};
        
        lineTopology.setOption(option);
        
        lineTopology.on('click', function (param) {
        	if(param.name == 'OLT' ){
        		var account = $("#account").val();
        		if (account == '' || account.length == 0) {
        			layer.msg('请输入宽带账号',{
                        time:1000
                    });
        		}else{
        			$.ajax({
                        url : project.ctxPath + "/data/oltEquipment/judgeExistByAccount.do",
                        data : {
                            account : account
                        },
                        success : function(retdata) {
                            if (retdata == null || retdata.length == 0) {
                            	layer.msg('该宽带账号没有OLT信息',{
                                    time:1000
                                });
                                return;
                            }
                            window.open(project.ctxPath + "/oltEquipmentInfo.do?oltip="+retdata);
                        }
                    });
        		}
        	}
          });
    }
});
