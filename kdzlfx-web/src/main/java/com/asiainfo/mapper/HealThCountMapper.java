package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.BusinessRet;
import com.asiainfo.model.chartData.UserCharacter;

public interface HealThCountMapper {
    @Select("SELECT USER_NAME,CITY_CODE,UNUSE_TIME"
            + " FROM CES_USER_CHARACTER_${month}" )
    List<UserCharacter> getUserListInfo(@Param("month") String month);
    
    @Select("SELECT MARK,NAME,VALUE FROM CES_BUSINESS_RET WHERE DIMENSION_TYPE =1 AND BUSINESS_TYPE = ${type} ORDER BY VALUE")
    List<ChartPoint> getBusinessRetAreaInfo(@Param("type") int type);
    
    @Select("SELECT t2.CITY_NAME AS MARK,t1.NAME,t1.VALUE FROM CES_BUSINESS_RET t1"
            +" LEFT JOIN CES_CITY_CODE t2 ON t1.MARK = t2.CITY_CODE"
            +" WHERE t1.DIMENSION_TYPE =2 AND t1.BUSINESS_TYPE = ${type} AND t1.MARK=${cityCode} ORDER BY t1.VALUE ASC")
    List<ChartPoint> getBusinessRetUserInfo(@Param("type") int type, @Param("cityCode") String cityCode);
    
