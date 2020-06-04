package org.gvm.product.gvmpoin.configuration.spring;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("ps_consumer_login");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping(Constant.OAUTH_TOKEN_URL);
    registry.addMapping(Constant.API_URL_V1 + "/**");
    registry.addMapping(Constant.API_URL_V2 + "/**");
  }
}
