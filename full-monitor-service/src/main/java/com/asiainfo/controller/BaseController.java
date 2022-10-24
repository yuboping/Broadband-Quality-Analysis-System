package com.asiainfo.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.asiainfo.initdata.InitDataListener;
import com.asiainfo.lcims.lcbmi.password.PasswordException;
import com.asiainfo.lcims.lcbmi.password.PwdDES3;
import com.asiainfo.lcims.lcbmi.password.PwdMD5;
import com.asiainfo.model.system.CesCityCode;

/**
 * 
 * <p>
 * Title:BaseController
 * </p>
 * <p>
 * Description:控制器基类
 * </p>
 * <p>
 * Company
 * </p>
 * 
 * @author
 * @date 2016年5月10日下午3:00:53
 */
public class BaseController {
    @Autowired
    protected InitDataListener initdata;
    private static final PwdDES3 PWD = new PwdDES3();
    private static final PwdMD5 MD5 = new PwdMD5();
    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, String[]> reqMap = request.getParameterMap();
        Map<String, Object> resultMap = new HashMap<String, Object>(0);
        resultMap.putAll(getIpAddr(request));
        for (Entry<String, String[]> m : reqMap.entrySet()) {
            String key = m.getKey();
            Object[] obj = (Object[]) reqMap.get(key);
            resultMap.put(key, (obj.length > 1) ? obj : obj[0]);
        }
        return resultMap;
    }

    public Map<String, Object> iPLocal() {
        Map<String, Object> params = new HashMap<String, Object>();
        InetAddress ia;
        try {
            ia = InetAddress.getLocalHost();
            params.put("localIp", ia.getHostAddress());
            params.put("ipN", InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            logger.error("ip error:" + e.getMessage());
        }
        return params;
    }

    public Map<String, Object> getIpAddr(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        params.put("ip", ip);
        return params;
    }

    public void writetoclient(String content, HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("text/html;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = httpServletResponse.getWriter();
            writer.print(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("error:" + e.getMessage());
        }
    }


    /**
     * 密码加密
     * 
     * @param password
     * @param passwordtype
     * @return
     */
    public String encryptPwd(String password, int passwordtype) {
        String encryptpwd = "";
        try {
            if (passwordtype == 0) {
                encryptpwd = password;
            } else if (passwordtype == 2) {
                encryptpwd = PWD.encryptPassword(password);
            } else if (passwordtype == 5) {
                encryptpwd = MD5.encryptPassword(password);
            }
        } catch (PasswordException e) {
            logger.error("密码加密错误", e);
            encryptpwd = password;
        }
        return encryptpwd;
    }

    protected void loadCitys(HttpServletRequest request, HttpSession session) {
        String admin = (String) session.getAttribute("username");
        List<CesCityCode> citys = initdata.getUserRolesPerms(admin).getCitys();
        request.setAttribute("citys", citys);
    }
}