package com.asiainfo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.service.impl.TerminalImpl;

/**
 * 终端质差
 * @author zhul
 *
 */
@Controller
public class TerminalQualityController {
    
    @Autowired
    TerminalImpl terminalImpl;
    
    @RequestMapping("terminalMonitor")
    public String getUserFigure(HttpServletRequest request) throws IOException {
        return "page/terminal/terminalMonitor";
    }
    
    
    
    /**
     * 前台数据查询
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("data/terminalMonitorInfo")
    @ResponseBody
    public List<ChartDatas> getMonitorTerminalData(HttpServletRequest request) throws IOException {
        String type = request.getParameter("type");
        return terminalImpl.getMonitorTerminalData(type);
    }
    
    /**
     * 数据转换，提高前台查询效率
     * @param request
     * @return
     */
    @RequestMapping("/terminalCount")
    @ResponseBody
    public String terminalCount(HttpServletRequest request) {
        String month = request.getParameter("month");
        terminalImpl.terminalCount(month);
        return "success";
    }
    
}
