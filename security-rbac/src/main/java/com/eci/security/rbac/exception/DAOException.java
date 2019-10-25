package com.eci.security.rbac.exception;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */

public final class DAOException extends RuntimeException {

    /**
     * 错误码
     */
    private  Integer code;

    public DAOException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public DAOException(Exception e) {
        super(e);
    }

    public Integer getCode() {
        return code;
    }

}