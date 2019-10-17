package com.eci.security.rbac.controller;

import com.eci.security.rbac.common.bo.AuthLoginBO;
import com.eci.security.rbac.common.bo.AuthRefreshTokenBO;
import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.service.AuthenticationServiceImpl;
import com.eci.security.rbac.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("login")
    public CommonResult<Oauth2Token> login(@RequestBody @Validated AuthLoginBO authLoginBO) throws UnsupportedEncodingException {
        String username = authLoginBO.getUsername();
        String password = authLoginBO.getPassword();
        String providerType = authLoginBO.getProviderType();
        Long appId = authLoginBO.getAppId();
        Oauth2Token token = authenticationService.login(username, password, providerType, appId);
        return CommonResult.success(token);
    }


    @PostMapping("refreshToken")
    public CommonResult<Oauth2Token> refreshToken(@RequestBody @Validated AuthRefreshTokenBO authRefreshTokenBO) throws UnsupportedEncodingException {
        String refreshToken = authRefreshTokenBO.getRefreshToken();
        Oauth2Token token = jwtUtil.createRefreshToken(refreshToken);
        return CommonResult.success(token);
    }
}
