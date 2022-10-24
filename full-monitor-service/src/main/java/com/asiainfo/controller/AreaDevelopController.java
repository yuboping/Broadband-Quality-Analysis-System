package com.asiainfo.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.service.MonthStatistics;

/**
 * 区域发展
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class AreaDevelopController extends BaseController {
    @Autowired
    private MonthStatistics monStatistics;

    /**
     * @param request
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("areaDevelopment")
    public String develop(HttpServletRequest request, HttpSession session)
            throws UnsupportedEncodingException {
        loadCitys(request, session);
        return "page/area/development";
    }

    /**
     * 各类用户数
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/areaDevelopment/allkindsNum")
    @ResponseBody
    public List<ChartDatas> allkindsNum(HttpServletRequest request, HttpSession session) {
        String queryDate = request.getParameter("queryDate");
        String cityCode = request.getParameter("cityCode");
        return monStatistics.allkindsNumForDate(cityCode, queryDate);
    }

}
