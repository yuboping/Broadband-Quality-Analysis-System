package com.asiainfo.model.chartData;

public class Evaluation {
    private String attr;
    private String precision;
    private String recall;
    private String f1_score;

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getRecall() {
        return recall;
    }

    public void setRecall(String recall) {
        this.recall = recall;
    }

    public String getF1_score() {
        return f1_score;
    }

    public void setF1_score(String f1_score) {
        this.f1_score = f1_score;
    }

}
