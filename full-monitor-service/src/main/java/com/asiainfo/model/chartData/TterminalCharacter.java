package com.asiainfo.model.chartData;

public class TterminalCharacter {
    private String user_name;
    private String city_code;
    private String city_name;
    // 厂家名称
    private String company_name;
    // 认证失败质差次数
    private Integer authfail_count;
    // 短时上下线质差次数
    private Integer shorttime_count;
    // 频繁掉线终端质差次数
    private Integer oftendown_count;
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getCity_code() {
        return city_code;
    }
    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }
    public String getCity_name() {
        return city_name;
    }
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
    public String getCompany_name() {
        return company_name;
    }
    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
    public Integer getAuthfail_count() {
        return authfail_count;
    }
    public void setAuthfail_count(Integer authfail_count) {
        this.authfail_count = authfail_count;
    }
    public Integer getShorttime_count() {
        return shorttime_count;
    }
    public void setShorttime_count(Integer shorttime_count) {
        this.shorttime_count = shorttime_count;
    }
    public Integer getOftendown_count() {
        return oftendown_count;
    }
    public void setOftendown_count(Integer oftendown_count) {
        this.oftendown_count = oftendown_count;
    }
}
