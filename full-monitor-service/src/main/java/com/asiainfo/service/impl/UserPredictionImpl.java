package com.asiainfo.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesPredictionFourthNumMapper;
import com.asiainfo.mapper.CesPredictionNumMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.UserPredictionInfo;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.UserPrediction;
import com.asiainfo.util.config.ParamConfig;

@Service
public class UserPredictionImpl extends BasicService implements UserPrediction {
    @Autowired
    private CesPredictionNumMapper prediction;
    @Autowired
    private CesPredictionFourthNumMapper fourthNum;

    @Override
    public List<ChartDatas> firstUserPredictionCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        List<UserPredictionInfo> retdata = getPredictionDatas(cityCode, dates);
        List<String> titles = Arrays.asList("50%-55%", "55%-60%", "60%-65%", "65%-70%", "70%-75%",
                "75%-80%", "80%-85%", "85%-90%", "90%-95%", "95%-100%");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<UserPredictionInfo> datas = (List<UserPredictionInfo>) retdata;
        for (UserPredictionInfo figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("离网概率")) {
                    return figures.getOff_num() == null ? "0" : figures.getOff_num();
                } else if (title.equals("50%-55%")) {
                    return figures.getPercent_50_55() == null ? "0" : figures.getPercent_50_55();
                } else if (title.equals("55%-60%")) {
                    return figures.getPercent_55_60() == null ? "0" : figures.getPercent_55_60();
                } else if (title.equals("60%-65%")) {
                    return figures.getPercent_60_65() == null ? "0" : figures.getPercent_60_65();
                } else if (title.equals("65%-70%")) {
                    return figures.getPercent_65_70() == null ? "0" : figures.getPercent_65_70();
                } else if (title.equals("70%-75%")) {
                    return figures.getPercent_70_75() == null ? "0" : figures.getPercent_70_75();
                } else if (title.equals("75%-80%")) {
                    return figures.getPercent_75_80() == null ? "0" : figures.getPercent_75_80();
                } else if (title.equals("80%-85%")) {
                    return figures.getPercent_80_85() == null ? "0" : figures.getPercent_80_85();
                } else if (title.equals("85%-90%")) {
                    return figures.getPercent_85_90() == null ? "0" : figures.getPercent_85_90();
                } else if (title.equals("90%-95%")) {
                    return figures.getPercent_90_95() == null ? "0" : figures.getPercent_90_95();
                } else if (title.equals("95%-100%")) {
                    return figures.getPercent_95_100() == null ? "0" : figures.getPercent_95_100();
                } else if (title.equals("到期未续约率")) {
                    return figures.getNotrenewed() == null ? "0" : figures.getNotrenewed();
                }
            }
        }
        return "0";
    }

    @Override
    public List<ChartDatas> firstOffnetRate(String cityCode) {
        List<ParamRelation> dates = getPre1year();
        List<UserPredictionInfo> retdata = getPredictionDatas(cityCode, dates);
        List<String> titles = Arrays.asList("离网概率");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> fourthOffnetRate(String cityCode) {
        List<ParamRelation> dates = getPre1year();
        List<UserPredictionInfo> retdata = null;
        if (ParamConfig.PROVINCIAL.equals(cityCode)) {// 全省
            retdata = fourthNum.predictionForProvince(dates.get(0).getValue(),
                    dates.get(dates.size() - 1).getValue());
        } else {// 查询某地市
            retdata = fourthNum.predictionForCity(cityCode, dates.get(0).getValue(),
                    dates.get(dates.size() - 1).getValue());
        }
        List<String> titles = Arrays.asList("离网概率");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> fourthUserPredictionCity(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        List<UserPredictionInfo> retdata = Collections.emptyList();
      //TODO 3.21演示需要，暂时不从数据库中取数据
//        if (ParamConfig.PROVINCIAL.equals(cityCode)) {// 全省
//            retdata = fourthNum.predictionForProvince(dates.get(0).getValue(),
//                    dates.get(dates.size() - 1).getValue());
//        } else {// 查询某地市
//            retdata = fourthNum.predictionForCity(cityCode, dates.get(0).getValue(),
//                    dates.get(dates.size() - 1).getValue());
//        }
        List<String> titles = Arrays.asList("50%-55%", "55%-60%", "60%-65%", "65%-70%", "70%-75%",
                "75%-80%", "80%-85%", "85%-90%", "90%-95%", "95%-100%");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> predictResult(String cityCode) {
        List<ParamRelation> dates = getNear12Month();
        List<UserPredictionInfo> retdata = getPredictionDatas(cityCode, dates);
        List<String> titles = Arrays.asList("50%-55%", "55%-60%", "60%-65%", "65%-70%", "70%-75%",
                "75%-80%", "80%-85%", "85%-90%", "90%-95%", "95%-100%");
        return useSameMarkData(titles, retdata, dates);
    }

    private List<UserPredictionInfo> getPredictionDatas(String cityCode,
            List<ParamRelation> dates) {
        List<UserPredictionInfo> retdata = null;
        if (ParamConfig.PROVINCIAL.equals(cityCode)) {// 全省
            retdata = prediction.predictionForProvince(dates.get(0).getValue(),
                    dates.get(dates.size() - 1).getValue());
        } else {// 查询某地市
            retdata = prediction.predictionForCity(cityCode, dates.get(0).getValue(),
                    dates.get(dates.size() - 1).getValue());
        }
        return retdata;
    }

    @Override
    public List<ChartDatas> fourthNotrenewedRate(String cityCode) {
        List<ParamRelation> dates = getPre1year();
        List<UserPredictionInfo> retdata = Collections.emptyList();
        // TODO 3.21演示需要，暂时不从数据库中取数据
//        if (ParamConfig.PROVINCIAL.equals(cityCode)) {// 全省
//            retdata = fourthNum.predictionForProvince(dates.get(0).getValue(),
//                    dates.get(dates.size() - 1).getValue());
//        } else {// 查询某地市
//            retdata = fourthNum.predictionForCity(cityCode, dates.get(0).getValue(),
//                    dates.get(dates.size() - 1).getValue());
//        }
        List<String> titles = Arrays.asList("到期未续约率");
        return useSameMarkData(titles, retdata, dates);
    }

}
