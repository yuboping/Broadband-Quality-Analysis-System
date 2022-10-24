var pageUrl = window.location.pathname;
loadMenu();
// ----------------------------------------------------------------------------
function loadMenu() {
    $.ajax({// 加载菜单数据
        type : "POST",
        url : project.ctxPath + "/data/loadMenu.do",
        dateType : "json",
        success : function(retdata) {
            var data = JSON.parse(retdata);
            data.forEach(function(row) {
                var url = project.ctxPath + row.url;
                var active = "";
                if (url === pageUrl) {
                    active = " active";
                }
                if (row.childen.length === 0) {// 无二级菜单
                    $(".nav_menu").append("<li class='nav_menu-item" + active + "'><a href='" + url + "'>" + row.show_name + "</a></li>");
                } else {
                    hasChild(row, active);
                }
            });
            loadAdmin();
        }
    });
}
function hasChild(menu, active) {// 加载二级菜单
    var show = [];
    show.push("<li class='nav_menu-item" + active + "'><a href='#'>" + menu.show_name + "<i class='iconfont icon-triangle'></i></a>");
    show.push("    <ul class='nav_submenu'>");
    menu.childen.forEach(function(row) {
        var select = "";
        var url = project.ctxPath + row.url;
        if (url === pageUrl) {
            select = "on";
            show[0] = show[0].replace(/nav_menu-item/, "nav_menu-item active");// 子菜单被选中是父菜单也应处于选中状态
        }
        if (row.url === "#") {
            url = "#";
        }
        show.push("        <li class='nav_submenu-item'><a class='" + select + "' href='" + url + "'>" + row.show_name + "</a></li>");
    });
    show.push("    </ul>");
    show.push("</li>");
    $(".nav_menu").append(show.join(""));
}
function loadAdmin() {
    var show = [];
    show.push("<li class='nav_menu-item'><a href='#' style='font-size: 12px;'>");
    show.push("    <i class='iconfont icon-yonghu pr10 ' style='font-size: 12px;'></i>" + username + "</a>")
    show.push("    <ul class='nav_submenu'>")
    show.push("        <li class='nav_submenu-item'><a href='" + project.ctxPath + "/logout.do'>退出</a></li>");
    show.push("    </ul>");
    show.push("</li>");
    $(".nav_menu").append(show.join(""));
}
