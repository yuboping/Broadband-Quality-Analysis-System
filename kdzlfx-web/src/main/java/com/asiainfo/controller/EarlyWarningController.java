package com.asiainfo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.service.MonthItemAnalysis;
import com.asiainfo.service.UserMaintain;
import com.asiainfo.service.UserPrediction;
import com.asiainfo.service.impl.MonthStatisticsImpl;

/**
 * 离网预警
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class EarlyWarningController extends BaseController {
    @Autowired
    private UserPrediction prediction;
    @Autowired
    private UserMaintain maintain;
    @Autowired
    private MonthStatisticsImpl statistics;
    @Autowired
    private MonthItemAnalysis monItem;
    /**
     * 一级预警
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("firstLevelAlarm")
    public String firstLevelAlarm(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/alarm/firstLevel";
    }

    /**
     * 二级预警
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("secondLevelAlarm")
    public String secondLevelAlarm(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/alarm/secondLevel";
    }

    /**
     * 三级预警
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("thirdLevelAlarm")
    public String thirdLevelAlarm(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/alarm/thirdLevel";
    }

    /**
     * 四级预警
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("fourthLevelAlarm")
    public String fourthLevelAlarm(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/alarm/fourthLevel";
    }

    /**
     * 一级用户离网预测，离网等级趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/firstUserPrediction")
    @ResponseBody
    public List<ChartDatas> firstUserPrediction(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return prediction.firstUserPredictionCity(cityCode);
    }

    /**
     * 四级用户离网预测，离网等级趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/fourthUserPrediction")
    @ResponseBody
    public List<ChartDatas> fourthUserPrediction(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return prediction.fourthUserPredictionCity(cityCode);
    }

    /**
     * 四级用户到期未续约率
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/fourthNotrenewedRate")
    @ResponseBody
    public List<ChartDatas> fourthNotrenewedRate(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return prediction.fourthNotrenewedRate(cityCode);
    }
    /**
     * 一级用户离网率
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/firstOffnetRate")
    @ResponseBody
    public List<ChartDatas> firstOffnetRate(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return prediction.firstOffnetRate(cityCode);
    }

    /**
     * 四级用户离网率
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/fourthOffnetRate")
    @ResponseBody
    public List<ChartDatas> fourthOffnetRate(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return prediction.fourthOffnetRate(cityCode);
    }

    /**
     * 一级用户维系、派单趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/firstUserMaintain")
    @ResponseBody
    public List<ChartDatas> firstUserMaintain(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return maintain.firstUserMaintainForCity(cityCode);
    }

    /**
     * 四级用户维系、派单趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/fourthUserMaintain")
    @ResponseBody
    public List<ChartDatas> fourthUserMaintain(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return maintain.fourthUserMaintainForCity(cityCode);
    }

    /**
     * 新增静默用户数趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/newSilence")
    @ResponseBody
    public List<ChartDatas> newSilence(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return statistics.silenceNumForDate(cityCode);
    }

    /**
     * 新增停机用户数趋势
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/newStop")
    @ResponseBody
    public List<ChartDatas> newStop(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return statistics.stopNumForDate(cityCode);
    }

    /**
     * 到期用户结构趋势：活跃、静默、停机、销户
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/alarm/expiringUser")
    @ResponseBody
    public List<ChartDatas> expiringUser(HttpServletRequest request, HttpSession session) {
        String cityCode = request.getParameter("cityCode");
        return monItem.expiringUser(cityCode);
    }
}
