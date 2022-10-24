<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../../common/default.jsp"%>
<html lang="en">
<%@include file="../../common/head.jsp"%>
<head>
<meta charset="UTF-8">
<title>安徽移动宽带业务质量分析系统</title>
<link rel="stylesheet" href="${staticPath}/css/layui.css"  media="all">
<script src="${staticPath}/js/layui.all.js" charset="utf-8"></script>
<script src="${staticPath}/js/laypage.js" charset="utf-8"></script>
</head>
<body class="page_content" style="background-color:#004981;">
    <%@include file="../../common/navigation.jsp"%>
    <div class="index_center">
	    <div class="home_content clearfix">
	         <div class="home_left" style="width: 100%; float: left">
	            <div style="color:#fff;">
	                <div class="mb20">
	                   <b>OLT设备总数：</b><b id="totalNum">0</b>
	                   <b class="ml100">近24小时报警数量：</b><b id="alarmNumTFH">0</b>
	                   <b class="ml100">当前报警数量：</b><b id="curAlarmNum">0</b>
	                </div>
	                <div class="mb20">
	                   <b class="pr5">OLT地图分布</b><i class="iconfont icon-wenhao" title="详细描述"></i>
	                </div>
<%--	                <div class="fl" id="oltinfo_map" style="width: 50%; height: 100%;"></div>--%>
	            </div>
				 <div style="display: flex;width: 100%;height:900px;">

					 <div style="width: 50%;">
						<div class="fl" id="oltinfo_map" style="width: 100%; height: 100%;"></div>
					 </div>

					 <div style="width: 50%;margin-left: 10px;">
						 <div class="info_box" style="display: flex;">
							 <div class="search_title" style="color: white;line-height: 46px;font-size: 16px;">OLT设备在线用户数查询：</div>
							 <div class="content_in mt5" style="margin-right: 10px;">
								 <input id="olt_count" class="form-control input-normal" type="text"
										style="width: 200px;" placeholder="请输入OLT设备">
							 </div>
							 <div class="search_btn mt5">
								 <a href="#" id="olt_search"><i class="iconfont icon-web-search"></i>查询</a>
							 </div>
						 </div>

						 <div>
							 <div style="color: white;line-height: 45px;font-size: 16px;">OLT设备最近20条告警：</div>
							 <div>
								 <table id="olt_table" class="table table-bordered table-condensed">
									 <tr style="font-size: 14px;color: white;">
										 <th class="active" style="background-color: transparent;">告警时间</th>
										 <th class="active" style="background-color: transparent;">OLT设备IP</th>
										 <th class="active" style="background-color: transparent;">在线用户中值</th>
										 <th class="active" style="background-color: transparent;">当前用户数</th>
										 <th class="active" style="background-color: transparent;">陡降用户数</th>
										 <th class="active" style="background-color: transparent;">陡降百分比</th>
									 </tr>
									 <tbody id="tbody" style="color: #ffffff;"></tbody>
								 </table>
							 </div>
						 </div>
					 </div>

				 </div>

				 <div style="width: 100%;min-height: 200px;margin-top: 10px;">
					 <div style="color: white;line-height: 20px;font-size: 16px;">OLT设备历史告警查询：</div>
					 <div class="info_box" style="display: flex;">
						 <div style="display: flex;">
							 <div class="search_title" style="color: white;font-size: 15px;">设备IP：</div>
							 <div class="content_in mt5">
								 <input id="olt_ip" class="form-control input-normal" type="text"
										style="width: 200px;" placeholder="请输入OLT设备IP">
										<input type="hidden" value="" id="olt_ip_hidden"/>
							 </div>
						 </div>
						 <div style="display: flex;margin-left: 20px;">
							 <div class="search_title" style="color: white;font-size: 15px;">开始时间：</div>
							 <div class="content_in mt5">
								 <input type="text" id="start_time" class="form-control input-normal" placeholder="请输入开始时间">
								 <input type="hidden" value="" id="start_time_hidden"/>
							 </div>
						 </div>
						 <div style="display: flex;margin-left: 20px;">
							 <div class="search_title" style="color: white;font-size: 15px;">结束时间：</div>
							 <div class="content_in mt5">
                                 <input type="text" id="end_time" class="form-control input-normal" placeholder="请输入结束时间">
                                 <input type="hidden" value="" id="end_time_hidden"/>
							 </div>
						 </div>

						 <div class="search_btn mt5" style="margin-left: 20px;">
							 <a id="olt_history_search"><i class="iconfont icon-web-search"></i>查询</a>
						 </div>
						 
						 <div class="search_btn mt5" style="margin-left: 20px;">
							 <a id="olt_history_export"><i class="iconfont icon-baocun"></i>导出</a>
						 </div>
					 </div>

                     <div style="margin-top: 20px;">
                         <div class="mb10 clearfix" style="color: #FFFFFF;">
                             <div class="fl"> 共查询到 <span id="querynum" style="color: deepskyblue;"></span>条数据 | </div>
                             <div class="fl ml10">第 <span id="currnum" >1-10</span>条数据 </div>
                             <input type="hidden" value="" id="page_curr"/>
                             <div id="operate_menu" class="fr"></div>
                         </div>
                         <table id="olt_history_table" class="table table-bordered table-condensed">
                             <tr style="font-size: 14px;color: white;">
                                 <th class="active" style="background-color: transparent;">告警时间</th>
                                 <th class="active" style="background-color: transparent;">OLT设备IP</th>
                                 <th class="active" style="background-color: transparent;">在线用户中值</th>
                                 <th class="active" style="background-color: transparent;">当前用户数</th>
                                 <th class="active" style="background-color: transparent;">陡降用户数</th>
                                 <th class="active" style="background-color: transparent;">陡降百分比</th>
                             </tr>
                             <tbody id="history_tbody" style="color: #ffffff;"></tbody>
                         </table>
						 <div id="pageinfo" class="fr parts_down_page clearfix"></div>
						 <div class="ad-page-outer clearfix "></div>
                     </div>
				 </div>
	        </div>
    	</div>
    </div>
    <script type="text/javascript">
    var province = "${province}";
    </script>
    <script type="text/JavaScript" src="${staticPath}/js/ces/linebindmonitor.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>