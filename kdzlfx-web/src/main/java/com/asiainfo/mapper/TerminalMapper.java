package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.TterminalFigure;

public interface TerminalMapper {
	
	@Select(value = "select l.user_name,l.city_code,case when l.badquality=1 then '是' else '否' end as badquality,"
			+ "case when l.inteligentgateway=1 then '是' else '否' end as inteligentgateway,l.SUBDEVICES,ce.CITY_NAME from "
			+ "CES_TERMINAL_CHARACTER_${month} l left join CES_CITY_CODE ce on l.CITY_CODE=ce.CITY_CODE where "
			+ " user_name=#{account} and rownum = 1")
	TterminalFigure getTterminalInfo(@Param("account") String account, @Param("month") String month);

}
