//package com.eci.security.rbac.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//
///**
// * @author T1m Zhang(49244143@qq.com) 2019/9/29.
// */
//
//@Configuration
//public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory()
//                .withClient("clientIdTest01")
//                .secret("clientsecrettest001")
//                .authorizedGrantTypes("authorization_code", "refresh_token")
//                .scopes("all");
//    }
//
//}
