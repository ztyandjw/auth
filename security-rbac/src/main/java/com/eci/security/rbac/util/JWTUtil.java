package com.eci.security.rbac.util;

import com.eci.security.rbac.common.vo.Oauth2Token;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
    protected Oauth2Token createAccessToken(Long id, String userName, List<String> roles) {
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(6000);
        LocalDateTime expireRefreshLocalDate = now.plusSeconds(12000);
        Date expireAccess =  Date.from(expireAccessLocalDate.atZone(myZone).toInstant());
        Date expireRefresh =  Date.from(expireRefreshLocalDate.atZone(myZone).toInstant());

        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();
        JwtBuilder builder = Jwts.builder()
                .setId(accessJti)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getKey())
                .claim("roles", roles)
                .claim("client_id", "testclient001")
                .claim("user_name", userName);
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();

        builder = Jwts.builder()
                .setId(refreshJti)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getKey())
                .claim("roles", roles)
                .claim("ati", accessJti)
                .claim("client_id", "testclient001")
                .claim("user_name", userName);
        builder.setExpiration(expireRefresh);
        String refreshToken = builder.compact();
        Oauth2Token oauth2Token = new Oauth2Token();
        oauth2Token.setAccessToken(accessToken);
        oauth2Token.setRefreshToken(refreshToken);
        oauth2Token.setJti(accessJti);

        oauth2Token.setExpiration(expireAccessLocalDate);

        return oauth2Token;

    }

    public Oauth2Token createJWT(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Oauth2Token oauth2Token =  createAccessToken(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getRoles());
        return oauth2Token;
    }


}
