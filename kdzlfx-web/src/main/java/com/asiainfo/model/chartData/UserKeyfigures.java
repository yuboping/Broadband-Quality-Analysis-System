package com.asiainfo.model.chartData;

/**
 * 用户关键指标：新增、销户、净增、停机、累计等用户数
 * 
 * @author luohuawuyin
 *
 */
public class UserKeyfigures {
    private String attr;
    private String addNum;
    private String cancelNum;
    private String ownNum;
    private String activeNum;
    private String stopNum;
    private String silentNum;
    private String cumulativeNum;
    private String regNum;
    private String secondOffnetNum;
    private String thirdOffnetNum;
    private String statisticsDate;
    private String cityCode;

    public static UserKeyfigures empty() {// 查询结果为空时的对象
        UserKeyfigures figures = new UserKeyfigures();
        figures.setActiveNum("0");
        figures.setAddNum("0");
        figures.setCancelNum("0");
        figures.setOwnNum("0");
        figures.setRegNum("0");
        figures.setSilentNum("0");
        figures.setStopNum("0");
        figures.setCumulativeNum("0");
        return figures;
    }

    public String getSecondOffnetNum() {
        return secondOffnetNum;
    }

    public void setSecondOffnetNum(String secondOffnetNum) {
        this.secondOffnetNum = secondOffnetNum;
    }

    public String getThirdOffnetNum() {
        return thirdOffnetNum;
    }

    public void setThirdOffnetNum(String thirdOffnetNum) {
        this.thirdOffnetNum = thirdOffnetNum;
    }

    public String getAddNum() {
        return addNum;
    }

    public void setAddNum(String addNum) {
        this.addNum = addNum;
    }

    public String getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(String cancelNum) {
        this.cancelNum = cancelNum;
    }

    public String getOwnNum() {
        return ownNum;
    }

    public void setOwnNum(String ownNum) {
        this.ownNum = ownNum;
    }

    public String getActiveNum() {
        return activeNum;
    }

    public void setActiveNum(String activeNum) {
        this.activeNum = activeNum;
    }

    public String getStopNum() {
        return stopNum;
    }

    public void setStopNum(String stopNum) {
        this.stopNum = stopNum;
    }

    public String getSilentNum() {
        return silentNum;
    }

    public void setSilentNum(String silentNum) {
        this.silentNum = silentNum;
    }

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getCumulativeNum() {
        return cumulativeNum;
    }

    public void setCumulativeNum(String cumulativeNum) {
        this.cumulativeNum = cumulativeNum;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

}
