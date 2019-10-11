package com.eci.security.rbac.config;


import com.eci.security.rbac.core.provider.LocalAuthenticationProvider;
import com.eci.security.rbac.core.provider.LocalUserDetailService;
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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */
@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LocalUserDetailService myUserDetailService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/login*").permitAll();
//        http.formLogin() // 表单方式
//                .and()
//                .authorizeRequests() // 授权配置
//                .anyRequest()  // 所有请求
//                .authenticated(); // 都需要认证
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


//    @Bean
//    public InitializeUserDetailsBeanManagerConfigurer initializeUserDetailsBeanManagerConfigurer(ApplicationContext context) {
//        return new InitializeUserDetailsBeanManagerConfigurer(context);
//    }

    /**
     * 本地账号名密码验证provider
     */
    @Bean
    public LocalAuthenticationProvider localAuthenticationProvider() {
        LocalAuthenticationProvider localAuthenticationProvider = new LocalAuthenticationProvider();
        localAuthenticationProvider.setMyUserDetailService(myUserDetailService);
        localAuthenticationProvider.setPasswordEncoder(this.encoder());
        return localAuthenticationProvider;
    }




    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("test123");
        return converter;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder s = new BCryptPasswordEncoder();

        System.out.println(s.encode("12345611"));
    }


}
