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
                <span class="content_title_small f14">OLT在线用户数信息分析</span>
            </h4>
        </div>
        <div class="layui-tab layui-tab-card">
            <div id="tab-detail" class="layui-tab-content" style="min-height: 100px; background: #fff;"></div>
        </div>
        <div class="clearfix">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>在线用户数分析图</b>
                    </p>
                    <div class="half50 mt10" id="userOnline" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
        <div class="clearfix threshold" id="userOnlineMapeeMain">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>${functionShownameMap.mapee}</b>
                    </p>
                    <div class="half50 mt10" id="userOnlineMapee" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
        <div class="clearfix threshold" id="userOnlineMaeMain">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>${functionShownameMap.mae}</b>
                    </p>
                    <div class="half50 mt10" id="userOnlineMae" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
        <div class="clearfix threshold" id="userOnlineSmapeMain">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>${functionShownameMap.smape}</b>
                    </p>
                    <div class="half50 mt10" id="userOnlineSmape" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
        <div class="clearfix threshold" id="userOnlineMapeMain">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>${functionShownameMap.mape}</b>
                    </p>
                    <div class="half50 mt10" id="userOnlineMape" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
        <div class="clearfix threshold" id="userOnlineMaseMain">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>${functionShownameMap.mase}</b>
                    </p>
                    <div class="half50 mt10" id="userOnlineMase" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
    var oltip = "${oltip}";
    var time = "${time}";
    </script>
    <script type="text/JavaScript" src="${staticPath}/js/ces/oltuseronlineinfo.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>