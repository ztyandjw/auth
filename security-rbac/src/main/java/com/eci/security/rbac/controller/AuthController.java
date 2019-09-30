package com.eci.security.rbac.controller;

import com.eci.security.rbac.common.bo.AuthLoginBO;
import com.eci.security.rbac.common.dataobject.RoleDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.core.UserNamePasswordAppidAuthenticationToken;
import com.eci.security.rbac.dao.RoleDAO;
import com.eci.security.rbac.dao.UserDAO;
import com.eci.security.rbac.service.UserServiceImpl;
import com.eci.security.rbac.util.JWTUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */


@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
//
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleDAO roleDAO;



    @Autowired
    private JWTUtil jwtUtil;



//    @Autowired
//    private MyTokenEndpoint myTokenEndpoint;

    @GetMapping("/get")
    public String test() {
        List<RoleDO> r = roleDAO.getRolesByUserid(1L);
        return "123";

    }




    @PostMapping("login")
    public CommonResult<Oauth2Token> login(@RequestBody @Validated AuthLoginBO authLoginBO) {
        Authentication authentication = authenticationManager.authenticate(new UserNamePasswordAppidAuthenticationToken(authLoginBO.getUsername(), authLoginBO.getPassword(), authLoginBO.getAppId()));


        SecurityContextHolder.getContext().setAuthentication(authentication);
        Oauth2Token jwt = jwtUtil.createJWT(authentication);
        return CommonResult.success(jwt);
    }


//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter(){
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey("test123");
//        return converter;
//    }
//
//    protected AuthenticationManager getAuthenticationManager() {
//        return authenticationManager;
//    }
//
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }




}
