package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

public interface UserMaintain {
    /**
     * 一级预警派单、维系数
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> firstUserMaintainForCity(String cityCode);

    /**
     * 一级预警用户数=派单用户数
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> firstDispatchNumForCity(String cityCode);

    /**
     * 四级预警用户数=派单用户数
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> fourthDispatchNumForCity(String cityCode);

    /**
     * 四级预警派单、维系数
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> fourthUserMaintainForCity(String cityCode);

}
