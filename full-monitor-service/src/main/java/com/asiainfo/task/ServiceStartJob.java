package com.asiainfo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.asiainfo.service.impl.LinebindMonitorImpl;
import com.asiainfo.util.common.EnumUtil;

@Service
public class ServiceStartJob implements ApplicationListener<ContextRefreshedEvent> {

    private static boolean isexecute = true;

    @Autowired
    private LinebindMonitorImpl LinebindMonitor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (isexecute) {// 保证只执行一次
            isexecute = false;
            // 项目启动执行刷新OLT设备历史告警数据
            LinebindMonitor.refreshCesOltHistory(EnumUtil.REFRESH_CES_OLT_HISTORY_ALL, "", "");
        }
    }
}
