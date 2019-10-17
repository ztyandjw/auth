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
import org.springframework.context.annotation.Bean;
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
    private JwtTokenStore tokenStore;
    @Autowired
    private BASE64Encoder base64Encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter jwtTokenEnhancer;


//    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
//        Map<String, String> parameters = new HashMap<String, String>();
//        Set<String> scope = extractScope(map);
//        Authentication user = userTokenConverter.extractAuthentication(map);
//        String clientId = (String) map.get(CLIENT_ID);
//        parameters.put(CLIENT_ID, clientId);
//        if (includeGrantType && map.containsKey(GRANT_TYPE)) {
//            parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
//        }
//        Set<String> resourceIds = new LinkedHashSet<String>(map.containsKey(AUD) ? getAudience(map)
//                : Collections.<String>emptySet());
//
//        Collection<? extends GrantedAuthority> authorities = null;
//        if (user==null && map.containsKey(AUTHORITIES)) {
//            @SuppressWarnings("unchecked")
//            String[] roles = ((Collection<String>)map.get(AUTHORITIES)).toArray(new String[0]);
//            authorities = AuthorityUtils.createAuthorityList(roles);
//        }
//        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null, null,
//                null);
//        return new OAuth2Authentication(request, user);
//    }





    protected String getBase64EncodedSecretKey(String key) throws UnsupportedEncodingException {
        byte[] b = key.getBytes("UTF-8");
        return base64Encoder.encode(b);
    }


    protected Oauth2Token createAccessAndRefreshToken(String userName, List<String> roles, List<String> authorities) throws UnsupportedEncodingException {
        String base64EncodedSecretKey = this.getBase64EncodedSecretKey(jwtConfig.getSignAndVerifyKey());
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
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .claim("roles", roles)
                .claim("client_id", "testclient001")
                .claim("user_name", userName)
                .claim("authorities", authorities);
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();

        builder = Jwts.builder()
                .setId(refreshJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, base64EncodedSecretKey)
                .claim("roles", roles)
                .claim("ati", accessJti)
                .claim("client_id", "testclient001")
                .claim("user_name", userName)
                .claim("scope", Arrays.asList("all"))
                .claim("authorities", authorities);
        builder.setExpiration(expireRefresh);
        String refreshToken = builder.compact();
        Oauth2Token oauth2Token = new Oauth2Token();
        oauth2Token.setAccessToken(accessToken);
        oauth2Token.setRefreshToken(refreshToken);
        oauth2Token.setJti(accessJti);
        oauth2Token.setExpiration(expireAccessLocalDate);
        return oauth2Token;

    }

    public Oauth2Token createAccessToken(Authentication authentication) throws UnsupportedEncodingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        Oauth2Token oauth2Token =  createAccessAndRefreshToken(userPrincipal.getUsername(), userPrincipal.getRoles(), authorities);
        return oauth2Token;
    }

    //验证refresh token是否过期
    protected boolean isExpired(OAuth2RefreshToken refreshToken) {
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
            return expiringToken.getExpiration() == null
                    || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        }
        return false;
    }





    public Oauth2Token createRefreshToken(String refreshTokenValue) throws UnsupportedEncodingException {
        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);

        if (refreshToken == null) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        }
        ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
        boolean  isExpired =  expiringToken.getExpiration() == null
                || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        if(isExpired) {
            throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
        }
        Map<String, ?> params = jwtTokenEnhancer.decode(refreshTokenValue);
        Oauth2Token oauth2Token =  createAccessAndRefreshToken((String)params.get("user_name"), (List<String>)params.get("roles"), (List<String>)params.get("authorities"));
        return oauth2Token;



//        OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
//
//
//            if (this.authenticationManager != null && !authentication.isClientOnly()) {
//                // The client has already been authenticated, but the user authentication might be old now, so give it a
//                // chance to re-authenticate.
//                Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUserAuthentication(), "", authentication.getAuthorities());
//
//
//            user = authenticationManager.authenticate(user);
//            Object details = authentication.getDetails();
//            authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
//            authentication.setDetails(details);
//        }
//        String clientId = authentication.getOAuth2Request().getClientId();
//
//
//        return clientId;


//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtConfig.getKey())
//                .parseClaimsJws(refreshToken)
//                .getBody();
//
//        String username = claims.getSubject();
    }




}
