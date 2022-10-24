package com.asiainfo.model.chartData;

public class UserCharacter {
    private String user_name;
    private String city_code;
    private float unuse_time; // 估算健康值用
    private float health_val;
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
    public float getUnuse_time() {
        return unuse_time;
    }
    public void setUnuse_time(float unuse_time) {
        this.unuse_time = unuse_time;
    }
    public float getHealth_val() {
        return health_val;
    }
    public void setHealth_val(float health_val) {
        this.health_val = health_val;
    }
}
