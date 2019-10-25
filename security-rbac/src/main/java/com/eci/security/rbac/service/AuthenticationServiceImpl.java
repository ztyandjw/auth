package com.eci.security.rbac.service;

import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.config.JWTConfig;
import com.eci.security.rbac.constant.ErrorCoceEnum;
import com.eci.security.rbac.constant.ProviderTypeEnum;
import com.eci.security.rbac.core.token.LocalUserNamePasswordAuthenticationToken;
import com.eci.security.rbac.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */

@Slf4j
@Service
public class AuthenticationServiceImpl {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JWTConfig jwtConfig;

    @Autowired
    private JwtAccessTokenConverter jwtTokenEnhancer;

    @Autowired
    private JwtTokenStore tokenStore;

    public Oauth2Token login(String username, String password, String providerType, Long appId) {
        String redisKey = String.format("%s%s:%s", jwtConfig.getRedisJwtPrefix(), appId.toString(), username);
        Authentication authentication = null;
        if (ProviderTypeEnum.equals(providerType, ProviderTypeEnum.LOCAL)) {
            authentication = new LocalUserNamePasswordAuthenticationToken(username, password, appId);
        }
        Oauth2Token oauth2Token = null;
        String oldToken = null;
        try {
            authentication = authenticationManager.authenticate(authentication);
            oldToken = stringRedisTemplate.opsForValue().get(redisKey);
            //redis不存在，说明没有用户登录，或者早已过期
            if (StringUtils.isBlank(oldToken)) {
                oauth2Token = jwtUtil.createAccessAndRefreshOauth2Token(authentication);
                String oauth2TokenValue = JsonUtil.ObjectToJson(oauth2Token);
                if(null == oauth2TokenValue) {
                    throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
                }
                stringRedisTemplate.opsForValue().set(redisKey, oauth2TokenValue);
                return oauth2Token;
            }
            //存在token，不考虑是否过期，直接将redis中的token返回
            oauth2Token = JsonUtil.jsonToObject(oldToken, Oauth2Token.class);
            if(null == oauth2Token) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
            }
            return oauth2Token;
        }
        catch (RedisSystemException e) {
            log.error("redis error... {}", e.toString());
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode(), e);
        }
        catch (BadCredentialsException e) {
            log.error("login failed, username: {}, password: {}... {}",username, password,  e.toString());
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH2_INVALID_GRANT_BAD_CREDENTIALS.getCode(), e);
        }
        catch (UnsupportedEncodingException e) {
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode(), e);
        }
    }




    public Oauth2Token createRefreshToken(String refreshTokenValue) throws IOException {
        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
        if (refreshToken == null) {
            log.error("Invalid refresh token: {}", refreshTokenValue);
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN.getCode());
        }
        Map<String, Object> params = jwtTokenEnhancer.decode(refreshTokenValue);
        Long appId = (Long)params.get("client_id");
        String username = (String)params.get("user_name");
        String redisKey = String.format("%s%s:%s", jwtConfig.getRedisJwtPrefix(), appId.toString(), username);
        ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
        boolean  refreshTokenExpired =  expiringToken.getExpiration() == null
                || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        Oauth2Token oauth2Token = null;
        try {
            if(refreshTokenExpired) {
                stringRedisTemplate.delete(redisKey);
                log.error("Invalid refresh token (expired): " + refreshToken);
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN_EXPIRED.getCode());
            }
            String oldToken = stringRedisTemplate.opsForValue().get(redisKey);
            if(StringUtils.isBlank(oldToken)) {
                log.error("token not found in redis");
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH2_TOKEN_NOT_FOUND.getCode());

            }
            oauth2Token = JsonUtil.jsonToObject(oldToken, Oauth2Token.class);
            if(null == oauth2Token) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
            }
            String oldRefreshToken = oauth2Token.getRefreshToken();
            if(!StringUtils.equals(refreshTokenValue, oldRefreshToken)) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN_NOT_FOUND.getCode());
            }
//            String accessTokenValue = oauth2Token.getAccessToken();
//            params = jwtTokenEnhancer.decode(accessTokenValue);
//            Long expired = (Long)params.get("exp");
//            //accessToken过期了
//            if(System.currentTimeMillis() > expired) {
//
//            }
            String newAccessToken = jwtUtil.createAccessTokenValue((String)params.get("user_name"), (List<String>)params.get("roles"), (List<String>)params.get("authorities"), (Long)params.get("client_id"));
            oauth2Token.setAccessToken(newAccessToken);
            String oauth2TokenValue = JsonUtil.ObjectToJson(oauth2Token);
            if(null == oauth2TokenValue) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
            }
            stringRedisTemplate.opsForValue().set(redisKey, oauth2TokenValue);
            return oauth2Token;
        }
        catch (RedisSystemException e) {
            log.error("redis error... {}", e.toString());
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode(), e);
        }
    }
}
