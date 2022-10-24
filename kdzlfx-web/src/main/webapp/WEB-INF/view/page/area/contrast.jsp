<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../../common/default.jsp"%>
<html lang="en">
<%@include file="../../common/head.jsp"%>
<head>
<meta charset="UTF-8">
<title>安徽移动宽带业务质量分析系统</title>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-select.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/tools/chartshow.js"></script>
</head>
<body class="page_content">
    <%@include file="../../common/navigation.jsp"%>
    <div class="index_center">
        <div class="clearfix">
            <h4 class="content_title"> 用户发展 
                <i class="glyphicon glyphicon-menu-right f12 ml5 mr5"></i> 
                <span class="content_title_small f14">区域对比</span>
            </h4>
            <div class="info_box fr"  style="margin-top: 0;">
                <div class="fl">
                    <div class="search_title fl">时间</div>
                    <div class="content_in fl mt5">
                        <input id="datetimepicker" class="form-control  input-normal" type="text" size="16" style="width: 150px;">
                    </div>
                </div>
            </div>
        </div>
        <div class="clearfix">
            <div class="half75 fl">
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>区域分布</b>
                    </p>
                    <div class="mt10" id="regNum_map" style="width: 100%; height: 450px;"></div>
                </div>
            </div>
            <div class="half25 fl">
                <div class="content_bg ml3 ">
                    <div class="mt10" id="regNum_bar" style="width: 100%; height: 470px;"></div>
                </div>
            </div>
        </div>
        <div class="content_bg">
            <p class="content_name">
                <b>区域发展趋势</b>
            </p>
            <div id="devTrend" style="width: 100%; height: 400px;"></div>
        </div>
        <div class="content_bg">
            <p class="content_name">
                <b>区域增长对比</b>
            </p>
            <div id="increase" style="width: 100%; height: 500px;"></div>
        </div>
        <div class="content_bg">
            <p class="content_name">
                <b>区域活跃对比</b>
            </p>
            <div id="activity" style="width: 100%; height: 400px;"></div>
        </div>
    </div>
    <script type="text/javascript">
        var province = "${province}";
    </script>
    <script type="text/JavaScript" src="${staticPath}/js/ces/contrast.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>