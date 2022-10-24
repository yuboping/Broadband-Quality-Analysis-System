package com.asiainfo.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesModelEvaluationMapper;
import com.asiainfo.mapper.CesTrainEvaluationMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.Evaluation;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.PredictEvaluation;

@Service
public class PredictEvaluationImpl extends BasicService implements PredictEvaluation {
    @Autowired
    private CesModelEvaluationMapper model;
    @Autowired
    private CesTrainEvaluationMapper train;

    @Override
    public List<ChartDatas> trainEvaluation(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        String model = getModel();
        List<Evaluation> retdata = train.evaluationForMonth("50", model, start, end);
        List<String> titles = Arrays.asList("准确率", "覆盖率", "F1分数");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> modelEvaluation(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<Evaluation> retdata = model.evaluationForMonth("50", cityCode, start, end);
        List<String> titles = Arrays.asList("准确率", "覆盖率", "F1分数");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<Evaluation> datas = (List<Evaluation>) retdata;
        for (Evaluation figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("准确率")) {
                    return figures.getPrecision() == null ? "0" : figures.getPrecision();
                } else if (title.equals("覆盖率")) {
                    return figures.getRecall() == null ? "0" : figures.getRecall();
                } else if (title.equals("F1分数")) {
                    return figures.getF1_score() == null ? "0" : figures.getF1_score();
                }
            }
        }
        return "0";
    }

}
