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
        <img src="${staticPath}/img/404.png" width="500"/>
        <div>
            <h3>尊敬的管理员，您好！</h3>
            <div class="c999 mt20">因系统出现异常，您的操作无法正确进行。请关闭浏览器，清除缓存，并重新登录系统再次进行操作。<br>如仍然无法正确操作，请您与系统管理人员取得联系，报故障情况。</div>
            <div class="search_btn " style="width: 200px; margin: 0 auto; margin-top: 20px;"><a href="${ctxPath}/">重新登录</a></div>
        </div>
    </div>
</body>
</html>
