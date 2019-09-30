//package com.eci.security.rbac.core;
//
//import com.google.common.collect.ImmutableMap;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.TokenRequest;
//import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * @author T1m Zhang(49244143@qq.com) 2019/9/29.
// */
//
//@Component
//public class MyTokenEndpoint extends TokenEndpoint {
//
//    public OAuth2AccessToken getOauth2AccessToken() {
//        String clientId = "clientIdTest01";
//
//        ClientDetails authenticatedClient = getClientDetailsService().loadClientByClientId(clientId);
//        Map<String, String> parameters = ImmutableMap.of("grant_type", "authorization_code", "code", "testAAA", "redirect_uri", "http://www.baidu.com", "client_id", authenticatedClient.getClientId(),"client_secret", authenticatedClient.getClientSecret());
//        TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);
//        OAuth2AccessToken token = getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
//        return token;
//    }
//}
