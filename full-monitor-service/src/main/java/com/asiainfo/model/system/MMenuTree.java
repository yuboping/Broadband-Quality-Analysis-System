package com.asiainfo.model.system;

public class MMenuTree {
    private String id;
    private String name;
    private String show_name;
    private Integer is_menu;
    private Integer is_grant;
    private Integer is_show;
    private String url;
    private String parent_id;
    private Integer sequence;
    private Integer menu_level;
    private String dynamic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getIs_menu() {
        return is_menu;
    }

    public void setIs_menu(Integer is_menu) {
        this.is_menu = is_menu;
    }

    public Integer getIs_grant() {
        return is_grant;
    }

    public void setIs_grant(Integer is_grant) {
        this.is_grant = is_grant;
    }

    public Integer getIs_show() {
        return is_show;
    }

    public void setIs_show(Integer is_show) {
        this.is_show = is_show;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getMenu_level() {
        return menu_level;
    }

    public void setMenu_level(Integer menu_level) {
        this.menu_level = menu_level;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

}
