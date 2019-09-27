package com.eci.security.rbac.controller;

import com.eci.security.rbac.common.bo.AuthLoginBO;
import com.eci.security.rbac.common.dataobject.RoleDO;
import com.eci.security.rbac.common.dataobject.UserDO;
import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.core.UserNamePasswordAppidAuthenticationToken;
import com.eci.security.rbac.dao.RoleDAO;
import com.eci.security.rbac.dao.UserDAO;
import com.eci.security.rbac.service.UserServiceImpl;
import com.eci.security.rbac.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private UserServiceImpl userService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/get")
    public String test() {
        List<RoleDO> r = roleDAO.getRolesByUserid(1L);
        return "123";

    }

    @PostMapping("login")
    public CommonResult<String> login(@RequestBody @Validated AuthLoginBO authLoginBO) {
        Authentication authentication = authenticationManager.authenticate(new UserNamePasswordAppidAuthenticationToken(authLoginBO.getUsername(), authLoginBO.getPassword(), authLoginBO.getAppId()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.createJWT(authentication);
        return CommonResult.success(jwt);
    }
//
//    protected AuthenticationManager getAuthenticationManager() {
//        return authenticationManager;
//    }
//
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }




}
