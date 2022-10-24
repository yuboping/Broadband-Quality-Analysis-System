package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.system.MMenuTree;

/**
 * 菜单表
 * 
 * @author luohuawuyin
 *
 */
public interface MMenuTreeMapper {
    @Select("select * from M_MENU_TREE")
    List<MMenuTree> getAllMenu();

    @Select("select distinct t3.* from M_ADMIN_ROLE t1,M_ROLE_PERMISSIONS t2,M_MENU_TREE t3"
            + " where  t1.roleid=t2.roleid and t2.permissionid=t3.id and t2.type='menu' and t1.admin=#{0}"
            + " order by t3.menu_level,t3.sequence")
    List<MMenuTree> getMenuByAdmin(String admin);
}
