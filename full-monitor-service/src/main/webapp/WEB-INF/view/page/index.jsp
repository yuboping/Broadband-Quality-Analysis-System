<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="../common/default.jsp"%>
<html lang="en">
<%@include file="../common/head.jsp"%>
<head>
    <meta charset="UTF-8">
    <title>安徽移动宽带业务质量分析系统</title>
</head>
<body class="page_content" style="background: #fff;">
    <!--头部导航开始-->
    <%@include file="../common/navigation.jsp"%>
    <!--头部导航结束-->
    <div class="home_center">
        <div class="home_content clearfix">
            <div class="home_left" id="keyfigures" style="width: 15%; float: left">
                <div class="mb20">
                    <b class="pr5">昨日关键指标</b><i class="iconfont icon-wenhao" title="详细描述"></i>
                </div>
            </div>
            <div class="home_left" style="width: 85%; float: left">
                <div style="padding-left: 40px;">
                    <div class="mb20">
                        <b class="pr5">用户分布</b><i class="iconfont icon-wenhao"  title="详细描述"></i>
                    </div>
                    <div class="fl" id="userScatter_map" style="width: 50%; height: 500px;"></div>
                    <div class="fl" id="userScatter_bar" style="width: 20%; height: 500px;"></div>
                    <div class="fl" id="userScatter_olt" style="width: 30%; height: 500px;">
                        <table id="userScatter_olttable" class="table table-hover" style="margin-left:50px;">
                            <tr>
                                <th class="active">OLTIP</th>
                                <th class="active">在线用户数</th>
                            </tr>
                            <tbody id="tbody"></tbody>
                        </table>
                </div>

            </div>
        </div>

        <!--折线图-->
        <div>
            <div class="home_content">
                <div class=" clearfix">
                    <div class="half50 fl" id="userChange_new" style="height: 150px;"></div>
                    <div class="half50 fl" id="userChange_cancel" style="height: 150px;"></div>
                </div>
                <div class=" mt20 clearfix">
                    <div class="half50 fl" id="userChange_increase" style="height: 150px;"></div>
                    <div class="half50 fl" id="userChange_stop" style="height: 150px;"></div>
                </div>
            </div>
        </div>

        <!--饼图-->
        <div class="mt20 mb10 ">
            <div class="home_content clearfix">
                <div class="half33 fl" id="userStructure_predict" style="height: 300px;"></div>
                <div class="half33 fl" id="badQualitydoubleCirque" style="height: 300px;"></div>
<%--                <div class="half33 fl" id="userStructure_terminal" style="height: 300px;"></div>--%>
                <div class="half33 fl" id="userStructure_broadbandtype" style="height: 300px;"></div>
            </div>
        </div>
	</div>
    </div>

    <!-- Modal -->
    <div class="modal bs-example-modal-lg" id="oltModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="margin-top: 90px;">
        <div class="modal-dialog" role="document" style="width: 1000px;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">olt在线用户数折线图</h4>
                </div>
                <div class="modal-body">
                    <div id="oltLine" style="height: 350px;width: 100%;"></div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">
    var province = "${province}";
    </script>
    <script type="text/JavaScript" src="${staticPath}/js/ces/index.js"></script>
    <!--foot-->
    <%@include file="../common/footer.jsp"%>
</body>
</html>
