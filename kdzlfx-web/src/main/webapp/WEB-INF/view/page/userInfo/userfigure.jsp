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
                <span class="content_title_small f14">用户画像</span>
            </h4>
            <div class="info_box fr" style="margin-top: 0;">
                <div class="ml20 fl">
                    <div class="search_title fl">宽带账号</div>
                    <div class="content_in fl mt5">
                        <input id="account" class="form-control  input-normal" type="text" size="16"
                               style="width: 150px;" placeholder="请输入查询账号">
                    </div>
                </div>
                <div class="ml20 fl">
                    <div class="search_btn mt5 fl">
                        <a href="#" id="query"><i class="iconfont icon-web-search"></i>查询</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-tab layui-tab-card">
            <ul id="tab-title" class="layui-tab-title">
            </ul>
            <div id="tab-detail" class="layui-tab-content" style="min-height: 100px; background: #fff;">
            </div>
        </div>
        
        <!--用户线路拓扑图-->
        <div class="content_bg">
            <p class="content_name">
                <b>用户线路拓扑图</b>
            </p>
            <div class="process_box">
                <div class="process_content" id="lineTopology"></div>
            </div>
        </div>

        <%--五个饼图--%>
        <div class="content_bg">
        	<p class="content_name">
                <b>健康度评分</b>
            </p>
            <div class="process_box">
                <div class="process_content" id="fiveBin"></div>
            </div>
        </div>

        <div class="clearfix">
            <div class="half50 fl">
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>健康度趋势</b>
                    </p>
                    <div class="half50 mt10" id="offnetTendency" style="width: 100%; height: 300px;"></div>
                </div>
            </div>
            <div class="half50 fl">
                <div class="content_bg ml3 ">
                    <p class="content_name">
                        <b>用户流量趋势</b>
                    </p>
                    <div class="half50 mt10" id="flowTendency" style="width: 100%; height: 300px;"></div>
                </div>
            </div>
        </div>
        <!--历史记录-->
        <div class="content_bg">
            <p class="content_name">
                <b>历史记录</b>
            </p>
            <div class="process_box" style="height: 1150px;">
                <div class="process_content" id="optHistory" style="height: 300px;"></div>
                <div style="display: flex;">
                    <div id="peakBandwidth" style="width: 50%; height: 400px;"></div>
                    <div style="width: 50%;height: 400px;">
                        <div style="font-weight: bold;font-size: 18px;">用户上网时间段特征</div>
                        <div id="internetFeatures" style="width: 100%; height: 94%;"></div>
                    </div>
                </div>
                <div style="display: flex;margin-top: 40px;">
                    <div id="userPreference" style="width: 50%; height: 400px;"></div>
                    <div id="DPIData" style="width: 50%; height: 400px;"></div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/JavaScript" src="${staticPath}/js/ces/userfigure.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>