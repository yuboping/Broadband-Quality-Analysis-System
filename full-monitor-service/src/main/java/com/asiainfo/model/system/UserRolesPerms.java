package com.asiainfo.model.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户信息、角色信息及菜单权限
 * 
 * @author luohuawuyin
 *
 */
public class UserRolesPerms {
    private String admin;
    private String password;
    private Integer passwordtype;
    private Integer status;
    private String name;
    private Date locktime;
    private String corpname;
    private String coaddr;
    private String contactphone;
    private String email;
    private Date modpwddate;
    private Date moddate;
    private String modoperator;
    private String description;
    private List<String> roles;
    private List<String> permissions;
    private List<MMenuTree> menutrees;
    private List<Menu> menu;
    private List<CesCityCode> citys;

    public List<CesCityCode> getCitys() {
        return citys;
    }

    public void setCitys(List<CesCityCode> citys) {
        this.citys = citys;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<MMenuTree> getMenutrees() {
        return menutrees;
    }

    public void setMenutrees(List<MMenuTree> menutrees) {
        this.menutrees = menutrees;
        if (menutrees != null) {// 将菜单树中的菜单url加入到用户权限列表中去
            setPermissions(menutrees);
            setMenu(menutrees);
        }

    }

    private void setMenu(List<MMenuTree> menutrees) {
        this.menu = makeMenu(menutrees, "0");
    }

    private List<Menu> makeMenu(List<MMenuTree> menutrees, String parentId) {
        List<Menu> ls = new ArrayList<>();
        for (MMenuTree tree : menutrees) {
            if (tree.getParent_id().equals(parentId)) {
                Menu me = new Menu();
                me.setId(tree.getId());
                me.setName(tree.getName());
                me.setShow_name(tree.getShow_name());
                me.setUrl(tree.getUrl());
                me.setMenu_level(tree.getMenu_level());
                List<Menu> child = makeMenu(menutrees, me.getId());
                me.setChilden(child);
                ls.add(me);
            }
        }
        return ls;
    }

    private void setPermissions(List<MMenuTree> menutrees) {
        List<String> ls = new ArrayList<>();
        for (MMenuTree tree : menutrees) {
            ls.add(tree.getUrl());
        }
        this.permissions = ls;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPasswordtype() {
        return passwordtype;
    }

    public void setPasswordtype(Integer passwordtype) {
        this.passwordtype = passwordtype;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLocktime() {
        return locktime;
    }

    public void setLocktime(Date locktime) {
        this.locktime = locktime;
    }

    public String getCorpname() {
        return corpname;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public String getCoaddr() {
        return coaddr;
    }

    public void setCoaddr(String coaddr) {
        this.coaddr = coaddr;
    }

    public String getContactphone() {
        return contactphone;
    }

    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getModpwddate() {
        return modpwddate;
    }

    public void setModpwddate(Date modpwddate) {
        this.modpwddate = modpwddate;
    }

    public Date getModdate() {
        return moddate;
    }

    public void setModdate(Date moddate) {
        this.moddate = moddate;
    }

    public String getModoperator() {
        return modoperator;
    }

    public void setModoperator(String modoperator) {
        this.modoperator = modoperator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<Menu> getMenu() {
        return menu;
    }
}
