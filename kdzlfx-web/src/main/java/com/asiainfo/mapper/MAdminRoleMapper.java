package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

/**
 * 角色表
 * 
 * @author luohuawuyin
 *
 */
public interface MAdminRoleMapper {
    @Select("select distinct roleid from M_ADMIN_ROLE where admin = #{0}")
    List<String> queryRoles();

}
