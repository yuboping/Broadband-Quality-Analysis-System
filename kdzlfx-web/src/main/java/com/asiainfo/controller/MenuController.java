package com.asiainfo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.initdata.InitDataListener;
import com.asiainfo.model.system.Menu;
import com.asiainfo.model.system.UserRolesPerms;

@Controller
/**
 * 菜单获取
 * 
 * @author luohuawuyin
 *
 */
public class MenuController extends BaseController {
    @Autowired
    private InitDataListener initdata;

    @RequestMapping("/data/loadMenu")
    @ResponseBody
    public List<Menu> usersCdrStatIndex(HttpSession session, HttpServletRequest request) {
        String username = (String) session.getAttribute("username");
        UserRolesPerms user = initdata.getUserRolesPerms(username);
        return user.getMenu();
    }

}
