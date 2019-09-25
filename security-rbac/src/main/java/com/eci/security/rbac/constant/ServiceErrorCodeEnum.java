package com.eci.security.rbac.constant;

/**
 * 错误码枚举类
 *
 * 验证模块，使用 1-001-000-000 段
 */
public enum ServiceErrorCodeEnum {

    // 验证模块
    NULL_POINTER_ERROR(1001001000, "用户不存在"),
    ;


    private final int code;
    private final String message;

    ServiceErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
