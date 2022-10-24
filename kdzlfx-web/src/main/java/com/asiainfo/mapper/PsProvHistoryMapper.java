package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.OptHistory;

public interface PsProvHistoryMapper {

    @Select("select username,otype operate,itime ctime from USER_HIS where username=#{username} order by itime desc")
    List<OptHistory> getByName(String username);

}