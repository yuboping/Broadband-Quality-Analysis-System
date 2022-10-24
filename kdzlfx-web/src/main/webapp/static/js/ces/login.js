$(document).ready(function() {
    $("#bodydiv").bind("keyup", function(e) {
        var theEvent = e || window.event;
        var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
        if (code == 13) {
            $('#submit').click();
        }
    });
    $("#submit").click(function() {
        var name = $("#name").val();
        var password = $("#password").val();
        if (!check(name, password)) {
            return;
        }
        login(name, password);
    });
    // 校验用户名，密码是否为空
    function check(name, password) {
        if (name == "") {
            $(".prompt").removeClass("hide");
            $(".prompt").html("<p>用户名不能为空</p>");
            return false;
        }
        if (password == "") {
            $(".prompt").removeClass("hide");
            $(".prompt").html("<p>密码不能为空</p>");
            return false;
        }
        return true;
    }
    // 登录
    function login(name, password) {
        $(".prompt").addClass("hide");
        $.ajax({
            type : "POST",
            data : {
                "name" : name,
                "password" : password
            },
            url : project.ctxPath + "/login.do",
            dateType : "json",
            success : function(retdata) {
                var data = JSON.parse(retdata);
                if (data.result) {
                    window.location.href = project.ctxPath + "/index.do";
                } else {
                    $(".prompt").removeClass("hide");
                    $(".prompt").html("<p>" + data.message + "</p>");
                }
            }
        });
    }
});
