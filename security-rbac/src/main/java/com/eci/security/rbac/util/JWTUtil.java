package com.eci.security.rbac.util;

import com.eci.security.rbac.common.dataobject.ResourceDO;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */



@EnableConfigurationProperties(JWTConfig.class)
@Slf4j
@Component
public class JWTUtil {

        @Autowired
        private JWTConfig jwtConfig;


    protected Oauth2Token createAccessToken(String userName, List<String> roles, Collection<? extends GrantedAuthority> authorities) {
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
                .claim("user_name", userName)
                .claim("authorities", authorities);
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();

        builder = Jwts.builder()
                .setId(refreshJti)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getKey())
                .claim("roles", roles)
                .claim("ati", accessJti)
                .claim("client_id", "testclient001")
                .claim("user_name", userName)
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
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
        Oauth2Token oauth2Token =  createAccessToken(userPrincipal.getUsername(), userPrincipal.getRoles(), authentication.getAuthorities());
        return oauth2Token;
    }


}
