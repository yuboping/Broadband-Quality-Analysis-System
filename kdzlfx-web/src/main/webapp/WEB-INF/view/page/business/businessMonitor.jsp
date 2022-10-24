<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../../common/default.jsp"%>
<html lang="en">
<%@include file="../../common/head.jsp"%>
<head>
<meta charset="UTF-8">
<title>安徽移动宽带业务质量分析系统</title>
<link rel="stylesheet" href="${staticPath}/css/layui.css"  media="all">
<script src="${staticPath}/js/layui.all.js" charset="utf-8"></script>
</head>
<body class="page_content">
    <%@include file="../../common/navigation.jsp"%>
    <div class="index_center">
        <div class="clearfix">
            <h4 class="content_title fl">
                <span class="content_title_small f14">&nbsp;&nbsp;&nbsp;业务监测</span>
            </h4>
        </div>
        <div class="layui-tab layui-tab-card">
            <ul id="tab-title" class="layui-tab-title">
            </ul>
            <div id="tab-detail" class="layui-tab-content" style="min-height: 100px; background: #fff;">
                <div class="clearfix">
	                <div class="half50 fl">
	                    <input id="health_city_code" type="hidden" />
	                    <div class="content_bg mr3 ">
	                        <p class="content_name">
	                            <b>地市健康度均分</b>
	                        </p>
	                        <div class="half50 mt10" id="areaHealthAvgSort" style="width: 80%; height: 512px;"></div>
	                    </div>
	                </div>
	                <div class="half50 fl">
	                    <div class="content_bg ml3 ">
	                        <p class="content_name">
	                            <b>评分最低前十位用户</b>
	                        </p>
	                        <div class="half50 mt10 clearfix" id="userHealthSort" style="width: 90%; height: 512px;margin-top: 50px;">
	                           <table id="userHealthSort_table" class="table table-hover" style="margin-left:50px;">
		                            <tr>
		                                <th class="active">用户账号</th>
		                                <th class="active">地市</th>
		                                <th class="active">健康值</th>
		                            </tr>
		                            <tbody id="tbody"></tbody>
		                        </table>
	                           
	                        </div>
	                    </div>
	                </div>
                </div>
            </div>
	            
        </div>
            
        
    </div>
    <script type="text/JavaScript" src="${staticPath}/js/ces/businessMonitor.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>