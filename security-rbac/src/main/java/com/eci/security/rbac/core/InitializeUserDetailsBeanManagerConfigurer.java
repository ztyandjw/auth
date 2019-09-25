package com.eci.security.rbac.core;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/24.
 */

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Lazily initializes the global authentication with a {@link UserDetailsService} if it is
 * not yet configured and there is only a single Bean of that type. Optionally, if a
 * {@link PasswordEncoder} is defined will wire this up too.
 *
 * @author Rob Winch
 * @since 4.1
 */

public class InitializeUserDetailsBeanManagerConfigurer
  extends GlobalAuthenticationConfigurerAdapter {

  static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 5000;

  private final ApplicationContext context;

  /**
   * @param context
   */
  public InitializeUserDetailsBeanManagerConfigurer(ApplicationContext context) {
    this.context = context;
  }

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.apply(new InitializeUserDetailsManagerConfigurer());
  }

  class InitializeUserDetailsManagerConfigurer
    extends GlobalAuthenticationConfigurerAdapter {
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      if (auth.isConfigured()) {
        return;
      }
      UserDetailsService userDetailsService = getBeanOrNull(
        UserDetailsService.class);
      if (userDetailsService == null) {
        return;
      }

      PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
      UserDetailsPasswordService passwordManager = getBeanOrNull(UserDetailsPasswordService.class);

      DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
      provider.setUserDetailsService(userDetailsService);
      if (passwordEncoder != null) {
        provider.setPasswordEncoder(passwordEncoder);
      }
      if (passwordManager != null) {
        provider.setUserDetailsPasswordService(passwordManager);
      }
      provider.afterPropertiesSet();

      auth.authenticationProvider(provider);
    }

    /**
     * @return
     */
    private <T> T getBeanOrNull(Class<T> type) {
      String[] userDetailsBeanNames = InitializeUserDetailsBeanManagerConfigurer.this.context
        .getBeanNamesForType(type);
      if (userDetailsBeanNames.length != 1) {
        return null;
      }

      return InitializeUserDetailsBeanManagerConfigurer.this.context
        .getBean(userDetailsBeanNames[0], type);
    }
  }
}
