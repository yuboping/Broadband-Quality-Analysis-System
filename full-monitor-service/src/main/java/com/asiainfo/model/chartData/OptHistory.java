package com.asiainfo.model.chartData;

import java.util.Date;

import com.asiainfo.model.enumeration.OperateType;
import com.asiainfo.util.common.DateUtil;

/**
 * 操作历史
 * 
 * @author luohuawuyin
 *
 */
public class OptHistory {
    private String username;
    private Integer operate;
    private String operateName;
    private Date ctime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
        this.operateName = OperateType.getName(operate);
    }

    public String getCtime() {
        return DateUtil.date2String(ctime);
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getOperateName() {
        return operateName;
    }

}
