package com.asiainfo.model.enumeration;

public enum OperateType {
    OPEN(1, "开户"), MODIFY(2, "修改"), SALESACCOUNT(3, "销户"), ;
    private int type;
    private String name;

    OperateType(int type, String name) {
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getName(int type) {
        for (OperateType opt : OperateType.values()) {
            if (opt.getType() == type) {
                return opt.getName();
            }
        }
        return "";
    }

}
