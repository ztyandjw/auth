//package com.eci.security.rbac.util;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2RefreshToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
///**
// * @author T1m Zhang(49244143@qq.com) 2019/9/26.
// */
//
//
//@Component
//public class Oauth2Util {
//
//    @Autowired
//    private MyJwtAccessTokenConverter accessTokenEnhancer;
//
//    public OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
//
////        int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
//        String value = UUID.randomUUID().toString();
////        if (validitySeconds > 0) {
////            return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
////                    + (validitySeconds * 1000L)));
////        }
//        return new DefaultOAuth2RefreshToken(value);
//    }
//
//
//    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) {
//        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
////        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
////        if (validitySeconds > 0) {
////            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
////        }
//        String refreshValue = UUID.randomUUID().toString();
//        DefaultOAuth2RefreshToken defaultOAuth2RefreshToken = new DefaultOAuth2RefreshToken(refreshValue);
//        token.setRefreshToken(defaultOAuth2RefreshToken);
//        token.setScope(null);
//
//        return accessTokenEnhancer != null ? accessTokenEnhancer.enhance(token, authentication) : token;
//    }
//}
