<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../../common/default.jsp"%>
<html lang="en">
<%@include file="../../common/head.jsp"%>
<head>
<meta charset="UTF-8">
<title>安徽移动宽带业务质量分析系统</title>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-select.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/tools/chartshow.js"></script>
</head>
<body class="page_content">
    <%@include file="../../common/navigation.jsp"%>
    <div class="index_center">
        <div class="clearfix">
            <h4 class="content_title"> 系统监控
                <i class="glyphicon glyphicon-menu-right f12 ml5 mr5"></i> 
                <span class="content_title_small f14">效果监控</span>
            </h4>
            <div class="info_box fr" style="margin-top: 0;">
                <div class="fl">
                    <div class="search_title fl">地市</div>
                    <div class="content_in mt5 fl">
                        <select id="cityCode" class="selectpicker" style="width: 150px;">
                            <c:forEach items="${citys}" var="cesCityCode" varStatus="s">
                                <option value="${cesCityCode.cityCode}">${cesCityCode.cityName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="clearfix">
            <div class="half50 fl">
                <div class="content_bg mr3 ">
                    <p class="content_name">
                        <b>训练效果</b>
                    </p>
                    <div class="half50 mt10" id="trainingEffect" style="width: 100%; height: 250px;"></div>
                </div>
            </div>
            <div class="half50 fl">
                <div class="content_bg ml3 ">
                    <p class="content_name">
                        <b>预测效果</b>
                    </p>
                    <div class="half50 mt10" id="predictEffect" style="width: 100%; height: 250px;"></div>
                </div>
            </div>
        </div>
        <div class="content_bg">
            <p class="content_name">
                <b>预测结果</b>
            </p>
            <div id="predictResult" style="width: 100%; height: 250px;"></div>
        </div>
    </div>
    <script type="text/JavaScript" src="${staticPath}/js/ces/effects.js"></script>
    <!--foot-->
    <%@include file="../../common/footer.jsp"%>
</body>
</html>