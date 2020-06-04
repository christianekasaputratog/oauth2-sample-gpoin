package org.gvm.product.gvmpoin.configuration.security;

import com.zaxxer.hikari.HikariDataSource;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import java.util.Collections;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  @Autowired
  private HikariDataSource dataSource;

  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager authenticationManager;

  @Autowired
  private MyPasswordEncoder psPasswordEncoder;

  @Autowired
  private ClientDetailsService clientDetailsService;

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer oauthServer) throws Exception {
    final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
    tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(tokenEnhancer()));

    oauthServer
        .tokenStore(tokenStore())
        .tokenEnhancer(tokenEnhancerChain)
        .authenticationManager(authenticationManager)
        .authorizationCodeServices(authorizationCodeServices())
        .pathMapping("/oauth/token", Constant.OAUTH_TOKEN_URL);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer
        .passwordEncoder(psPasswordEncoder)
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients
        .jdbc(dataSource)
        .clients(clientDetailsService);
  }

  @Bean
  public TokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }

  @Bean
  public TokenEnhancer tokenEnhancer() {
    return new CustomTokenEnhancer();
  }

  @Bean
  public AuthorizationCodeServices authorizationCodeServices() {
    return new JdbcAuthorizationCodeServices(dataSource);
  }
}
