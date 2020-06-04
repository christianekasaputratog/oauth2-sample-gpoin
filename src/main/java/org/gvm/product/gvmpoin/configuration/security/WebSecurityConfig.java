package org.gvm.product.gvmpoin.configuration.security;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private MyPasswordEncoder myPasswordEncoder;

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /**
   * Configure Global Authentication .
   *
   * @param auth For memory authentication;
   * @throws Exception throws Exception
   */
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(myPasswordEncoder);
  }

  @Bean
  public AuthenticationEntryPoint customAuthEntryPoint() {
    return new WebApiAuthenticationEntryPoint();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    http
        .csrf()
        .disable() // temp because error in production

        .sessionManagement()
        .sessionFixation()
        .none()

        .and()
        .headers()
        .frameOptions()
        .disable() // allow iframe

        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            customAuthEntryPoint(), new RegexRequestMatcher(Constant.WEB_API_URL,
                HttpMethod.GET.name()))
        .and()

        .authorizeRequests()
        .antMatchers(Constant.WEB_API_URL + "/consumer/get_pin",
            Constant.WEB_API_URL + "/consumer/pin",
            Constant.WEB_API_URL + "/consumer/forgot_pin",
            Constant.WEB_API_URL + "/consumer/null_ps_id",
            Constant.WEB_API_URL + "/consumer/status",
            Constant.WEB_API_URL + "/consumer/sync/client",
            Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH + "/partner/all",
            Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH + "/reward/popular",
            Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH + "/promotion/active",
            Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH
                + "/reward/latest/exclude/popular")
        .permitAll()
        .and()

        .authorizeRequests()
        .antMatchers(Constant.WEB_API_URL + "/**")
        .authenticated()
        .and()

        .requestCache()
        .requestCache(new NullRequestCache());
//        .and()
//        .httpBasic()
//        .and()
//
//        .formLogin()
//        .loginPage("/login")
//        .permitAll()
//        .and()
//
//        .logout()
//        .permitAll();
  }
}
