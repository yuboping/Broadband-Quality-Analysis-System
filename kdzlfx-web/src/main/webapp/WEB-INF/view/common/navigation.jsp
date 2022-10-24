<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="index_top">
    <div class="lw_top clearfix">
        <div class="lw_logo fl" onclick="window.open('${ctxPath}/index.do','_self');">
            <div class="fl">
                <img src="${staticPath}/img/logo.png" width="40" height="40">
            </div>
            <div class="logo_name fl">安徽移动宽带业务质量分析系统</div>
        </div>
        <div class="fr">
            <nav class="nav">
                <ul class="nav_menu">
                </ul>
            </nav>
        </div>
    </div>
</div>
<script type="text/JavaScript"> 
var username = "<%=session.getAttribute("username")%>"; 
</script>
<script type="text/JavaScript" src="${staticPath}/js/ces/navigation.js"></script>