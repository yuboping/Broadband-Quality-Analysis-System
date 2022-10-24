package com.asiainfo.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesMaintainNumMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.UserMaintainInfo;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.UserMaintain;

@Service
public class UserMaintainImpl extends BasicService implements UserMaintain {
    @Autowired
    private CesMaintainNumMapper maintain;


    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<UserMaintainInfo> datas = (List<UserMaintainInfo>) retdata;
        for (UserMaintainInfo figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("派单用户数") || title.equals("一级预警用户数")) {
                    return figures.getDispatch_num() == null ? "0" : figures.getDispatch_num();
                } else if (title.equals("维系用户数")) {
                    return figures.getWx_total() == null ? "0" : figures.getWx_total();
                } else if (title.equals("维系成功数")) {
                    return figures.getWx_success_num() == null ? "0" : figures.getWx_success_num();
                } else if (title.equals("维系率")) {
                    return figures.getDispatch_rate() == null ? "0" : figures.getDispatch_rate();
                } else if (title.equals("维系成功率")) {
                    return figures.getSuccess_rate() == null ? "0" : figures.getSuccess_rate();
                } else if (title.equals("四级预警用户数")) {
                    return figures.getDispatch_num_4th() == null ? "0"
                            : figures.getDispatch_num_4th();
                } else if (title.equals("四级维系用户数")) {
                    return figures.getWx_total_4th() == null ? "0" : figures.getWx_total_4th();
                } else if (title.equals("四级维系成功数")) {
                    return figures.getWx_success_num_4th() == null ? "0"
                            : figures.getWx_success_num_4th();
                } else if (title.equals("四级维系率")) {
                    return figures.getDispatch_rate_4th() == null ? "0"
                            : figures.getDispatch_rate_4th();
                } else if (title.equals("四级维系成功率")) {
                    return figures.getSuccess_rate_4th() == null ? "0"
                            : figures.getSuccess_rate_4th();
                }
            }
        }
        return "0";
    }

    @Override
    public List<ChartDatas> firstUserMaintainForCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        List<UserMaintainInfo> retdata = maintain.maintainForCity(cityCode,
                dates.get(0).getValue(), dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("派单用户数", "维系用户数", "维系成功数", "维系率", "维系成功率");// 顺序不能调换
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> fourthUserMaintainForCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        // TODO 3.21演示需要，暂时不从数据库中取数据
        List<UserMaintainInfo> retdata = Collections.emptyList();
//        List<UserMaintainInfo> retdata = maintain.maintainForCity(cityCode, dates.get(0).getValue(),
//                dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("四级预警用户数", "四级维系用户数", "四级维系成功数", "四级维系率", "四级维系成功率");// 顺序不能调换
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> firstDispatchNumForCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        List<UserMaintainInfo> retdata = maintain.maintainForCity(cityCode, dates.get(0).getValue(),
                dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("一级预警用户数");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> fourthDispatchNumForCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
      //TODO 3.21演示需要，暂时不从数据库中取数据
        List<UserMaintainInfo> retdata = Collections.emptyList();
//        List<UserMaintainInfo> retdata = maintain.maintainForCity(cityCode, dates.get(0).getValue(),
//                dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("四级预警用户数");
        return useSameMarkData(titles, retdata, dates);
    }
}
