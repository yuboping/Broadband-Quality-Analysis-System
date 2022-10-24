<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" ></c:set>
<c:set var="staticPath" value="${ctxPath}/static" ></c:set>
<html>
<head>
    <meta charset="UTF-8">
    <title>安徽移动宽带业务质量分析系统</title>
    <link href="${staticPath}/css/iconfont/iconfont.css" rel="stylesheet" media="screen">
    <link href="${staticPath}/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="${staticPath}/css/css.css" rel="stylesheet" media="screen">
</head>
<body class="page_content" >
    <div class="index_center" style="text-align: center;">
        <img src="${staticPath}/img/error.png" width="500" />
        <div>
            <h3>尊敬的管理员，您好！</h3>
            <div class="c999 mt20">您没有权限访问当前页面，如需访问，请您与系统管理人员联系添加访问权限，谢谢。</div>
            <div class="search_btn " style="width: 200px; margin: 0 auto; margin-top: 20px;"><a id="returnBtn" href="window.history.go(-1)">返回</a></div>
        </div>
    </div>
</body>
</html>
