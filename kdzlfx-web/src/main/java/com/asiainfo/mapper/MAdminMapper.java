package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.system.UserRolesPerms;

public interface MAdminMapper {
    @Results({ @Result(column = "admin", property = "admin"),
            @Result(column = "password", property = "password"),
            @Result(column = "passwordtype", property = "passwordtype"),
            @Result(column = "status", property = "status"),
            @Result(column = "name", property = "name"),
            @Result(column = "locktime", property = "locktime"),
            @Result(column = "corpname", property = "corpname"),
            @Result(column = "coaddr", property = "coaddr"),
            @Result(column = "contactphone", property = "contactphone"),
            @Result(column = "email", property = "email"),
            @Result(column = "modpwddate", property = "modpwddate"),
            @Result(column = "moddate", property = "moddate"),
            @Result(column = "modoperator", property = "modoperator"),
            @Result(column = "description", property = "description"),
            @Result(column = "admin", property = "roles", many = @Many(select = "com.asiainfo.mapper.MAdminRoleMapper.queryRoles")),
            @Result(column = "admin", property = "menutrees", many = @Many(select = "com.asiainfo.mapper.MMenuTreeMapper.getMenuByAdmin")),
            @Result(column = "admin", property = "citys", many = @Many(select = "com.asiainfo.mapper.CesCityCodeMapper.getCityByAdmin")) })
    @Select("select admin,password,passwordtype,status,name,locktime,corpname,coaddr,"
            + "contactphone,email,modpwddate,moddate,modoperator,description"
            + " from M_ADMIN where admin = #{loginName,jdbcType=VARCHAR}")
    UserRolesPerms getAdminByName(String loginName);

}
