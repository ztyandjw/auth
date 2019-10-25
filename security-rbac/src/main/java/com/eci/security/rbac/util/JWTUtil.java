package com.eci.security.rbac.util;

import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.common.vo.UserPrincipal;
import com.eci.security.rbac.config.JWTConfig;

import com.eci.security.rbac.constant.ErrorCoceEnum;
import com.eci.security.rbac.dao.AppDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    private  static final BASE64Encoder base64Encoder = new BASE64Encoder();


    private String getBase64EncodedSecretKey(String key) throws UnsupportedEncodingException {
        byte[] bytes;
        try {
            bytes = key.getBytes("UTF-8");
            return base64Encoder.encode(bytes);
        } catch (UnsupportedEncodingException e) {
            log.error("{} 无法进行base64编码", key);
            throw e;
        }
    }

    public String createAccessTokenValue(String userName, List<String> roles, List<String> authorities, Long appId) throws UnsupportedEncodingException {
        String base64EncodedSecretKey = this.getBase64EncodedSecretKey(jwtConfig.getSignAndVerifyKey());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(jwtConfig.getAccessTokenExpireSeconds());
        Date expireAccess =  Date.from(expireAccessLocalDate.atZone(myZone).toInstant());
        String accessJti = UUID.randomUUID().toString();
        JwtBuilder builder = Jwts.builder()
                .setId(accessJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .claim("roles", roles)
                .claim("client_id", appId)
                .claim("user_name", userName)
                .claim("authorities", authorities);
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();
        return accessToken;
    }


    public Oauth2Token createAccessAndRefreshOauth2Token(Authentication authentication) throws UnsupportedEncodingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getRoles();
        String username = userPrincipal.getUsername();
        Long appId = userPrincipal.getAppId();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String base64EncodedSecretKey = getBase64EncodedSecretKey(jwtConfig.getSignAndVerifyKey());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(jwtConfig.getAccessTokenExpireSeconds());
        LocalDateTime expireRefreshLocalDate = now.plusSeconds(jwtConfig.getRefreshTokenExpireSeconds());
        Date expireAccess =  Date.from(expireAccessLocalDate.atZone(myZone).toInstant());
        Date expireRefresh =  Date.from(expireRefreshLocalDate.atZone(myZone).toInstant());
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();
        JwtBuilder builder = Jwts.builder()
                .setId(accessJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .claim("roles", roles)
                .claim("client_id", appId)
                .claim("user_name", username)
                .claim("authorities", authorities);
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();
        builder = Jwts.builder()
                .setId(refreshJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .claim("roles", roles)
                .claim("ati", accessJti)
                .claim("client_id", appId)
                .claim("user_name", username)
                .claim("scope", Arrays.asList("all"))
                .claim("authorities", authorities);
        builder.setExpiration(expireRefresh);
        String refreshToken = builder.compact();
        Oauth2Token oauth2Token = new Oauth2Token();
        oauth2Token.setAccessToken(accessToken);
        oauth2Token.setRefreshToken(refreshToken);
        oauth2Token.setJti(accessJti);
        now = LocalDateTime.now(myZone);
        oauth2Token.setExpireIn(ChronoUnit.SECONDS.between(now, expireAccessLocalDate));
        return oauth2Token;
    }

    //验证refresh token是否过期
    private boolean isExpired(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
            return expiringToken.getExpiration() == null
                    || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        }
        return false;
    }
}
