package com.eci.security.rbac.util;

import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.UUID;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/26.
 */

public class Oauth2Util {


    public OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {

//        int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
        String value = UUID.randomUUID().toString();
//        if (validitySeconds > 0) {
//            return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
//                    + (validitySeconds * 1000L)));
//        }
        return new DefaultOAuth2RefreshToken(value);
    }
}
