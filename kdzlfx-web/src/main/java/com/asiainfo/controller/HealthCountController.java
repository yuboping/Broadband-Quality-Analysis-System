package com.asiainfo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.service.impl.HealThCountImpl;
/**
 * 健康值计算
 * @author zhul
 *
 */
@Controller
public class HealthCountController {
    
    @Autowired
    private HealThCountImpl healThCountImpl;
    
    @RequestMapping("/healthCount")
    @ResponseBody
    public String healthCount(HttpServletRequest request) {
        String month = request.getParameter("month");
        healThCountImpl.healthCount(month);
        return "success";
    }
    
    
    
}
