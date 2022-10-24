package com.asiainfo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.Olt;
import com.asiainfo.model.chartData.OltHistory;
import com.asiainfo.service.impl.LinebindMonitorImpl;
import com.asiainfo.util.common.BeanToMapUtils;
import com.asiainfo.util.common.ConfigUtil;
import com.asiainfo.util.common.EnumUtil;
import com.asiainfo.util.page.Page;

/**
 * 线路质量监测
 *
 */
@Controller
public class LinebindMonitorController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LinebindMonitorController.class);

    private static boolean isexecuting = true;

    @Autowired
    private LinebindMonitorImpl linebindMonitor;

    /**
     * 
     * @Title: getLinebindMonitor @Description: TODO(线路质量监测) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         String 返回类型 @throws
     */
    @RequestMapping("linebindMonitor")
    public String getLinebindMonitor(HttpServletRequest request) throws IOException {
        String province = ConfigUtil.getPropertyKey("provicename");
        request.setAttribute("province", province);
        return "page/linebind/linebindmonitor";
    }

    /**
     * 
     * @Title: oltEquipmentInfo @Description: TODO(OLT设备信息) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         String 返回类型 @throws
     */
    @RequestMapping("oltEquipmentInfo")
    public String oltEquipmentInfo(HttpServletRequest request) throws IOException {
        request.setAttribute("oltip", request.getParameter("oltip"));
        return "page/linebind/oltequipmentinfo";
    }

    /**
     *
     * @Title: oltUserOnlineInfo @Description: TODO(在线用户数) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         String 返回类型 @throws
     */
    @RequestMapping("oltUserOnlineInfo")
    public String oltUserOnlineInfo(HttpServletRequest request) throws IOException {
        String time = request.getParameter("time").toString();
        String oltip = request.getParameter("oltip").toString();
        Map<String, String> functionShownameMap = new HashMap<String, String>();
        functionShownameMap.put("mapee", LinebindMonitorImpl.MAPEE_FUNCTION);
        functionShownameMap.put("mae", LinebindMonitorImpl.MAE_FUNCTION);
        functionShownameMap.put("smape", LinebindMonitorImpl.SMAPE_FUNCTION);
        functionShownameMap.put("mape", LinebindMonitorImpl.MAPE_FUNCTION);
        functionShownameMap.put("mase", LinebindMonitorImpl.MASE_FUNCTION);
        request.setAttribute("functionShownameMap", functionShownameMap);
        request.setAttribute("time", time);
        request.setAttribute("oltip", oltip);
        return "page/linebind/oltuseronlineinfo";
    }

    /**
     * 
     * @Title: getOltEquipmentInfo @Description: TODO(获取OLT设备信息数据) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         List<ChartDatas> 返回类型 @throws
     */
    @RequestMapping("data/oltEquipment/info")
    @ResponseBody
    public List<ChartDatas> getOltEquipmentInfo(HttpServletRequest request) throws IOException {
        return linebindMonitor.getOltEquipmentInfo(request.getParameter("oltip").toString());
    }

    /**
     * 
     * @Title: getUserOnline @Description: TODO(获取在线用户数) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         List<ChartDatas> 返回类型 @throws
     */
    @RequestMapping("data/oltUserOnline/Info")
    @ResponseBody
    public Map<String, Object> getUserOnlineInfo(HttpServletRequest request) throws IOException {
        String time = request.getParameter("time").toString();
        String oltip = request.getParameter("oltip").toString();
        return linebindMonitor.getUserOnlineMapInfo(oltip, time);
    }

    /**
     * 
     * @Title: getAuthFailRate @Description: TODO(获取认证失败率) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         List<ChartDatas> 返回类型 @throws
     */
    @RequestMapping("data/oltEquipment/authFailRate")
    @ResponseBody
    public List<Olt> getAuthFailRate(HttpServletRequest request) throws IOException {
        return linebindMonitor.getAuthFailRate(request.getParameter("oltip").toString());
    }

    /**
     * 
     * @Title: judgeExistByAccount @Description:
     *         TODO(根据用户名判断是否存在OLT信息) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         Boolean 返回类型 @throws
     */
    @RequestMapping("data/oltEquipment/judgeExistByAccount")
    @ResponseBody
    public String judgeExistByAccount(HttpServletRequest request) throws IOException {
        return linebindMonitor.judgeExistByAccount(request.getParameter("account").toString());
    }

    /**
     * 
     * @Title: getUserOnline @Description: TODO(获取在线用户数) @param @param
     *         request @param @return @param @throws IOException 参数 @return
     *         List<ChartDatas> 返回类型 @throws
     */
    @RequestMapping("data/oltEquipment/userOnline")
    @ResponseBody
    public List<Olt> getUserOnline(HttpServletRequest request) throws IOException {
        String time = request.getParameter("time").toString();
        String oltip = request.getParameter("oltip").toString();
        return linebindMonitor.getUserOnline(oltip, time);
    }

    /**
     * 
     * @Title: forecastOltOnline @Description:
     *         TODO(时间序列分解和异常检测OLT在线用户数) @param @param request @param @throws
     *         IOException 参数 @return void 返回类型 @throws
     */
    @RequestMapping("data/forecastOltOnline")
    @ResponseBody
    public void forecastOltOnline(HttpServletRequest request) throws IOException {
        long startTimeForecastOltOnline = System.nanoTime();
        String format = request.getParameter("format").toString();
        String time = request.getParameter("time").toString();
        String beginTime = request.getParameter("beginTime").toString();
        String endTime = request.getParameter("endTime").toString();
        String oltip = request.getParameter("oltip").toString();
        Boolean isUseDB = Boolean.valueOf(request.getParameter("isUseDB").toString());
        logger.info("LinebindMonitorController forecastOltOnline parametertime is " + time
                + " total Start");
        logger.info("LinebindMonitorController forecastOltOnline parametertime is " + time
                + "  isexecuting : " + isexecuting);
        if (!isUseDB && isexecuting) {
            isexecuting = false;
            // time 格式只支持yyyy-MM-dd HH:mm:ss
            linebindMonitor.egadsForecastOltRefreshOltList(time, beginTime, endTime);
            List<Olt> oltList = new ArrayList<Olt>();
            oltList.addAll(LinebindMonitorImpl.OLT_MEMORY.getOltList());
            isexecuting = true;
            linebindMonitor.forecastOltOnline(format, time, beginTime, endTime, oltip, oltList,
                    false, true);
            linebindMonitor.isCesOltCollect(format, time, beginTime, endTime, oltip, oltList, false,
                    true);
        } else {
            linebindMonitor.forecastOltOnline(format, time, beginTime, endTime, oltip,
                    new ArrayList<Olt>(), true, true);
            linebindMonitor.isCesOltCollect(format, time, beginTime, endTime, oltip,
                    new ArrayList<Olt>(), true, true);
        }
        linebindMonitor.refreshCesOltHistory(EnumUtil.REFRESH_CES_OLT_HISTORY_TIME, time, "");
        long endTimeForecastOltOnline = System.nanoTime();
        logger.info("LinebindMonitorController forecastOltOnline parametertime is " + time
                + " forecastOltOnline total time consuming : "
                + (endTimeForecastOltOnline - startTimeForecastOltOnline) + "ns");
        logger.info("LinebindMonitorController forecastOltOnline parametertime is " + time
                + " total End");
    }

    /**
     * 
     * @Title: forecastOltOnline @Description:
     *         TODO(时间序列分解和异常检测OLT在线用户数自动) @param @param request @param @throws
     *         IOException 参数 @return void 返回类型 @throws
     */
    @RequestMapping("data/forecastOltOnline/auto")
    @ResponseBody
    public void forecastOltOnlineAuto(HttpServletRequest request) throws IOException {
        long startTimeForecastOltOnline = System.nanoTime();
        String beginTime = request.getParameter("beginTime").toString();
        String endTime = request.getParameter("endTime").toString();
        String oltip = request.getParameter("oltip").toString();
        logger.info("LinebindMonitorController forecastOltOnlineAuto parameterbeginTime is "
                + beginTime + " parameterendTime is " + endTime + " total Start");
        linebindMonitor.forecastOltOnlineAuto(beginTime, endTime, oltip);
        linebindMonitor.refreshCesOltHistory(EnumUtil.REFRESH_CES_OLT_HISTORY_PART, beginTime,
                endTime);
        long endTimeForecastOltOnline = System.nanoTime();
        logger.info("LinebindMonitorController forecastOltOnlineAuto parameterbeginTime is "
                + beginTime + " parameterendTime is " + endTime
                + " forecastOltOnlineAuto total time consuming : "
                + (endTimeForecastOltOnline - startTimeForecastOltOnline) + "ns");
        logger.info("LinebindMonitorController forecastOltOnlineAuto parameterbeginTime is "
                + beginTime + " parameterendTime is " + endTime + " total End");
    }

    /**
     * 
     * @Title: refreshCesOltHistory @Description:
     *         TODO(刷新CesOltHistory) @param @param request @param @throws
     *         IOException 参数 @return void 返回类型 @throws
     */
    @RequestMapping("data/refreshCesOltHistory")
    @ResponseBody
    public void refreshCesOltHistory(HttpServletRequest request) throws IOException {
        String beginTime = request.getParameter("beginTime").toString();
        String endTime = request.getParameter("endTime").toString();
        String type = request.getParameter("type").toString();
        linebindMonitor.refreshCesOltHistory(type, beginTime, endTime);
    }

    /**
     *
     * @Title: forecastOltOnline @Description:
     *         TODO(时间序列分解和异常检测OLT在线用户数) @param @param request @param @throws
     *         IOException 参数 @return void 返回类型 @throws
     */
    @RequestMapping("data/forecastOltOnline/GUI")
    @ResponseBody
    public void forecastOltOnlineGUI(HttpServletRequest request) throws IOException {
        String format = request.getParameter("format").toString();
        String time = request.getParameter("time").toString();
        String beginTime = request.getParameter("beginTime").toString();
        String endTime = request.getParameter("endTime").toString();
        String oltip = request.getParameter("oltip").toString();
        linebindMonitor.forecastOltOnlineGUI(format, time, beginTime, endTime, oltip,
                new ArrayList<Olt>(), true);
    }

    /**
     * olt设备最近20条告警
     * 
     * @return
     */
    @RequestMapping("data/oltList.do")
    @ResponseBody
    public List<Olt> oltLists() {
        return linebindMonitor.oltList();
    }

    /**
     * 查询olt设备历史告警
     * 
     * @param request
     * @return
     */
    @RequestMapping("data/oltHistoryList.do")
    @ResponseBody
    public Page queryOltHistoryList(HttpServletRequest request) {
        Map<String, Object> params = getParams(request);
        OltHistory oltHistory = BeanToMapUtils.toBean(OltHistory.class, params);

        int pageNumber = 1;
        if (null != request.getParameter("pageNumber")) {
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        }

        Page oltHistoryList = linebindMonitor.getOltHistoryList(oltHistory, pageNumber);
        oltHistoryList.setTotalPages(oltHistoryList.getTotalPages());
        oltHistoryList.setStart(oltHistoryList.getStart());
        oltHistoryList.setEnd(oltHistoryList.getEnd());
        return oltHistoryList;
    }

    /**
     * oltIP详情
     * 
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("data/olt/info.do")
    @ResponseBody
    public List<ChartDatas> getOltInfo(HttpServletRequest request) {
        return linebindMonitor.getOltInfo(request.getParameter("oltip"));
    }

    /**
     * 
     * @Title: exportReport @Description: TODO(导出报表) @param @param
     *         request @param @param response 参数 @return void 返回类型 @throws
     */
    @RequestMapping("data/export/oltHistoryList.do")
    public void exportReport(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = getParams(request);
        OltHistory oltHistory = BeanToMapUtils.toBean(OltHistory.class, params);

        HSSFWorkbook wb = linebindMonitor.exportReport(oltHistory);
        String reportname = "OLT设备历史告警";

        writeInfoInResponse(response, wb, reportname);
        logger.info("exportReport end.");
    }

    private void writeInfoInResponse(HttpServletResponse response, HSSFWorkbook wb,
            String reportname) {
        try {
            reportname = URLEncoder.encode(reportname + ".xls", "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error("UnsupportedEncodingException:", e1);
        }
        response.setContentType("octets/stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + reportname);
        OutputStream output = null;
        try {
            output = response.getOutputStream();
            wb.write(output);
        } catch (IOException e) {
            logger.error("IOException:", e);
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(wb);
        }
    }
}
