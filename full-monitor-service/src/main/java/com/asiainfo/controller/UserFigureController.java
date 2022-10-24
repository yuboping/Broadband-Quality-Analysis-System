package com.asiainfo.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.OptHistory;
import com.asiainfo.service.impl.UserFigureImpl;

/**
 * 用户画像
 * 
 * @author luohuawuyin
 *
 */
@Controller
public class UserFigureController {
    @Autowired
    private UserFigureImpl userFigure;
    /**
     * 用户画像
     * 
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("userFigure")
    public String getUserFigure(HttpServletRequest request) throws IOException {
        return "page/userInfo/userfigure";
    }

    /**
     * 用户上月画像基本信息
     * 
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("data/userFigure/info")
    @ResponseBody
    public List<ChartDatas> getUserInfo(HttpServletRequest request) throws IOException {
        return userFigure.getUserInfo(request.getParameter("account"));
    }

    /**
     * 预测离网概率趋势、用户流量趋势
     * 
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("data/userFigure/tendency")
    @ResponseBody
    public List<ChartDatas> getTendency(HttpServletRequest request) throws IOException {
        return userFigure.getUserTendency(request.getParameter("account"));
    }

    @RequestMapping("data/userFigure/history")
    @ResponseBody
    public List<OptHistory> getHistory(HttpServletRequest request) throws IOException {
        return userFigure.getHistory(request.getParameter("account"));
    }
    
    @RequestMapping("data/userFigure/characterHealth")
    @ResponseBody
    public ChartDatas getCharacterHealthVal(HttpServletRequest request) throws IOException {
        String account = request.getParameter("account");
        return userFigure.getCharacterHealthVal(account);
    }


}
