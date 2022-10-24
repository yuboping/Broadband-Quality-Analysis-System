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
import com.asiainfo.util.common.ConfigUtil;

/**
 * 区域对比
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class AreaContrastController extends BaseController {
    @Autowired
    private MonthStatistics monStatistics;

    /**
     * @param request
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("areaContrast")
    public String contrast(HttpServletRequest request, HttpSession session)
            throws UnsupportedEncodingException {
        String province = ConfigUtil.getPropertyKey("provicename");
        request.setAttribute("province", province);
        return "page/area/contrast";
    }


    /**
     * 各类用户数
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/areaContrast/allkindsNum")
    @ResponseBody
    public List<ChartDatas> allkindsNum(HttpServletRequest request, HttpSession session) {
        return monStatistics.allkindsNumForCity(request.getParameter("month"));
    }

}
