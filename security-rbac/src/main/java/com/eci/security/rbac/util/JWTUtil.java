package com.eci.security.rbac.util;

import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.common.vo.UserPrincipal;
import com.eci.security.rbac.config.JWTConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
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
    @Autowired
    private TokenStore tokenStore;


    protected Oauth2Token createAccessToken(String userName, List<String> roles, Collection<? extends GrantedAuthority> authorities) throws UnsupportedEncodingException {
        String APP_KEY = "test123";
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] b = APP_KEY.getBytes("UTF-8");
        String fuck = encoder.encode(b);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(6000);
        LocalDateTime expireRefreshLocalDate = now.plusSeconds(12000);
        Date expireAccess =  Date.from(expireAccessLocalDate.atZone(myZone).toInstant());
        Date expireRefresh =  Date.from(expireRefreshLocalDate.atZone(myZone).toInstant());
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        JwtBuilder builder1 = Jwts.builder()
                .setId(accessJti)
                .signWith(signatureAlgorithm, fuck)
                .claim("roles", roles)
                .claim("client_id", "testclient001")
                .claim("user_name", userName)
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        builder1.setExpiration(expireAccess);
        String accessToken = builder1.compact();

        JwtBuilder builder2 = Jwts.builder()
                .setId(refreshJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, fuck)
                .claim("roles", roles)
                .claim("ati", accessJti)
                .claim("client_id", "testclient001")
                .claim("user_name", userName)
                .claim("scope", Arrays.asList("all"))
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        builder2.setExpiration(expireRefresh);
        String refreshToken = builder2.compact();
        Oauth2Token oauth2Token = new Oauth2Token();
        oauth2Token.setAccessToken(accessToken);
        oauth2Token.setRefreshToken(refreshToken);
        oauth2Token.setJti(accessJti);
        oauth2Token.setExpiration(expireAccessLocalDate);
        return oauth2Token;

    }

    public Oauth2Token createJWT(Authentication authentication) throws UnsupportedEncodingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Oauth2Token oauth2Token =  createAccessToken(userPrincipal.getUsername(), userPrincipal.getRoles(), authentication.getAuthorities());
        return oauth2Token;
    }

    public void getRefreshToken(String refreshToken) {
        OAuth2RefreshToken a = tokenStore.readRefreshToken(refreshToken);
        System.out.println(a.getValue());
        System.out.println("1`23");

//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtConfig.getKey())
//                .parseClaimsJws(refreshToken)
//                .getBody();
//
//        String username = claims.getSubject();
    }




}
