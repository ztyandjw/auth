package com.eci.security.rbac.core;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */

public class UserNamePasswordAppidAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Integer appId;

    public Integer getAppId() {
      return appId;
    }

    public UserNamePasswordAppidAuthenticationToken(Object principal, Object credentials, Integer appId) {
        super(principal, credentials);
        this.appId = appId;
    }
}
