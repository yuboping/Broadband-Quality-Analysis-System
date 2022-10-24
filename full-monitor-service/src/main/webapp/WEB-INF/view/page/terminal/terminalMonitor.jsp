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
                <span class="content_title_small f14">&nbsp;&nbsp;&nbsp;终端质差分析</span>
            </h4>
        </div>
        <div class="layui-tab layui-tab-card">
            <ul id="tab-title" class="layui-tab-title">
            </ul>
            <div id="tab-detail" class="layui-tab-content" style="min-height: 100px; background: #fff;">
                <div class="clearfix">
	                <div class="half33 fl">
	                    <input id="terminal_type" type="hidden" />
	                    <div class="content_bg mr3 ">
	                        <p class="content_name">
	                            <b>认证失败终端</b>
	                        </p>
	                        <div class="half33 mt10" id="authFailSort" style="width: 80%; height: 512px;"></div>
	                    </div>
	                </div>
                    
                    <div class="half33 fl">
                        <input id="health_city_code" type="hidden" />
                        <div class="content_bg mr3 ">
                            <p class="content_name">
                                <b>短时频繁上下线</b>
                            </p>
                            <div class="half33 mt10" id="shotOftenUpDownSort" style="width: 80%; height: 512px;"></div>
                        </div>
                    </div>
                    
                    <div class="half33 fl">
                        <input id="health_city_code" type="hidden" />
                        <div class="content_bg mr3 ">
                            <p class="content_name">
                                <b>频繁掉线终端</b>
                            </p>
                            <div class="half33 mt10" id="oftenDownLineSort" style="width: 80%; height: 512px;"></div>
                        </div>
                    </div>
                    
                </div>
            </div>
	            
        </div>
            
        
    </div>
    <script type="text/JavaScript" src="${staticPath}/js/ces/terminalMonitor.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>