var chartshow = (function() {
	var precolor = [ "#4ba4f5", "#FF7F50", "#e0f7ff", "#2EADA4", "#D2E593",
			"#7DC9E1", "#FCD0BD", "#F39F8F", "#AFA9B3", "#AA6F8B", "#00FA9A",
			"#87CEFA", "#A6E930" ];
	return {
		provinceMap : provinceMap,// 省份地图样式
		horizontalBar : horizontalBar,// 横向堆积柱状图
		singleLine : singleLine,// 单折线图
		singleAreaLine : singleAreaLine,// 单个面积图
		singleCirque : singleCirque,// 单个圆环图
		doubleCirque : doubleCirque,// 双个圆环图
		singleVerticalBar : singleVerticalBar,// 单个纵向条形图
		upDownLine : upDownLine,// 上下折线图
		upDownBar : upDownBar,// 上下柱状图
		multipleLine : multipleLine,// 多折线图
		multipleVerticalBar : multipleVerticalBar,// 多纵向柱状图
		verticalBar : verticalBar,
		oltLine : oltLine, // olt折线图
		oltMap : oltMap,// OLT地图分布样式
		alarmLine : alarmLine,// 告警折线图
		alarmThresholdLine : alarmThresholdLine,
		showNoDataLoading : showNoDataLoading,
		alarmThresholdForecastLine:alarmThresholdForecastLine,
		alarmThresholdFunLine:alarmThresholdFunLine,
	};
	function verticalBar(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : [],
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : []
			},
			grid : {
				left : '1%',
				right : '1%',
				bottom : '1%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				data : [],
			},
			yAxis : {
				type : 'value',
				show : true
			},
			series : []
		};
		for (var i = 0; i < data.length; i++) {
			option.color.push(color[i]);
			option.legend.data.push(data[i].title);
			option.series.push({
				name : data[i].title,
				type : 'bar',
				stack : '总量',
				barWidth : 12,
				data : []
			});
			for (var j = 0; j < data[i].data.length; j++) {
				if (i == 0) {
					option.xAxis.data.push(data[i].data[j].name);
				}
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function upDownBar(data, cuscolor) {// data需包含3组数据
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : []
			},
			grid : {
				left : '1%',
				right : '1%',
				bottom : '0%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				axisTick : {
					show : false
				},
				data : []
			},
			yAxis : {
				type : 'value'
			},
			series : [ {
				name : '',
				type : 'bar',
				barMaxWidth : 30,
				label : {
					normal : {
						show : true,
						position : 'top'
					}
				},
				itemStyle : {
					normal : {
						color : '#2ec7c9',
						lineStyle : {
							color : '#2ec7c9'
						}
					}
				},
				data : []
			}, {
				name : '',
				type : 'bar',
				stack : '总量',
				barMaxWidth : 5,
				label : {
					normal : {
						show : true,
						position : 'bottom'
					}
				},
				itemStyle : {
					normal : {
						color : '#4eadef',
						lineStyle : {
							color : '#4eadef'
						}
					}
				},
				data : []
			}, {
				name : '',
				type : 'bar',
				stack : '总量',
				label : {
					normal : {
						show : true,
						position : 'top'
					}
				},
				itemStyle : {
					normal : {
						color : '#b29ddd',
						lineStyle : {
							color : '#b29ddd'
						}
					}
				},
				barWidth : '10%',
				data : []
			} ]
		};
		for (var i = 0; i < data.length; i++) {
			option.legend.data.push(data[i].title);
			option.series[i].name = data[i].title;
			for (var j = 0; j < data[i].data.length; j++) {
				if (i == 0) {
					option.xAxis.data.push(data[i].data[j].name);
				}
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function multipleVerticalBar(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : [ '#4ba4f5', "#2EADA4", "#D2E593", "#7DC9E1", "#FCD0BD",
					"#F39F8F", "#AFA9B3", "#AA6F8B", "#00FA9A", "#87CEFA",
					"#FF7F50", "#A6E930" ],
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'shadow'
				}
			},
			grid : {
				left : '1%',
				right : '1%',
				bottom : '0%',
				containLabel : true
			},
			legend : {
				data : []
			},
			calculable : true,
			xAxis : {
				type : 'category',
				axisTick : {
					show : false
				},
				data : []
			},
			yAxis : {
				type : 'value'
			},
			series : []
		};
		for (var i = 0; i < data.length; i++) {
			option.legend.data.push(data[i].title);
			option.series.push({
				name : data[i].title,
				type : 'bar',
				barMaxWidth : 30,
				data : []
			});
			for (var j = 0; j < data[i].data.length; j++) {
				if (i == 0) {
					option.xAxis.data.push(data[i].data[j].name);
				}
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function multipleLine(data, cuscolor, unit) {
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : [],
				y : 'top'
			},
			grid : {
				left : '3%',
				right : '4%',
				bottom : '0%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				data : []
			},
			yAxis : {
				type : 'value',
				axisLabel : {
					formatter : '{value}' + (unit == null ? '' : unit)
				}
			},
			series : []
		};
		for (var i = 0; i < data.length; i++) {
			option.legend.data.push(data[i].title);
			option.series.push({
				name : data[i].title,
				type : 'line',
				smooth : true,
				data : []
			});
			for (var j = 0; j < data[i].data.length; j++) {
				if (i == 0) {
					option.xAxis.data.push(data[i].data[j].name);
				}
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function upDownLine(data, cuscolor) {
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					animation : false
				}
			},
			legend : {
				data : [],
				x : 'center'
			},
			axisPointer : {
				link : {
					xAxisIndex : 'all'
				}
			},
			grid : [ {
				left : 100,
				right : 50,
				height : '35%'
			}, {
				left : 100,
				right : 50,
				top : '55%',
				height : '35%'
			} ],
			xAxis : [ {
				type : 'category',
				boundaryGap : false,
				axisLine : {
					onZero : true
				},
				data : []
			}, {
				gridIndex : 1,
				type : 'category',
				boundaryGap : false,
				axisLine : {
					onZero : true
				},
				data : [],
				position : 'top'
			} ],
			yAxis : [ {
				name : '流量(MB)',
				type : 'value',
			}, {
				gridIndex : 1,
				name : '流量(MB)',
				type : 'value',
				inverse : true
			} ],
			series : []
		};
		for (var i = 0; i < data.length; i++) {
			option.legend.data.push(data[i].title);
			if (i == 0) {
				option.series.push({
					name : data[i].title,
					type : 'line',
					symbolSize : 8,
					hoverAnimation : false,
					data : []
				});
			}
			if (i == 1) {
				option.series.push({
					name : data[i].title,
					type : 'line',
					smooth : true,
					xAxisIndex : 1,
					yAxisIndex : 1,
					symbolSize : 8,
					hoverAnimation : false,
					data : []
				});
			}
			for (var j = 0; j < data[i].data.length; j++) {
				option.xAxis[i].data.push(data[i].data[j].name);
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function singleVerticalBar(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : [ color[0] ],
			title : {
				text : data.title,
				textStyle : {
					fontSize : 14
				},
				x : 'left'
			},
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			grid : {
				left : '-10%',
				right : '4%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				data : [],
				axisTick : {
					show : false
				}
			},
			yAxis : {
				axisLine : {
					show : false
				},
				axisTick : {
					show : false
				},
				axisLabel : { // 这个是隐藏Y轴的数值
					show : false
				},
				splitLine : {
					show : false
				}
			},
			series : [ {
				name : data.title,
				type : 'bar',
				barWidth : '40%',
				data : []
			} ]
		};
		data.data.forEach(function(row) {
			option.xAxis.data.push(row.name);
			option.series[0].data.push(row.value);
		});
		return option;
	}
	function singleCirque(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : color,
			title : {
				text : data.title,
				textStyle : {
					fontSize : 14
				},
				x : 'left'
			},
			tooltip : {
				trigger : 'item',
				formatter : "{a} <br/>{b}: {c} ({d}%)"
			},
			legend : {
				orient : 'horizontal',
				x : 'center',
				y : 'bottom',
				data : [],
				show : false
			},
			series : [ {
				name : data.title,
				type : 'pie',
				radius : [ '40%', '60%' ],
				itemStyle : {
					normal : {
						label : {
							show : true,
							formatter : [ '{title|{b}}{abg|}', '{per|{d}%}' ]
									.join('\n'),
							backgroundColor : '#eee',
							borderColor : '#aaa',
							borderWidth : 1,
							borderRadius : 4,
							rich : {
								title : {
									color : '#eee',
									align : 'center',
									padding : [ 4, 4 ]
								},
								abg : {
									backgroundColor : '#333',
									width : '100%',
									align : 'right',
									height : 25,
									borderRadius : [ 4, 4, 0, 0 ]
								},
								per : {
									// backgroundColor: '#333',
									color : '#333',
									height : 24,
									align : 'center'
								}
							}
						},
						labelLine : {
							show : true
						}
					}
				},
				data : data.data
			} ]
		};
		data.data.forEach(function(row) {
			option.legend.data.push(row.name);
		});
		return option;
	}
	function singleLine(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			title : {
				text : data.title,
				textStyle : {
					fontSize : 14
				},
				x : 'left'
			},
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'line',
					label : {
						backgroundColor : '#6a7985'
					}
				}
			},
			grid : {
				left : '-8%',
				right : '4%',
				bottom : '6%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				boundaryGap : false,
				data : [],
				axisTick : {
					show : false
				},
				axisLabel : {
					interval : 0,// 横轴信息全部显示
					rotate : -50,// -30度角倾斜显示
				}
			},
			yAxis : {
				axisLine : {
					show : false
				},
				axisTick : {
					show : false
				},
				axisLabel : { // 这个是隐藏Y轴的数值
					show : false
				},
				splitLine : {
					show : false
				}
			},
			series : [ {
				name : data.title,
				data : [],
				type : 'line',
				smooth : true,
				itemStyle : {
					normal : {
						color : color[0],
						lineStyle : {
							color : color[0]
						}

					}
				},
				areaStyle : {
					normal : {
						color : color[1]
					}
				}
			} ]
		};
		data.data.forEach(function(row) {
			option.xAxis.data.push(row.name);
			option.series[0].data.push(row.value);
		});
		return option;
	}
	function singleAreaLine(data, cuscolor, unit) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			title : {
				text : data.title,
				textStyle : {
					fontSize : 14
				},
				x : 'left'
			},
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'line'
				},
				formatter : "{b} <br/>{a}: {c}" + (unit == null ? '%' : unit)
			},
			grid : {
				left : '-5%',
				right : '4%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				boundaryGap : false,
				data : [],
				axisTick : {
					show : false
				}
			},
			yAxis : {
				axisLine : {
					show : false
				},
				axisTick : {
					show : false
				},
				axisLabel : { // 这个是隐藏Y轴的数值
					show : false
				},
				splitLine : {
					show : false
				}
			},
			series : [ {
				name : data.title,
				data : [],
				type : 'line',
				smooth : true,
				itemStyle : {
					normal : {
						lineStyle : {
							color : color[0]
						}
					}
				},
				areaStyle : {
					normal : {
						color : color[1]
					}
				}
			} ]
		};
		data.data.forEach(function(row) {
			option.xAxis.data.push(row.name);
			option.series[0].data.push(row.value);
		});
		return option;
	}
	function provinceMap(data, cuscolor) {
		var color = (cuscolor == null ? [ "#4ba4f5", "#e0f7ff" ] : cuscolor);
		var max = getMax(data.data);
		var option = {
			dataRange : {
				x : 'right',
				min : 0,
				max : max,
				color : color,
				itemWidth : 15, // 值域图形宽度，线性渐变水平布局宽度为该值 * 10
				itemHeight : 100,
				text : [ '单位：个', '' ], // 文本，默认为数值文本
				calculable : true
			},
			tooltip : {
				trigger : 'item'
			},
			series : [ {
				layoutCenter : [ '50%', '48%' ],
				layoutSize : 480,
				name : data.title,
				type : 'map',
				mapType : 'province',
				zoom : 1,
				selectedMode : 'single',
				itemStyle : {
					normal : {
						areaColor : '#323c49',
						borderWidth : 1,
						borderColor : '#8585aa',
						label : {
							show : true
						}
					},
					emphasis : {
						areaColor : '#ffed00',
						label : {
							show : true
						}
					}
				},
				lineStyle : {
					normal : {
						color : 'black',
						type : 'dashed'
					}

				},
				data : data.data
			} ]
		};
		return option;
	}
	function getMax(data) {// 求数组的最大值
		var max = parseInt(data[0].value);
		for (var i = 0; i < data.length; i++) {
			var value = parseInt(data[i].value)
			if (value > max) {
				max = value;
			}
		}
		if (max == 0) {
			return 100;
		}
		return max;
	}
	function horizontalBar(data, cuscolor) {// 横向柱状图
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : [],
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				},
			},
			legend : {
				data : []
			},
			grid : {
				left : '1%',
				// right : '-11%',
				bottom : '1%',
				containLabel : true
			},
			xAxis : {
				type : 'value',
				show : false,
			},
			yAxis : {
				type : 'category',
				data : [],
				axisLabel : {
					formatter : function(value) {
						var res = value;
						if (res.length > 20) {
							res = res.substring(0, 20) + "...";
						}
						return res;
					}
				},
				axisLine : {
					show : false
				},
				axisTick : {
					show : false
				}
			},
			series : []
		};
		for (var i = 0; i < data.length; i++) {
			option.color.push(color[i]);
			option.legend.data.push(data[i].title);
			option.series.push({
				name : data[i].title,
				type : 'bar',
				stack : '总量',
				barWidth : 12,
				data : []
			});
			for (var j = 0; j < data[i].data.length; j++) {
				if (i == 0) {
					option.yAxis.data.push(data[i].data[j].name);
				}
				option.series[i].data.push(data[i].data[j].value);
			}
		}
		return option;
	}
	function doubleCirque(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			color : color,
			title : {
				text : "用户智能网关结构",
				textStyle : {
					fontSize : 14
				},
				x : 'left'
			},
			tooltip : {
				trigger : 'item',
				// formatter:function(data) {
				// return data.name + ":" + data.value + "<br/>" +
				// data.percent.toFixed(5) + "%)";
				// }
				formatter : "{b}: {c}<br/> {d}%"
			},
			series : [
					{
						name : '质差设备',
						type : 'pie',
						selectedMode : 'single',
						radius : [ 0, '30%' ],

						label : {
							normal : {
								position : 'inner'
							}
						},
						labelLine : {
							normal : {
								show : false
							}
						},
						data : data.innerData
					},
					{
						name : '访问来源',
						type : 'pie',
						radius : [ '41%', '61%' ],
						label : {
							normal : {
								formatter : [ '{title|{b}}{abg|}', '{per|{d}%}' ]
										.join('\n'),
								backgroundColor : '#eee',
								borderColor : '#aaa',
								borderWidth : 1,
								borderRadius : 4,
								rich : {
									title : {
										color : '#eee',
										align : 'center',
										padding : [ 4, 4 ]
									},
									abg : {
										backgroundColor : '#333',
										width : '100%',
										align : 'right',
										height : 25,
										borderRadius : [ 4, 4, 0, 0 ]
									},
									per : {
										// backgroundColor: '#333',
										color : '#333',
										height : 24,
										align : 'center'
									}
								}
							}
						},
						data : data.outerData
					} ]
		};
		return option;
	}
	function oltLine(data, cuscolor) {
		var color = (cuscolor == null ? precolor : cuscolor);
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'line',
					label : {
						backgroundColor : '#6a7985'
					}
				}
			},
			grid : {
				left : '2%',
				right : '2%',
				bottom : '15%',
				containLabel : true
			},
			xAxis : {
				type : 'category',
				data : data.xData,
				axisLabel : {
					// interval : 0,// 横轴信息全部显示
					rotate : -10
				// -30度角倾斜显示
				}
			},
			yAxis : {
				type : 'value',
				axisLabel : { // 这个是隐藏Y轴的数值
					show : true
				}
			},
			noDataLoadingOption : {
				effect : 'bubble',
				effectOption : {
					effect : {
						n : 30
					}
				},
				text : '暂无数据',
				textStyle : {
					fontSize : 20
				}
			},
			series : [ {
				data : data.yData,
				type : 'line',
				smooth : true,
				itemStyle : {
					normal : {
						color : color[0],
						lineStyle : {
							color : color[0]
						}

					}
				},
				areaStyle : {
					normal : {
						color : color[1]
					}

				}
			} ]
		};
		return option;
	}

	function oltMap(data, cuscolor) {
		var option = {
			geo : {
				show : true,
				map : 'province',
				zoom : 1.1,
				silent : true,
				roam : true,
				itemStyle : {
					normal : {
						areaColor : 'transparent',
						borderColor : '#1A90CA',
						borderWidth : 3,
						shadowBlur : 30
					},
					emphasis : {
						areaColor : '#2B91B7',
					}
				}
			},
			series : [ {
				type : 'scatter',
				coordinateSystem : 'geo',
				symbolSize : function(val) {
					return val[2] / 10;
				}
			}, {
				type : 'map',
				roam : true
			}, {
				type : 'effectScatter',
				coordinateSystem : 'geo',
				data : convertData(data.sort(function(a, b) {
					return b.value - a.value;
				})),
				symbolSize : function(val) {
					return val[2] / 10;
				},
				rippleEffect : {
					brushType : 'stroke'
				},
				label : {
					normal : {
						formatter : '{b}',
						position : 'right',
						show : false
					},
					emphasis : {
						formatter : '{b}',
						position : 'right',
						color : '#66FF00',
						show : true
					}
				},
				itemStyle : {
					normal : {
						color : function(value) {
							if (value.value[3] == 1) {
								return "#F4E925";
							} else {
								return "#F4E925";
							}
						},
						shadowBlur : 5
					},
					emphasis : {
						color : '#66FF00',
						shadowBlur : 10
					}
				}
			}, ]

		};
		return option;
	}
	function convertData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			var geoCoord = new Array()
			geoCoord[0] = data[i].oltgeox
			geoCoord[1] = data[i].oltgeoy
			if (geoCoord) {
				res.push({
					name : data[i].oltip,
					value : geoCoord.concat(data[i].value).concat(
							data[i].isalarm)
				});
			}
		}
		return res;
	}

	function alarmLine(data, cuscolor, unit) {
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : [],
				y : 'top'
			},
			grid : {
				left : '8%',
				right : '4%',
				bottom : '8%',
				containLabel : false
			},
			xAxis : {
				type : 'category',
				data : convertXAxisData(data)
			},
			yAxis : {
				type : 'value',
				min : 'dataMin', // 最小值
				axisLabel : {
					formatter : '{value}' + (unit == null ? '' : unit)
				}
			},
			series : [ {
				type : 'line',
				showSymbol : false,
				hoverAnimation : false,
				animation : false,
				data : convertYAxisData(data),
				markPoint : {
					symbol : 'circle', // 设定为实心点
					symbolSize : 5, // 设定实心点的大小
					data : convertMarkPointData(data),
					itemStyle : {
						normal : {
							borderColor : '#FF0033',
							borderWidth : 5,
							label : {
								show : false
							}
						},
						emphasis : {
							label : {
								show : true,
								position : 'top', // 'left'|'right'|'top'|'bottom'
								color : '#FF0033',
								fontSize : 18,
							}
						}
					},
				},
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						color : "#0099FF"
					},
					lineStyle : {
						color : '#0099FF' // 改变折线颜色
					}
				},
			} ]
		};
		return option;
	}

	function convertXAxisData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			res.push(data[i].name);
		}
		return res;
	}

	function convertYAxisData(data,name) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			switch(name) {
		     case 'mapee':
		    	 res.push(data[i].mapee);
		        break;
		     case 'mae':
		    	 res.push(data[i].mae);
		        break;
		     case 'smape':
		    	 res.push(data[i].smape);
		        break;
		     case 'mape':
		    	 res.push(data[i].mape);
		        break;
		     case 'mase':
		    	 res.push(data[i].mase);
		        break;
		     default:
		    	 res.push(data[i].value);
			} 
		}
		return res;
	}

	function convertMarkPointData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			if (data[i].isalarm == 1) {
				var col; //点的颜色
				var text; //值为0时，展示无
				if (data[i].iscollection == 0) {
					col = 'green';
					text = '无';
				} else {
					col = 'red';
					text = data[i].value;
				}

				res.push({
					name: data[i].name,
					value: text,
					yAxis: data[i].value,
					xAxis: i,
					itemStyle: {color: col}
				});
			}
		}
		return res;
	}

	function pushCollectionData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			if (data[i].isalarm == 1) {
				if (data[i].iscollection == 0) {
					res.push({
						name: data[i].name,
						yAxis: data[i].value,
						xAxis: i,
					});
				}
			}
		}
		return res;
	}

	function showNoDataLoading() {
		var option = {
			text : '暂无数据',
			color : '#ffffff',
			textColor : '#8a8e91',
			maskColor : 'rgba(255, 255, 255, 0.8)',
		}
		return option;
	}

	function alarmThresholdLine(data, cuscolor, unit) {
		var yData = pushCollectionData(data);
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				},
				formatter: function (params) {
					var res='<div><p>'+params[0].name+'</p></div>';
					var flag = 0;
					for (var l in yData) {
						if(params[0].name == yData[l].name) {
							res += '<p>'+params[0].seriesName+': 无</p>';
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						res += '<p>'+params[0].seriesName+':'+params[0].data+'</p>'
					}
					return res;
				}
			},
			legend : {
				data : [],
				y : 'top'
			},
			grid : {
				left : '8%',
				right : '4%',
				bottom : '8%',
				containLabel : false
			},
			xAxis : {
				type : 'category',
				data : convertXAxisData(data)
			},
			yAxis : {
				type : 'value',
				min : 'dataMin', // 最小值
				axisLabel : {
					formatter : '{value}' + (unit == null ? '' : unit)
				},
				splitLine : {
					show : true,
				}
			// 保留网格区域
			},
			dataZoom : [
					{
						type : 'inside',
						start : 0,
						end : 100
					},
					{
						start : 0,
						end : 100,
						handleIcon : 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9zM13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
						handleSize : '80%',
						handleStyle : {
							color : '#fff',
							shadowBlur : 3,
							shadowColor : 'rgba(0, 0, 0, 0.6)',
							shadowOffsetX : 2,
							shadowOffsetY : 2
						},
						bottom:"-1%"
					} ],
			series : [
			{
				name : '在线用户数',
				type : 'line',
				symbolSize: 10,
				showSymbol : false,
				hoverAnimation : false,
				animation : false,
				data : convertYAxisData(data),
				markPoint : {
					symbol : 'circle', // 设定为实心点
					symbolSize : 8, // 设定实心点的大小
					data : convertMarkPointData(data),
					itemStyle : {
						normal : {
							borderWidth : 8,
							label : {
								show : false
							}
						},
						emphasis : {
							label : {
								show : true,
								position : 'top', // 'left'|'right'|'top'|'bottom'
								// color : '#FF0033',
								fontSize : 18
							}
						}
					}
				},
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						color : "#0099FF"
					},
					lineStyle : {
						color : '#0099FF' // 改变折线颜色
					}
				}
			}]
		};
		return option;
	}

	function convertYAxisUpperValueData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			res.push(data[i].upperValue);// item.u - item.l
		}
		return res;
	}

	function convertYAxisLowerValueData(data) {
		var res = [];
		for (var i = 0; i < data.length; i++) {
			res.push(data[i].lowerValue);// item.l + base
		}
		return res;
	}

	function alarmThresholdForecastLine(data, fdata, cuscolor, unit) {
		var yData = pushCollectionData(data);
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : ['原始数据', '预测数据'],
				y : 'top'
			},
			grid : {
				left : '8%',
				right : '4%',
				bottom : '8%',
				containLabel : false
			},
			xAxis : {
				type : 'category',
				data : convertXAxisData(data)
			},
			yAxis : {
				type : 'value',
				min : 'dataMin', // 最小值
				axisLabel : {
					formatter : '{value}' + (unit == null ? '' : unit)
				},
				splitLine : {
					show : true,
				}
			// 保留网格区域
			},
			dataZoom : [
					{
						type : 'inside',
						start : 0,
						end : 100
					},
					{
						start : 0,
						end : 100,
						handleIcon : 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9zM13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
						handleSize : '80%',
						handleStyle : {
							color : '#fff',
							shadowBlur : 3,
							shadowColor : 'rgba(0, 0, 0, 0.6)',
							shadowOffsetX : 2,
							shadowOffsetY : 2
						},
						bottom:"-1%"
					} ],
			series : [
			{
				name : '原始数据',
				type : 'line',
				symbolSize: 10,
				showSymbol : false,
				hoverAnimation : false,
				animation : false,
				data : convertYAxisData(data),
				markPoint : {
					symbol : 'circle', // 设定为实心点
					symbolSize : 8, // 设定实心点的大小
					data : convertMarkPointData(data),
					itemStyle : {
						normal : {
							borderWidth : 8,
							label : {
								show : false
							}
						},
						emphasis : {
							label : {
								show : true,
								position : 'top', // 'left'|'right'|'top'|'bottom'
								// color : '#FF0033',
								fontSize : 18
							}
						}
					}
				},
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						color : "#0099FF"
					},
					lineStyle : {
						color : '#0099FF' // 改变折线颜色
					}
				}
			},{
				name : '预测数据',
				type : 'line',
				symbolSize: 10,
				showSymbol : false,
				hoverAnimation : false,
				animation : false,
				data : convertYAxisData(fdata),
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						color : "#808080",
						lineStyle:{
							width:2,
							type:'dotted'  //'dotted'虚线 'solid'实线
		                }
					},
					lineStyle : {
						color : '#808080'// 改变折线颜色
					}
				}
			}]
		};
		return option;
	}
	
	
	function alarmThresholdFunLine(data,finityData,name,showname,cuscolor,unit) {
		var yData = pushCollectionData(data);
		var option = {
			tooltip : {
				trigger : 'axis',
				axisPointer : { // 坐标轴指示器，坐标轴触发有效
					type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend : {
				data : [],
				y : 'top'
			},
			grid : {
				left : '8%',
				right : '4%',
				bottom : '8%',
				containLabel : false
			},
			xAxis : {
				type : 'category',
				data : convertXAxisData(data)
			},
			yAxis : {
				type : 'value',
				min : 'dataMin', // 最小值
				axisLabel : {
					formatter : '{value}' + (unit == null ? '' : unit)
				},
				splitLine : {
					show : true,
				}
			// 保留网格区域
			},
			dataZoom : [
					{
						type : 'inside',
						start : 0,
						end : 100
					},
					{
						start : 0,
						end : 100,
						handleIcon : 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9zM13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
						handleSize : '80%',
						handleStyle : {
							color : '#fff',
							shadowBlur : 3,
							shadowColor : 'rgba(0, 0, 0, 0.6)',
							shadowOffsetX : 2,
							shadowOffsetY : 2
						},
						bottom:"-1%"
					} ],
			series : [
			{
				name : showname,
				type : 'line',
				symbolSize: 10,
				showSymbol : false,
				hoverAnimation : false,
				animation : false,
				data : convertYAxisData(data,name),
				itemStyle : {
					normal : {
						label : {
							show : true
						},
						color : cuscolor
					},
					lineStyle : {
						color : cuscolor
					}
				},
				markLine: {
	                silent: true,
	                symbol: ['none', 'none'],
	                lineStyle: {
	                    width: 2
	                },
	                data: [{
	                    yAxis: finityData,
	                    label: {
	                    	padding:[-13,-20,15,-100],
	                    	formatter: "告警线："+finityData
	                    },
	                    lineStyle: {
	                        color: 'red'
	                    }
	                }]
	            }
			}]
		};
		return option;
	}
})();