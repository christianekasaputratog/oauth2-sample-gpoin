package org.gvm.product.gvmpoin.configuration.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@Configuration
public class ThymeleafConfig {

  @Bean
  public SpringSecurityDialect springSecurityDialect() {
    return new SpringSecurityDialect();
  }
}