package com.eci.security.rbac.config;


import com.eci.security.rbac.util.JwtAccessTokenConverter;
import com.eci.security.rbac.core.provider.LocalAuthenticationProvider;
import com.eci.security.rbac.core.provider.LocalUserDetailService;
import com.eci.security.rbac.util.JwtTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.provider.token.TokenStore;
import sun.misc.BASE64Encoder;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */
@Configuration
@EnableWebSecurity
public class MySecurityCongiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private  JWTConfig jwtConfig;

    @Autowired
    private LocalUserDetailService myUserDetailService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/login*").permitAll();
    }

    @Bean
    public LocalAuthenticationProvider localAuthenticationProvider() {
        LocalAuthenticationProvider localAuthenticationProvider = new LocalAuthenticationProvider();
        localAuthenticationProvider.setMyUserDetailService(myUserDetailService);
        localAuthenticationProvider.setPasswordEncoder(this.encoder());
        return localAuthenticationProvider;
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JwtTokenStore jwtTokenStore() {

        return new JwtTokenStore(jwtAccessTokenConverter());
    }





    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtConfig.getSignAndVerifyKey());
        converter.setVerifierKey(jwtConfig.getSignAndVerifyKey());
        return converter;
    }

    @Bean
    public BASE64Encoder base64Encoder() {
        return new BASE64Encoder();
    }



    public static void main(String[] args) {
        BCryptPasswordEncoder s = new BCryptPasswordEncoder();

        System.out.println(s.encode("12345611"));
    }


}
