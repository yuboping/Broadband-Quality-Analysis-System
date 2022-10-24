package com.asiainfo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.service.PredictEvaluation;
import com.asiainfo.service.SysMonitor;
import com.asiainfo.service.UserPrediction;

/**
 * 系统监控
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class SysMonitorController extends BaseController {
    @Autowired
    private SysMonitor monitor;
    @Autowired
    private PredictEvaluation evaluation;
    @Autowired
    private UserPrediction prediction;
    /**
     * 采集监控
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("collections")
    public String collection(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/monitor/collections";
    }

    /**
     * 特征监控
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("features")
    public String features(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/monitor/features";
    }

    /**
     * 效果监控
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("effects")
    public String effects(HttpServletRequest request, HttpSession session) {
        loadCitys(request, session);
        return "page/monitor/effects";
    }

    /**
     * AAA监控数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/aaaMon")
    @ResponseBody
    public List<ChartDatas> aaaMon(HttpServletRequest request, HttpSession session) {
        return monitor.aaaForMonth(request.getParameter("cityCode"));
    }

    /**
     * CRM监控数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/crmMon")
    @ResponseBody
    public List<ChartDatas> crmMon(HttpServletRequest request, HttpSession session) {
        return monitor.crmForMonth(request.getParameter("cityCode"));
    }

    /**
     * 报障数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/faultMon")
    @ResponseBody
    public List<ChartDatas> faultMon(HttpServletRequest request, HttpSession session) {
        return monitor.faultForMonth(request.getParameter("cityCode"));
    }

    /**
     * 线路数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/lineMon")
    @ResponseBody
    public List<ChartDatas> lineMon(HttpServletRequest request, HttpSession session) {
        return monitor.lineForMonth(request.getParameter("cityCode"));
    }

    /**
     * 行为数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/behaviourMon")
    @ResponseBody
    public List<ChartDatas> behaviourMon(HttpServletRequest request, HttpSession session) {
        return monitor.behaviourForMonth(request.getParameter("cityCode"));
    }

    /**
     * 投诉数据
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/complaintMon")
    @ResponseBody
    public List<ChartDatas> complaintMon(HttpServletRequest request, HttpSession session) {
        return monitor.complaintForMonth(request.getParameter("cityCode"));
    }

    /**
     * 特征表数量
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/featureNum")
    @ResponseBody
    public List<ChartDatas> featureNum(HttpServletRequest request, HttpSession session) {
        return monitor.featureNumForMonth(request.getParameter("cityCode"));
    }

    /**
     * 特征表标记数量
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/markingNum")
    @ResponseBody
    public List<ChartDatas> markingNum(HttpServletRequest request, HttpSession session) {
        return monitor.markingNumForMonth(request.getParameter("cityCode"));
    }

    /**
     * 表关联率
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/correlationRate")
    @ResponseBody
    public List<ChartDatas> correlationRate(HttpServletRequest request, HttpSession session) {
        return monitor.correlationRateForMonth(request.getParameter("cityCode"));
    }

    /**
     * 训练效果
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/trainingEffect")
    @ResponseBody
    public List<ChartDatas> trainingEffect(HttpServletRequest request, HttpSession session) {
        return evaluation.trainEvaluation(request.getParameter("cityCode"));
    }

    /**
     * 预测效果
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/predictEffect")
    @ResponseBody
    public List<ChartDatas> predictEffect(HttpServletRequest request, HttpSession session) {
        return evaluation.modelEvaluation(request.getParameter("cityCode"));
    }

    /**
     * 预测结果
     * 
     * @param request
     * @param session
     * @return
     */
    @RequestMapping("data/monitor/predictResult")
    @ResponseBody
    public List<ChartDatas> predictResult(HttpServletRequest request, HttpSession session) {
        return prediction.predictResult(request.getParameter("cityCode"));
    }
}
