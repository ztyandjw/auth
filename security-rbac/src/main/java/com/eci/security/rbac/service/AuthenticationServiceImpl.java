package com.eci.security.rbac.service;

import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.constant.ProviderTypeEnum;
import com.eci.security.rbac.core.token.LocalUserNamePasswordAuthenticationToken;
import com.eci.security.rbac.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */


@Service
public class AuthenticationServiceImpl {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;

    public Oauth2Token login(String username, String password, String providerType, Long appId) throws UnsupportedEncodingException {
        Authentication authentication = null;
        if(ProviderTypeEnum.equals(providerType, ProviderTypeEnum.LOCAL)) {
            authentication = new LocalUserNamePasswordAuthenticationToken(username, password, appId);
        }
        authentication = authenticationManager.authenticate(authentication);
        return jwtUtil.createJWT(authentication);
    }





}
