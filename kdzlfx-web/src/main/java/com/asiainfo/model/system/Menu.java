package com.asiainfo.model.system;

import java.util.List;

/**
 * 菜单
 * 
 * @author luohuawuyin
 *
 */
public class Menu {
    private String id;
    private String name;
    private String show_name;
    private String url;
    private boolean active;
    private Integer menu_level;
    private List<Menu> childen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShow_name() {
        return show_name;
    }

    public void setShow_name(String show_name) {
        this.show_name = show_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getMenu_level() {
        return menu_level;
    }

    public void setMenu_level(Integer menu_level) {
        this.menu_level = menu_level;
    }

    public List<Menu> getChilden() {
        return childen;
    }

    public void setChilden(List<Menu> childen) {
        this.childen = childen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
