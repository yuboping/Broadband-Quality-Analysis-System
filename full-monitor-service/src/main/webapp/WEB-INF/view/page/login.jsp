<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../common/default.jsp"%>
<html lang="en">
<head>
    <title>宽带业务质量分析系统登录页</title>
    <link href="${staticPath}/css/login.css" rel="stylesheet" media="screen">
    <link rel="icon" href="${staticPath}/img/favicon.ico" />
</head>

<body>
<div class="bg" id="bodydiv">
    <div class="login_center ">
        <div class="logo clearfix">
            <div class="fl"><img src="${staticPath}/img/logo.png" width="40px"height="40px"></div>
            <div class="fl" style="color: #fff; font-size: 20px;line-height: 40px ; font-weight:bolder;padding-left: 10px; ">安徽移动宽带业务质量分析系统</div>
        </div>
    </div>
    <div class="login_center clearfix "  style="margin-top: 150px;">
            <div class="fl" style="">
                <img src="${staticPath}/img/login_tu.png" width="600">
            </div>
            <div class="fr" style="background: #fff; width: 300px;padding: 0 40px; margin-top: 50px;">
                <div>
                    <p style="font-size: 18px;">用户登录</p>
                    <div class="prompt hide">
                        <p>密码错误，请重新核实后登录！</p>
                    </div>
                    <form id="loginForm" class="login_ul" action="${ctxPath}/login.do" method="post">
                    <div>
                        <div>
                            <input name="name" id="name" type="text" placeholder="用户名">
                        </div>
                        <div>
                            <input name="password" id="password" type="password" placeholder="密码">
                        </div>

                    </div>
                    <div id="submit" class="btn">登录</div>
                    </form>
                </div>
            </div>
    </div>

</div>
<script type="text/JavaScript" src="${staticPath}/js/jquery-1.11.0.min.js"></script>
<script type="text/JavaScript" src="${staticPath}/js/ces/login.js"></script>
</body>
</html>