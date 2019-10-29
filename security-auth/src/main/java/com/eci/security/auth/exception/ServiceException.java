package com.eci.security.auth.exception;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */

public final class ServiceException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(Integer code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}