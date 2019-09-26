package com.eci.security.rbac.util;

import com.eci.security.rbac.common.vo.UserPrincipal;
import com.eci.security.rbac.config.JWTConfig;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */



@EnableConfigurationProperties(JWTConfig.class)
@Slf4j
@Component
public class JWTUtil {

        @Autowired
        private JWTConfig jwtConfig;

    /**
     *
     * 创建JWT
     *
     * @param id  用户id
     * @param userName  用户名
     * @param roles  角色列表
     * @return
     */
    protected String createJWT(Long id, String userName, List<String> roles) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .setId(id.toString())
                .setSubject(userName)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getKey())
                .claim("roles", roles);
        builder.setExpiration(null);
        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(600);
        Date expireDate =  Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        builder.setExpiration(expireDate);
        String jwt = builder.compact();
        return jwt;
    }

    public String createJWT(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createJWT(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getRoles());
    }




}
