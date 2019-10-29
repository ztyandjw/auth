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
import java.time.Instant;
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

    @Autowired
    private JwtAccessTokenConverter jwtTokenEnhancer;

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

    public Oauth2Token refreshAccessToken(Oauth2Token oauth2Token) throws UnsupportedEncodingException {
        String accessToken = oauth2Token.getAccessToken();
        Map<String, Object>params = jwtTokenEnhancer.decode(accessToken);
        String username = (String)params.get("user_name");
        List<String> roles = (List<String>)params.get("roles");
        List<String> authorities = (List<String>)params.get("authorities");
        Long appId = (Long)params.get("client_id");
        String newAccessToken = this.createAccessTokenValue(username, roles, authorities, appId);
        oauth2Token.setAccessToken(newAccessToken);
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(jwtConfig.getAccessTokenExpireSeconds());
        oauth2Token.setExpireIn(ChronoUnit.SECONDS.between(now, expireAccessLocalDate));
        return oauth2Token;
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

//    //验证refresh token是否过期
//    private boolean isExpired(OAuth2RefreshToken refreshToken) {
//        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
//            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
//            return expiringToken.getExpiration() == null
//                    || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
//        }
//        return false;
//    }

    public boolean isExpired(String token) {
        Map<String, Object>params = jwtTokenEnhancer.decode(token);
        Long expired = (Long)params.get("exp") * 1000;
        //没有过期
        if( System.currentTimeMillis() < expired) {
            return false;
        }
        return true;
    }

    public long getExpiredSeconds(long expiredTimestamp) {
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expiredLocalDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiredTimestamp), myZone);
        return ChronoUnit.SECONDS.between(now, expiredLocalDate);
    }

    public long getAccessExpiredTimestamp(String accessToken) {
        Map<String, Object>params = jwtTokenEnhancer.decode(accessToken);
        Long expiredTimestamp = (Long)params.get("exp") * 1000;
        return expiredTimestamp;
    }

//    public long getRefreshExpiredTimestamp(String refreshToken) {
//        Map<String, Object>params = jwtTokenEnhancer.decode(refreshToken);
//        Long expiredTimestamp = (Long)params.get("exp") * 1000;
//        return expiredTimestamp;
//    }

}
