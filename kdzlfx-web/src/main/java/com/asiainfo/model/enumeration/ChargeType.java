package com.asiainfo.model.enumeration;

/**
 * 费用类型
 * 
 * @author luohuawuyin
 *
 */
public enum ChargeType {
    MONTH("1", "包月"),
    YEAR("2", "包年"),
    OTHER("3", "其他"),
    ;
    private String type;
    private String name;

    ChargeType(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getName(String type) {
        for (ChargeType opt : ChargeType.values()) {
            if (opt.getType().equals(type)) {
                return opt.getName();
            }
        }
        return "--";
    }

}
