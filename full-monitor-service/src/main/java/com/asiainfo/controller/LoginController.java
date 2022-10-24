package com.asiainfo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.asiainfo.model.system.WebResult;
import com.asiainfo.util.common.ConfigUtil;

/**
 * 
 * @ClassName: IndexController
 * @Description: TODO(主页)
 * @author
 * @date
 *
 */
@Controller
public class LoginController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String HOME = "";
    /**
     * 
     * @Title: login @Description: TODO(登录) @param @param request @param @return
     *         参数 @return String 返回类型 @throws
     */
    @RequestMapping("login")
    @ResponseBody
    public WebResult login(HttpServletRequest request, HttpSession session) {
        String admin = request.getParameter("name");
        String password = request.getParameter("password");
        logger.info("login success , params = {};" + admin);
        try {
            String encrypt = encryptPwd(password,
                    Integer.parseInt(ConfigUtil.getPropertyKey("passwordtype")));
            logger.info("{},{}", admin, encrypt);
            SecurityUtils.getSubject().login(new UsernamePasswordToken(admin, encrypt));
            session.setAttribute("username", admin);
            return WebResult.SUCCESS;
        } catch (ExcessiveAttemptsException e) {
            logger.error("", e);
            return new WebResult("登录错误超过5次");
        } catch (AuthenticationException e) {
            logger.error("", e);
            return new WebResult("用户名或密码错误");
        }
    }

    @RequestMapping("/logout")
    public RedirectView logout(SessionStatus status, RedirectAttributes redirectAttributes) {
        status.setComplete();
        // 使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        SecurityUtils.getSubject().logout();
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        RedirectView view = new RedirectView();
        view.setUrl(HOME);
        return view;
    }

}