    @Select("SELECT 1 AS DIMENSION_TYPE, 1 AS BUSINESS_TYPE, t1.CITY_CODE AS MARK,t2.CITY_NAME AS NAME,t1.HEALTH_VAL AS VALUE FROM"
            + " (SELECT CITY_CODE,ROUND(AVG(HEALTH_VAL),2) AS HEALTH_VAL  from CES_USER_CHARACTER_${month} GROUP BY CITY_CODE) t1" 
            + " LEFT JOIN CES_CITY_CODE t2 ON t1.CITY_CODE = t2.CITY_CODE"
            +" WHERE t1.CITY_CODE!='0016'"
            +" ORDER BY t1.HEALTH_VAL DESC")
    List<BusinessRet> getUserCharacterHealthRet(@Param("month") String month);
    

    
    @Select("SELECT 2 AS DIMENSION_TYPE, 1 AS BUSINESS_TYPE,table_alias.NAME,table_alias.MARK,table_alias.VALUE "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM (  select USER_NAME AS NAME,CITY_CODE AS MARK,HEALTH_VAL AS VALUE from CES_USER_CHARACTER_${month} where CITY_CODE = ${cityCode}" 
            + " ORDER BY HEALTH_VAL ASC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<BusinessRet> getUserCharacterHealthRetByArea(@Param("month") String month, @Param("cityCode") String cityCode);
    
    @Delete("DELETE FROM CES_BUSINESS_RET WHERE BUSINESS_TYPE = ${businessType}")
    void deleteBusinessRet(@Param("businessType") int businessType);
    
    
    @Insert("<script> insert all into CES_BUSINESS_RET(DIMENSION_TYPE,BUSINESS_TYPE,MARK,NAME,VALUE) values " +
            "  <foreach collection='result' item='item' separator=' into CES_BUSINESS_RET(DIMENSION_TYPE,BUSINESS_TYPE,MARK,NAME,VALUE) values' > " +
            "  (#{item.dimension_type},#{item.business_type},#{item.mark},#{item.name},#{item.value}) \n" +
            "  </foreach>  select 1 from dual </script>")
    Boolean insertBusinessRet(@Param(value = "result") List<BusinessRet> data);
    
    @Select("SELECT 1 AS DIMENSION_TYPE, 3 AS BUSINESS_TYPE, t1.CITY_CODE AS MARK,t2.CITY_NAME AS NAME,t1.HEALTH_VAL AS VALUE FROM"
            + " (SELECT CITY_CODE,ROUND(AVG(HEALTH_VAL),2) AS HEALTH_VAL  from CES_COMPLAINT_CHARACTER_${month} GROUP BY CITY_CODE) t1" 
            + " LEFT JOIN CES_CITY_CODE t2 ON t1.CITY_CODE = t2.CITY_CODE"
            +" WHERE t1.CITY_CODE!='0016'"
            +" ORDER BY t1.HEALTH_VAL DESC")
    List<BusinessRet> getComplaintCharacterHealthRet(@Param("month") String month);
    
    @Select("SELECT 2 AS DIMENSION_TYPE, 3 AS BUSINESS_TYPE,table_alias.NAME,table_alias.MARK,table_alias.VALUE "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM (  select USER_NAME AS NAME,CITY_CODE AS MARK,HEALTH_VAL AS VALUE from CES_COMPLAINT_CHARACTER_${month} where CITY_CODE = ${cityCode}" 
            + " ORDER BY HEALTH_VAL ASC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<BusinessRet> getComplaintCharacterHealthRetByArea(@Param("month") String month, @Param("cityCode") String cityCode);
    
    @Select("SELECT 1 AS DIMENSION_TYPE, ${businessType} AS BUSINESS_TYPE, t1.CITY_CODE AS MARK,t2.CITY_NAME AS NAME,t1.HEALTH_VAL AS VALUE FROM"
            + " (SELECT CITY_CODE,ROUND(AVG(HEALTH_VAL),2) AS HEALTH_VAL  from ${table}_${month} GROUP BY CITY_CODE) t1" 
            + " LEFT JOIN CES_CITY_CODE t2 ON t1.CITY_CODE = t2.CITY_CODE"
            +" WHERE t1.CITY_CODE!='0016'"
            +" ORDER BY t1.HEALTH_VAL DESC")
    List<BusinessRet> getCharacterHealthRet(@Param("month") String month, @Param("table") String table,
            @Param("businessType") int businessType);
    
    @Select("SELECT 2 AS DIMENSION_TYPE, ${businessType} AS BUSINESS_TYPE,table_alias.NAME,table_alias.MARK,table_alias.VALUE "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM (  select USER_NAME AS NAME,CITY_CODE AS MARK,HEALTH_VAL AS VALUE from ${table}_${month} where CITY_CODE = ${cityCode}" 
            + " ORDER BY HEALTH_VAL ASC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<BusinessRet> getCharacterHealthRetByArea(@Param("month") String month, @Param("table") String table,
            @Param("cityCode") String cityCode, @Param("businessType") int businessType);
    
    @Select("SELECT 1 AS DIMENSION_TYPE, 2 AS BUSINESS_TYPE, t1.CITY_CODE AS MARK,t2.CITY_NAME AS NAME,t1.HEALTH_VAL AS VALUE FROM"
            + " (SELECT CITY_CODE,ROUND(AVG(HEALTH_VAL),2) AS HEALTH_VAL  from CES_TERMINAL_CHARACTER_${month} GROUP BY CITY_CODE) t1" 
            + " LEFT JOIN CES_CITY_CODE t2 ON t1.CITY_CODE = t2.CITY_CODE"
            +" WHERE t1.CITY_CODE!='0016'"
            +" ORDER BY t1.HEALTH_VAL DESC")
    List<BusinessRet> getTerminalCharacterHealthRet(@Param("month") String month);
    
    @Select("SELECT 2 AS DIMENSION_TYPE, 2 AS BUSINESS_TYPE,table_alias.NAME,table_alias.MARK,table_alias.VALUE "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM (  select USER_NAME AS NAME,CITY_CODE AS MARK,HEALTH_VAL AS VALUE from CES_TERMINAL_CHARACTER_${month} where CITY_CODE = ${cityCode}" 
            + " ORDER BY HEALTH_VAL ASC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<BusinessRet> getTerminalCharacterHealthRetByArea(@Param("month") String month, @Param("cityCode") String cityCode);
    
}
