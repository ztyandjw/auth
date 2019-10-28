package com.eci.security.rbac.controller;

import com.eci.security.rbac.common.bo.AuthLoginBO;
import com.eci.security.rbac.common.bo.AuthRefreshTokenBO;
import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.service.AuthenticationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;


    @PostMapping("login")
    public CommonResult<Oauth2Token> login(@RequestBody @Validated AuthLoginBO authLoginBO) throws IOException {
        String username = authLoginBO.getUsername();
        String password = authLoginBO.getPassword();
        String providerType = authLoginBO.getProviderType();
        Long appId = authLoginBO.getAppId();
        return CommonResult.success(authenticationService.login(username, password, providerType, appId));
    }


    @PostMapping("refreshToken")
    public CommonResult<Oauth2Token> refreshToken(@RequestBody @Validated AuthRefreshTokenBO authRefreshTokenBO) throws IOException {
        String refreshToken = authRefreshTokenBO.getRefreshToken();
        return CommonResult.success(authenticationService.createRefreshToken(refreshToken));
    }
}
