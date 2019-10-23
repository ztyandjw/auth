package com.eci.security.rbac.service;

import com.eci.security.rbac.common.vo.CommonResult;
import com.eci.security.rbac.common.vo.Oauth2Token;
import com.eci.security.rbac.config.JWTConfig;
import com.eci.security.rbac.constant.ProviderTypeEnum;
import com.eci.security.rbac.core.token.LocalUserNamePasswordAuthenticationToken;
import com.eci.security.rbac.util.JWTUtil;
import com.eci.security.rbac.util.JwtAccessTokenConverter;
import com.eci.security.rbac.util.JwtTokenStore;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */


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

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public Oauth2Token login(String username, String password, String providerType, Long appId) throws IOException {
        String redisKey = jwtConfig.getRedisJwtPrefix() + username;
        Authentication authentication = null;
        if(ProviderTypeEnum.equals(providerType, ProviderTypeEnum.LOCAL)) {
            authentication = new LocalUserNamePasswordAuthenticationToken(username, password, appId);
        }
        authentication = authenticationManager.authenticate(authentication);
        String oldToken = stringRedisTemplate.opsForValue().get(redisKey);
        Oauth2Token oauth2Token = null;
        //假设redis中本来不存在，生成新的token

        if(StringUtils.isBlank(oldToken)) {
            oauth2Token = jwtUtil.createAccessToken(authentication);
            stringRedisTemplate.opsForValue().set(redisKey, MAPPER.writeValueAsString(oauth2Token));
            return oauth2Token;
        }
        oauth2Token  = MAPPER.readValue(oldToken, Oauth2Token.class);
        return oauth2Token;
    }



    public Oauth2Token createRefreshToken(String refreshTokenValue) throws IOException {



        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
        if (refreshToken == null) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        }
        ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
        boolean  isExpired =  expiringToken.getExpiration() == null
                || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        Map<String, ?> params = jwtTokenEnhancer.decode(refreshTokenValue);
        String redisKey = jwtConfig.getRedisJwtPrefix() + (String)params.get("user_name");
        if(isExpired) {
            stringRedisTemplate.delete(redisKey);
            throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
        }

        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        Oauth2Token oauth2Token = MAPPER.readValue(redisValue, Oauth2Token.class);
        String oldRefreshToken = oauth2Token.getRefreshToken();
        if(!StringUtils.equals(refreshTokenValue, oldRefreshToken)) {
            throw new InvalidGrantException("Invalid refresh token, can't equals to old refreshToken: " + refreshTokenValue);
        }

        String newAccessToken = jwtUtil.createAccessTokenValue((String)params.get("user_name"), (List<String>)params.get("roles"), (List<String>)params.get("authorities"), (String)params.get("client_id"));
        oauth2Token.setAccessToken(newAccessToken);
        stringRedisTemplate.opsForValue().set(redisKey, MAPPER.writeValueAsString(oauth2Token));
        return oauth2Token;
    }
}
