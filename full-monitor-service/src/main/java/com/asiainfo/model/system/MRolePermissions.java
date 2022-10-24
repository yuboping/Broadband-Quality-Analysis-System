package com.asiainfo.model.system;

/**
 * 用户与角色关系
 * 
 * @author luohuawuyin
 *
 */
public class MRolePermissions {
    private String admin;
    private String roleid;
    private String name;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
