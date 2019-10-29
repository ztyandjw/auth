package com.eci.security.auth.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/11.
 */

public enum ProviderTypeEnum {

    LOCAL(1, "LOCAL"),
    AD(2, "ad"),
    OA(3, "OA"),
    WEBCHAT(5, "WEBCHAT"),
    QQ(6, "QQ"),
    ;

    /**
     * 类型值
     */
    private final Integer value;
    /**
     * 类型名
     */
    private final String name;

    ProviderTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Boolean equals(String var1, ProviderTypeEnum var2) {
        return StringUtils.equalsIgnoreCase(var1, var2.getName());
    }

}
