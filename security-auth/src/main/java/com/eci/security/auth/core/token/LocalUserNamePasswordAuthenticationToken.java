package com.eci.security.auth.core.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 * 账号名密码token包装类（UsernamePasswordAuthenticationToken）
 */

public class LocalUserNamePasswordAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    public Long getAppId() {
        return appId;
    }

    private Long appId;
    private final Object principal;
    private Object credentials;

    // ~ Constructors
    // ===================================================================================================

    public LocalUserNamePasswordAuthenticationToken(Object principal, Object credentials, Long appId) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.appId = appId;
        setAuthenticated(false);
    }


    public LocalUserNamePasswordAuthenticationToken(Object principal, Object credentials,
                                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }

    // ~ Methods
    // ========================================================================================================

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}

