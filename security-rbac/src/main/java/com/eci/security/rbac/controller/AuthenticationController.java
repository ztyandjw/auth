//package com.eci.security.rbac.controller;
//
//import com.eci.security.rbac.common.bo.AuthLoginBO;
//import com.eci.security.rbac.common.vo.CommonResult;
//import com.eci.security.rbac.common.vo.Oauth2Token;
//import com.eci.security.rbac.core.UserNamePasswordAppidAuthenticationToken;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author T1m Zhang(49244143@qq.com) 2019/10/10.
// */
//@RestController
//@RequestMapping("/auth")
//@Slf4j
//public class AuthenticationController {
//
//    @PostMapping("login")
//    public CommonResult<Oauth2Token> login(@RequestBody @Validated AuthLoginBO authLoginBO) {
//        String userName = authLoginBO.getUsername();
//        String password = authLoginBO.getPassword();
//        String providerType = authLoginBO.getProviderType();
//        Long appId = authLoginBO.getAppId();
//        Authentication authentication = authenticationManager.authenticate(new UserNamePasswordAppidAuthenticationToken(authLoginBO.getUsername(), authLoginBO.getPassword(), authLoginBO.getAppId()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Oauth2Token jwt = jwtUtil.createJWT(authentication);
//        return CommonResult.success(jwt);
//    }
//}
