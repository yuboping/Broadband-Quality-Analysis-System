package com.asiainfo.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asiainfo.mapper.OltMapper;
import com.asiainfo.model.chartData.Olt;
import com.asiainfo.util.common.DateUtil;

@Component
public class MakeOltMapJob {
    private static final String FORMAT = "MM";

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private OltMapper oltMapper;

    // @Autowired
    // private LinebindMonitorImpl LinebindMonitor;

    // 10分钟00执行一次 0 0/10 * * * ?
    // @Scheduled(cron = "0 0/10 * * * ?")
    // public void job() {
    // String month = DateUtil.getMinusMonths(FORMAT, 0);
    // String path =
    // this.getClass().getResource("/").getPath().replaceAll("WEB-INF/classes/",
    // "")
    // + "static/js/map/oltmapinfo";
    // // oltmap信息
    // Map<String, Integer> retMap = new HashMap<String, Integer>();
    // retMap.put("totalNum", oltMapper.getOltMapTotalNum());
    // retMap.put("alarmNumTFH", oltMapper.getOltMapAlarmNumTFH());
    // // 当前周期是近一周
    // retMap.put("curAlarmNum", oltMapper.getOltMapCurAlarmNum());
    // String jsonString = JSON.toJSONString(retMap);
    // FileUtil.writeFile(path + "/oltmapinfo.json", jsonString, "UTF-8");
    // // map地图数据
    // List<Olt> oltList = oltMapper.getOltMap(month);
    // JSONArray array = JSONArray.parseArray(JSON.toJSONString(oltList));
    // FileUtil.writeFile(path + "/oltmap.json", array.toString(), "UTF-8");
    // // 刷新OLT设备历史告警数据
    // LinebindMonitor.refreshCesOltHistory(EnumUtil.REFRESH_CES_OLT_HISTORY_ALL,
    // "", "");
    // }

    // OLT在线用户数一周中值统计每天凌晨0点00秒执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void makeMidOltJob() {
        String month = DateUtil.getMinusMonths(FORMAT, 0);
        String preMonth = DateUtil.getMinusMonths(FORMAT, 1);
        String time = DateUtil.getDate(DEFAULT_FORMAT);
        List<Olt> midOltList = oltMapper.getMidOltIp(month, preMonth, time);
        for (Olt midOlt : midOltList) {
            Olt midOnlineNum = oltMapper.getMidOltNum(month, preMonth, midOlt.getOltip(), time);
            oltMapper.insertMidOlt(midOlt.getOltip(), midOnlineNum.getValue(), time);
        }
    }

    // OLT在线用户数中值全部导入统计执行一次
    // @Scheduled(cron = "0 10 16 30 12 ?")
    // public void makeAllMidOltJob() {
    // List<String> list = AccountDate.getEveryday("2019-11-01", "2019-12-29");
    // for (String result : list) {
    // DateTimeFormatter dfd = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    // LocalDateTime now = LocalDateTime.parse(result + " 00:00:00", dfd);
    // DateTimeFormatter df = DateTimeFormatter.ofPattern(FORMAT);
    // String time = now.format(dfd);
    // String month = now.minusMonths(0).format(df);
    // String preMonth = now.minusMonths(1).format(df);
    // List<Olt> midOltList = oltMapper.getMidOltIp(month, preMonth, time);
    // for (Olt midOlt : midOltList) {
    // Olt midOnlineNum = oltMapper.getMidOltNum(month, preMonth,
    // midOlt.getOltip(), time);
    // oltMapper.insertMidOlt(midOlt.getOltip(), midOnlineNum.getValue(), time);
    // }
    // }
    // }
}
