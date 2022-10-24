package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.system.CesCityCode;

public interface CesCityCodeMapper {


    @Select("select city_code cityCode,city_name cityName,eparchy_code eparchyCode,dealtime,remark from CES_CITY_CODE")
    List<CesCityCode> getAll();

    @Select("select city_code cityCode,city_name cityName,dealtime,remark from CES_CITY_CODE where city_code != #{code}")
    List<CesCityCode> getCitys(String code);

    @Select("select distinct t3.city_code cityCode,t3.city_name cityName,t3.eparchy_code eparchyCode"
            + " from M_ADMIN_ROLE t1,M_ROLE_PERMISSIONS t2,CES_CITY_CODE t3"
            + " where  t1.roleid=t2.roleid and t2.permissionid=t3.city_code and t2.type='area' and t1.admin=#{0}"
            + " order by t3.city_code")
    List<CesCityCode> getCityByAdmin(String admin);

}