package com.asiainfo.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.service.impl.HealThCountImpl;

/**
 * 业务质量监测
 * @author zhul
 *
 */
@Controller
public class BusinessMonitorController {
    @Autowired
    private HealThCountImpl healThCountImpl;
    
    /**
     * 
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("businessMonitor")
    public String getUserFigure(HttpServletRequest request) throws IOException {
        return "page/business/businessMonitor";
    }
    
    @RequestMapping("data/areaMonitorInfo")
    @ResponseBody
    public ChartDatas getMonitorAreaData(HttpServletRequest request) throws IOException {
        String type = request.getParameter("type");
        return healThCountImpl.getMonitorAreaData(type);
    }
    
    @RequestMapping("data/userMonitorInfo")
    @ResponseBody
    public ChartDatas getMonitorUserData(HttpServletRequest request) throws IOException {
        String type = request.getParameter("type");
        String cityCode = request.getParameter("cityCode");
        return healThCountImpl.getMonitorUserData(type, cityCode);
    }
}
