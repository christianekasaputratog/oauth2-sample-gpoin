package org.gvm.product.gvmpoin.configuration.security;

import com.zaxxer.hikari.HikariDataSource;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

  private static final String RESOURCE_ID = "gvm-api";

  @Autowired
  private HikariDataSource dataSource;

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
    resources
        .resourceId(RESOURCE_ID)
        .tokenStore(tokenStore());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/consumer/get_pin",
            Constant.API_URL_V1 + "/consumer/change_pin")
        .permitAll()
        .and()

        .antMatcher(Constant.API_URL_V1 + "/**")
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/client/add")
        .access(hasScope(AuthScope.SUPERADMIN))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/client/approve")
        .access(hasScope(AuthScope.SUPERADMIN))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/client/reset_password")
        .access(hasScope(AuthScope.SUPERADMIN))

        .antMatchers(HttpMethod.GET, "/integrator/get_access_token")
        .access(hasScope(AuthScope.SUPERADMIN))
        .antMatchers(HttpMethod.POST, "/integrator/insert_consumer")
        .access(hasScope(AuthScope.SUPERADMIN))
        .antMatchers(HttpMethod.POST, "/integrator/activate_all")
        .access(hasScope(AuthScope.SUPERADMIN))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/get_psid_by_email")
        .access(hasScope(AuthScope.READ))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/get_profile_by_psid")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/get_profile_by_email")
        .access(hasScope(AuthScope.READ))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/consumer/add_by_email")
        .access(hasScope(AuthScope.READ))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/consumer/profile")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/profile")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/profile/email")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/consumer/identities/email")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))

        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/balance/add")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/balance/substract")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/balance")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/balance/history")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/campaign/info")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/campaign/leaderboard/score")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/campaign/leaderboard")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/campaign/leaderboard/position")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.PUT, Constant.API_URL_V1 + "/campaign/leaderboard/score")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/continous_engagement/count")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/continous_engagement/count")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/tier-system/progress")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/promotion/active")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))

        .antMatchers(HttpMethod.GET, Constant.API_URL_V1 + "/reward-system/**")
        .access(hasScope(AuthScope.READ, AuthScope.CLIENT))
        .antMatchers(HttpMethod.POST, Constant.API_URL_V1 + "/reward-system/**")
        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT));

//    http
//        .antMatcher(Constant.API_URL_V2 + "/**")
//        .authorizeRequests()
//        .antMatchers(HttpMethod.POST, Constant.API_URL_V2 + "/balance/add")
//        .access(hasScope(AuthScope.WRITE, AuthScope.CLIENT));

  }

  private String hasScope(AuthScope... scopes) {
    StringBuilder sb = new StringBuilder();
    for (AuthScope authScope : scopes) {
      sb.append("#oauth2.hasScope('").append(authScope.toString()).append("') or ");
    }
    sb.setLength(sb.length() - 4);
    return sb.toString();
  }

  @Bean
  public TokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }

  /**
   * @return Default Token Services .
   */
  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    return defaultTokenServices;
  }
}
