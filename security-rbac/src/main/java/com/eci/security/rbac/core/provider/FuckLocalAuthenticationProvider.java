package com.eci.security.rbac.core.provider;

import com.eci.security.rbac.core.token.LocalUserNamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.rcp.RemoteAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */


public class FuckLocalAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware
         {


             @Override
             public void afterPropertiesSet() throws Exception {

             }

             @Override
             public void setMessageSource(MessageSource messageSource) {

             }

             @Override
             public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                 return null;
             }

             @Override
             public boolean supports(Class<?> authentication) {
                 return false;
             }
         }
