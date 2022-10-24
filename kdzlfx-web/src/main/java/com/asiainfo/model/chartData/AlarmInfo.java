package com.asiainfo.model.chartData;

import java.util.Date;

public class AlarmInfo {

    private String alarm_id;

    private String alarm_name;

    private String alarm_level;

    private String alarm_rule;

    private String alarm_modes;

    private String alarm_msg;

    private Date alarm_time;

    private String alarm_dimension1;

    private String alarm_dimension2;

    private String alarm_dimension3;

    private Date create_time;

    private Date clear_time;

    private String delete_state;

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getAlarm_name() {
        return alarm_name;
    }

    public void setAlarm_name(String alarm_name) {
        this.alarm_name = alarm_name;
    }

    public String getAlarm_level() {
        return alarm_level;
    }

    public void setAlarm_level(String alarm_level) {
        this.alarm_level = alarm_level;
    }

    public String getAlarm_rule() {
        return alarm_rule;
    }

    public void setAlarm_rule(String alarm_rule) {
        this.alarm_rule = alarm_rule;
    }

    public String getAlarm_modes() {
        return alarm_modes;
    }

    public void setAlarm_modes(String alarm_modes) {
        this.alarm_modes = alarm_modes;
    }

    public String getAlarm_msg() {
        return alarm_msg;
    }

    public void setAlarm_msg(String alarm_msg) {
        this.alarm_msg = alarm_msg;
    }

    public Date getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(Date alarm_time) {
        this.alarm_time = alarm_time;
    }

    public String getAlarm_dimension1() {
        return alarm_dimension1;
    }

    public void setAlarm_dimension1(String alarm_dimension1) {
        this.alarm_dimension1 = alarm_dimension1;
    }

    public String getAlarm_dimension2() {
        return alarm_dimension2;
    }

    public void setAlarm_dimension2(String alarm_dimension2) {
        this.alarm_dimension2 = alarm_dimension2;
    }

    public String getAlarm_dimension3() {
        return alarm_dimension3;
    }

    public void setAlarm_dimension3(String alarm_dimension3) {
        this.alarm_dimension3 = alarm_dimension3;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getClear_time() {
        return clear_time;
    }

    public void setClear_time(Date clear_time) {
        this.clear_time = clear_time;
    }

    public String getDelete_state() {
        return delete_state;
    }

    public void setDelete_state(String delete_state) {
        this.delete_state = delete_state;
    }
}
