package com.eci.security.rbac.constant;

/**
 * 错误码枚举类
 *
 * 验证模块，使用 1-001-000-000 段
 */
public enum ErrorCoceEnum {


    // ========== OAUTH2 模块 ==========
    OAUTH2_UNKNOWN(1001001000, "未知错误"), // 预留
    OAUTH2_INVALID_GRANT_BAD_CREDENTIALS(1001001001, "账号或者密码不正确"), // 暂时没用到
//    OAUTH_INVALID_ACCESS_TOKEN_NOT_FOUND(1001001010, "访问令牌不存在"),
//    OAUTH_INVALID_ACCESS_TOKEN_EXPIRED(1001001011, "访问令牌已过期"),
//    OAUTH_INVALID_ACCESS_TOKEN_INVALID(1001001100, "访问令牌已失效"),
    OAUTH2_TOKEN_NOT_FOUND(1001001101, "令牌不存在，请重新登录"),
    OAUTH_INVALID_REFRESH_TOKEN_NOT_FOUND(1001001021, "刷新令牌不存在，请重新登录"),
    OAUTH_INVALID_REFRESH_TOKEN(1001001021, "刷新令牌不正确"),
    OAUTH_INVALID_REFRESH_TOKEN_EXPIRED(1001001022, "刷新令牌已过期，请重新登录"),
    //OAUTH_INVALID_REFRESH_TOKEN_INVALID(1001001023, "刷新令牌已失效"),

    // ========== OAUTH2 模块 ==========
    SERVICE_INTERNAL_ERROR(500, "服务内部错误"),
    SERVICE_ERROR_REQUEST(400, "请求错误");

    ;


    private final int code;
    private final String message;

    ErrorCoceEnum(int code, String message) {
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
