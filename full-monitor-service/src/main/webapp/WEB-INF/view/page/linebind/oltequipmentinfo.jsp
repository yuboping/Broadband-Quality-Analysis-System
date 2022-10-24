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
                <span class="content_title_small f14">OLT设备信息（${oltip}）</span>
            </h4>
        </div>
        <div class="layui-tab layui-tab-card">
            <div id="tab-detail" class="layui-tab-content" style="min-height: 100px; background: #fff;"></div>
        </div>
        <div class="clearfix">
            <div>
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>在线用户数</b>
                    </p>
                    <div class="half50 mt10" id="userOnline" style="width: 100%; height: 350px;"></div>
                </div>
            </div>
<%--            <div class="half50 fl">--%>
<%--                <div class="content_bg ml3 ">--%>
<%--                    <p class="content_name">--%>
<%--                        <b>认证失败率</b>--%>
<%--                    </p>--%>
<%--                    <div class="half50 mt10" id="authFailRate" style="width: 100%; height: 300px;"></div>--%>
<%--                </div>--%>
<%--            </div>--%>
        </div>
    </div>
    <script type="text/javascript">
    var oltip = "${oltip}";
    </script>
    <script type="text/JavaScript" src="${staticPath}/js/ces/oltequipmentinfo.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>