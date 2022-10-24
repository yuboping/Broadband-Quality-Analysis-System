package com.asiainfo.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.BadQualityPieChartVO;
import com.asiainfo.model.chartData.OltInfo;
import com.asiainfo.service.BadQuality;
import com.asiainfo.service.DayStatistics;
import com.asiainfo.service.MonthItemAnalysis;
import com.asiainfo.service.MonthStatistics;
import com.asiainfo.service.UserMaintain;
import com.asiainfo.util.common.ConfigUtil;
import com.asiainfo.util.config.ParamConfig;

/**
 * 首页
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class HomePageController extends BaseController {
    @Autowired
    private DayStatistics dayStatistics;
    @Autowired
    private MonthStatistics monStatistics;
    @Autowired
    private MonthItemAnalysis monItem;
    @Autowired
    private UserMaintain maintain;
    @Autowired
    private BadQuality badQuality;

    /**
     * @param request
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("index")
    public String index(HttpServletRequest request, HttpSession session)
            throws UnsupportedEncodingException {
        String province = ConfigUtil.getPropertyKey("provicename");
        request.setAttribute("province", province);
        return "page/index";
    }

    /**
     * 昨日关键指标
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/keyfigures")
    @ResponseBody
    public List<ChartDatas> scatter(HttpServletRequest request, HttpSession session) {
        return dayStatistics.yesKeyfigures();
    }

    /**
     * 用户分布
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/userScatter")
    @ResponseBody
    public List<ChartDatas> userScatter(HttpServletRequest request, HttpSession session) {
        return monStatistics.userScatterForCity();
    }

    /**
     * 近30日各用户变化
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/userChange")
    @ResponseBody
    public List<ChartDatas> userChange(HttpServletRequest request, HttpSession session) {
        return dayStatistics.nearly30DaysKeyfigures();
    }

    /**
     * 用户结构环形结构
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/userStructure")
    @ResponseBody
    public List<ChartDatas> userStructure(HttpServletRequest request, HttpSession session) {
        return monItem.structure();
    }

    /**
     * 四级预警用户数
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/fourthLevel")
    @ResponseBody
    public List<ChartDatas> fourthLevel(HttpServletRequest request, HttpSession session) {
        return maintain.fourthDispatchNumForCity(ParamConfig.PROVINCIAL);
    }

    /**
     * 一级预警用户数
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/firstLevel")
    @ResponseBody
    public List<ChartDatas> firstLevel(HttpServletRequest request, HttpSession session) {
        return maintain.firstDispatchNumForCity(ParamConfig.PROVINCIAL);
    }

    /**
     * 二级、三级预警用户数
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/secondThirdLevel")
    @ResponseBody
    public List<ChartDatas> secondThirdLevel(HttpServletRequest request, HttpSession session) {
        return monStatistics.warningUser(ParamConfig.PROVINCIAL);
    }

    /**
     * 双环图数据获取
     */
    @RequestMapping("data/homepage/getBadQualityPieData")
    @ResponseBody
    public Map<String, List<BadQualityPieChartVO>> badQualityDoubleCirque() {
        return badQuality.getBadQualityPieChartData();
    }

    /**
     * OLT
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/userOlt")
    @ResponseBody
    public List<OltInfo> userOlt(HttpServletRequest request, HttpSession session) {
        return monStatistics.userOlt();
    }

    /**
     * OLTbyoltip
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/homepage/userOltByOltip")
    @ResponseBody
    public Map<String, List<String>> userOltByOltip(HttpServletRequest request, HttpSession session) {
        List<OltInfo> oltInfoList = monStatistics.userOltByOltip(request.getParameter("oltip"));
        List<String> xRayData = new ArrayList<>();
        List<String> yRayData = new ArrayList<>();
        for (OltInfo oltInfo : oltInfoList) {
            xRayData.add(oltInfo.getSta_date().substring(11,16));
            yRayData.add(oltInfo.getOnlinenum());
        }
        Map<String, List<String>> rayData = new HashMap<>();
        rayData.put("xData", xRayData);
        rayData.put("yData", yRayData);
        return rayData;
    }
}
