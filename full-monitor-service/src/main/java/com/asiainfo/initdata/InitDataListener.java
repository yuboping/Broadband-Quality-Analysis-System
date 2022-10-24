package com.asiainfo.initdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesCityCodeMapper;
import com.asiainfo.model.system.CesCityCode;
import com.asiainfo.model.system.UserRolesPerms;
import com.asiainfo.util.common.Judgment;
import com.asiainfo.util.config.ParamConfig;

/**
 * 初始化加载数据到内存
 * 
 * @author qinwoli
 * 
 */
@Service
public class InitDataListener implements InitializingBean {
    @Inject
    private CesCityCodeMapper cityMapper;
    private static final Map<String, UserRolesPerms> USERMAP = new HashMap<>();
    private static List<CesCityCode> LS_CITY = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        LS_CITY = cityMapper.getCitys(ParamConfig.PROVINCIAL);
    }

    /**
     * 根据用户名查询用户的角色、菜单信息
     * 
     */
    public UserRolesPerms getUserRolesPerms(String admin) {
        return USERMAP.get(admin);
    }

    public void putUserRolesPerms(UserRolesPerms user) {
        USERMAP.put(user.getAdmin(), user);
    }

    /**
     * 根据cityCode获取cityName
     * 
     * @param cityCode
     * @return
     */
    public String getCityName(String cityCode) {
        if (Judgment.listIsNull(LS_CITY)) {
            return "";
        }
        for (CesCityCode area : LS_CITY) {
            if (area.getCityCode().equals(cityCode)) {
                return area.getCityName();
            }
        }
        return "";
    }

    public List<CesCityCode> getAllCitys() {
        return LS_CITY;
    }

}
