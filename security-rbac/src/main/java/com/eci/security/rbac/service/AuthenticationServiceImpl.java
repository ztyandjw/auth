package com.eci.security.rbac.service;

import com.eci.security.rbac.common.dataobject.AppDO;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.config.JWTConfig;
import com.eci.security.rbac.constant.ErrorCoceEnum;
import com.eci.security.rbac.constant.ProviderTypeEnum;
import com.eci.security.rbac.core.provider.MyAuthenticationManager;
import com.eci.security.rbac.core.token.LocalUserNamePasswordAuthenticationToken;
import com.eci.security.rbac.dao.AppDAO;
import com.eci.security.rbac.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */

@Slf4j
@Service
public class AuthenticationServiceImpl {

    @Autowired
    private MyAuthenticationManager MyProviderManager;
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

    @Autowired
    private AppDAO appDAO;

    public Oauth2Token login(String username, String password, String providerType, Long appId) {
        AppDO appDO = appDAO.selectByAppid(appId);
        if(null == appDO) {
            log.error("appId: {} 没有匹配应用", appId);
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_ERROR_REQUEST.getCode(),  "入参请求错误，参数{} 值{} {}", "appId,", appId, "没有匹配的应用");

        }
        String redisKey = String.format("%s%s:%s", jwtConfig.getRedisJwtPrefix(), appId.toString(), username);
        Authentication authentication = null;
        if (ProviderTypeEnum.equals(providerType, ProviderTypeEnum.LOCAL)) {
            authentication = new LocalUserNamePasswordAuthenticationToken(username, password, appId);
        }
        Oauth2Token oauth2Token = null;
        String oldToken = null;
        try {
            authentication = MyProviderManager.authenticate(authentication);
            oldToken = stringRedisTemplate.opsForValue().get(redisKey);
            //redis不存在，说明没有用户登录，或者记录已经被删除
            if (StringUtils.isBlank(oldToken)) {
                oauth2Token = jwtUtil.createAccessAndRefreshOauth2Token(authentication);
                String oauth2TokenValue = JsonUtil.ObjectToJson(oauth2Token);
                if(null == oauth2TokenValue) {
                    throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
                }
                stringRedisTemplate.opsForValue().set(redisKey, oauth2TokenValue);
                return oauth2Token;
            }
            oauth2Token = JsonUtil.jsonToObject(oldToken, Oauth2Token.class);
            if(null == oauth2Token) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
            }
            String accessTokenValue = oauth2Token.getAccessToken();
            String refreshTokenValue = oauth2Token.getRefreshToken();
            boolean refreshTokenExpired = jwtUtil.isExpired(refreshTokenValue);
            //假设refreshToken过期了，重新生成一个新的oauth2 Token
            if(refreshTokenExpired) {
                oauth2Token = jwtUtil.createAccessAndRefreshOauth2Token(authentication);
                String oauth2TokenValue = JsonUtil.ObjectToJson(oauth2Token);
                if(null == oauth2TokenValue) {
                    throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
                }
                stringRedisTemplate.opsForValue().set(redisKey, oauth2TokenValue);
                return oauth2Token;
            }
            boolean acccessTokenExpired =  jwtUtil.isExpired(accessTokenValue);
            //accessToken过期，更新accessToken
            if(acccessTokenExpired) {
                oauth2Token = jwtUtil.refreshAccessToken(oauth2Token);
                stringRedisTemplate.opsForValue().set(redisKey, JsonUtil.ObjectToJson(oauth2Token));
                return oauth2Token;
            }
            long accessTokenExpiredTimestamp = jwtUtil.getAccessExpiredTimestamp(accessTokenValue);
            oauth2Token.setExpireIn(jwtUtil.getExpiredSeconds(accessTokenExpiredTimestamp));
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtil.ObjectToJson(oauth2Token));
            return  oauth2Token;
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
        OAuth2RefreshToken refreshToken = null;
        try {
            refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
            //refreshToken不合法,应用跳转登录界面重新登录
            if (refreshToken == null) {
                log.error("Invalid refresh token: {}", refreshTokenValue);
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN.getCode());
            }
        }
        //refreshToken不合法,应用跳转登录界面重新登录
        catch (Exception e) {
            log.error("Invalid refresh token: {}", refreshTokenValue);
            throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN.getCode());
        }

        Map<String, Object> params = jwtTokenEnhancer.decode(refreshTokenValue);
        Long appId = (Long)params.get("client_id");
        String username = (String)params.get("user_name");
        String redisKey = String.format("%s%s:%s", jwtConfig.getRedisJwtPrefix(), appId.toString(), username);
        boolean refreshTokenExpired  = jwtUtil.isExpired(refreshTokenValue);
//        ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
//        boolean  refreshTokenExpired =  expiringToken.getExpiration() == null
//                || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        Oauth2Token oauth2Token = null;
        try {
            //假设refreshToken已经过期，需要重新登录
            if(refreshTokenExpired) {
                stringRedisTemplate.delete(redisKey);
                log.error("Invalid refresh token (expired): " + refreshToken);
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN_EXPIRED.getCode());
            }
            String oldTokenValue = stringRedisTemplate.opsForValue().get(redisKey);
            //假设redis中没有token，需要重新登录
            if(StringUtils.isBlank(oldTokenValue)) {
                log.error("token not found in redis");
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH2_TOKEN_NOT_FOUND.getCode());

            }
            oauth2Token = JsonUtil.jsonToObject(oldTokenValue, Oauth2Token.class);
            if(null == oauth2Token) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.SERVICE_INTERNAL_ERROR.getCode());
            }
            String oldRefreshToken = oauth2Token.getRefreshToken();
            //假设refreshToken与原始refreshToken不一致，重新登录
            if(!StringUtils.equals(refreshTokenValue, oldRefreshToken)) {
                throw ServiceExceptionUtil.exception(ErrorCoceEnum.OAUTH_INVALID_REFRESH_TOKEN_NOT_FOUND.getCode());
            }
            oauth2Token = jwtUtil.refreshAccessToken(oauth2Token);
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
