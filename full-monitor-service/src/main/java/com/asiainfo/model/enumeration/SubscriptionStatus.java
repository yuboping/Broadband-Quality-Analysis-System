package com.asiainfo.model.enumeration;

/**
 * 订购类型
 * 
 * @author luohuawuyin
 *
 */
public enum SubscriptionStatus {
    SINGLEWIDTH("0", "单宽"),
    OTHER("1", "其他"),
    ;
    private String type;
    private String name;

    SubscriptionStatus(String type, String name) {
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
        for (SubscriptionStatus opt : SubscriptionStatus.values()) {
            if (opt.getType().equals(type)) {
                return opt.getName();
            }
        }
        return SubscriptionStatus.OTHER.getName();
    }
}
