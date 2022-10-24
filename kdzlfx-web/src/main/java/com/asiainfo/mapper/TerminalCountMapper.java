package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.BusinessRet;
import com.asiainfo.model.chartData.TerminalRest;
import com.asiainfo.model.chartData.TterminalCharacter;

public interface TerminalCountMapper {
    @Select("SELECT DATA_TYPE,ATTR_CODE,ATTR_NAME,MVAL FROM CES_TERMINAL_REST WHERE DIMENSION_TYPE = ${type} ORDER BY MVAL")
    List<TerminalRest> getMonitorTerminalData(@Param("type") int type);
    
    @Delete("DELETE FROM CES_TERMINAL_REST WHERE DIMENSION_TYPE = ${dimensionType}")
    void deleteTerminalRet(@Param("dimensionType") int dimensionType);
    
    /**
     * 汇总地市信息
     * @param month
     * @return
     */
    @Select("SELECT t1.CITY_CODE,t2.CITY_NAME,t1.AUTHFAIL_COUNT,t1.SHORTTIME_COUNT,t1.OFTENDOWN_COUNT"
            +" FROM( SELECT CITY_CODE, SUM(AUTHFAIL_FLAG) AS AUTHFAIL_COUNT,"
            +" SUM(SHORTTIME_FLAG) AS SHORTTIME_COUNT,SUM(OFTENDOWN_FLAG) AS OFTENDOWN_COUNT"
            +" FROM CES_TERMINAL_CHARACTER_${month} GROUP BY CITY_CODE ) t1"
            +" LEFT JOIN CES_CITY_CODE t2 ON t1.CITY_CODE = t2.CITY_CODE")
    List<TterminalCharacter> getTerminalCharacterData(@Param("month") String month);
    
    @Insert("<script> insert all into CES_TERMINAL_REST(DIMENSION_TYPE,DATA_TYPE,ATTR_CODE,ATTR_NAME,MVAL) values " +
            "  <foreach collection='result' item='item' separator=' into CES_TERMINAL_REST(DIMENSION_TYPE,DATA_TYPE,ATTR_CODE,ATTR_NAME,MVAL) values' > " +
            "  (#{item.dimension_type},#{item.data_type},#{item.attr_code},#{item.attr_name},#{item.mval}) \n" +
            "  </foreach>  select 1 from dual </script>")
    Boolean insertTerminalRet(@Param(value = "result") List<TerminalRest> data);
    
    @Select("SELECT table_alias.USER_NAME,table_alias.CITY_CODE,table_alias.${field2} "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM (  select USER_NAME,CITY_CODE,${field2} from CES_TERMINAL_CHARACTER_${month} WHERE ${field1} = 1" 
            + " ORDER BY ${field2} DESC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<TterminalCharacter> getTerminalCharacterDataOrderByUser(@Param("month") String month,
            @Param("field1") String field1, @Param("field2") String field2);
    
    /**
     * 获取厂家数据
     * @param month
     * @param field1
     * @param field2
     * @return
     */
    @Select("SELECT table_alias.COMPANY_NAME,table_alias.${asfield} "
            + " FROM (SELECT tt.*, ROWNUM AS rowno"
            + " FROM ( select COMPANY_NAME,SUM(${field}) AS ${asfield} from COMPANY_USER_BQ_${month} WHERE COMPANY_NAME IS NOT NULL" 
            + " GROUP BY COMPANY_NAME ORDER BY SUM(${field}) DESC) tt WHERE ROWNUM <= 10) table_alias WHERE table_alias.rowno >= 0")
    List<TterminalCharacter> getCompanyData(@Param("month") String month,
             @Param("field") String field, @Param("asfield") String asfield);
}
